package io.topiacoin.node.model.provider;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.topiacoin.node.exceptions.BlockchainAlreadyExistsException;
import io.topiacoin.node.exceptions.ContainerAlreadyExistsException;
import io.topiacoin.node.exceptions.DataItemAlreadyExistsException;
import io.topiacoin.node.exceptions.InitializationException;
import io.topiacoin.node.exceptions.MicroNetworkAlreadyExistsException;
import io.topiacoin.node.exceptions.NoSuchBlockchainException;
import io.topiacoin.node.exceptions.NoSuchContainerException;
import io.topiacoin.node.exceptions.NoSuchDataItemException;
import io.topiacoin.node.exceptions.NoSuchMicroNetworkException;
import io.topiacoin.node.exceptions.NoSuchNodeException;
import io.topiacoin.node.model.BlockchainInfo;
import io.topiacoin.node.model.Challenge;
import io.topiacoin.node.model.ContainerInfo;
import io.topiacoin.node.model.DataItemInfo;
import io.topiacoin.node.model.MicroNetworkInfo;
import io.topiacoin.node.model.MicroNetworkState;
import io.topiacoin.node.model.NodeConnectionInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("filesystem")
public class SQLiteDataModelProvider implements DataModelProvider {

    private Log _log = LogFactory.getLog(this.getClass());

    private String _databasePath;
    private String _databaseName;

    private ObjectMapper _objectMapper;

    @Override
    public void initialize() {
        _log.info("Initializing SQLite Data Model Provider");

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception e) {
            _log.info("Unable to load the SQLite JDBC Driver", e);
            throw new InitializationException("Unable to load the SQLite JDBC Driver", e);
        }

        try {
            initializeDatabase();
        } catch (SQLException e) {
            throw new InitializationException("Exception initializing the database schema", e);
        }

        _objectMapper = new ObjectMapper();

        _log.info("Initialized SQLite Data Model Provider");
    }

    @Override
    public void shutdown() {
        _log.info("Shutting Down SQLite Data Model Provider");
        _log.info("Shutdown SQLite Data Model Provider");
    }

    private Connection getConnection() throws SQLException {
        // Enable Foreign Key Constraints
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);

        return DriverManager.getConnection("jdbc:sqlite:" + _databasePath + "/" + _databaseName, config.toProperties());
    }

    private void initializeDatabase() throws SQLException {
        Connection c = getConnection();

        // Containers Table
        String containerCreateSQL = "CREATE TABLE IF NOT EXISTS Containers (" +
                "containerID    TEXT PRIMARY KEY    NOT NULL, " +
                "expiration     INT(20)             NOT NULL, " +
                "challenge      TEXT );";
        PreparedStatement createContainersPS = c.prepareStatement(containerCreateSQL);
        createContainersPS.execute();

        // DataItems Table
        String dataItemCreateSQL = "CREATE TABLE IF NOT EXISTS DataItems (" +
                "dataItemID     TEXT PRIMARY KEY    NOT NULL, " +
                "size           INT(20)             NOT NULL, " +
                "dataHash       TEXT                NOT NULL );";
        PreparedStatement createDataItemsPS = c.prepareStatement(dataItemCreateSQL);
        createDataItemsPS.execute();

        // DataItems/Containers Table
        String dataItemContainerCreateSQL = "CREATE TABLE IF NOT EXISTS DataItemsContainer (" +
                "containerID    TEXT                NOT NULL, " +
                "dataItemID     TEXT                NOT NULL," +
                "PRIMARY KEY (containerID, dataItemID)," +
                "FOREIGN KEY (containerID) REFERENCES Containers(containerID)," +
                "FOREIGN KEY (dataItemID) REFERENCES DataItems(dataItemID) );";
        PreparedStatement createDataItemsContainerPS = c.prepareStatement(dataItemContainerCreateSQL);
        createDataItemsContainerPS.execute();

        // MicroNetwork Table
        String microNetworkCreateSQL = "CREATE TABLE IF NOT EXISTS MicroNetworks (" +
                "microNetworkID TEXT PRIMARY KEY    NOT NULL, " +
                "containerID    TEXT                NOT NULL, " +
                "path           TEXT, " +
                "state          TEXT                NOT NULL, " +
                "rpcURL         TEXT, " +
                "p2pURL         TEXT );";
        PreparedStatement microNetworkInfoPS = c.prepareStatement(microNetworkCreateSQL);
        microNetworkInfoPS.execute();
    }

    @Override
    public ContainerInfo createContainer(
            String containerID,
            long expirationDate,
            Challenge challenge)
            throws ContainerAlreadyExistsException {

        ContainerInfo containerInfo = null;

        try (Connection c = getConnection()) {
            String challengeString = _objectMapper.writeValueAsString(challenge);

            String insertSQL = "INSERT INTO Containers (`containerID`, `expiration`, `challenge`) VALUES (?, ?, ?)";
            PreparedStatement ps = c.prepareStatement(insertSQL);

            ps.setString(1, containerID);
            ps.setLong(2, expirationDate);
            ps.setString(3, challengeString);

            ps.executeUpdate();

            containerInfo = new ContainerInfo(containerID, expirationDate, challenge);
        } catch (SQLiteException e) {
            SQLiteErrorCode errorCode = e.getResultCode();
            if (errorCode == SQLiteErrorCode.SQLITE_CONSTRAINT_PRIMARYKEY) {
                throw new ContainerAlreadyExistsException("A container already exists with the specified ID");
            }
            throw new RuntimeException("Exception while using SQLite", e);
        } catch (SQLException e) {
            throw new RuntimeException("Exception while using SQLite", e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Exception while Mapping Object to JSON", e);
        }

        return containerInfo;
    }

    @Override
    public void updateContainer(ContainerInfo updatedContainer)
            throws NoSuchContainerException {

        ContainerInfo containerInfo = null;

        try (Connection c = getConnection()) {
            String containerID = updatedContainer.getId();
            long expirationDate = updatedContainer.getExpirationDate();
            Challenge challenge = updatedContainer.getChallenge();
            String challengeString = (challenge != null ? _objectMapper.writeValueAsString(challenge) : null);

            String updateSQL = "UPDATE Containers SET `expiration` = ?, `challenge` = ? WHERE `containerID` = ?;";
            PreparedStatement ps = c.prepareStatement(updateSQL);

            ps.setLong(1, expirationDate);
            ps.setString(2, challengeString);
            ps.setString(3, containerID);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new NoSuchContainerException("Unable to update the container.  Entry does not exist.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Exception while using SQLite", e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Exception while Mapping Object to JSON", e);
        }
    }

    @Override
    public ContainerInfo getContainer(String containerID) {
        ContainerInfo containerInfo = null;

        try (Connection c = getConnection()) {
            String selectSQL = "SELECT expiration, challenge FROM Containers WHERE `containerID` = ?;";
            PreparedStatement ps = c.prepareStatement(selectSQL);

            ps.setString(1, containerID);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long expiration = rs.getLong(1);
                String challengeString = rs.getString(2);

                Challenge challenge = _objectMapper.readValue(challengeString, Challenge.class);

                containerInfo = new ContainerInfo(containerID, expiration, challenge);
            }

            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Exception while using SQLite", e);
        } catch (JsonParseException e) {
            throw new RuntimeException("Exception while Parsing JSON", e);
        } catch (JsonMappingException e) {
            throw new RuntimeException("Exception while Mapping JSON to Object", e);
        } catch (IOException e) {
            throw new RuntimeException("IUException getting a container", e);
        }

        return containerInfo;
    }

    @Override
    public boolean removeContainer(String containerID) {

        ContainerInfo containerInfo = null;

        try (Connection c = getConnection()) {
            String deleteSQL = "DELETE FROM Containers WHERE `containerID` = ?;";
            PreparedStatement ps = c.prepareStatement(deleteSQL);

            ps.setString(1, containerID);

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Exception while using SQLite", e);
        }
    }

    @Override
    public void addDataItemToContainer(
            String dataItemID,
            String containerID)
            throws NoSuchContainerException, DataItemAlreadyExistsException, NoSuchDataItemException {

        ContainerInfo containerInfo = null;

        try (Connection c = getConnection()) {
            if ( getContainer(containerID) == null ){
                throw new NoSuchContainerException("The specified container does not exist");
            }
            if ( getDataItem(dataItemID) == null ){
                throw new NoSuchDataItemException("The specified data item does not exist");
            }

            String deleteSQL = "INSERT INTO `DataItemsContainer` (`containerID`, `dataItemID`) VALUES (?, ?)";
            PreparedStatement ps = c.prepareStatement(deleteSQL);

            ps.setString(1, containerID);
            ps.setString(2, dataItemID);

            ps.executeUpdate();
        } catch (SQLiteException e) {
            SQLiteErrorCode errorCode = e.getResultCode();
            if (errorCode == SQLiteErrorCode.SQLITE_CONSTRAINT_PRIMARYKEY) {
                throw new DataItemAlreadyExistsException("The data item is already in the specified container");
            }
            throw new RuntimeException("Exception while using SQLite", e);
        } catch (SQLException e) {
            throw new RuntimeException("Exception while using SQLite", e);
        }
    }

    @Override
    public boolean removeDataItemFromContainer(
            String dataItemID,
            String containerID)
            throws NoSuchContainerException {

        ContainerInfo containerInfo = null;

        try (Connection c = getConnection()) {
            if ( getContainer(containerID) == null ){
                throw new NoSuchContainerException("The specified container does not exist");
            }

            String deleteSQL = "DELETE FROM `DataItemsContainer` WHERE `containerID` = ? AND `dataItemID` = ?";
            PreparedStatement ps = c.prepareStatement(deleteSQL);

            ps.setString(1, containerID);
            ps.setString(2, dataItemID);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Exception while using SQLite", e);
        }
    }

    @Override
    public boolean isDataItemInContainer(
            String dataItemID,
            String containerID)
            throws NoSuchContainerException {

        ContainerInfo containerInfo = null;

        try (Connection c = getConnection()) {
            if ( getContainer(containerID) == null ){
                throw new NoSuchContainerException("The specified container does not exist");
            }

            String deleteSQL = "SELECT * FROM `DataItemsContainer` WHERE `containerID` = ? AND `dataItemID` = ?";
            PreparedStatement ps = c.prepareStatement(deleteSQL);

            ps.setString(1, containerID);
            ps.setString(2, dataItemID);

            ResultSet rs = ps.executeQuery();
            boolean found = rs.next();
            rs.close();

            return found;
        } catch (SQLException e) {
            throw new RuntimeException("Exception while using SQLite", e);
        }
    }

    @Override
    public boolean isDataItemInAnyContainer(String dataItemID) {

        ContainerInfo containerInfo = null;

        try (Connection c = getConnection()) {

            String deleteSQL = "SELECT COUNT(*) FROM `DataItemsContainer` WHERE `dataItemID` = ?";
            PreparedStatement ps = c.prepareStatement(deleteSQL);

            ps.setString(1, dataItemID);

            ResultSet rs = ps.executeQuery();
            boolean found = false ;
            if (rs.next()) {
                found = rs.getLong(1) > 0;
            }
            rs.close();

            return found;
        } catch (SQLException e) {
            throw new RuntimeException("Exception while using SQLite", e);
        }
    }

    @Override
    public DataItemInfo createDataItem(
            String dataItemID,
            long size,
            String dataHash)
            throws DataItemAlreadyExistsException {

        DataItemInfo dataItemInfo = null;

        try (Connection c = getConnection()) {

            String insertSQL = "INSERT INTO DataItems (`dataItemID`, `size`, `dataHash`) VALUES (?, ?, ?)";
            PreparedStatement ps = c.prepareStatement(insertSQL);

            ps.setString(1, dataItemID);
            ps.setLong(2, size);
            ps.setString(3, dataHash);

            ps.executeUpdate();

            dataItemInfo = new DataItemInfo(dataItemID, size, dataHash);
        } catch (SQLiteException e) {
            SQLiteErrorCode errorCode = e.getResultCode();
            if (errorCode == SQLiteErrorCode.SQLITE_CONSTRAINT_PRIMARYKEY) {
                throw new DataItemAlreadyExistsException("A data item already exists with the specified ID");
            }
            throw new RuntimeException("Exception while using SQLite", e);
        } catch (SQLException e) {
            throw new RuntimeException("Exception while using SQLite", e);
        }

        return dataItemInfo;
    }

    @Override
    public void updateDataItem(DataItemInfo updatedDataItem)
            throws NoSuchDataItemException {

        DataItemInfo dataItemInfo = null;

        try (Connection c = getConnection()) {

            String dataItemID = updatedDataItem.getId();
            long size = updatedDataItem.getSize();
            String dataHash = updatedDataItem.getDataHash();

            String insertSQL = "UPDATE DataItems SET `size` = ?, `dataHash` = ? WHERE `dataItemID` = ?";
            PreparedStatement ps = c.prepareStatement(insertSQL);

            ps.setLong(1, size);
            ps.setString(2, dataHash);
            ps.setString(3, dataItemID);

            int rowsAffected = ps.executeUpdate() ;
            if ( rowsAffected == 0 ) {
                throw new NoSuchDataItemException("The specified Data Item does not exist") ;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Exception while using SQLite", e);
        }
    }

    @Override
    public DataItemInfo getDataItem(String dataItemID) {

        DataItemInfo dataItemInfo = null;

        try (Connection c = getConnection()) {

            String insertSQL = "SELECT `size`, `dataHash` FROM DataItems WHERE `dataItemID` = ?";
            PreparedStatement ps = c.prepareStatement(insertSQL);

            ps.setString(1, dataItemID);

            ResultSet rs = ps.executeQuery();
            if ( rs.next() ) {
                long size = rs.getLong(1) ;
                String dataHash = rs.getString(2);
            dataItemInfo = new DataItemInfo(dataItemID, size, dataHash);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Exception while using SQLite", e);
        }

        return dataItemInfo;
    }

    @Override
    public List<DataItemInfo> getDataItems(String containerID)
            throws NoSuchContainerException {

        List<DataItemInfo> dataItemInfos = new ArrayList<>();

        try (Connection c = getConnection()) {

            if ( getContainer(containerID) == null ){
                throw new NoSuchContainerException("The specified container does not exist");
            }

            String insertSQL = "SELECT di.`dataItemID`, di.`size`, di.`dataHash` FROM DataItemsContainer AS dic LEFT JOIN DataItems AS di ON dic.`dataItemID` = di.`dataItemID`  WHERE dic.`containerID` = ?";
            PreparedStatement ps = c.prepareStatement(insertSQL);

            ps.setString(1, containerID);

            ResultSet rs = ps.executeQuery();
            while ( rs.next() ) {
                String dataItemID = rs.getString(1);
                long size = rs.getLong(2) ;
                String dataHash = rs.getString(3);
                DataItemInfo dataItemInfo = new DataItemInfo(dataItemID, size, dataHash);
                dataItemInfos.add(dataItemInfo);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Exception while using SQLite", e);
        }

        return dataItemInfos;
    }

    @Override
    public boolean removeDataItem(String dataItemID) {

        DataItemInfo dataItemInfo = null;

        try (Connection c = getConnection()) {

            String insertSQL = "DELETE FROM DataItems WHERE `dataItemID` = ?";
            PreparedStatement ps = c.prepareStatement(insertSQL);

            ps.setString(1, dataItemID);

            int rowsAffected = ps.executeUpdate() ;
            return ( rowsAffected > 0 );
        } catch (SQLException e) {
            throw new RuntimeException("Exception while using SQLite", e);
        }
    }

    @Override
    public boolean removeDataItems(String containerID)
            throws NoSuchContainerException {

        List<DataItemInfo> dataItemInfos = new ArrayList<>();

        try (Connection c = getConnection()) {

            if ( getContainer(containerID) == null ){
                throw new NoSuchContainerException("The specified container does not exist");
            }

            String removeSQL = "DELETE FROM DataItemsContainer WHERE `containerID` = ?" ;
            PreparedStatement ps = c.prepareStatement(removeSQL);

            ps.setString(1, containerID);

            int rowsAffected = ps.executeUpdate();

            return (rowsAffected > 0);
        } catch (SQLException e) {
            throw new RuntimeException("Exception while using SQLite", e);
        }
    }

    @Override
    public MicroNetworkInfo createMicroNetwork(
            String microNetworkID,
            String containerID,
            String path,
            MicroNetworkState state,
            String rpcURL,
            String p2pURL)
            throws MicroNetworkAlreadyExistsException {

        MicroNetworkInfo containerInfo = null;

        try (Connection c = getConnection()) {

            String insertSQL = "INSERT INTO MicroNetworks (`microNetworkID`, `containerID`, `path`, `state`, `rpcURL`, `p2pURL`) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = c.prepareStatement(insertSQL);

            ps.setString(1, microNetworkID);
            ps.setString(2, containerID);
            ps.setString(3, path);
            ps.setString(4, state.name());
            ps.setString(5, rpcURL);
            ps.setString(6, p2pURL);

            ps.executeUpdate();

            containerInfo = new MicroNetworkInfo(microNetworkID, containerID, path, state, rpcURL, p2pURL);
        } catch (SQLiteException e) {
            SQLiteErrorCode errorCode = e.getResultCode();
            if (errorCode == SQLiteErrorCode.SQLITE_CONSTRAINT_PRIMARYKEY) {
                throw new MicroNetworkAlreadyExistsException("A MicroNetwork already exists with the specified ID");
            }
            throw new RuntimeException("Exception while using SQLite", e);
        } catch (SQLException e) {
            throw new RuntimeException("Exception while using SQLite", e);
        }

        return containerInfo;
    }

    @Override
    public void updateMicroNetwork(MicroNetworkInfo updatedMicroNetwork)
            throws NoSuchMicroNetworkException {

        try (Connection c = getConnection()) {

            String microNetworkID = updatedMicroNetwork.getId();
            String containerID = updatedMicroNetwork.getContainerID();
            String path = updatedMicroNetwork.getPath();
            MicroNetworkState state = updatedMicroNetwork.getState();
            String rpcURL = updatedMicroNetwork.getRpcURL();
            String p2pURL = updatedMicroNetwork.getP2pURL();

            String insertSQL = "UPDATE MicroNetworks SET `containerID` = ?, `path` = ?, `state` = ?, `rpcURL` = ?, `p2pURL` = ? WHERE `microNetworkID` = ?;";
            PreparedStatement ps = c.prepareStatement(insertSQL);

            ps.setString(1, containerID);
            ps.setString(2, path);
            ps.setString(3, state.name());
            ps.setString(4, rpcURL);
            ps.setString(5, p2pURL);
            ps.setString(6, microNetworkID);

            int rowsAffected = ps.executeUpdate();

            if ( rowsAffected == 0 ) {
                throw new NoSuchMicroNetworkException("The specified micronetwork does not exist") ;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Exception while using SQLite", e);
        }
    }

    @Override
    public MicroNetworkInfo getMicroNetwork(String microNetworkID) {
        MicroNetworkInfo microNetworkInfo = null;

        try (Connection c = getConnection()) {
            String selectSQL = "SELECT `containerID`, `path`, `state`, `rpcURL`, `p2pURL` FROM MicroNetworks WHERE `microNetworkID` = ?;";
            PreparedStatement ps = c.prepareStatement(selectSQL);

            ps.setString(1, microNetworkID);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String containerID = rs.getString(1);
                String path = rs.getString(2);
                String stateStr = rs.getString(3);
                String rpcURL = rs.getString(4);
                String p2pURL = rs.getString(5);

                MicroNetworkState state = MicroNetworkState.valueOf(stateStr) ;

                microNetworkInfo = new MicroNetworkInfo(microNetworkID, containerID, path, state, rpcURL, p2pURL);
            }

            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Exception while using SQLite", e);
        }

        return microNetworkInfo;
    }

    @Override
    public boolean removeMicroNetwork(String microNetworkID) {

        ContainerInfo containerInfo = null;

        try (Connection c = getConnection()) {
            String deleteSQL = "DELETE FROM MicroNetworks WHERE `microNetworkID` = ?;";
            PreparedStatement ps = c.prepareStatement(deleteSQL);

            ps.setString(1, microNetworkID);

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Exception while using SQLite", e);
        }
    }

    @Override
    public BlockchainInfo createBlockchain(
            String blockchainID,
            String localPath)
            throws BlockchainAlreadyExistsException {
        return null;
    }

    @Override
    public void updateBlockchain(BlockchainInfo updatedMicroNetwork)
            throws NoSuchBlockchainException {

    }

    @Override
    public BlockchainInfo getBlockchain(String blockchainID) {
        return null;
    }

    @Override
    public boolean removeBlockchain(String blockchainID) {
        return false;
    }

    @Override
    public NodeConnectionInfo createNodeConnectionInfo(
            String containerID,
            String nodeID,
            String rpcURL,
            String p2pURL) {
        return null;
    }

    @Override
    public NodeConnectionInfo getNodeConnectionInfo(
            String containerID,
            String nodeID) {
        return null;
    }

    @Override
    public void updateNodeConnectionInfo(NodeConnectionInfo info)
            throws NoSuchNodeException {

    }

    @Override
    public boolean removeNodeConnectionInfo(
            String containerID,
            String nodeID) {
        return false;
    }

    // -------- Accessor Methods --------


    public void setDatabasePath(String databasePath) {
        this._databasePath = databasePath;
    }

    public void setDatabaseName(String databaseName) {
        this._databaseName = databaseName;
    }
}

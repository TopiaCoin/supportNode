package io.topiacoin.node.smsc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.topiacoin.eosrpcadapter.EOSRPCAdapter;
import io.topiacoin.eosrpcadapter.exceptions.ChainException;
import io.topiacoin.eosrpcadapter.exceptions.WalletException;
import io.topiacoin.eosrpcadapter.messages.Action;
import io.topiacoin.eosrpcadapter.messages.SignedTransaction;
import io.topiacoin.eosrpcadapter.messages.TableRows;
import io.topiacoin.eosrpcadapter.messages.Transaction;
import io.topiacoin.node.model.ChallengeSolution;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EOSSMSCManagerTest extends AbstractSMSCManagerTest {

    private EOSRPCAdapter _eosRPCAdapter;
    private String _contractAccount;
    private String _signingAccount;
    private String _stakingAccount;
    private String _containerAccount;
    private String _walletName;

    @BeforeClass
    public static void setUpClass() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    protected SMSCManager getSMSCManager() {
        try {
            URL nodeURL = new URL("http://127.0.0.1:8888/");
            URL walletURL = null;
            _contractAccount = "inita";
            _signingAccount = "inita";
            _stakingAccount = "initb";
            _containerAccount = "initb";

            String signingKey = "5KQwrPbwdL6PhXujxW37FSSQZ1JiwsST4cqQzDeyXtP79zkvFD3";
            String stakingKey = "5KQwrPbwdL6PhXujxW37FSSQZ1JiwsST4cqQzDeyXtP79zkvFD3";

            _eosRPCAdapter = new EOSRPCAdapter(nodeURL, walletURL);

            _walletName = "test-" + System.currentTimeMillis() / 1000;

            _eosRPCAdapter.wallet().create(_walletName);
            _eosRPCAdapter.wallet().importKey(_walletName, signingKey);
            _eosRPCAdapter.wallet().importKey(_walletName, stakingKey);

            EOSSMSCManager manager = new EOSSMSCManager();
            manager.setEosRPCAdapter(_eosRPCAdapter);
            manager.setSigningAccount(_signingAccount);
            manager.setStakingAccount(_stakingAccount);
            manager.setContractAccount(_contractAccount);
            manager.setWalletName(_walletName);
            manager.initialize();

            return manager;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (WalletException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected void transferFunds(String sourceAccount, float tokenAmount) throws WalletException, ChainException {

        List<Transaction.Authorization> authorizations = new ArrayList<>();
        authorizations.add(new Transaction.Authorization(sourceAccount, "active"));

        String amount = String.format("%.4f TPC", tokenAmount);

        Map<String, Object> args = new HashMap<>();
        args.put("from", sourceAccount);
        args.put("to", _contractAccount);
        args.put("quantity", amount);
        args.put("memo", "Container Payment " + System.currentTimeMillis());

        Action action = new Action("eosio.token", "transfer", authorizations, args);
        Date expirationDate = new Date(System.currentTimeMillis() + 60000);

        _eosRPCAdapter.pushTransaction(action, expirationDate, _walletName);

    }

    @Override
    protected String createContainer() throws WalletException, ChainException {

        // Transfer the required funds
        transferFunds(_containerAccount, 1.0f);

        // Create the container
        String containerID = Long.toUnsignedString(UUID.randomUUID().getLeastSignificantBits());

        System.err.println ( "------ " + containerID + " ------") ;

        List<Transaction.Authorization> authorizations = new ArrayList<>();
        authorizations.add(new Transaction.Authorization(_containerAccount, "active"));

        Map<String, Object> args = new HashMap<>();
        args.put("containerID", containerID);
        args.put("capacity", 1);
        args.put("nodeCount", 1);
        args.put("owningAccount", _containerAccount);

        Action action = new Action(_contractAccount, "create", authorizations, args);
        Date expirationDate = new Date(System.currentTimeMillis() + 60000);

        _eosRPCAdapter.pushTransaction(action, expirationDate, _walletName);

        return containerID;
    }

    @Override
    protected void terminateContainer(String containerID) throws WalletException, ChainException {
        List<Transaction.Authorization> authorizations = new ArrayList<>();
        authorizations.add(new Transaction.Authorization(_signingAccount, "active"));

        Map<String, Object> args = new HashMap<>();
        args.put("containerID", containerID);

        Action action = new Action(_contractAccount, "terminate", authorizations, args);
        Date expirationDate = new Date(System.currentTimeMillis() + 60000);

        _eosRPCAdapter.pushTransaction(action, expirationDate, _walletName);
    }

    @Override
    protected void submitProofSolutionHash(String containerID, String nodeID, String verificationValue, String transactionID, int blockNumber, String chunkHash) throws Exception {

        // Generate the Proof Solution Hash
        MessageDigest sha256 = MessageDigest.getInstance("sha256");
        ChallengeSolution solution = new ChallengeSolution(verificationValue, transactionID, blockNumber, chunkHash);
        ObjectMapper mapper = new ObjectMapper();
        sha256.update(mapper.writeValueAsBytes(solution));
        String hash = Hex.encodeHexString(sha256.digest());

        // Submit the Proof Solution Hash
        List<Transaction.Authorization> authorizations = new ArrayList<>();
        authorizations.add(new Transaction.Authorization(_containerAccount, "active"));

        Map<String, Object> args = new HashMap<>();
        args.put("containerID", containerID);
        args.put("nodeID", nodeID);
        args.put("solutionHash", hash);

        Action action = new Action(_contractAccount, "setproof", authorizations, args);
        Date expirationDate = new Date(System.currentTimeMillis() + 60000);

        _eosRPCAdapter.pushTransaction(action, expirationDate, _walletName);
    }

    @Override
    protected String fileDispute(String containerID, String nodeID, String chainURL, List<String> chunkIDs) throws Exception {
        String disputeID = null;

        // File the Dispute
        List<Transaction.Authorization> authorizations = new ArrayList<>();
        authorizations.add(new Transaction.Authorization(_containerAccount, "active"));

        Map<String, Object> args = new HashMap<>();
        args.put("containerID", containerID);
        args.put("nodeID", nodeID);
        args.put("chainURL", chainURL);
        args.put("disputedChunkIDs", chunkIDs);

        Action action = new Action(_contractAccount, "filedispute", authorizations, args);
        Date expirationDate = new Date(System.currentTimeMillis() + 60000);

        _eosRPCAdapter.pushTransaction(action, expirationDate, _walletName);

        // Retrieve the Dispute ID of the just filed dispute
        String table = "disputes";
        int limit = Integer.MAX_VALUE;
        TableRows tableRows = _eosRPCAdapter.chain().getTableRows(
                _contractAccount,
                _contractAccount,
                table,
                limit,
                true);

        for ( Map<String,Object> row : tableRows.rows) {
            if ( row.get("containerID").equals(containerID) &&
                    row.get("nodeID").equals(nodeID)) {
                disputeID = row.get("disputeID").toString() ;
                break;
            }
        }

        return disputeID;
    }





    @Test
    public void testTransactionChecksumFailure() {

    }
}

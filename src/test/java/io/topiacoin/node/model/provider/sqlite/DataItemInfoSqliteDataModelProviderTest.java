package io.topiacoin.node.model.provider.sqlite;

import io.topiacoin.node.model.DataModel;
import io.topiacoin.node.model.provider.AbstractDataItemInfoDataModelProviderTest;
import io.topiacoin.node.model.provider.SQLiteDataModelProvider;
import org.junit.After;

import java.io.File;

public class DataItemInfoSqliteDataModelProviderTest extends AbstractDataItemInfoDataModelProviderTest {

    private DataModel _dataModel;
    private SQLiteDataModelProvider _sqliteDataModelProvider;
    private String _databasePath = "target" ;
    private String _databaseName = "dataItemTest.sqlite";

    public DataModel getDataModel() {
        File dbFile = new File(_databasePath, _databaseName);
        dbFile.delete();

        _sqliteDataModelProvider = new SQLiteDataModelProvider();
        _sqliteDataModelProvider.setDatabasePath(_databasePath);
        _sqliteDataModelProvider.setDatabaseName(_databaseName);
        _sqliteDataModelProvider.initialize();

        _dataModel = new DataModel();
        _dataModel.setProvider(_sqliteDataModelProvider);
        _dataModel.initialize();

        return _dataModel;
    }

    @After
    public void tearDownDataModel() {
        if (_dataModel != null) {
            _dataModel.shutdown();
            _dataModel = null;
        }

        if (_sqliteDataModelProvider != null) {
            _sqliteDataModelProvider.shutdown();
            _sqliteDataModelProvider = null;
        }

        File dbFile = new File(_databasePath, _databaseName);
        dbFile.delete();
    }
}

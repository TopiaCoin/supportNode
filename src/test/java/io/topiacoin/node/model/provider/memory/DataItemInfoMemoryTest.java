package io.topiacoin.node.model.provider.memory;

import io.topiacoin.node.Configuration;
import io.topiacoin.node.core.DefaultConfiguration;
import io.topiacoin.node.model.DataModel;
import io.topiacoin.node.model.provider.AbstractDataModelContainerInfoTest;
import io.topiacoin.node.model.provider.AbstractDataModelDataItemInfoTest;
import io.topiacoin.node.model.provider.DataModelProvider;
import io.topiacoin.node.model.provider.MemoryDataModelProvider;
import org.junit.After;

public class DataItemInfoMemoryTest extends AbstractDataModelDataItemInfoTest {

    private DataModel _dataModel;
    private DataModelProvider _memoryDataModelProvider;

    public DataModel getDataModel() {
        _memoryDataModelProvider = new MemoryDataModelProvider();
        _memoryDataModelProvider.initialize();

        _dataModel = new DataModel();
        _dataModel.setProvider(_memoryDataModelProvider);
        _dataModel.initialize();

        return _dataModel;
    }

    @After
    public void tearDownDataModel() {
        if (_dataModel != null) {
            _dataModel.shutdown();
            _dataModel = null;
        }

        if (_memoryDataModelProvider != null) {
            _memoryDataModelProvider.shutdown();
            _memoryDataModelProvider = null;
        }
    }
}

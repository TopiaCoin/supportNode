package io.topiacoin.node.model.provider.memory;

import io.topiacoin.node.Configuration;
import io.topiacoin.node.core.DefaultConfiguration;
import io.topiacoin.node.model.DataModel;
import io.topiacoin.node.model.provider.ContainerInfoTest;

public class ContainerInfoMemoryTest extends ContainerInfoTest {

	@Override public DataModel initDataModel() {
		Configuration config = new DefaultConfiguration();
		config.setConfigurationOption("model.storage.type", "memory");
		DataModel.initialize(config);
		return DataModel.getInstance();
	}

	@Override public void tearDownDataModel() {
		DataModel.getInstance().close();
	}
}

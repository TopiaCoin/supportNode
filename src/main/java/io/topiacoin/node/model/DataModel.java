package io.topiacoin.node.model;

import io.topiacoin.node.model.provider.DataModelProvider;
import io.topiacoin.node.model.provider.MemoryDataModelProvider;

public class DataModel {

	private static DataModel __instance;

	private DataModelProvider _provider;

	protected DataModel(Configuration _config) {
		if(_config != null) {
			if (_config.getConfigurationOption("model.storage.type", "memory").equalsIgnoreCase("memory")) {
				_provider = new MemoryDataModelProvider();
			} else {
				_provider = new SQLiteDataModelProvider(_config);
			}
		} else {
			throw new NotInitializedException();
		}
	}

	public static synchronized DataModel getInstance() {
		if (__instance == null) {
			throw new NotInitializedException();
		}
		return __instance;
	}

	public static synchronized void initialize(Configuration config) {
		__instance = new DataModel(config);
	}

}

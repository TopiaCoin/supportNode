package io.topiacoin.node.proof;

import io.topiacoin.node.micronetwork.MicroNetworkManager;
import io.topiacoin.node.storage.provider.DataStorageProvider;
import io.topiacoin.node.storage.provider.MemoryDataStorageProvider;
import org.junit.After;

public class SDFSProofSolverTest extends AbstractProofSolverTest {

    private SDFSProofSolver _proofSolver;
    private MemoryDataStorageProvider _dataStorageProvider;
    private MicroNetworkManager _microNetworkManager;

    @After
    public void tearDown() throws Exception {
        if ( _proofSolver != null )
            _proofSolver.shutdown();
        _proofSolver = null;

        if ( _microNetworkManager != null )
            _microNetworkManager.shutdown();
        _microNetworkManager = null;

        if ( _dataStorageProvider != null )
            _dataStorageProvider.shutdown();
        _dataStorageProvider = null;
    }

    @Override
    protected ProofSolver getProofSolver() {
        if ( _proofSolver == null ) {
            _proofSolver = new SDFSProofSolver();
            _proofSolver.setDataStorageProvider(getDataStorageProvider());
            _proofSolver.setMicroNetworkManager(getMicroNetworkManager());

            _proofSolver.initialize();
        }

        return _proofSolver;
    }

    private MicroNetworkManager getMicroNetworkManager() {
        if ( _microNetworkManager == null ) {
            _microNetworkManager = new MicroNetworkManager();
            _microNetworkManager.initialize();
        }
        return _microNetworkManager;
    }

    @Override
    protected DataStorageProvider getDataStorageProvider() {
        if ( _dataStorageProvider == null ) {
            _dataStorageProvider = new MemoryDataStorageProvider();
            _dataStorageProvider.initialize();
        }
        return _dataStorageProvider;
    }
}

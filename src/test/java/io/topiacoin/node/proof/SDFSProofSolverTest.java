package io.topiacoin.node.proof;

import io.topiacoin.node.storage.provider.DataStorageProvider;
import io.topiacoin.node.storage.provider.MemoryDataStorageProvider;
import org.junit.After;

public class SDFSProofSolverTest extends AbstractProofSolverTest {

    private SDFSProofSolver _proofSolver;
    private MemoryDataStorageProvider _dataStorageProvider;

    @After
    public void tearDown() throws Exception {
        if ( _proofSolver != null )
            _proofSolver.shutdown();
        _proofSolver = null;
        _dataStorageProvider = null;
    }

    @Override
    protected ProofSolver getProofSolver() {
        if ( _proofSolver == null ) {
            _proofSolver = new SDFSProofSolver();
            _proofSolver._dataStorageProvider = getDataStorageProvider();

            _proofSolver.initialize();
        }

        return _proofSolver;
    }

    @Override
    protected DataStorageProvider getDataStorageProvider() {
        if ( _dataStorageProvider == null ) {
            _dataStorageProvider = new MemoryDataStorageProvider();
        }
        return _dataStorageProvider;
    }
}

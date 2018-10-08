package io.topiacoin.node.proof;

import io.topiacoin.node.model.Challenge;
import io.topiacoin.node.model.ChallengeSolution;

public interface ProofSolver {

    void initialize();

    void shutdown();

    ChallengeSolution generateSolution(Challenge proofChallenge) ;
}

package io.topiacoin.node.proof;

import io.topiacoin.node.model.Challenge;
import io.topiacoin.node.model.ChallengeChunkInfo;
import io.topiacoin.node.model.ChallengeSolution;
import io.topiacoin.node.storage.provider.DataStorageProvider;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;

public abstract class AbstractProofSolverTest {

    protected abstract ProofSolver getProofSolver();

    protected abstract DataStorageProvider getDataStorageProvider();

    @Test
    public void testSanity() throws Exception {
        fail("This test is not sane");
    }

    @Test
    public void testGenerateSolution() throws Exception {

        ProofSolver proofSolver = getProofSolver();
        DataStorageProvider dataStorageProvider = getDataStorageProvider();

        // Put test data into the data storage provider
        String inputData = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Quisque nec dapibus justo in est nec sem rhoncus hendrerit non vel orci. " +
                "Vivamus mattis ex dolor, vitae sodales dolor pretium vitae. " +
                "Ut blandit leo ac massa dapibus, fringilla euismod est volutpat. " +
                "Suspendisse potenti nunc ex est, sodales at felis ac, blandit cursus justo. " +
                "Vestibulum scelerisque enim justo, sit amet tristique neque semper vel. " +
                "Curabitur sagittis lobortis eros ac pharetra. " +
                "Nam sit amet justo varius, tincidunt arcu at, porta purus. " +
                "Phasellus in mi purus nunc rutrum ultricies blandit. " +
                "Morbi varius, quam in faucibus interdum, risus ante porttitor nibh, sit amet porttitor tortor magna et metus. " +
                "Fusce fringilla in ligula non condimentum. " +
                "Curabitur id augue lectus magna et metus. " +
                "Mauris id felis vel mi consequat porta et faucibus orci." +
                "Duis vulputate tincidunt libero ac consequat. " +
                "Interdum et malesuada fames ac ante ipsum primis in faucibus. " +
                "Nam quis nunc ut ex commodo volutpat. " +
                "Quisque auctor risus quis quam consequat, eu vehicula mi varius. " +
                "Ut non risus non sapien pharetra euismod vitae ac lorem. " +
                "Mauris aliquet nec magna congue vestibulum. " +
                "Curabitur fermentum elit vitae lacus auctor rhoncus. " +
                "Quisque vitae efficitur justo aenean erat massa, sollicitudin id facilisis non, elementum id nisi. " +
                "Phasellus nec cursus arcu, sit amet volutpat quam. " +
                "Praesent hendrerit libero eget felis tristique volutpat. " +
                "Vivamus sit amet pretium dolor nam sit amet aliquam lorem, at eleifend ante. " +
                "Proin in lacus lacinia, auctor sapien eu, tempus arcu. " +
                "Suspendisse lacinia nec metus at feugiat nunc et erat eleifend, convallis lacus et, porttitor magna. " +
                "Fusce consequat massa ut odio scelerisque varius quisque lacinia eget velit nec commodo. " +
                "Etiam ex erat, posuere et purus at, vehicula rhoncus mauris. " +
                "Mauris rutrum sagittis dui, et fermentum nunc rutrum ac. " +
                "Cras tellus nisi, pulvinar bibendum pellentesque sed, ullamcorper sit amet orci phasellus eleifend cursus dui ut condimentum. " +
                "Nam faucibus vestibulum pharetra phasellus quis sem et libero mattis rhoncus sit amet a dui.";

        String containerID = "0xdeadbeef";

        String[] dataStrings = inputData.split("\\.") ;
        byte[][] data = new byte[dataStrings.length][];
        String[] chunkIDs = new String[dataStrings.length] ;
        for ( int i = 0 ; i < dataStrings.length ; i++ ) {
            chunkIDs[i] = String.format("%08d", i) ;
            data[i] = dataStrings[i].getBytes();
        }

        for ( int i = 0 ; i < data.length ; i++) {
            dataStorageProvider.saveData(chunkIDs[i], new ByteArrayInputStream(data[i]));
        }

        List<ChallengeChunkInfo> infoList = new ArrayList<>();
        for ( int i = 0 ; i < data.length ; i++ ) {
            ChallengeChunkInfo info = new ChallengeChunkInfo(chunkIDs[i], i, 20);
            infoList.add(info) ;
        }

        Challenge challenge = new Challenge(containerID, infoList);

        ChallengeSolution solution = proofSolver.generateSolution(challenge);

        assertNotNull ( solution ) ;

        String expectedChunkHash = "337bfd809c9600236bd25786be3b3189e0f06021adcff81716ded83508f9ade4";
        assertEquals ( expectedChunkHash, solution.getChunkHash()) ;
    }
}

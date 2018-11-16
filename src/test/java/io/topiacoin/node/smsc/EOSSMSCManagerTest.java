package io.topiacoin.node.smsc;

import io.topiacoin.eosrpcadapter.EOSRPCAdapter;
import io.topiacoin.eosrpcadapter.exceptions.WalletException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.BeforeClass;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.Security;

public class EOSSMSCManagerTest extends AbstractSMSCManagerTest {

    @BeforeClass
    public static void setUpClass() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    protected SMSCManager getSMSCManager() {
        try {
            URL nodeURL = new URL("http://127.0.0.1:8888/");
            URL walletURL = null;
            String _contractAccount = "inita";
            String _signingAccount = "inita";
            String _stakingAccount = "initb";

            String signingKey = "5KQwrPbwdL6PhXujxW37FSSQZ1JiwsST4cqQzDeyXtP79zkvFD3";
            String stakingKey = "5KQwrPbwdL6PhXujxW37FSSQZ1JiwsST4cqQzDeyXtP79zkvFD3";

            EOSRPCAdapter eosRPCAdapter = new EOSRPCAdapter(nodeURL, walletURL);

            String walletName = "test-" + System.currentTimeMillis()/1000;

            eosRPCAdapter.wallet().create(walletName);
            eosRPCAdapter.wallet().importKey(walletName, signingKey);
            eosRPCAdapter.wallet().importKey(walletName, stakingKey);

            EOSSMSCManager manager = new EOSSMSCManager();
            manager.setEosRPCAdapter(eosRPCAdapter);
            manager.setSigningAccount(_signingAccount);
            manager.setStakingAccount(_stakingAccount);
            manager.setContractAccount(_contractAccount);
            manager.setWalletName(walletName);
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
}

package io.topiacoin.node.smsc;

import io.topiacoin.eosrpcadapter.EOSRPCAdapter;
import io.topiacoin.eosrpcadapter.exceptions.ChainException;
import io.topiacoin.eosrpcadapter.exceptions.WalletException;
import io.topiacoin.eosrpcadapter.messages.Action;
import io.topiacoin.eosrpcadapter.messages.SignedTransaction;
import io.topiacoin.eosrpcadapter.messages.Transaction;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.BeforeClass;

import java.net.MalformedURLException;
import java.net.URL;
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

            _walletName = "test-" + System.currentTimeMillis()/1000;

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

        String amount = String.format( "%.4f TPC", tokenAmount );

        Map<String, Object> args = new HashMap<>();
        args.put("from", sourceAccount);
        args.put("to", _contractAccount);
        args.put("quantity", amount);
        args.put("memo", "ContainerPayment");

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
}

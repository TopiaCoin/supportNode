package io.topiacoin.node.smsc;

public class EOSSMSCManagerTest extends AbstractSMSCManagerTest {
    @Override
    protected SMSCManager getSMSCManager() {
        return new EOSSMSCManager();
    }
}

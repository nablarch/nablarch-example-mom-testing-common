package com.nablarch.example.sample.mom;

import static org.junit.Assert.assertNotNull;

import org.apache.activemq.broker.BrokerService;

import nablarch.core.repository.SystemRepository;
import nablarch.fw.messaging.MessagingContext;
import nablarch.test.RepositoryInitializer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * {@link EmbeddedMessagingProviderTest}のテストクラス。
 */
public class EmbeddedMessagingProviderTest {

    @Before
    public void setUp() {
        RepositoryInitializer.reInitializeRepository("EmbeddedMessagingProvider.xml");
    }

    @After
    public void tearDown() throws Exception {
        RepositoryInitializer.revertDefaultRepository();
    }

    /**
     * プロバイダの初期化が可能であることを確認する。
     */
    @Test
    public void testInitialize() {
        EmbeddedMessagingProvider embeddedMessagingProvider = null;
        try {
            embeddedMessagingProvider = SystemRepository.get("messagingProvider");
            embeddedMessagingProvider.initialize();

            //プロバイダ初期化の成功確認のために、コンテキストを取得してみる。
            MessagingContext context = embeddedMessagingProvider.createContext();
            assertNotNull(context);
        } finally {
            if (embeddedMessagingProvider != null) {
                //後続のテストのためにサーバを停止する。
                embeddedMessagingProvider.stopServer();
            }
        }
    }

    /**
     * 既に使用済みのポートを使用しようとした場合、実行時例外が送出されることを確認する。
     *
     * @throws Exception
     */
    @Test(expected = RuntimeException.class)
    public void testInitializeUsedPort() throws Exception {
        EmbeddedMessagingProvider embeddedMessagingProvider = null;
        BrokerService broker = new BrokerService();
        try {
            broker.setPersistent(false);
            broker.setUseJmx(false);
            broker.addConnector("tcp://localhost:61616");
            embeddedMessagingProvider = SystemRepository.get("messagingProvider");
            embeddedMessagingProvider.initialize();
        } finally {
            broker.stop();
            broker.waitUntilStopped();
            if (embeddedMessagingProvider != null) {
                //後続のテストのためにサーバを停止する。
                embeddedMessagingProvider.stopServer();
            }
        }
    }
}

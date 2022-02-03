package com.nablarch.example.sample.mom;

import org.apache.activemq.broker.BrokerService;

import nablarch.core.repository.SystemRepository;
import nablarch.fw.messaging.MessagingContext;
import nablarch.test.RepositoryInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * {@link EmbeddedMessagingProviderTest}のテストクラス。
 */
class EmbeddedMessagingProviderTest {
    EmbeddedMessagingProvider embeddedMessagingProvider;

    @BeforeEach
    void setUp() {
        RepositoryInitializer.reInitializeRepository("EmbeddedMessagingProvider.xml");
        embeddedMessagingProvider = SystemRepository.get("messagingProvider");
    }

    @AfterEach
    void tearDown() throws Exception {
        if (embeddedMessagingProvider != null) {
            //後続のテストのためにサーバを停止する。
            embeddedMessagingProvider.stopServer();
        }

        RepositoryInitializer.revertDefaultRepository();
    }

    /**
     * プロバイダの初期化が可能であることを確認する。
     */
    @Test
    void testInitialize() {
        embeddedMessagingProvider.initialize();

        //プロバイダ初期化の成功確認のために、コンテキストを取得してみる。
        MessagingContext context = embeddedMessagingProvider.createContext();
        assertNotNull(context);
    }

    /**
     * 既に使用済みのポートを使用しようとした場合、実行時例外が送出されることを確認する。
     */
    @Test
    void testInitializeUsedPort() throws Exception {
        BrokerService broker = new BrokerService();
        try {
            broker.setPersistent(false);
            broker.setUseJmx(false);
            broker.addConnector("tcp://localhost:61616");

            assertThrows(RuntimeException.class, () -> embeddedMessagingProvider.initialize());
        } finally {
            broker.stop();
            broker.waitUntilStopped();
        }
    }
}

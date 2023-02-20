package com.nablarch.example.sample.mom;

import nablarch.core.repository.SystemRepository;
import nablarch.fw.messaging.MessagingContext;
import nablarch.test.RepositoryInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;


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
}

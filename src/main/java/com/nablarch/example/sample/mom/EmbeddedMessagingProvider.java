package com.nablarch.example.sample.mom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Queue;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;

import nablarch.core.repository.initialization.Initializable;
import nablarch.fw.messaging.provider.JmsMessagingProvider;

/**
 * ActiveMQを組み込みモードで起動して使用するMessagingProvider。
 * <p>
 * メッセージングの受信側に組み込むことを想定している。<br />
 * 送信側は、本MessagingProviderで起動したActiveMQに接続する。
 * </p>
 * @author Nabu Rakutaro
 */
@SuppressWarnings({"ProhibitedExceptionThrown", "unpublishedApi"})
public class EmbeddedMessagingProvider extends JmsMessagingProvider implements Initializable {

    /** キューマネージャ */
    private BrokerService broker = new BrokerService();

    /** キューのリスト(親クラスから簡単に取り出す方法がないため、本クラスでも保持する) */
    private List<ActiveMQQueue> queueList = new ArrayList<>();

    /** アプリケーションが接続するためのURI */
    private String url = "tcp://localhost:61616";

    /**
     * 初期化処理を行う。
     *
     * 組み込みActiveMQの起動と、ConnectionFactoryの設定を行う。
     * 本処理で使用するパラメータは、コンポーネント定義ファイルで設定する前提である。
     */
    @Override
    public void initialize() {
        try {
            broker.setPersistent(false);
            broker.setUseJmx(false);
            broker.addConnector(url);
        } catch (Exception e) {
            // BrokerService#addConnectorがExceptionを送出する可能性があるため、Exceptionを指定してcatchしている
            throw new RuntimeException("an Error occurred while launch the messaging broker", e);
        }
        startServer();

        broker.setDestinations(queueList.toArray(new ActiveMQQueue[queueList.size()]));
        setConnectionFactory(new ActiveMQConnectionFactory(url));
    }

    /**
     * 内蔵サーバを開始する。
     */
    protected void startServer() {
        try {
            broker.start();
            broker.waitUntilStarted();
        } catch (Exception e) {
            // BrokerService#startがExceptionを送出する可能性があるため、Exceptionを指定してcatchしている
            stopServer();
            throw new RuntimeException(e);
        }
    }

    /**
     * 内蔵サーバを停止する。
     */
    protected void stopServer() {
        try {
            broker.stop();
            broker.waitUntilStopped();
            broker = null;
        } catch (Exception e) {
            //BrokerService#stopがExceptionを送出する可能性があるため、Exceptionを指定してcatchしている
            throw new RuntimeException(e);
        }
    }

    /**
     * このキューマネージャが管理するキューの論理名を設定する。
     *
     * @param names キュー名の一覧
     */
    public void setQueueNames(List<String> names) {
        Map<String, Queue> queueMap = new HashMap<>();
        names.forEach(name -> {
            ActiveMQQueue activeMQQueue = new ActiveMQQueue(name);
            queueList.add(activeMQQueue);
            queueMap.put(name, activeMQQueue);
        });
        setDestinations(queueMap);
    }

    /**
     * アプリケーションが接続に使用するURIを指定する。
     *
     * @param url アプリケーションが接続に使用するURI
     */
    public void setUrl(String url) {
        this.url = url;
    }
}

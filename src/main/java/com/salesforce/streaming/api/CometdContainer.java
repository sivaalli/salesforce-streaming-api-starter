package com.salesforce.streaming.api;

import com.google.common.base.Preconditions;
import org.cometd.bayeux.Channel;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.ClientTransport;
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import javax.annotation.concurrent.GuardedBy;
import java.util.EnumSet;
import java.util.Set;

/**
 * Bootstraps {@linkplain BayeuxClient}. Also provides helper methods to subscribe/unsubscribe to channels.
 */
public class CometdContainer implements SmartLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(CometdContainer.class);
    private static final Set<BayeuxClient.State> VALID_STATES = EnumSet.of(BayeuxClient.State.CONNECTING, BayeuxClient.State.CONNECTED);

    private final StreamingApiProperties config;
    private final Object lifeCycleMonitor = new Object();
    private final ClientSessionChannel.MessageListener listener;
    private final ClientTransportCallback authCallback;
    private final HttpClient jettyClient;
    private final MessageListenerRegistry registry;

    @GuardedBy("lifeCycleMonitor")
    private boolean isRunning;
    private BayeuxClient cometd;

    public CometdContainer(ClientTransportCallback authCallback,
                           StreamingApiProperties config,
                           ClientSessionChannel.MessageListener listener,
                           HttpClient jettyClient,
                           MessageListenerRegistry registry) {
        this.jettyClient = jettyClient;
        this.authCallback = authCallback;
        this.config = config;
        this.listener = listener;
        this.registry = registry;
    }

    private static void configureMetaChannelListeners(BayeuxClient client) {
//        client.getChannel(Channel.META_CONNECT).addListener((ClientSessionChannel.MessageListener) (channel, message) -> {
//        });
        // todo add listeners to all meta channels for debugging. on debug log level.
    }

    @Override
    public void start() {
        logger.debug("Starting {}", getClass().getSimpleName());
        synchronized (lifeCycleMonitor) {
            try {
                doStart();
            } catch (InterruptedException e) {
                throw new CometdBootstrapException(e);
            }
        }
    }

    @Override
    public void stop() {
        logger.debug("Stopping {}", getClass().getSimpleName());
        synchronized (lifeCycleMonitor) {
            doStop();
        }
    }

    private void doStop() {
        if (!isRunning) {
            return;
        }

        cometd.disconnect();
        cometd.waitFor(config.getShutdownTimeoutMs(), BayeuxClient.State.DISCONNECTED);
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    private void doStart() throws InterruptedException {
        if (isRunning) {
            return;
        }

        final ClientTransport clientTransport = new LongPollingTransport(null, jettyClient) {
            @Override
            protected void customize(Request request) {
                authCallback.callback(request);
            }
        };

        cometd = new BayeuxClient(config.getBaseUri(), clientTransport);
        isRunning = true;

        configureMetaChannelListeners(cometd);
        final BayeuxClient.State result = cometd.handshake(config.getHandshakeTimeoutMs());
        if (!VALID_STATES.contains(result)) {
            throw new CometdBootstrapException("Handshake failed. Expected states are " + VALID_STATES + " but the result state is " + result);
        }
        logger.info("Handshake successful !!!");
        subscribe(registry.allRegisteredChannels());
    }

    private void subscribe(Set<String> channels) throws InterruptedException {
        Preconditions.checkArgument(channels != null, "Channels supplied is null.");
        Preconditions.checkArgument(!channels.isEmpty(), "Channels supplied is empty. Non empty collection is expected.");

        for (String channel : channels) {
            logger.info("Subscribing to channel [{}]", channel);
            if (channel.startsWith(Channel.META)) {
                cometd.getChannel(channel).addListener(listener);
            } else {
                cometd.getChannel(channel).subscribe(listener,
                        message -> {
                            if (message.isSuccessful()) {
                                logger.info("Subscription to [{}] is successful", channel);
                            } else {
                                logger.error("Subscription to [{}] is failed", channel);
                            }
                        });
            }
        }
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

}

package com.salesforce.streaming.api;

import com.salesforce.streaming.api.auth.AuthenticationManager;
import com.salesforce.streaming.api.auth.Authenticator;
import com.salesforce.streaming.api.auth.SalesforceAuthenticator;
import com.salesforce.streaming.api.listeners.SimpleListener;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.client.BayeuxClient;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnClass(BayeuxClient.class)
@Configuration(proxyBeanMethods = false) // disable cglib proxying
@EnableConfigurationProperties(StreamingApiProperties.class)
public class SalesforceStreamingApiConfiguration {

    @Bean
    public CometdContainer bootstrap(HttpClient client,
                                     ClientTransportCallback authCallback,
                                     StreamingApiProperties properties,
                                     ClientSessionChannel.MessageListener listener,
                                     MessageListenerRegistry messageListenerRegistry) {
        return new CometdContainer(authCallback, properties, listener, client, messageListenerRegistry);
    }

    @Bean
    public ClientSessionChannel.MessageListener listener(MessageListenerRegistry registry) {
        return new SimpleListener(registry);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public HttpClient client() {
        return new HttpClient(new SslContextFactory.Client());
    }

    @Bean
    public ClientTransportCallback authCallback(AuthenticationManager manager) {
        return new SalesforceStreamingTransportCallback(manager);
    }

    @Bean
    public AuthenticationManager manager(Authenticator authenticator) {
        return new AuthenticationManager(authenticator);
    }

    @Bean
    public Authenticator authenticator(StreamingApiProperties configProperties) {
        return new SalesforceAuthenticator(configProperties.getCreds());
    }

    @Bean
    public MessageListenerRegistry messageListenerRegistry() {
        return new MessageListenerRegistry();
    }
}

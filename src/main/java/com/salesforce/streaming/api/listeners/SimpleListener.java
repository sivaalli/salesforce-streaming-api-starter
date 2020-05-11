package com.salesforce.streaming.api.listeners;

import com.salesforce.streaming.api.MessageListenerRegistry;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SimpleListener implements ClientSessionChannel.MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(SimpleListener.class);

    private final MessageListenerRegistry registry;

    public SimpleListener(MessageListenerRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void onMessage(ClientSessionChannel sessionChannel, Message message) {
        final String channel = message.getChannel();
        final MessageListenerRegistry.ObjectAndMethodHolder registeredMethod = registry.getRegisteredMethod(channel);
        if (registeredMethod == null) {
            return;
        }
        final Method method = registeredMethod.getMethod();
        final Object object = registeredMethod.getObject();

        try {
            logger.debug("Invoking method [{}] on bean [{}]", method.getName(), object.getClass());
            method.invoke(object, message);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

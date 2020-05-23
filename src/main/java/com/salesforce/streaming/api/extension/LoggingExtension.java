package com.salesforce.streaming.api.extension;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingExtension implements ClientSession.Extension {

    private static final Logger logger = LoggerFactory.getLogger(LoggingExtension.class);

    @Override
    public boolean rcv(ClientSession session, Message.Mutable message) {
        logger.info("rcv() called {}", message);
        return true;
    }

    @Override
    public boolean rcvMeta(ClientSession session, Message.Mutable message) {
        logger.info("rcvMeta() called {}", message);
        return true;
    }

    @Override
    public boolean send(ClientSession session, Message.Mutable message) {
        logger.info("send() called {}", message);
        return true;
    }

    @Override
    public boolean sendMeta(ClientSession session, Message.Mutable message) {
        logger.info("sendMeta() called {}", message);
        return true;
    }
}
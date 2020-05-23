package com.salesforce.streaming.api.main;

import com.salesforce.streaming.api.annotation.ChannelListener;
import org.cometd.bayeux.Channel;
import org.cometd.bayeux.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MyListener {

    private static final Logger logger = LoggerFactory.getLogger(MyListener.class);

    @ChannelListener(channel = "/topic/InvoiceStatementUpdates")
    public void listen(Message message) {
    }

    @ChannelListener(channel = "/topic/InvoiceStatementUpdates1")
    public void listen2(Message message) {
    }


}

# salesforce-streaming-api-starter
Pretty much under development. But after completed a interested listener can subscribe by just adding this code:

```java
@Component
public class MyListener {

    private static final Logger logger = LoggerFactory.getLogger(MyListener.class);

    @ChannelListener(channel = "/topic/InvoiceStatementUpdates")
    public void listen(Message message) {
        logger.info("Message is " + message);
    }

    @ChannelListener(channel = Channel.META_HANDSHAKE)
    public void metaHandshake(Message message) {
        logger.info("Handshake Message is " + message);
    }

    @ChannelListener(channel = "/topic/InvoiceStatementUpdates1")
    public void listen2(Message message) {
        logger.info("Message2 is " + message);
    }


}

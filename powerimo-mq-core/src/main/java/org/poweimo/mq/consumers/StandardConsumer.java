package org.poweimo.mq.consumers;

import com.rabbitmq.client.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.poweimo.mq.Message;
import org.poweimo.mq.config.RabbitConfig;
import org.poweimo.mq.converters.MessageConverter;
import org.poweimo.mq.enums.RouteResolution;
import org.poweimo.mq.routers.MessageRouter;

import java.io.IOException;

/**
 * <p>StandardConsumer class.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
@Slf4j
@Getter
public class StandardConsumer implements Consumer, ChannelSupport {
    @Setter
    private Channel channel;
    private final MessageRouter _router;
    private final MessageConverter _messageConverter;

    /**
     * <p>Constructor for StandardConsumer.</p>
     *
     * @param config a {@link org.poweimo.mq.config.RabbitConfig} object
     */
    public StandardConsumer(RabbitConfig config) {
        _router = config.getMessageRouter();
        _messageConverter = config.getMessageConverter();
    }

    /** {@inheritDoc} */
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
        Message message;
        try {
            message = _messageConverter.decode(consumerTag,  envelope, basicProperties, bytes);
        } catch (Exception ex) {
            channel.basicReject(envelope.getDeliveryTag(), false);
            log.error("[MQ] Exception on parsing message. Message was rejected. consumerTag=({}), Envelope=({}), basicProperties=({}), bytes[]=({})", consumerTag, envelope, basicProperties, bytes, ex);
            return;
        }

        try {
            RouteResolution routeResolution = _router.route(message);
            if (routeResolution == RouteResolution.ACKNOWLEDGE) {
                channel.basicAck(envelope.getDeliveryTag(), false);
            } else if (routeResolution == RouteResolution.DLQ) {
                channel.basicReject(envelope.getDeliveryTag(), false);
            } else if (routeResolution == RouteResolution.REQUEUE) {
                channel.basicNack(envelope.getDeliveryTag(), false, true);
            } else  {
                log.error("[MQ] Route resolution unknown. Message was rejected.");
                channel.basicReject(envelope.getDeliveryTag(), false);
            }

        } catch (Exception ex1) {
            log.error("Exception on routing message. Message will be rejected and pushed to DLQ. Message={}", message, ex1);
            channel.basicReject(envelope.getDeliveryTag(), false);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void handleConsumeOk(String s) {

    }

    /** {@inheritDoc} */
    @Override
    public void handleCancelOk(String s) {

    }

    /** {@inheritDoc} */
    @Override
    public void handleCancel(String s) throws IOException {

    }

    /** {@inheritDoc} */
    @Override
    public void handleShutdownSignal(String s, ShutdownSignalException e) {

    }

    /** {@inheritDoc} */
    @Override
    public void handleRecoverOk(String s) {

    }
}

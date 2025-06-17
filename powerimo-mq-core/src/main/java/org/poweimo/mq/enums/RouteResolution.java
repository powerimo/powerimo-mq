package org.poweimo.mq.enums;

public enum RouteResolution {

    /**
     * Successfully handled message result
     */
    ACKNOWLEDGE,

    /**
     * "Please Send message again"
     */
    REQUEUE,

    /**
     * Error happened during handle of the message. Rejects the message for routing it to DLQ
     */
    DLQ
}

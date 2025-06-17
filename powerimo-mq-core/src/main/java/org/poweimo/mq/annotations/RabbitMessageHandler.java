package org.poweimo.mq.annotations;

import java.lang.annotation.*;

/**
 * <p>RabbitMessageHandler class.</p>
 *
 * @author andev
 * @version $Id: $Id
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RabbitMessageHandler {
    String routingKey() default "";
    String[] routingKeys() default {};
}

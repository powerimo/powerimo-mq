package org.poweimo.mq.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RabbitMessageHandler {
    String routingKey() default "";
}

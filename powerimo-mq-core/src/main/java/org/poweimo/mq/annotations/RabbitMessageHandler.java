package org.poweimo.mq.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RabbitMessageHandler {
    String routingKey() default "";
    String[] routingKeys() default {};
}

package com.salesforce.streaming.api.annotation;

import java.lang.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ChannelListener {

    String channel() default "";
}

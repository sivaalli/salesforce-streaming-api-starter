package com.salesforce.streaming.api;

import com.google.common.collect.Maps;
import com.salesforce.streaming.api.annotation.ChannelListener;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MessageListenerRegistry implements BeanPostProcessor {

    private Map<String, ObjectAndMethodHolder> listeners = new HashMap<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> targetClass = AopUtils.getTargetClass(bean);
        final Map<Method, ChannelListener> methods = MethodIntrospector.selectMethods(targetClass,
                (MethodIntrospector.MetadataLookup<ChannelListener>) method ->
                        AnnotationUtils.findAnnotation(method, ChannelListener.class));

        final Map<String, ObjectAndMethodHolder> beanListeners = methods.entrySet().stream()
                .flatMap(entry -> {
                    final ChannelListener annotation = entry.getValue();
                    final Method method = entry.getKey();
                    return Stream.of(Maps.immutableEntry(annotation.channel(), new ObjectAndMethodHolder(bean, method)));
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        listeners.putAll(beanListeners);
        return bean;
    }

    public ObjectAndMethodHolder getRegisteredMethod(String channel) {
        return listeners.get(channel);
    }

    public Set<String> allRegisteredChannels(){
        return new HashSet<>(listeners.keySet());
    }

    public static class ObjectAndMethodHolder {
        private final Object object;
        private final Method method;

        public ObjectAndMethodHolder(Object object, Method method) {
            this.object = object;
            this.method = method;
        }

        public Object getObject() {
            return object;
        }

        public Method getMethod() {
            return method;
        }
    }
}

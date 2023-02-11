package com.extension.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanPostProcessor {

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        if (BeanLifeCycle.BEAN_NAME.equals(beanName)) {
            log.info("【Bean生命周期】1-1 调用InstantiationAwareBeanPostProcessor.postProcessBeforeInstantiation()");
        }
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) {
        if (BeanLifeCycle.BEAN_NAME.equals(beanName)) {
            log.info("【Bean生命周期】2-3 调用InstantiationAwareBeanPostProcessor.postProcessAfterInstantiation()");
        }
        return true;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
        if (BeanLifeCycle.BEAN_NAME.equals(beanName)) {
            log.info("【Bean生命周期】2-4 调用InstantiationAwareBeanPostProcessor.postProcessProperties()");
        }
        return null;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (BeanLifeCycle.BEAN_NAME.equals(beanName)) {
            log.info("【Bean生命周期】3-8.调用BeanPostProcessor.postProcessBeforeInitialization()");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (BeanLifeCycle.BEAN_NAME.equals(beanName)) {
            log.info("【Bean生命周期】3-12.调用BeanPostProcessor.postProcessAfterInitialization()");
        }
        return bean;
    }
}

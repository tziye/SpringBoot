package com.mongodb.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MetricsPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RepositoryFactoryBeanSupport) {
            RepositoryFactoryBeanSupport repositoryFactoryBean = (RepositoryFactoryBeanSupport) bean;
            repositoryFactoryBean.addRepositoryFactoryCustomizer(repositoryFactory ->
                    repositoryFactory.addInvocationListener(invocation -> log.info("Metrics: {}", invocation)));
        }
        return bean;
    }
}

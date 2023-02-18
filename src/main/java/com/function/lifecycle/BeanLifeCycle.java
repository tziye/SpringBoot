package com.function.lifecycle;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Bean生命周期
 */
@Slf4j
@Data
@NoArgsConstructor
public class BeanLifeCycle implements InitializingBean, BeanNameAware, DisposableBean, ApplicationContextAware {

    static String BEAN_NAME = "beanLifeCycle";

    int id;
    String value;

    @Autowired
    public BeanLifeCycle(int id, String value) {
        this.id = id;
        this.value = value;
        log.info("【Bean生命周期】1-2.调用构造函数");
    }

    @Autowired
    public void setId(int id) {
        this.id = id;
        log.info("【Bean生命周期】2-5.属性注入（id）");
    }

    @Autowired
    public void setValue(String value) {
        this.value = value;
        log.info("【Bean生命周期】2-5.属性注入（value）");
    }

    @Override
    public void setBeanName(String name) {
        log.info("【Bean生命周期】2-6.调用BeanNameAware.setBeanName()");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        log.info("【Bean生命周期】3-7.调用ApplicationContext.setApplicationContext()");
    }

    @PostConstruct
    public void postConstruct() {
        log.info("【Bean生命周期】3-9.调用@PostConstruct");
    }

    @Override
    public void afterPropertiesSet() {
        log.info("【Bean生命周期】3-10.调用InitializingBean.afterPropertiesSet()");
    }

    public void initMethod() {
        log.info("【Bean生命周期】3-11.调用init-method");
    }

    @PreDestroy
    public void preDestroy() {
        log.info("【Bean生命周期】4-13.调用@PreDestroy");
    }

    @Override
    public void destroy() {
        log.info("【Bean生命周期】4-14.调用DisposableBean.destroy()");
    }

    public void destroyMethod() {
        log.info("【Bean生命周期】4-15.调用destroy-method");
    }

}

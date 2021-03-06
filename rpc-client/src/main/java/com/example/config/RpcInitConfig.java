package com.example.config;

import com.example.annotation.RpcClient;
import com.example.factory.RpcClinetFactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Map;
import java.util.Set;

/**
 * @author xuyongjia
 * @date 2020/7/25
 */
public class RpcInitConfig implements ImportBeanDefinitionRegistrar{
    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        // 获得一个扫描器
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        // 设置RpcClient注解扫描器
        scanner.addIncludeFilter(new AnnotationTypeFilter(RpcClient.class));


        // 扫描指定包下的所有带@RpcClient的类，并遍历
        Set<BeanDefinition> beanDefinitions =  scanner.findCandidateComponents("com.example.client");
        beanDefinitions.forEach(beanDefinition -> {
            // 如果是注解类
            if (beanDefinition instanceof AnnotatedBeanDefinition){
                // 获得被注解的类的信息
                AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) beanDefinition;
                // 获得beanClassName
                String beanClassName = annotatedBeanDefinition.getBeanClassName();
                Map<String, Object> paraMap = annotatedBeanDefinition.getMetadata()
                        .getAnnotationAttributes(RpcClient.class.getCanonicalName());

                // 针对这些类，都使用RpcClinetFactoryBean去构造BeanDefinition
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RpcClinetFactoryBean.class);
                // 调用构造方法填充属性
                builder.addConstructorArgValue(beanClassName);
                // 设置根据类型填充属性。实际是填充类
                builder.getBeanDefinition().setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
                // 最终，为每个注解标记的类注册一个BeanDefinition，具体的BeanDefinition实现为RpcClinetFactoryBean
                // 在RpcClinetFactoryBean的getObject中
                beanDefinitionRegistry.registerBeanDefinition(beanClassName, builder.getBeanDefinition());

            }
        });
    }

    //允许Spring扫描接口上的注解
    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
            }
        };
    }


}

package com.example.factory;

import com.example.client.RpcDynamicPro;
import lombok.Data;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;

@Data
public class RpcClinetFactoryBean implements FactoryBean {

    @Autowired
    private RpcDynamicPro rpcDynamicPro;

    private Class<?> classType;


    public RpcClinetFactoryBean(Class<?> classType) {
        this.classType = classType;
    }

    @Override
    public Object getObject(){
        ClassLoader classLoader = classType.getClassLoader();
        Object object = Proxy.newProxyInstance(classLoader,new Class<?>[]{classType},rpcDynamicPro);
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        return this.classType;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
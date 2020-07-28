package com.example.client;

import com.alibaba.fastjson.JSON;
import com.example.data.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 代理方法类
 * @author xuyongjia
 * @date 2020/7/25
 */
@Component
@Slf4j
public class RpcDynamicPro implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String requestJson = objectToJson(method, args);
        Long threadId = Thread.currentThread().getId();
        NettyClientConnect nettyClientConnect = new NettyClientConnect();
        nettyClientConnect.connect(requestJson,threadId);
        return nettyClientConnect.getResponse(threadId).getResult();
    }


    public String objectToJson(Method method,Object [] args){
        Request request = new Request();
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String className = method.getDeclaringClass().getName();
        request.setMethodName(methodName);
        request.setParameTypes(parameterTypes);
        request.setParameters(args);
        request.setClassName(getClassName(className));
        return JSON.toJSONString(request);
    }

    private String getClassName(String beanClassName){
        String className = beanClassName.substring(beanClassName.lastIndexOf(".")+1);
        className = className.substring(0,1).toLowerCase() + className.substring(1);
        return className;
    }
}

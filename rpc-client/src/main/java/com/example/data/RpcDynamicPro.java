package com.example.data;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketTimeoutException;

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
        // JSON.toJSONString()
        String requestJson = objectToJson(method, args);
        Response response = new Response();
        try(Socket client = new Socket("127.0.0.1", 20006)) {
            client.setSoTimeout(10000);
            //获取Socket的输出流，用来发送数据到服务端
            PrintStream out = new PrintStream(client.getOutputStream());
            //获取Socket的输入流，用来接收从服务端发送过来的数据
            BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
            //发送数据到服务端
            out.println(requestJson);

            String responseJson = buf.readLine();
            response = JSON.parseObject(responseJson, Response.class);
        } catch(SocketTimeoutException e){
            log.info("Time out, No response");
        }
        return response.getResult();
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

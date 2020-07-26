package com.example.annotation;

import java.lang.annotation.*;

/**
 * @author xuyongjia
 * @date 2020/7/25
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RpcClient {
}

package com.example.myapp.annotation;

import com.example.myapp.enums.ApiName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记需要埋点追踪的接口方法。
 * AOP 切面拦截此注解，在方法成功返回后异步记录调用日志。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackCall {

    /**
     * 接口名称，对应 ApiName 枚举。
     */
    ApiName value();
}

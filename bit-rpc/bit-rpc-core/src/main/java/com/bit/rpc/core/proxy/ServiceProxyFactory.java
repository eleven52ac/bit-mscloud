package com.bit.rpc.core.proxy;

import java.lang.reflect.Proxy;

/**
 * @Datetime: 2026年01月13日18:22
 * @Author: Eleven52AC
 * @Description: 服务代理工厂
 */
public class ServiceProxyFactory {

    /**
     * 根据服务类获取代理对象
     *
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }

}

package com.luban.framework;

import com.luban.register.ZookeeperRegister;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class ProxyFactory {

    public static <T> T getProxy(final Class interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                String mock = System.getProperty("mock");
                if (mock.startsWith("return:")) {
                    String result = mock.replace("return:", "");
                    return result;
                }

                Invocation invocation = new Invocation(interfaceClass.getName(), method.getName(), args, method.getParameterTypes());
                List<URL> urlList = ZookeeperRegister.get(interfaceClass.getName());
                URL url = LoadBalance.random(urlList);
                System.out.println(url);
                Protocol protocol = ProtocolFactory.getProtocol();
                String result = protocol.send(url, invocation);
                return result;
            }
        });
    }
}

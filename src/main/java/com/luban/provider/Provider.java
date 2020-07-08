package com.luban.provider;

import com.luban.framework.Protocol;
import com.luban.framework.ProtocolFactory;
import com.luban.framework.URL;
import com.luban.provider.api.HelloService;
import com.luban.provider.impl.HelloServiceImpl;
import com.luban.register.ZookeeperRegister;

public class Provider {

    private static boolean isRun = true;

    public static void main(String[] args) {
        // 注册服务
        URL url = new URL("localhost", Integer.valueOf(System.getProperty("port")));
//        RemoteMapRegister.regist(HelloService.class.getName(), url);
        ZookeeperRegister.regist(HelloService.class.getName(), url);
        LocalRegister.regist(HelloService.class.getName(), HelloServiceImpl.class);

        // 启动Tomcat
        Protocol protocol = ProtocolFactory.getProtocol();
        protocol.start(url);



    }
}

package com.luban.framework;

import com.luban.protocol.dubbo.DubboProtocol;
import com.luban.protocol.http.HttpProtocol;

public class ProtocolFactory {

    public static Protocol getProtocol() {
        // 工厂模式
        String name = System.getProperty("protocolName");
        if (name == null || name.equals("")) name = "http";
        switch (name) {
            case "http":
                return new HttpProtocol();
            case "dubbo":
                return new DubboProtocol();
            default:
                break;
        }
        return new HttpProtocol();
    }
}

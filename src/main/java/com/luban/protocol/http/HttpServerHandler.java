package com.luban.protocol.http;

import com.alibaba.fastjson.JSONObject;
import com.luban.framework.Invocation;
import com.luban.provider.LocalRegister;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class HttpServerHandler {

    public void handler(HttpServletRequest req, HttpServletResponse resp) {

        try {
            Invocation invocation = JSONObject.parseObject(req.getInputStream(), Invocation.class);
            var interfaceName = invocation.getInterfaceName();
            var implClass = LocalRegister.get(interfaceName);
            var method = implClass.getMethod(invocation.getMethodName(), invocation.getParamType());
            var result = (String) method.invoke(implClass.newInstance(), invocation.getParams());

            System.out.println("tomcat:" + result);
            IOUtils.write(result, resp.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }
}

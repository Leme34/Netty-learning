package com.luban.framework;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Invocation implements Serializable {

    private String interfaceName;
    private String methodName;
    private Object[] params;
    private Class[] paramType;
}

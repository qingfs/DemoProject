package com.example.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class Request implements Serializable {
    private static final long serialVersionUID = 3933918042687238629L;
    private String className;
    private String methodName;
    private Class<?> [] parameTypes;
    private Object [] parameters;
}
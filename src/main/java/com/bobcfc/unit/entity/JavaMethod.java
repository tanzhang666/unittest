package com.bobcfc.unit.entity;

import lombok.Data;

@Data
public class JavaMethod {

    private String methodName;

    private String returnType;

    private String[] paramTypes;

    private String methodBody;
}

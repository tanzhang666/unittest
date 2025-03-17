package com.bobcfc.unit.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class JavaClass {

    private String className;

    private Map<String,JavaMethod> methods;
}

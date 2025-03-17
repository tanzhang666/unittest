package com.bobcfc.unit.service;

public class PromptConstruction {

    // 构建用于生成单元测试的提示
    public String buildTestGenerationPrompt(String className, String methodName, String returnType,String methodBody) {
        return "Generate a JUnit test case for the method `" + methodName + "` in the class `" + className + "`. " +
               "The method returns a value of type `" + returnType + "`. " +
                "The method returns a value of type `" + returnType + "`. " +
                "The method body is :"+ methodBody+ "`. " +
               "The test should check for valid inputs, edge cases, and exception handling if applicable.";
    }
}
package com.bobcfc.unit.service;

import com.bobcfc.unit.config.FileConfig;
import lombok.RequiredArgsConstructor;
import org.objectweb.asm.tree.MethodNode;

import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * This class generates unit tests from methods extracted from bytecode by calling the Ollama API.
 */
@RequiredArgsConstructor
public class TestGenerator {

    private final Map<String, Method> methods;

    // Inject FileConfig via constructor using Lombok's @RequiredArgsConstructor
    private final FileConfig fileConfig;

    public static void generateTestInput(String className, String methodName, String returnType, String methodBody) {
        // 将方法实现和字节码信息结合，构建完整的 AI 输入
        StringBuilder prompt = new StringBuilder();
        prompt.append("Class: " + className + "\n");
        prompt.append("Method: " + methodName + "\n");
        prompt.append("Return Type: " + returnType + "\n");
        prompt.append("Method Body: " + methodBody + "\n");
        prompt.append("Description: Based on the body, generate JUnit tests covering edge cases.\n");

        System.out.println("Prompt to AI: \n" + prompt.toString());
        // 这里可以将该 prompt 传给 AI
    }

    /**
     * Calls the Ollama API to generate a unit test based on the provided Java Method.
     *
     * @param method The Java Method object containing method details.
     * @return A string representing the generated unit test code.
     */
    private String callOllamaApiToGenerateTest(Method method) {
        AiCaller aiCaller = new AiCaller();
        // Assuming generateContent() returns a string with the unit test
        //转换method为json输入给ai
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        StringBuilder jsonBuilder = new StringBuilder("{");
        jsonBuilder.append("\"methodName\": \"").append(methodName).append("\", ");
        jsonBuilder.append("\"parameterTypes\": [");
        for (int i = 0; i < parameterTypes.length; i++) {
            jsonBuilder.append("\"").append(parameterTypes[i].getName()).append("\"");
            if (i < parameterTypes.length - 1) {
                jsonBuilder.append(", ");
            }
        }
        jsonBuilder.append("]}");
        String methodJson = jsonBuilder.toString();
        String testContent = aiCaller.generateContent(methodJson);
        return testContent != null ? testContent : "// Unit test for " + method.getName() + "\n";
    }

//    /**
//     * Converts ASM MethodNode objects to JavaMethod objects.
//     *
//     * @param asmMethods A list of ASM MethodNode objects.
//     * @return A list of JavaMethod objects representing the same methods.
//     */
//    private static List<Method> convertAsmMethodsToJavaMethods(List<MethodNode> asmMethods) {
//        // Implementation to convert ASM MethodNodes to JavaMethods
//        return asmMethods.stream()
//                .map(asmMethod -> new Method(
//                        asmMethod.name,
//                        asmMethod.desc,
//                        asmMethod.access))
//                .toList();
//    }

    /**
     * Saves generated tests to a file using FileConfig.
     *
     * @param className The name of the class for which the tests were generated.
     * @param testCode  A string containing the generated unit test code.
     */
    private void saveGeneratedTestsToFile(String className, String testCode) {
        // Implementation to write testCode to a file specified in FileConfig
        try (FileWriter writer = new java.io.FileWriter(fileConfig.getResultFilePath() + "/" + className + "Test.java")) {
            writer.write(testCode);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to save generated tests", e);
        }
    }
}

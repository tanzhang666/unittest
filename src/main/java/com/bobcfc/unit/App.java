package com.bobcfc.unit;

import com.bobcfc.unit.config.FileConfig;
import com.bobcfc.unit.entity.JavaClass;
import com.bobcfc.unit.entity.JavaMethod;
import com.bobcfc.unit.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class App {

    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
        FileConfig fileConfig = new FileConfig();
        ByteCodeParser byteCodeParser = new ByteCodeParser();
        SourceCodeParser sourceCodeParser = new SourceCodeParser();
        PromptConstruction promptConstruction = new PromptConstruction();
        AiCaller aiCaller = new AiCaller();
        try {
//            Map<String, JavaClass> methodMap = byteCodeParser.parseBytecode(fileConfig.getByteCodeFilePath());
            Map<String, JavaClass> methodMap = byteCodeParser.parseBytecode("C:\\Users\\admin\\IdeaProjects\\bobcfc-limit-n\\bobcfc-limit-service\\target\\classes\\com\\bobcfc\\limit\\domain\\limitcenter\\service\\impl\\AutoAdjustAmountServiceImpl.class");
            methodMap = sourceCodeParser.parseSourceCode("C:\\Users\\admin\\IdeaProjects\\bobcfc-limit-n\\bobcfc-limit-service\\src\\main\\java\\com\\bobcfc\\limit\\domain\\limitcenter\\service\\impl\\AutoAdjustAmountServiceImpl.java", methodMap);
            for (Map.Entry<String, JavaClass> entry : methodMap.entrySet()) {
                JavaClass javaClass = entry.getValue();
                Map<String, JavaMethod> methods = javaClass.getMethods();
                for (Map.Entry<String, JavaMethod> method : methods.entrySet()) {
                    String prompt = promptConstruction.buildTestGenerationPrompt(javaClass.getClassName(), method.getValue().getMethodName(), method.getValue().getReturnType(), method.getValue().getMethodBody());
                    System.out.println(prompt);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

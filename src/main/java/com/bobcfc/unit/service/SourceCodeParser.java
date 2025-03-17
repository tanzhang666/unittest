package com.bobcfc.unit.service;

import cn.hutool.core.util.StrUtil;
import com.bobcfc.unit.entity.JavaClass;
import com.bobcfc.unit.entity.JavaMethod;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.printer.PrettyPrinter;
import com.github.javaparser.printer.configuration.PrettyPrinterConfiguration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SourceCodeParser {

    public Map<String, JavaClass> parseSourceCode(String sourceFilePath, Map<String, JavaClass> javaClassMap) {
        try (FileInputStream fileInputStream = new FileInputStream(sourceFilePath)) {
            JavaParser javaParser = new JavaParser();
            ParseResult<CompilationUnit> cu = javaParser.parse(fileInputStream);
            // 检查解析是否成功
            if (!cu.isSuccessful()) {
                throw new IOException("Failed to parse source file");
            }
            CompilationUnit compilationUnit = cu.getResult().get();

            PackageDeclaration packageDeclaration = compilationUnit.getPackageDeclaration().orElse(null);
            String packageName = (packageDeclaration != null) ? packageDeclaration.getNameAsString() : "";

            TypeDeclaration<?> typeDecl = compilationUnit.getTypes().stream()
                    .filter(type -> type instanceof TypeDeclaration)
                    .map(type -> (TypeDeclaration<?>) type)
                    .findFirst()
                    .orElseThrow(() -> new IOException("No type declaration found in the source file."));

            String className = typeDecl.getNameAsString();
            String fullyQualifiedClassName = packageName.isEmpty()
                    ? className
                    : StrUtil.replace(packageName,".","/") + "/" + className;

            System.out.println("Processing class: " + fullyQualifiedClassName);

            JavaClass javaClass = javaClassMap.get(fullyQualifiedClassName);
            if (javaClass == null) {
                System.out.println("JavaClass not found in the map.");
                return javaClassMap;
            }

            // Extract methods
            List<MethodDeclaration> methods = typeDecl.getMethods();

            PrettyPrinter prettyPrinter = new PrettyPrinter(new PrettyPrinterConfiguration());

            for (MethodDeclaration method : methods) {
                String methodName = method.getNameAsString();
                String returnType = method.getType().toString();
                // Extract parameters
                List<String> parameterTypes = method.getParameters()
                        .stream()
                        .map(param -> param.getType().toString())
                        .collect(Collectors.toList());

                // Extract method body
                String methodBody = prettyPrinter.print(method.getBody().orElse(null));

                System.out.println("Method: " + methodName);
                System.out.println("Return type: " + returnType);
                System.out.println("Parameters: " + parameterTypes);
                System.out.println("Method Body:"+methodBody);
                System.out.println(methodBody);

                // Update JavaClass with method information
                JavaMethod javaMethod = javaClass.getMethods().get(methodName);
                javaMethod.setMethodBody(methodBody);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing source file: " + sourceFilePath, e);
        }
        return javaClassMap;
    }
}

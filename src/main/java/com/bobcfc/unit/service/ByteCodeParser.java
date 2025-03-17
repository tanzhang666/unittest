package com.bobcfc.unit.service;

import com.bobcfc.unit.entity.JavaClass;
import com.bobcfc.unit.entity.JavaMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.MethodVisitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ByteCodeParser {


    public Map<String, JavaClass> parseBytecode(String classFilePath) throws IOException {

        FileInputStream fis = new FileInputStream(new File(classFilePath));

        ClassReader classReader = new ClassReader(fis);
        Map<String,JavaClass> javaClassMap = new HashMap<>();
        JavaClass javaClass = new JavaClass();
        String className = classReader.getClassName();
        System.out.println(className);
        javaClass.setClassName(className);
        javaClassMap.put(className,javaClass);
        Map<String,JavaMethod> javaMethods = new HashMap<>();
        // 使用 ASM 解析字节码
        javaClass.setMethods(javaMethods);
        classReader.accept(new ClassVisitor(Opcodes.ASM9) {

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

            // 提取方法信息

            String returnType = Type.getReturnType(descriptor).getClassName();

            String[] paramTypes = Arrays.stream(Type.getArgumentTypes(descriptor)).map(Type::getClassName).toArray(String[]::new);

            JavaMethod javaMethod = new JavaMethod();
            javaMethod.setMethodName(name);
            javaMethod.setParamTypes(paramTypes);
            javaMethod.setReturnType(returnType);
            javaMethods.put(name,javaMethod);
            // 打印方法签名和类型信息

            System.out.println("ByteCodeParser-Method: " + name);

            System.out.println("ByteCodeParser-Return Type: " + returnType);

            System.out.println("ByteCodeParser-Parameters: " + Arrays.toString(paramTypes));


            return super.visitMethod(access, name, descriptor, signature, exceptions);

        }

        }, 0);
        return javaClassMap;
    }

}

package com.bobcfc.unit.service;

import com.bobcfc.unit.config.FileConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class is designed to extract Java classes from a JAR file and retrieve
 * a list of their methods, which can be used for further processing.
 */
public class CodeExtractor {

    // Logger instance for logging purposes
    private static final Logger logger = LoggerFactory.getLogger(CodeExtractor.class);

    // Path to the JAR file that needs to be parsed
    private FileConfig fileConfig;

    private String name;

    /**
     * Constructor to initialize the CodeExtractor with a path to a JAR file.
     *
     * @param fileConfig The absolute or relative path to the JAR file.
     */
    public CodeExtractor(FileConfig fileConfig,String name) {
        this.fileConfig = fileConfig;
        this.name=name;
    }

    /**
     * Retrieves a list of methods from all classes contained within the JAR file.
     *
     * @return A list of Method objects representing all the methods found in the JAR file's classes.
     * @throws IOException If an I/O error occurs while reading the JAR file.
     */
    public Map<String,Method> getMethodList() throws IOException {
        logger.info("Starting to extract methods from JAR file: {}", name);

        // List to hold all the Method objects found
        Map<String,Method> javaMethods = new HashMap<>();

        // JarFile object to read the contents of the JAR file
        JarFile jarFile = null;
        try {
            // Open the JAR file using the provided path
            jarFile = new JarFile(fileConfig.getSourceFilePath()+name);

            // Get an enumeration of all entries in the JAR file
            Enumeration<JarEntry> entries = jarFile.entries();

            // Iterate through each entry in the JAR file
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                // Check if the entry is a .class file and not a directory
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    // Convert the class path to its corresponding fully qualified name
                    String className = entry.getName()
                            .substring(0, entry.getName().length() - 6)
                            .replace('/', '.');

                    logger.debug("Processing class: {}", className);

                    try {
                        // Load the class from the fully qualified name
                        Class<?> clazz = Class.forName(className);

                        // Get all declared methods in the class
                        Method[] methods = clazz.getDeclaredMethods();
                        // Add each method to the list of Java methods
                        for (Method method : methods) {
                            javaMethods.put(clazz.getName(),method);
                            logger.trace("Found method: {} in class: {}", method.getName(), className);
                        }
                    } catch (ClassNotFoundException e) {
                        logger.error("Class not found: {}", className, e);
                    }
                }
            }
        } finally {
            // Ensure the JAR file is closed after processing
            if (jarFile != null) {
                jarFile.close();
                logger.info("Finished extracting methods from JAR file. Total methods found: {}", javaMethods.size());
            }
        }

        return javaMethods;
    }
}

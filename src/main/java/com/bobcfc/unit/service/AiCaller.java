package com.bobcfc.unit.service;

import cn.hutool.json.JSONObject;

//此类用来调用ollama接口根据输入的prompt生成对应的内容
public class AiCaller {

    // Method to call Ollama API and generate content based on input prompt
    public String generateContent(String prompt) {
        // This method should handle the API call to Ollama and return the generated content
        // Assuming we have a hypothetical method to make an HTTP request using OkHttp
        String generatedContent = "";
        try {
            // Example of how you might set up an HTTP request using OkHttp
            okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
            okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://api.ollama.com/generate")
                .header("Content-Type", "application/json")
                .post(okhttp3.RequestBody.create("{\"prompt\": \"" + prompt + "\"}", okhttp3.MediaType.get("application/json; charset=utf-8")))
                .build();

            okhttp3.Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                // Assuming the response is a JSON string with a "content" field
                String responseBody = response.body().string();
                generatedContent = new JSONObject(responseBody).get("content", String.class);
            } else {
                throw new RuntimeException("Failed to generate content: HTTP error code " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generatedContent;
    }
}

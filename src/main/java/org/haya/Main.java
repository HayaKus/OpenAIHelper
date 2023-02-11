package org.haya;

import okhttp3.*;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    private static String OPEN_AI_ADDRESS = "http://127.0.0.1:5000/chat";
    private static int CONNEXT_TIMEOUT = 120; // seconds
    private static int READ_TIMEOUT = 120; // seconds
    private static String NO_REPLY_ERROR =  "The other party did not reply within " + CONNEXT_TIMEOUT + "s";
    private static OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(CONNEXT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .build();

    public static void main(String[] args) {
        openAiStart();
    }

    public static void openAiStart() {
        Scanner scanner = new Scanner(System.in);
        String content = "";
        String originResult = "";
        String result = null;
        while (true) {
            System.out.printf("你：");
            content = scanner.nextLine();
            originResult = unicodeToStr(sendRequest(content)).replaceAll("\\\\n", "\n");
            result = originResult.substring(1, originResult.length() - 2);
            System.out.println(result);
        }
    }

    /**
     * send Open AI request
     */
    public static String sendRequest(String requestData) {
        String content = "{\"prompt\":\"" + requestData + "\"}";
        RequestBody body = RequestBody.create(
                null, content);
        Request request = new Request.Builder()
                .url(OPEN_AI_ADDRESS)
                .post(body)
                .header("Content-Type", "application/json")
                .build();
        return makeRequest(request);
    }

    public static String makeRequest(Request request) {
        Call call = httpClient.newCall(request);
        try (Response response = call.execute()) {
            ResponseBody body = response.body();
            if(body == null){
                return NO_REPLY_ERROR;
            }
            return body.string();
        } catch (IOException e) {
            return NO_REPLY_ERROR;
        }
    }

    /**
     * Unicode to String
     */
    public static String unicodeToStr(String string) {
        String prefix = "\\u";
        if (string == null || string.indexOf(prefix) < 0) {
            return string;
        }

        StringBuilder value = new StringBuilder(string.length() >> 2);
        String[] strings = string.split("\\\\u");
        String hex, mix;
        char hexChar;
        int ascii, n;

        if (strings[0].length() > 0) {
            value.append(strings[0]);
        }

        try {
            for (int i = 1; i < strings.length; i++) {
                hex = strings[i];
                if (hex.length() > 3) {
                    mix = "";
                    if (hex.length() > 4) {
                        mix = hex.substring(4, hex.length());
                    }
                    hex = hex.substring(0, 4);

                    try {
                        Integer.parseInt(hex, 16);
                    } catch (Exception e) {
                        value.append(prefix).append(strings[i]);
                        continue;
                    }

                    ascii = 0;
                    for (int j = 0; j < hex.length(); j++) {
                        hexChar = hex.charAt(j);
                        n = Integer.parseInt(java.lang.String.valueOf(hexChar), 16);
                        ascii += n * ((int) Math.pow(16, (hex.length() - j - 1)));
                    }
                    value.append((char) ascii).append(mix);
                } else {
                    value.append(prefix).append(hex);
                }
            }
        } catch (Exception e) {
            return null;
        }

        return value.toString();
    }
}

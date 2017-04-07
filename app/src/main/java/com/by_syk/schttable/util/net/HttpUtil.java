package com.by_syk.schttable.util.net;

import android.util.Log;

import com.by_syk.schttable.util.C;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 高效网络请求工具类
 * （封装OkHttp）
 * 
 * @author shijkui
 */
public class HttpUtil {
    private static OkHttpClient okHttpClient = null;
    
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    static {
        // 超时设置
        // OkHttp2
//        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
//        okHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);
//        okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        // OkHttp3
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(6, TimeUnit.SECONDS)
                .readTimeout(12, TimeUnit.SECONDS)
                .build();
    }

    /**
     * GET请求
     * 
     * @param url
     * @return
     * @throws IOException
     */
    public static String get(String url) throws IOException {
        Log.d(C.LOG_TAG, "HttpUtil - get: " + url);

        Request request = (new Request.Builder())
                .url(url)
                //.method("GET", null) // 默认
                .build();
        
        Response response = okHttpClient.newCall(request).execute();
        //return response.body().string();
        if (response.isSuccessful()) {
            // body.string() which closes body anyway, if there's or there's no exception
            // https://github.com/square/okhttp/issues/2311
            return response.body().string();
        } else {
            response.body().close();
            throw new IOException("Unexpected code " + response);
        }
    }
    
    /**
     * GET请求
     * 
     * @param url
     * @return
     */
    public static String getNoException(String url) {
        try {
            return get(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 异步GET请求
     * 
     * @param url
     * @param callback 回调接口
     */
    public static void get(String url, Callback callback) {
        Request request = (new Request.Builder())
                .url(url)
                .build();
        
        okHttpClient.newCall(request).enqueue(callback);
    }
    
    /**
     * POST请求
     * 
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public static String post(String url, String json) throws IOException {
        RequestBody requestBody = RequestBody.create(JSON, json);
        
        Request request = (new Request.Builder())
                .url(url)
                .post(requestBody)
                .build();
        
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            response.body().close();
            throw new IOException("Unexpected code " + response);
        }
    }
    
    /**
     * POST请求
     * 
     * @param url
     * @param json
     * @return
     */
    public static String postNoException(String url, String json) {
        try {
            return post(url, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 异步POST请求
     * 
     * @param url
     * @param json
     * @param callback 回调接口
     * @throws IOException
     */
    public static void post(String url, String json, Callback callback) {
        RequestBody requestBody = RequestBody.create(JSON, json);
        
        Request request = (new Request.Builder())
                .url(url)
                .post(requestBody)
                .build();
        
        okHttpClient.newCall(request).enqueue(callback);
    }
    
    /**
     * POST请求，参数 map 为空则转 为GET请求
     * 
     * @param url
     * @param map
     * @return
     * @throws IOException
     */
    public static String post(String url, Map<String, String> map) throws IOException {
        if (map == null) {
            return get(url);
        }

        // OkHttp 2.x
//        // 默认编码UTF-8
//        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            formEncodingBuilder.add(entry.getKey(), entry.getValue());
//        }
//        RequestBody requestBody = formEncodingBuilder.build();

        // OkHttp 3
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            formBodyBuilder.add(entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = formBodyBuilder.build();
        
        Request request = (new Request.Builder())
                .url(url)
                .post(requestBody)
                .build();
        
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            response.body().close();
            throw new IOException("Unexpected code " + response);
        }
    }
    
    /**
     * POST请求
     * 
     * @param url
     * @param map
     * @return
     */
    public static String postNoException(String url, Map<String, String> map) {
        try {
            return post(url, map);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 异步POST请求，参数 map 为空则转为GET请求
     * 
     * @param url
     * @param map
     * @param callback 回调接口
     * @throws IOException
     */
    public static void post(String url, Map<String, String> map, Callback callback) {
        if (map == null) {
            get(url, callback);
            return;
        }

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            formBodyBuilder.add(entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = formBodyBuilder.build();
        
        Request request = (new Request.Builder())
                .url(url)
                .post(requestBody)
                .build();
        
        okHttpClient.newCall(request).enqueue(callback);
    }
    
    /**
     * 下载文件
     * 
     * @param url
     * @param targetFile
     * @return
     * @throws IOException
     */
    public static File downloadFile(String url, File targetFile) throws IOException {
        Log.d(C.LOG_TAG, "HttpUtil - downloadFile: " + url);

        Request request = (new Request.Builder())
                .url(url)
                .build();
        
        Response response = okHttpClient.newCall(request).execute();

        ResponseBody responseBody = response.body();
        InputStream inputStream = responseBody.byteStream();
        FileOutputStream fos = new FileOutputStream(targetFile);
        byte[] buffer = new byte[2048];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }
        fos.close();
        inputStream.close();
        responseBody.close();
        
        return targetFile;
    }
    
    public static File downloadFileNoException(String url, File targetFile) {
        try {
            return downloadFile(url, targetFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.example.picture.util;


import android.util.Log;

import com.example.picture.okhttp.ProgressRequestBody;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by QiuQ on 2016-12-01.
 */

public class NetWorkUtils {
    //https工具类
    public static final MediaType JSON = MediaType
            .parse("application/json; charset=utf-8");
    public static final int DOWN_TOTAL_LENGTH_CODE = 1357;
    public static final String CONNECT_SERVER_ERROR = "{\"ROOT\":{\"BODY\":{\"info\":[]},\"HEAD\":{\"ResCode\":\"545\",\"TrsAppType\":\"1\",\"ResMsg\":\"连接服务器失败,请稍后重试!\"}}}";
    public static final String UP_FILE_ERROR = "{\"resCode\":\"099\",\"resMsg\":\"上传文件失败\"}";
    private OkHttpClient client;
    private SSLContext sslContext = null;

    public NetWorkUtils() {
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//                在实现的X509TrustManager子类中checkServerTrusted函数效验服务器端证书的合法性。
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
//                需添加主机名弱校验
                return true;
            }
        };
        client = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory())
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .hostnameVerifier(DO_NOT_VERIFY)
                .build();
    }


    public String post(String json, String trsCode) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(Configure.URL_HTTPS)
                .addHeader("code", trsCode).post(body).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return CONNECT_SERVER_ERROR;
        } finally {
            if (response != null) {
                response.body().close();
            }
        }
        return CONNECT_SERVER_ERROR;
    }


    public String uploadFile(File file,String code, String url, ProgressRequestBody.OnFileUpClick onFileUpClick) {
        if (client != null && file != null && url != null) {
            StringBuilder resultBuffer = new StringBuilder("");
            String name=code+"_"+file.getName();
            MultipartBody.Builder form = new MultipartBody.Builder().setType(MultipartBody.FORM);
            form.addPart(Headers.of("Content-Disposition", "form-data; name=\"file\";filename=\"" + name + "\""), RequestBody.create(MediaType.parse("image/*"), file));
            ProgressRequestBody body = new ProgressRequestBody(form.build());
            body.setOnFileUpClick(onFileUpClick);
            Request request = new Request
                    .Builder()
                    .url(url)
                    .addHeader("Connection", "keepAlive")
                    .addHeader("Charset", "UTF-8")
                   .post(body)
                    .build();
            try {
                Call call = client.newCall(request);
                Response response = call.execute();
                if (response.isSuccessful()) {
                    resultBuffer.replace(0, resultBuffer.length(), response.body().string());
                    return resultBuffer.toString();
                }
                response.body().close();

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return "{\"resCode\":\"000\",\"resMsg\":\"上传文件失败\"}";
    }

    public String uploadFileGetProgress(File file, String uniqueNo, String fileCode, ProgressRequestBody.OnFileUpClick fileUpClick) {
        Map<String, String> params = new HashMap<>();
        params.put("busiType", "0");//业务类型 0-支取 1-贷款
        params.put("flag", "up");// 操作标志：up-上传 down-下载
        params.put("uniqueNo", uniqueNo);//业务唯一编号 app定
        params.put("fileType", fileCode);//文件所属类型 code码
        StringBuilder buffer = new StringBuilder();
        String url = Configure.URL_FILE_HTTPS;
        url += "?";
        for (String key : params.keySet()) {
            //添加参数
            buffer.append(key).append("=").append(params.get(key)).append("&");
        }
        url += buffer.toString();
        if (url.lastIndexOf("&") != -1) {
            url = url.substring(0, url.lastIndexOf("&"));
        }
        try {
            return uploadFile(file,fileCode, url, fileUpClick);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{\"resCode\":\"099\",\"resMsg\":\"上传文件失败\"}";
    }


    public String uploadFile(File file, String uniqueNo, String fileCode) {
        StringBuilder resultBuffer = new StringBuilder("");
        Map<String, String> params = new HashMap<>();
        params.put("busiType", "0");//业务类型 0-支取 1-贷款
        params.put("flag", "up");// 操作标志：up-上传 down-下载
        params.put("uniqueNo", uniqueNo);//业务唯一编号 app定
        params.put("fileType", fileCode);//文件所属类型 code码
        MultipartBody.Builder form = new MultipartBody.Builder().setType(MultipartBody.FORM);
        StringBuilder buffer = new StringBuilder();
        String url = Configure.URL_FILE_HTTPS;
        url += "?";
        for (String key : params.keySet()) {
            //添加参数
            buffer.append(key).append("=").append(params.get(key)).append("&");
        }
        url += buffer.toString();
        if (url.lastIndexOf("&") != -1) {
            url = url.substring(0, url.lastIndexOf("&"));
        }
        form.addPart(Headers.of("Content-Disposition", "form-data; name=\"file\";filename=\"" + file.getName() + "\""), RequestBody.create(MediaType.parse("image/png"), file));
        RequestBody body = form.build();
        Request request = new Request
                .Builder()
                .url(url)
                .addHeader("Connection", "keepAlive")
                .addHeader("Charset", "UTF-8")
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                resultBuffer.replace(0, resultBuffer.length(), response.body().string());
                return resultBuffer.toString();
            }
            response.body().close();
        } catch (IOException e) {
            e.printStackTrace();
            return UP_FILE_ERROR;
        }
        return UP_FILE_ERROR;
    }

    public String uploadFileList(List<File> fileList, String uniqueNo, String fileCode) {
        StringBuilder resultBuffer = new StringBuilder("");
        Map<String, String> params = new HashMap<>();
        params.put("busiType", "0");//业务类型 0-支取 1-贷款
        params.put("flag", "up");// 操作标志：up-上传 down-下载
        params.put("uniqueNo", uniqueNo);//业务唯一编号 app定
        params.put("fileType", fileCode);//文件所属类型 code码
        MultipartBody.Builder form = new MultipartBody.Builder().setType(MultipartBody.FORM);
        StringBuilder buffer = new StringBuilder();
        String url = Configure.URL_FILE_HTTPS;
        url += "?";
        for (String key : params.keySet()) {
            //添加参数
            buffer.append(key).append("=").append(params.get(key)).append("&");
        }
        url += buffer.toString();
        if (url.lastIndexOf("&") != -1) {
            url = url.substring(0, url.lastIndexOf("&"));
        }
        for (int i = 0; i < fileList.size(); i++) {
            form.addPart(Headers.of("Content-Disposition", "form-data; name=\"file\";filename=\"" + fileList.get(i).getName() + "\""), RequestBody.create(MediaType.parse("image/png"), fileList.get(i)));
        }
        RequestBody body = form.build();
        Request request = new Request
                .Builder()
                .url(url)
                .addHeader("Connection", "keepAlive")
                .addHeader("Charset", "UTF-8")
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                resultBuffer.replace(0, resultBuffer.length(), response.body().string());
                Log.e("upFileFlag", resultBuffer.toString());
                return resultBuffer.toString();
            } else {
                Log.e("error", response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return UP_FILE_ERROR;
        }
        return UP_FILE_ERROR;
    }


}


package cn.suozhi.DiBi.common.util;

import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.NonNull;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocketListener;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * OkHttp 工具类
 *      -- get 和 post 严格区分，post请求时如不传参，会传递MediaType为null的空RequestBody
 *      -- get 请求在回调中会返回拼接前的url
 *      -- 请求头如需添加参数，参数名须在{@link #HEADER_NAME}中提前配置，且使用时注意顺序
 *          调用方法{@link #getJsonHeader2(String, String, String, OnDataListener, String...)}
 *              或 {@link #postJsonHeader2(String, String, String, OnDataListener, String...)}
 */
public class OkHttpUtil {

    private static final String[] HEADER_NAME = {"Authorization", "language"};
    private static OkHttpClient okHttpClient;
    private static Handler handler = new Handler();

    public static void initOkHttp() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(60, TimeUnit.MINUTES)
                .writeTimeout(60, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true);

        //处理https协议
        SSLContext sc;
        TrustAllManager tm = new TrustAllManager();
        try {
            sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{tm}, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
            sc = null;
        }
        if (sc != null) {
            okHttpClient = builder.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()), tm)
                    .hostnameVerifier((hostname, session) -> true)
                    .build();
        } else {
            okHttpClient = builder.hostnameVerifier((hostname, session) -> true)
                    .build();
        }
    }

    private static class  TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class Tls12SocketFactory extends SSLSocketFactory {

        private final String[] TLS_SUPPORT_VERSION = {"TLSv1","TLSv1.1", "TLSv1.2"};

        final SSLSocketFactory delegate;

        public Tls12SocketFactory(SSLSocketFactory delegate) {
            this.delegate = delegate;
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return delegate.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return delegate.getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
            return patch(delegate.createSocket(s, host, port, autoClose));
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            return patch(delegate.createSocket(host, port));
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
            return patch(delegate.createSocket(host, port, localHost, localPort));
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            return patch(delegate.createSocket(host, port));
        }

        @Override
        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
            return patch(delegate.createSocket(address, port, localAddress, localPort));
        }

        private Socket patch(Socket s) {
            if (s instanceof SSLSocket) {
                ((SSLSocket) s).setEnabledProtocols(TLS_SUPPORT_VERSION);
            }
            return s;
        }
    }

    /**
     * 取消所有请求
     */
    public static void cancel() {
        okHttpClient.dispatcher().cancelAll();
    }

    public static void getStream(final String url, final OnBytesListener listener) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(() -> {
                    try {
                        if (listener != null) {
                            Log.e("loge", "onFailure: " + url + "\n" + e.getMessage());
                            listener.onFailure(url, e.getMessage());
                        }
                    } catch (Exception e1) {
                        Log.e("loge", "E: " + e1.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final byte[] bytes = response.body().bytes();
                handler.post(() -> {
                    if (listener != null) {
                        listener.onResponse(url, bytes);
                    }
                });
            }
        });
    }

    public static void download(final String url, final String filePath, final OnDataListener dataListener) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onFail(dataListener, url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                File file = new File(filePath);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                InputStream is = response.body().byteStream();
                OutputStream os = new FileOutputStream(file);
                byte[] bs = new byte[1024];
                int len;
                while ((len = is.read(bs)) != -1) {
                    os.write(bs, 0, len);
                }
                is.close();
                os.close();
                onResp(dataListener, url, filePath, null);
            }
        });
    }

    /**
     * 下载文件
     */
    public static void fileDownload(final String url, final String filePath,
                                    final OnProgressListener progressListener, final OnDataListener dataListener) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                onFail(dataListener, url, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                File file = new File(filePath);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                InputStream is = response.body().byteStream();
                long length = response.body().contentLength();
                OutputStream os = new FileOutputStream(file);
                byte[] bs = new byte[1024 * 2];
                int len;
                long count = 0;
                while ((len = is.read(bs)) != -1) {
                    count += len;
                    os.write(bs, 0, len);
                    if (progressListener != null) {
                        progressListener.onProgress(0, (int) (count * 100 / length));
                    }
                }
                is.close();
                os.close();
                onResp(dataListener, url, "suc", null);
            }
        });
    }

    /**
     * 同步get请求
     */
    public static String getJson(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return okHttpClient.newCall(request).execute().body().string();
    }

    public static void getJson(String url, OnDataListener dataListener, String... kv) {
        getJsonToken(url, null, dataListener, kv);
    }

    public static void getJsonToken(String url, String token, OnDataListener dataListener, String... kv) {
        getJsonHeader2(url, token, null, dataListener, kv);
    }

    public static void getJsonHeader2(String url, String header1, String header2,
                                      OnDataListener dataListener, String... kv) {
        if (TextUtils.isEmpty(url)) {
            Log.e("loge", "getJson: 请求链接为空");
            return;
        }
        if (kv.length % 2 != 0) {//键值对不匹配
            Log.e("loge", "getJson: 请求参数不匹配");
            return;
        }
        getJsonHeader(url + getKVJoint(kv), dataListener, header1, header2);
    }

    private static String getKVJoint(String[] kv) {
        if (kv.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < kv.length / 2; i++) {
            sb.append(i == 0 ? "?" : "&")
                    .append(kv[i * 2])
                    .append("=")
                    .append(kv[i * 2 + 1]);
        }
        return sb.toString();
    }

    /**
     * 携带多头部  -  GET
     */
    public static void getJsonHeader(String url, OnDataListener dataListener, String... header) {
        Request.Builder builder = new Request.Builder().url(url);
        if (header.length > 0) {
            for (int i = 0; i < header.length; i++) {
                if (!TextUtils.isEmpty(header[i])) {
                    builder.addHeader(HEADER_NAME[i], header[i]);
                }
            }
        }
//        Log.e("loge", "url: " + url);
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        okHttpClient.newCall(builder.build()).enqueue(new OkHttpCallback(url, dataListener));
    }


    public static void postJson(String url, String json, OnDataListener dataListener) {
        //1.键值对时使用
//        FormBody body = new FormBody.Builder().add("InJson", json).build();

        //Body体时使用
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        postJsonSessionHeader(url, null, body, dataListener);
    }

    public static void postJson(String url, OnDataListener dataListener, String... kv) {
        postJsonTokenSession(url, null, null, dataListener, kv);
    }

    public static void postJsonToken(String url, String token, OnDataListener dataListener, String... kv) {
        postJsonTokenSession(url, token, null, dataListener, kv);
    }

    public static void postJsonSession(String url, String session, OnDataListener dataListener, String... kv) {
        postJsonTokenSession(url, null, session, dataListener, kv);
    }

    public static void postJsonTokenSession(String url, String token, String session,
                                            OnDataListener dataListener, String... kv) {
        postJsonHeader2Session(url, token, null, session, dataListener, kv);
    }

    public static void postJsonHeader2(String url, String header1, String header2,
                                       OnDataListener dataListener, String... kv) {
        postJsonHeader2Session(url, header1, header2, null, dataListener, kv);
    }

    /**
     * 添加2Header
     */
    public static void postJsonHeader2Session(String url, String header1, String header2, String session,
                                              OnDataListener dataListener, String... kv) {
        if (TextUtils.isEmpty(url)) {
            Log.e("loge", "postJson: 请求链接为空");
            return;
        }
        if (kv.length % 2 != 0) {//键值对不匹配
            Log.e("loge", "postJson: 请求参数不匹配");
            return;
        }
        postJsonSessionHeader(url, session, getKVBuild(kv), dataListener, header1, header2);
    }

    private static RequestBody getKVBuild(String[] kv) {
        if (kv.length == 0) {
            return null;
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (int i = 0; i < kv.length / 2; i++) {
            builder.add(kv[i * 2], kv[i * 2 + 1]);
//            Log.e("loge", kv[i * 2] + ": " + kv[i * 2 + 1]);
        }
        return builder.build();
    }

    /**
     * 附带Session、携带多头部  -  POST
     */
    private static void postJsonSessionHeader(String url, String session, RequestBody body,
                                              OnDataListener dataListener, String... header) {
        Request.Builder builder = new Request.Builder().url(url);
        if (header.length > 0) {
            for (int i = 0; i < header.length; i++) {
                if (!TextUtils.isEmpty(header[i])) {
                    builder.addHeader(HEADER_NAME[i], header[i]);
                }
            }
        }
        if (!TextUtils.isEmpty(session)) {
//            Log.e("loge", "session: " + session);
            builder.addHeader("cookie", session);
        }
        if (body != null) {
            builder.post(body);
        } else {
            builder.post(RequestBody.create(null, ""));
        }
        builder.addHeader("Connection", "close");
        okHttpClient.newCall(builder.build()).enqueue(new OkHttpCallback(url, dataListener));
    }


    public static void putJson(String url, OnDataListener dataListener, String... kv) {
        putJsonToken(url, null, dataListener, kv);
    }

    public static void putJsonToken(String url, String token, OnDataListener dataListener, String... kv) {
        putJsonHeader2Session(url, token, null, null, dataListener, kv);
    }

    /**
     * 添加2Header
     */
    public static void putJsonHeader2Session(String url, String header1, String header2, String session,
                                              OnDataListener dataListener, String... kv) {
        if (TextUtils.isEmpty(url)) {
            Log.e("loge", "postJson: 请求链接为空");
            return;
        }
        if (kv.length % 2 != 0) {//键值对不匹配
            Log.e("loge", "postJson: 请求参数不匹配");
            return;
        }
        putJsonSessionHeader(url, session, getKVBuild(kv), dataListener, header1, header2);
    }

    /**
     * 附带Session、携带多头部  -  PUT
     */
    private static void putJsonSessionHeader(String url, String session, RequestBody body,
                                              OnDataListener dataListener, String... header) {
        Request.Builder builder = new Request.Builder().url(url);
        if (header.length > 0) {
            for (int i = 0; i < header.length; i++) {
                if (!TextUtils.isEmpty(header[i])) {
                    builder.addHeader(HEADER_NAME[i], header[i]);
                }
            }
        }
        if (!TextUtils.isEmpty(session)) {
//            Log.e("loge", "session: " + session);
            builder.addHeader("cookie", session);
        }
        if (body != null) {
            builder.put(body);
        } else {
            builder.put(RequestBody.create(null, ""));
        }
        builder.addHeader("Connection", "close");
        okHttpClient.newCall(builder.build()).enqueue(new OkHttpCallback(url, dataListener));
    }


    public static void deleteJsonToken(String url, String token, OnDataListener dataListener, String... kv) {
        deleteJsonHeader2Session(url, token, null, null, dataListener, kv);
    }

    /**
     * 添加2Header
     */
    public static void deleteJsonHeader2Session(String url, String header1, String header2, String session,
                                             OnDataListener dataListener, String... kv) {
        if (TextUtils.isEmpty(url)) {
            Log.e("loge", "postJson: 请求链接为空");
            return;
        }
        if (kv.length % 2 != 0) {//键值对不匹配
            Log.e("loge", "postJson: 请求参数不匹配");
            return;
        }
        deleteJsonSessionHeader(url, session, getKVBuild(kv), dataListener, header1, header2);
    }

    /**
     * 附带Session、携带多头部  -  DELETE
     */
    private static void deleteJsonSessionHeader(String url, String session, RequestBody body,
                                              OnDataListener dataListener, String... header) {
        Request.Builder builder = new Request.Builder().url(url);
        if (header.length > 0) {
            for (int i = 0; i < header.length; i++) {
                if (!TextUtils.isEmpty(header[i])) {
                    builder.addHeader(HEADER_NAME[i], header[i]);
                }
            }
        }
        if (!TextUtils.isEmpty(session)) {
//            Log.e("loge", "session: " + session);
            builder.addHeader("cookie", session);
        }
        if (body != null) {
            builder.delete(body);
        } else {
            builder.delete(RequestBody.create(null, ""));
        }
        builder.addHeader("Connection", "close");
        okHttpClient.newCall(builder.build()).enqueue(new OkHttpCallback(url, dataListener));
    }


    /**
     * Socket长连接
     */
    public static void socket(String url, WebSocketListener socketListener) {
        if (!TextUtils.isEmpty(url)) {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            okHttpClient.newWebSocket(request, socketListener);
//            okHttpClient.dispatcher().executorService().shutdown();//清除并关闭线程池
        }
    }


    /**
     * post提交Bitmap -- 不带进度条
     */
    public static void postFORM(String url, String token, Bitmap bitmap, OnDataListener dataListener, String... kv) {
        if (bitmap == null) {
            return;
        }
        postFORM(url, token, BitMapUtil.Bitmap2Bytes(bitmap), dataListener, kv);
    }

    /**
     * post提交bytes -- 不带进度条
     */
    public static void postFORM(String url, String token, final byte[] bytes, OnDataListener dataListener, String... kv) {
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MultipartBody.FORM;
            }

            @Override
            public long contentLength() {
                return bytes.length;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(bytes);
            }
        };
        createCall(url, token, requestBody, kv).enqueue(new OkHttpCallback(url, dataListener));
    }

    /**
     * post上传File -- 不带参数 不带进度条
     */
    public static void postFORM(String url, String token, File file, OnDataListener dataListener, String... kv) {
        if (file == null) {
            return;
        }
        RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
        createCall(url, token, requestBody, kv).enqueue(new OkHttpCallback(url, dataListener));
    }

    /**
     * post上传Bitmap -- 带进度条
     */
    public static void postFORM(String url, String token, final int index, Bitmap bitmap,
                                final OnProgressListener progressListener, OnDataListener dataListener,
                                String... kv) {
        if (bitmap == null) {
            return;
        }
        final byte[] bytes = BitMapUtil.Bitmap2Bytes(bitmap);
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MultipartBody.FORM;
            }

            @Override
            public long contentLength() {
                return bytes.length;
            }

            @Override
            public void writeTo(@NonNull BufferedSink sink) throws IOException {
                Source source = Okio.source(new ByteArrayInputStream(bytes));
                int count = 0;//已上传长度
                int size;//每次循环上传长度
                Buffer buffer = new Buffer();
                while (true) {
                    size = (int) source.read(buffer, 1024);
                    if (size < 0) {
                        break;
                    } else {
                        sink.write(buffer, size);
                        count += size;
                        if (progressListener != null) {
                            int rate = (int) (count * 100.0 / contentLength());
                            progressListener.onProgress(index, rate);
                        }
                    }
                }
            }
        };
        createCall(url, token, requestBody, kv).enqueue(new OkHttpCallback(url, dataListener));
    }

    /**
     * post上传File -- 带进度条
     */
    public static void postFORM(String url, String token, final int index, final File file,
                                final OnProgressListener progressListener, OnDataListener dataListener,
                                String... kv) {
        if (file == null) {
            return;
        }
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MultipartBody.FORM;
            }

            @Override
            public long contentLength() throws IOException {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source = Okio.source(file);
                int count = 0;//已上传长度
                int size;//每次循环上传长度
                Buffer buffer = new Buffer();
                while (true) {
                    size = (int) source.read(buffer, 1024);
                    if (size < 0) {
                        break;
                    } else {
                        sink.write(buffer, size);
                        count += size;
                        if (progressListener != null) {
                            int rate = (int) (count * 100.0 / contentLength());
                            progressListener.onProgress(index, rate);
                        }
                    }
                }
            }
        };
        createCall(url, token, requestBody, kv).enqueue(new OkHttpCallback(url, dataListener));
    }

    /**
     * 创建Call对象 - 上传multipart
     */
    private static Call createCall(String url, String token, RequestBody requestBody, String... kv) {
        if (kv.length % 2 != 0) {//键值对不匹配
            throw new RuntimeException("createCall: 请求参数不匹配");
        }
        MultipartBody.Builder mb = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (int i = 0; i < kv.length / 2; i++) {
            mb.addFormDataPart(kv[i * 2], kv[i * 2 + 1]);
        }
        MultipartBody body = mb.addFormDataPart("file",
                System.currentTimeMillis() + ".jpg", requestBody)
                .build();
        Request.Builder builder = new Request.Builder().url(url);
        if (!TextUtils.isEmpty(token)) {
            builder.addHeader(HEADER_NAME[0], token);
        }
        return okHttpClient.newCall(builder.post(body).build());
    }

    /**
     * 结果回调
     */
    static class OkHttpCallback implements Callback {

        private String url;
        private OnDataListener dataListener;

        public OkHttpCallback(String url, OnDataListener dataListener) {
            this.url = url;
            this.dataListener = dataListener;
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            onFail(dataListener, url, e);
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//            Log.e("LogTC1", "response: " + response.body());
//            url.equals("http://restapi.dibic.net/user/v1/currency/coinIntro")
            onResp(dataListener, url, response.body().string(), getSession(response.headers()));
        }

        private String getSession(Headers headers) {
            String s;
            try {
                List<String> cookies = headers.values("Set-Cookie");
                String session = cookies.get(0);
                s = session.substring(0, session.indexOf(";"));
            } catch (Exception e) {
                s = null;
            }
            return s;
        }
    }

    private static void onFail(final OnDataListener dataListener, final String url, final IOException e) {
        handler.post(() -> {
            try {
                if (dataListener != null) {
                    Log.e("loge", "onFailure: " + url + "\n" + e.getMessage());
                    dataListener.onFailure(url, e.getMessage());
                }
            } catch (Exception e1) {
                Log.e("loge", "E: " + e1.getMessage());
            }
        });
    }

    private static void onResp(final OnDataListener dataListener, final String url, final String json,
                               final String session) {
        handler.post(() -> {
            try {
                if (dataListener != null) {
                    if (!TextUtils.isEmpty(json)) {
//                        Log.e("LogTC2","json-->>" + json);
                        dataListener.onResponse(url, json, session);
                    } else {
                        dataListener.onFailure(url, "Return json is EMPTY!!!");
                    }
                }
            } catch (Exception e) {
                Log.e("loge", "E: " + e.getMessage() + "\n" + json);
            }
        });
    }


    public interface OnDataListener {
        void onResponse(String url, String json, String session);
        void onFailure(String url, String error);
    }

    public interface OnBytesListener {
        void onResponse(String url, byte[] bytes);
        void onFailure(String url, String error);
    }

    /*public interface OnResponseListener {
        void onResponse(String url, ResponseBody body);
        void onFailure(String url, String error);
    }*/

    public interface OnProgressListener {
        void onProgress(int index, int rate);
    }
}

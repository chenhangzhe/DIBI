package cn.suozhi.DiBi.common.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

/**
 * WebSocket工具类
 */
public class SocketUtil {

    /**
     * 初始化Client
     */
    public static WebSocketClient client(String url, OnSocketListener listener) {
        WebSocketClient client = new WebSocketClient(URI.create(url)) {
            @Override
            public void onOpen(ServerHandshake handshake) {
//                Log.e("loge", "onOpen: ");
                if (listener != null) {
                    listener.onOpen(handshake);
                }
            }

            @Override
            public void onMessage(String message) {
                Log.e("loge", "onMessage: " + message);
            }

            @Override
            public void onMessage(ByteBuffer bytes) {
//                Log.e("loge", "onByteBuffer: " + bytes);
                if (listener != null) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        try {
                            listener.onMessage(bytes);
                        } catch (Exception e) {
                            Log.e("loge", "E: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.e("loge", "onClose: " + reason);
                if (listener != null) {
                    new Handler(Looper.getMainLooper()).post(() -> listener.onClose(reason));
                }
            }

            @Override
            public void onError(Exception ex) {
                Log.e("loge", "onError: " + ex.getMessage());
                onClose(0, ex.getMessage(), false);
            }
        };
        return client;
    }

    /**
     * Client是否链接
     */
    public static boolean isConnect(WebSocketClient client) {
        return client != null && client.getConnection().isOpen();
    }

    public static void close(WebSocketClient client) {
        if (isConnect(client)) {
            client.close();
        }
    }

    public abstract static class OnSocketListener {
        public void onOpen(ServerHandshake handshake){}
        public void onMessage(String message){}
        public void onMessage(ByteBuffer bytes) throws Exception {}
        public void onClose(String message){}
    }
}

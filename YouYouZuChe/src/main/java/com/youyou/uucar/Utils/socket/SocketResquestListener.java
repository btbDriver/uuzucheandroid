package com.youyou.uucar.Utils.socket;

/**
 * Created by taurusxi on 14-8-16.
 */
public interface SocketResquestListener {

    // 成功
    public static final int RESULT_CODE_OK = 0;

    // HTTP失败
    public static final int RESULT_CODE_HTTP_ERROR = -1;

    // 解析回包失败
    public static final int RESULT_CODE_DECODE_ERROR = -2;

    // 没有网络
    public static final int RESULT_CODE_NETWORK_ERROR = -3;

    //长连接没有连接
    public static final int RESULT_CODE_SOCKET_NOT_CONNECT = -4;

    void onApiResponse(int result, int errorCode, byte[] response, byte[] request);

    void onSocketApiResponse(int seq, int result, int errorCode, byte[] response, byte[] request);

}

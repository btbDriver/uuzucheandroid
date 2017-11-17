package com.youyou.uucar.Utils.socket;

/**
 * socket的返回
 *
 * @author philzhang
 */
public class SocketResponse {


    public byte[] responseData;

    /**
     * 协议的头
     */
    public SocketStreamConnHead streamHeader;

    public byte[] A2Key;
}

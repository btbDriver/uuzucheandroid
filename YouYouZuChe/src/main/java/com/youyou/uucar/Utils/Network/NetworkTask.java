package com.youyou.uucar.Utils.Network;

import android.content.Context;

/**
 * Created by taurusxi on 14-8-6.
 */
public class NetworkTask {
    public static String HTTPS_TYPE = "https://";
    public static String HTTP_TYPE = "http://";
    //    public static String BASEURL = "115.28.82.160"; //测试环境
    public static String BASEURL = "42.96.249.15"; //正式环境

    public interface Method {
        int DEPRECATED_GET_OR_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    public static final int DEFALT_METHOD = Method.POST;
    public static final String DEFALT_TAG = "Http_connect";
    public static final int CAMERA_TYPE = 1;
    public static final int PROTOBUF_TYPE = 2;
    public static final String PUBLIC_SSL = HTTPS_TYPE + BASEURL + ":8083/public";
    public static final String PUBLIC_HTTP = HTTP_TYPE + BASEURL + ":8081/public";
    public static final String LOGIN_HTTP = HTTP_TYPE + BASEURL + ":8081";
    public static final String LOGIN_SSL = HTTPS_TYPE + BASEURL + ":8083";
    public static final String PHOTO_UPLOAD_HTTP = HTTP_TYPE + BASEURL + ":28080/fileupload/fileupload.f";

    public static final int SSL_NETWORK = 1;  //走 SSL
    public static final int PUBLIC_NETWORK = 2; // 走 public
    public static final int HTTP_NETWORK = 3; // 走 http
    private int seq;


    private int method;
    private int type;
    private UUParams uuParams;
    private Context context;
    private String httpUrl;
    private String tag;
    private byte[] busiData;
    private boolean isUsePublic = false;


    public NetworkTask(UUParams uuParams) {
        this.httpUrl = PHOTO_UPLOAD_HTTP;
        method = DEFALT_METHOD;
        tag = DEFALT_TAG;
        this.uuParams = uuParams;
        ua = DEFEALT_UA;
        this.type = CAMERA_TYPE;
        this.isUsePublic = false;
    }

    public NetworkTask(int cmd) {
        method = DEFALT_METHOD;
        tag = DEFALT_TAG;
        ua = DEFEALT_UA;
        this.cmd = cmd;
        this.tag = cmd + "";
        this.type = PROTOBUF_TYPE;
        this.isUsePublic = false;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    private String ua;
    public static String DEFEALT_UA = "A_121&ADR&Android4.4.2&qq";
    private int cmd;

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public byte[] getBusiData() {
        return busiData;
    }

    public void setBusiData(byte[] busiData) {
        this.busiData = busiData;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public int getMethod() {
        return method;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public UUParams getUuParams() {
        return uuParams;
    }

    public void setUuParams(UUParams uuParams) {
        this.uuParams = uuParams;
    }

    public boolean isUsePublic() {
        return isUsePublic;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public void setUsePublic(boolean isUsePublic) {
        this.isUsePublic = isUsePublic;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("NetworkTask___DEFALT_METHOD =");
        stringBuilder.append(getMethod() + "_");
        stringBuilder.append("TYPE =");
        stringBuilder.append(getType() + "_");
        stringBuilder.append("CMD =");
        stringBuilder.append(getCmd() + "_");
        stringBuilder.append("TAG=" + getTag());
        return stringBuilder.toString();
    }
}

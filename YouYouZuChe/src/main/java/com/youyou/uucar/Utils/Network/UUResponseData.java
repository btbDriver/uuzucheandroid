package com.youyou.uucar.Utils.Network;

import com.uu.client.bean.head.HeaderCommon;
import com.youyou.uucar.Utils.socket.SocketStreamConnHead;

/**
 * Created by taurusxi on 14-8-29.
 */
public class UUResponseData {


    public SocketStreamConnHead getHead() {
        return head;
    }

    public void setHead(SocketStreamConnHead head) {
        this.head = head;
    }

    private SocketStreamConnHead head;

    private int ret; // 0代表接口调用成功

    private int cmd;

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    private int seq;


    public HeaderCommon.ResponseCommonMsg getResponseCommonMsg() {
        return responseCommonMsg;
    }

    public void setResponseCommonMsg(HeaderCommon.ResponseCommonMsg responseCommonMsg) {
        this.responseCommonMsg = responseCommonMsg;
    }

    private HeaderCommon.ResponseCommonMsg responseCommonMsg;
    private String toastMsg; // 接口返回的吐司

    private byte[] busiData; // 实际的业务数据

    private boolean isHttp;// 区别  SSL  和 http ，判断是否需要加密

    public boolean isHttp() {
        return isHttp;
    }

    public void setHttp(boolean isHttp) {
        this.isHttp = isHttp;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getToastMsg() {
        return toastMsg == null ? "" : toastMsg;
    }

    public void setToastMsg(String toastMsg) {
        this.toastMsg = toastMsg;
    }

    public byte[] getBusiData() {
        return busiData;
    }

    public void setBusiData(byte[] busiData) {
        this.busiData = busiData;
    }


}

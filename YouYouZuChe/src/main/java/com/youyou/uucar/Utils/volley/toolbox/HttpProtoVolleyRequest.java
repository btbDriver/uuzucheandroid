

package com.youyou.uucar.Utils.volley.toolbox;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.uu.client.bean.head.HeaderCommon;
import com.youyou.uucar.Utils.Network.AESUtils;
import com.youyou.uucar.Utils.Network.NetworkTask;
import com.youyou.uucar.Utils.Network.UserSecurityConfig;
import com.youyou.uucar.Utils.Network.UserSecurityMap;
import com.youyou.uucar.Utils.Support.MLog;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public abstract class HttpProtoVolleyRequest extends Request<HeaderCommon.ResponsePackage> {
    private final Listener<HeaderCommon.ResponsePackage> mListener;

    /**
     * Creates a new request with the given method.
     *
     * @param method        the request {@link com.android.volley.Request.Method} to use
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public HttpProtoVolleyRequest(int method, String url, Listener<HeaderCommon.ResponsePackage> listener,
                                  ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    /**
     * Creates a new GET request.
     *
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public HttpProtoVolleyRequest(String url, Listener<HeaderCommon.ResponsePackage> listener, ErrorListener errorListener) {
        this(Method.POST, url, listener, errorListener);
    }

    @Override
    protected Response<HeaderCommon.ResponsePackage> parseNetworkResponse(NetworkResponse response) {


        HeaderCommon.ResponsePackage resPackage = null;
        try {
//            byte[] output = AESUtils.decrypt(UserSecurityConfig.b3Key, response.data);
            resPackage = HeaderCommon.ResponsePackage.parseFrom(response.data);
            return Response.success(resPackage, HttpHeaderParser.parseCacheHeaders(response));
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(HeaderCommon.ResponsePackage response) {
        mListener.onResponse(response);

    }

    @Override
    public byte[] getBody() throws AuthFailureError {

        final NetworkTask task = getNetworkTask();
        boolean isPublic = UserSecurityConfig.isPuilc(task) || task.isUsePublic();
        HeaderCommon.CommonReqHeader.Builder headerBuilder = HeaderCommon.CommonReqHeader.newBuilder();
        UserSecurityMap.SecurityItem securityItem = new UserSecurityMap.SecurityItem(task, isPublic, UserSecurityConfig.b3Key_ticket, UserSecurityConfig.b2_ticket, UserSecurityConfig.b3_ticket, UserSecurityConfig.userId_ticket);
        UserSecurityMap.put(task.getSeq(), securityItem);
        headerBuilder.setSeq(task.getSeq());
        headerBuilder.setCmd(task.getCmd());
        if (isPublic) {
//            headerBuilder.setB2(ByteString.copyFrom(UserSecurityConfig.b2));
        } else {
            headerBuilder.setB2(ByteString.copyFrom(securityItem.b2Item));
        }
        headerBuilder.setUuid(UserSecurityConfig.UUID);
        headerBuilder.setUa(task.getUa());
        HeaderCommon.RequestData.Builder requestData = HeaderCommon.RequestData.newBuilder();
        requestData.setBusiData(ByteString.copyFrom(task.getBusiData()));
        requestData.setHeader(headerBuilder.build());
        HeaderCommon.RequestPackage.Builder prpBuilder = HeaderCommon.RequestPackage.newBuilder();
        if (isPublic) {
            prpBuilder.setUserId(0);
//            prpBuilder.setB3(ByteString.copyFrom(UserSecurityConfig.b3));
            prpBuilder.setReqData(ByteString.copyFrom(AESUtils.encrypt(UserSecurityConfig.b3Key, requestData.build().toByteArray())));
        } else {
            prpBuilder.setUserId(securityItem.userIdItem);

            prpBuilder.setB3(ByteString.copyFrom(securityItem.b3Item));
            prpBuilder.setReqData(ByteString.copyFrom(AESUtils.encrypt(securityItem.b3KeyItem, requestData.build().toByteArray())));
        }

        MLog.e("TAG", "UUID:" + UserSecurityConfig.UUID + "___UA:" + task.getUa());

        MLog.e("TAG", "request__seq:" + task.getSeq() + "__cmd:" + task.getCmd() + "___id:" + prpBuilder.getUserId() + " networkItem = " + task.toString() + "  b3  =" + prpBuilder.getB3());
        byte[] toSendData = prpBuilder.build().toByteArray();
        return toSendData;
    }

    public abstract NetworkTask getNetworkTask();

}

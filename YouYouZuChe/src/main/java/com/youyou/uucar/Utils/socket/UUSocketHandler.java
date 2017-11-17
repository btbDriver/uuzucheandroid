package com.youyou.uucar.Utils.socket;

import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.uu.client.bean.head.HeaderCommon;
import com.youyou.uucar.Utils.Network.AESUtils;
import com.youyou.uucar.Utils.Network.UUResponseData;
import com.youyou.uucar.Utils.Network.UserSecurityConfig;
import com.youyou.uucar.Utils.Support.Config;
import com.youyou.uucar.Utils.Support.MLog;
import com.youyou.uucar.Utils.Support.SysConfig;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class UUSocketHandler {

    private SocketStreamConnHead header;
    //    private int offset = 0;
//    private int totalLen = 0;
//    private int startOffset = 0;
    private static final int HEAD_SIZE = 4;
    private ByteBuffer headerBuffer;
    private ByteBuffer recDataBuffer;
    private SocketCommunication mSocketComminucator;
    private static final Logger logger = LoggerFactory.getLogger("SocketCommunication");

    public UUSocketHandler(SocketCommunication socketComminucator) {
        headerBuffer = ByteBuffer.allocate(4);
        this.mSocketComminucator = socketComminucator;
    }

    public void write(SocketChannel channel, byte[] data) {
        if (data != null) {
            ByteBuffer bytebuffer = ByteBuffer.wrap(data);
            int len = 0;
            try {
                len = channel.write(bytebuffer);
                if (len == -1) {
                    loggerSdCard("write————= -1");
                    MLog.e("SocketCommunication", "write————= -1");
                    mSocketComminucator.reset();

                }
            } catch (IOException e) {
                e.printStackTrace();
                loggerSdCard("write异常");
                MLog.e("SocketCommunication", "write异常");
                mSocketComminucator.reset();
            }
        }
    }

    public void read(SelectionKey selectionKey, SocketChannel channel, SocketCommunication.Listener mListener) throws IOException {
        int offset = 0;
        int totalLen = 0;
        int startOffset = 0;
        if (offset < 4) {
            if (offset == 0) {
                headerBuffer.clear();
            }
            int len = channel.read(headerBuffer);
            if (len == -1) {
                if (mSocketComminucator != null) {
                    loggerSdCard("read__111__= -1");
                    MLog.e("SocketCommunication", "read__111__= -1");
                    mSocketComminucator.reset();
                    if (mSocketComminucator.mConnectListener != null) {
                        mSocketComminucator.mConnectListener.closeConnect();
                    }
                }
                return;
            }
            offset += len;
            headerBuffer.flip();

            if (offset == 4) {
                byte[] headerdata = headerBuffer.array();
                header = getStreamConnHeader(headerdata);
                offset = HEAD_SIZE - startOffset;
                totalLen = header.dwLength;
                recDataBuffer = ByteBuffer.allocate(header.dwLength);
                recDataBuffer.clear();
            }
        }

        if (totalLen > 0 && offset < totalLen) {
            int len = channel.read(recDataBuffer);
            if (len == -1) {
                if (mSocketComminucator != null) {
                    loggerSdCard("read__222__= -1");
                    MLog.e("SocketCommunication", "read__222__= -1");
                    mSocketComminucator.reset();
                    if (mSocketComminucator.mConnectListener != null) {
                        mSocketComminucator.mConnectListener.closeConnect();
                    }
                }
                return;
            }
            offset += len;
        }

        if (totalLen > 0 && offset >= totalLen) {

            try {
                //从服务端读取数据
                byte[] response = recDataBuffer.array();
//                SocketResponse socketResponse = new SocketResponse();
//                socketResponse.responseData = response;
                UUResponseData uuResponseData = new UUResponseData();
                uuResponseData.setHead(header);
                HeaderCommon.ResponsePackage responsePackage = HeaderCommon.ResponsePackage.parseFrom(response);
                loggerSdCard("responsePackage:ret" + responsePackage.getRet() + "__seq:" + responsePackage.getSeq());
                MLog.e("SocketCommunication", "responsePackage:ret" + responsePackage.getRet() + "__seq:" + responsePackage.getSeq());
                if (responsePackage.getRet() == 0) {
                    //成功
                    if (responsePackage.getSeq() == -1) {
                        //长连接PUSH
                        HeaderCommon.ResponseData responseData = HeaderCommon.ResponseData.parseFrom(AESUtils.decrypt(UserSecurityConfig.b3Key_ticket, responsePackage.getResData().toByteArray()));
                        int cmd = responseData.getCmd();
                        uuResponseData.setCmd(cmd);
                        uuResponseData.setSeq(-1);
                        uuResponseData.setRet(0);
                        uuResponseData.setBusiData(responseData.getBusiData().toByteArray());
                        mListener.onReader(3, true, uuResponseData);
                    } else {
                        //心跳,握手协议返回
                        HeaderCommon.ResponseData responseData = HeaderCommon.ResponseData.parseFrom(AESUtils.decrypt(UserSecurityConfig.b3Key_ticket, responsePackage.getResData().toByteArray()));
                        int cmd = responseData.getCmd();
                        uuResponseData.setCmd(cmd);
                        uuResponseData.setSeq(responsePackage.getSeq());
                        uuResponseData.setRet(0);
                        uuResponseData.setBusiData(responseData.getBusiData().toByteArray());
                        mListener.onReader(3, false, uuResponseData);

                    }
                } else if (responsePackage.getRet() == -11) {
                    loggerSdCard( "返回-11，关闭长连接");
                    MLog.e("SocketCommunication", "返回-11，关闭长连接");
                    mSocketComminucator.close();


                } else if (responsePackage.getRet() == -13) {
                    loggerSdCard( "返回-13，关闭长连接");
                    MLog.e("SocketCommunication", "返回-13，关闭长连接");
                    mSocketComminucator.close();

                } else if (responsePackage.getRet() == -12) {
                    loggerSdCard( "返回-12，关闭长连接");
                    MLog.e("SocketCommunication", "返回-12，关闭长连接");
                    mSocketComminucator.close();
                }
                header = null;
            } catch (Exception e) {
                e.printStackTrace();
                MLog.e("SocketCommunication", e.getMessage());
                loggerSdCard( "read 读取异常："+ e.getMessage());
                MLog.e("SocketCommunication", "read 读取异常");
                mSocketComminucator.reset();
            }
        }
    }

    public void reset() {
        header = null;
//        totalLen = 0;
//        offset = 0;
//        startOffset =0;
    }


    public static SocketStreamConnHead getStreamConnHeader(byte[] result) {
        if (result.length < 4) {
            return null;
        }
        SocketStreamConnHead header = new SocketStreamConnHead();
        ByteArrayInputStream bais = null;
        DataInputStream dis = null;
        try {
            bais = new ByteArrayInputStream(result);
            dis = new DataInputStream(bais);

            int length = dis.readInt();
//            byte[] szMagic = new byte[4];
//            dis.read(szMagic);
//
//            short verison = dis.readShort();
//            int seq = dis.readInt();
//            byte cType = dis.readByte();
//            byte cSubType = dis.readByte();
//            dis.skip(16);
            header.dwLength = length;
//            header.szMagic = szMagic;
//            header.version = verison;
//            header.dwSeq = seq;
//            header.cType = cType;
//            header.cSubType = cSubType;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bais != null) {
                try {
                    bais.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return header;
    }

    private void loggerSdCard(String msg) {
        if (SysConfig.DEVELOP_MODE) {
            logger.debug(Config.getCurrentTime() + " " + msg);
        }
    }

}


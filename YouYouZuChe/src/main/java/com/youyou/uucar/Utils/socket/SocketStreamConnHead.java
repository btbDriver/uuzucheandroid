package com.youyou.uucar.Utils.socket;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SocketStreamConnHead {

    public SocketStreamConnHead() {

    }

    public SocketStreamConnHead(int dwLength, byte[] szMagic, short version, int dwSeq, byte cType, byte cSubType) {
        this.dwLength = dwLength;
//        this.szMagic = szMagic;
//        this.version = version;
//        this.dwSeq = dwSeq;
//        this.cType = cType;
//        this.cSubType = cSubType;
    }

    /**
     * 整个包长
     */
    public int dwLength;

//    /** TXWB 4个自己 */
//    public byte[] szMagic;

//    /** 版本号 */
//    public short version;

//    /** 序列号 */
//    public int dwSeq;

//    /** 通道类型 1：数据Api通道，2：push通道 */
//    public byte cType;

//    /** 链路控制包，1：握手包，2：api数据包 */
//    public byte cSubType;
//
//    /** 16个字节的预留字段 */
//    public byte[] szResever = new byte[16];

    public byte[] toBytes() {
        ByteArrayOutputStream baos = null;
        DataOutputStream dos = null;
        try {
            baos = new ByteArrayOutputStream();
            dos = new DataOutputStream(baos);
            dos.writeInt(dwLength);
//            dos.write(szMagic);
//            dos.writeShort(version);
//            dos.writeInt(dwSeq);
//            dos.writeByte(cType);
//            dos.writeByte(cSubType);
//            dos.write(szResever);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "dwLength:" + dwLength;
    }
//
//    public boolean isPush(){
//        return cSubType == 3;
//    }
}

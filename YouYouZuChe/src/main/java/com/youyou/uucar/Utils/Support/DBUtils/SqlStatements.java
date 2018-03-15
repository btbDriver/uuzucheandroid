package com.youyou.uucar.Utils.Support.DBUtils;

/**
 * 专门存放建表的sql语句
 *
 * @author Administrator
 */
public class SqlStatements {
    // 创建标准代码表
    public static final String CREATE_P_CODE_TABLE = "CREATE TABLE IF NOT EXISTS P_CODE ("
            + "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + "CODE_ID      NUMBER(16) not null,"
            + "CODE_SORT_ID NUMBER(16) not null,"
            + "P_CODE       VARCHAR2(16),       "
            + "CODE_TYPE    VARCHAR2(16),       "
            + "ORG_NO       VARCHAR2(16),       "
            + "VALUE        VARCHAR2(256),      "
            + "NAME         VARCHAR2(256),      "
            + "DISP_SN      NUMBER(5),          "
            + "CONTENT1     VARCHAR2(256),      "
            + "CONTENT2     VARCHAR2(256),      "
            + "CONTENT3     VARCHAR2(256),      "
            + "CONTENT4     VARCHAR2(256),      "
            + "CONTENT5     VARCHAR2(256) )      ";

}

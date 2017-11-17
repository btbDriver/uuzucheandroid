package com.youyou.uucar.Utils.Support.DBUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * sqlite数据存储，存储抄表下载的数据
 *
 * @author jimmy
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "WebService";

    // 数据库文件目标存放路径为系统默认位置，pad.android.marketing包名
    public static String SYSDB_PATH = "/data/data/com.youyou.uucar/databases/";
    //    public static String SYSDB_PATH = Environment.";
    // 把数据库文件存放在SD卡
    public static String SD_DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/youyou/database/";
    public static String path = "";

    // 数据库版本
    public static final int VERSION = 1;
    public static final int newVersion = 2;

    // 数据库名称
    public static final String DATABASE_NAME = "uuzuche_pb.db";
    public static final String ASSETS_NAME = "uuzuche_pb.db";

    private final Context myContext;

    public DataBaseHelper(Context context, String name,
                          CursorFactory factory, int version) {
        super(context, name, null, version);
        this.myContext = context;
    }

    public DataBaseHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public DataBaseHelper(Context context, String name) {
        this(context, name, VERSION);
    }

    public DataBaseHelper(Context context) {
        this(context, checkSdCard());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SqlStatements.CREATE_P_CODE_TABLE);
        Log.e(TAG, "onCreate:表");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
        Log.e(TAG, "onUpgrade:更新表");
    }

    /**
     * 判断是否存在内存卡
     *
     * @return
     */
    public static String checkSdCard() {
        // 判断sdcard是否存在
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// SdCard存在并且可以进行读写
//            path = SD_DB_PATH + DATABASE_NAME;
//        } else {
//            path = SYSDB_PATH + DATABASE_NAME;
//        }
        path = SYSDB_PATH + DATABASE_NAME;
        return path;
    }

    /**
     * 复制assets下的小数据库文件时用的方法
     *
     * @throws java.io.IOException
     */
    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(ASSETS_NAME);
        String outFileName = SD_DB_PATH + DATABASE_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

}

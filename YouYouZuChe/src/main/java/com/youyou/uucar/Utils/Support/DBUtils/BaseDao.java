package com.youyou.uucar.Utils.Support.DBUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.util.ArrayList;
import java.util.List;

public class BaseDao {

    /**
     * 数据库的帮助类，一般一个数据库对应一个该类，用于做初始化（如建表删表、数据库升级）的动作
     */
    private SQLiteOpenHelper dbHelper;
    /**
     * 存储在SD卡数据库文件的路径
     */
    private String dbPath;

    /**
     * 数据库操作类
     */
    public SQLiteDatabase db;

    /**
     * 构造函数
     *
     * @param context
     */
    public BaseDao(Context context) {
        this.dbHelper = new DataBaseHelper(context);
        this.dbPath = DataBaseHelper.checkSdCard();
    }

    /**
     * 构造函数
     *
     * @param dbHelper
     */
    public BaseDao(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.dbPath = DataBaseHelper.checkSdCard();
    }

    /**
     * 根据是否存在内存卡获取数据库操作类SQLiteDatabase
     *
     * @return mDb
     */
    public SQLiteDatabase getSqlite() {
        SQLiteDatabase mDb = null;
        // 判断sdcard是否存在
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {
//            // 将数据库的文件创建在SD卡中，那么创建数据库mDb如下操作：
//            mDb = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
//        } else {
        // 把数据库文件默认放在系统中,那么创建数据库mDb如下操作
        mDb = dbHelper.getWritableDatabase();
//        }
        return mDb;
    }

    /**
     * 获取某个数据库表对应的VO对象
     *
     * @param sql   sql语句
     * @param clazz VO类的class
     * @return
     */
    public Object getModel(String sql, Class clazz) {
        db = getSqlite();
        Object object = null;
        try {
            object = DaoUtil.sql2VO(db, sql, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return object;
    }

    /**
     * 获取某个数据库表对应的VO对象
     *
     * @param sql           sql语句
     * @param clazz         VO类的class
     * @param selectionArgs ?值的数组
     * @return
     */
    public Object getModel(String sql, Class clazz, String[] selectionArgs) {
        db = getSqlite();
        Object object = null;
        try {
            object = DaoUtil.sql2VO(db, sql, selectionArgs, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return object;
    }

    /**
     * 获取sql对应的VO对象List
     *
     * @param sql
     * @param clazz
     * @return
     */
    public List getModelList(String sql, Class clazz) {
        db = getSqlite();
        List result = new ArrayList();
        try {
            result = DaoUtil.sql2VOList(db, sql, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return result;
    }

    /**
     * 获取sql对应的VO对象List
     *
     * @param sql
     * @param clazz
     * @param selectionArgs
     * @return
     */
    public List getModelList(String sql, Class clazz, String[] selectionArgs) {
        db = getSqlite();
        List result = new ArrayList();
        try {
            result = DaoUtil.sql2VOList(db, sql, selectionArgs, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return result;
    }

    /**
     * 执行sql语句获取对应的Cursor对象
     *
     * @param sql
     * @param selectionArgs
     * @return
     */
    public Cursor executeQuery(String sql, String[] selectionArgs) {
        Cursor cur = null;
        try {
            db = getSqlite();
            cur = db.rawQuery(sql, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cur;
    }

    /**
     * 执行sql语句获取对应的Cursor对象
     *
     * @param sql
     * @param selectionArgs
     * @return
     */
    public Cursor executeQuery2(String sql, String[] selectionArgs) {
        Cursor cur = null;
        try {
            cur = db.rawQuery(sql, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cur;
    }

    /**
     * 记录插入数据库，入参为表名及参数值集合
     *
     * @param tableName
     * @param cv
     * @return
     */
    public boolean insert(String tableName, ContentValues cv) {
        boolean flag = true;
        long result = 0;
        try {
            db = getSqlite();
            result = db.insert(tableName, null, cv);
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        } finally {
            if (db != null) {
                db.close();
            }
            if (result == -1) {
                flag = false;
            }

        }
        return flag;
    }

    /**
     * 增、删、改都可用的函数
     *
     * @param sql
     * @param bindArgs
     * @return
     */
    public boolean saveOrUpdateOrDel(String sql, String[] bindArgs) {
        boolean flag = true;
        try {
            db = getSqlite();
            db.execSQL(sql, bindArgs);
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return flag;
    }

    /**
     * 修改数据库记录
     *
     * @param tableName   表名
     * @param cv          参数值集合
     * @param whereClause where语句
     * @param whereArgs   where语句对应的参数值
     * @return
     */
    public boolean update(String tableName, ContentValues cv,
                          String whereClause, String[] whereArgs) {
        boolean flag = true;
        try {
            db = getSqlite();
            db.update(tableName, cv, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return flag;
    }

    /**
     * 删除记录
     *
     * @param tableName   表名
     * @param whereClause where语句
     * @param whereArgs   where语句对应的参数值
     * @return
     */
    public boolean delete(String tableName, String whereClause,
                          String[] whereArgs) {
        boolean flag = true;
        try {
            db = getSqlite();
            db.delete(tableName, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return flag;
    }
}

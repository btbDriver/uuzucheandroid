package com.youyou.uucar.Utils.Support.DBUtils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 通过SQL语句查询出结果并封闭到VO里
 * 使用时需要注意：
 * 考虑到Android的性能问题，VO没有使用Setter和Getter，而是直接用public的属性。
 * VO的属性命名规则：和公司web框架的命名一样，把'_'去掉，把下一个字符变成大写，如org_no 命名就是orgNo
 */
public class DaoUtil {

    /**
     * 通过SQL语句获得对应的VO。注意：VO的属性命名规则：和公司web框架的命名一样
     *
     * @param db
     * @param sql
     * @param clazz
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static Object sql2VO(SQLiteDatabase db, String sql, Class clazz) throws Exception {
        Cursor c = db.rawQuery(sql, null);
        return cursor2VO(c, clazz);
    }

    /**
     * 通过SQL语句获得对应的VO。注意：VO的属性命名规则：和公司web框架的命名一样
     *
     * @param db
     * @param sql
     * @param selectionArgs
     * @param clazz
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static Object sql2VO(SQLiteDatabase db, String sql,
                                String[] selectionArgs, Class clazz) throws Exception {
        Cursor c = db.rawQuery(sql, selectionArgs);
        return cursor2VO(c, clazz);
    }

    /**
     * 通过SQL语句获得对应的VO的List。注意：VO的属性命名规则：和公司web框架的命名一样
     *
     * @param db
     * @param sql
     * @param clazz
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static List sql2VOList(SQLiteDatabase db, String sql, Class clazz) throws Exception {
        Cursor c = db.rawQuery(sql, null);
        return cursor2VOList(c, clazz);
    }

    /**
     * 通过SQL语句获得对应的VO的List。注意：VO的属性命名规则：和公司web框架的命名一样
     *
     * @param db
     * @param sql
     * @param selectionArgs
     * @param clazz
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static List sql2VOList(SQLiteDatabase db, String sql,
                                  String[] selectionArgs, Class clazz) throws Exception {
        Cursor c = db.rawQuery(sql, selectionArgs);
        return cursor2VOList(c, clazz);
    }

    /**
     * 通过Cursor转换成对应的VO。注意：VO的属性命名规则：和公司web框架的命名一样
     *
     * @param c
     * @param clazz
     * @return
     */
    @SuppressWarnings({"rawtypes", "unused"})
    private static Object cursor2VO(Cursor c, Class clazz) throws Exception {
        if (c == null) {
            return null;
        }
        Object obj;
        int i = 1;
        try {
            if (c.moveToNext()) {
                obj = setValues2Fields(c, clazz);
            } else {
                obj = clazz.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj = null;
            throw e;
        } finally {
            c.close();
        }
        return obj;
    }

    /**
     * 通过Cursor转换成对应的VO集合。注意：VO的属性命名规则：和公司web框架的命名一样
     *
     * @param c
     * @param clazz
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static List cursor2VOList(Cursor c, Class clazz) throws Exception {
        if (c == null) {
            return null;
        }
        List list = new LinkedList();
        Object obj;
        try {
            while (c.moveToNext()) {
                obj = setValues2Fields(c, clazz);
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            c.close();
        }
        return list;
    }

    /**
     * 把值设置进类属性里
     *
     * @param columnNames
     * @param fields
     * @param c
     * @param obj
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    private static Object setValues2Fields(Cursor c, Class clazz)
            throws Exception {
        String[] columnNames = c.getColumnNames();// 字段数组
        Object obj = clazz.newInstance();
        Field[] fields = clazz.getFields();

        for (Field _field : fields) {
            Class<? extends Object> typeClass = _field.getType();// 属性类型
            for (int j = 0; j < columnNames.length; j++) {
                String columnName = columnNames[j];
                typeClass = getBasicClass(typeClass);
                boolean isBasicType = isBasicType(typeClass);
                String column = columnName;//如果数据库字段名称包含_，就去掉，以便和类属性比较时能一致。
                if (columnName.contains("_")) {
                    column = columnName.replace("_", "");
                }
                if (isBasicType) {
                    if (column.equalsIgnoreCase(_field.getName())) {// 是基本类型  
                        String _str = c.getString(c.getColumnIndex(columnName));
                        if (_str == null) {
                            break;
                        }
                        _str = _str == null ? "" : _str;
                        Constructor<? extends Object> cons = typeClass
                                .getConstructor(String.class);
                        Object attribute = cons.newInstance(_str);
                        _field.setAccessible(true);
                        _field.set(obj, attribute);
                        break;
                    }
                } else {
                    Object obj2 = setValues2Fields(c, typeClass);// 递归
                    _field.set(obj, obj2);
                    break;
                }

            }
        }
        return obj;
    }

    /**
     * 判断是不是基本类型
     *
     * @param typeClass
     * @return
     */
    @SuppressWarnings("rawtypes")
    private static boolean isBasicType(Class typeClass) {
        if (typeClass.equals(Integer.class) || typeClass.equals(Long.class)
                || typeClass.equals(Float.class)
                || typeClass.equals(Double.class)
                || typeClass.equals(Boolean.class)
                || typeClass.equals(Byte.class)
                || typeClass.equals(Short.class)
                || typeClass.equals(String.class)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获得包装类
     *
     * @param typeClass
     * @return
     */
    @SuppressWarnings("all")
    public static Class<? extends Object> getBasicClass(Class typeClass) {
        Class _class = basicMap.get(typeClass);
        if (_class == null)
            _class = typeClass;
        return _class;
    }

    @SuppressWarnings("rawtypes")
    private static Map<Class, Class> basicMap = new HashMap<Class, Class>();

    static {
        basicMap.put(int.class, Integer.class);
        basicMap.put(long.class, Long.class);
        basicMap.put(float.class, Float.class);
        basicMap.put(double.class, Double.class);
        basicMap.put(boolean.class, Boolean.class);
        basicMap.put(byte.class, Byte.class);
        basicMap.put(short.class, Short.class);
    }
}  



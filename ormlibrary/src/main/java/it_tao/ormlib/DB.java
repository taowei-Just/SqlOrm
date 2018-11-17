package it_tao.ormlib;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it_tao.ormlib.util.ClassUtil;
import it_tao.ormlib.util.CursorUtils;
import it_tao.ormlib.util.SQLBuilder;

public class DB {
    private static final boolean DEBUG = true;
    private static final int VERSION = 1;
    private static DB myDb;
    private static SQLiteHelpder sqliteHelpder;
    private Context mContext;
    private SQLiteDatabase db;
    private String dbName;
    private String tableName = null;

    public static DB getDb() {
        return myDb;
    }

    public DB(Context context, String dbFolder, String dbName) {
        this.mContext = context;
        this.dbName = dbName;
        sqliteHelpder = new SQLiteHelpder(context, dbFolder, dbName);
        this.db = sqliteHelpder.getWritableDatabase();
    }

    public DB(Context context, String dbFolder, String dbName, String tableName) {
        this.mContext = context;
        this.dbName = dbName;
        this.tableName = tableName;
        sqliteHelpder = new SQLiteHelpder(context, dbFolder, dbName);
        this.db = sqliteHelpder.getWritableDatabase();
    }

//    public static synchronized DBName getInstance(Context context) {
//        if ((myDb == null) || ((myDb != null) && (myDb.db != null) && (!myDb.db.isOpen()))) {
//            myDb = new DBName(context);
//        }
//        return myDb;
//    }
//
//    public static synchronized DBName getInstance(Context context, String dbFolder) {
//        if ((myDb == null) || ((myDb != null) && (myDb.db != null) && (!myDb.db.isOpen()))) {
//            myDb = new DBName(context, dbFolder);
//        }
//        return myDb;
//    }
//
//    public static synchronized DBName getInstance(Context context, String dbFolder, String tableName) {
//        if ((myDb == null) || ((myDb != null) && (myDb.db != null) && (!myDb.db.isOpen()))) {
//            myDb = new DBName(context, dbFolder, tableName);
//        }
//        return myDb;
//    }

    public void beginTransaction() {
        this.db.beginTransaction();
    }

    public void commitTransaction() {
        this.db.setTransactionSuccessful();
        this.db.endTransaction();
    }

    public void save(Object entity) {
        checkTableExist(entity.getClass());
        checkFaildExist(entity.getClass());

        String sql;
        if (TextUtils.isEmpty(tableName)) {
            sql = SQLBuilder.getInsertSQL(entity);
        } else {

            sql = SQLBuilder.getInsertSQL(entity, tableName);
        }
        execSQL(sql);
    }

    public void delete(Object entity) {
        checkTableExist(entity.getClass());
        checkFaildExist(entity.getClass());
        String deleteSQ;
        if (TextUtils.isEmpty(tableName))
            deleteSQ = SQLBuilder.getDeleteSQL(entity);
        else
            deleteSQ = SQLBuilder.getDeleteSQL(entity, tableName);

        execSQL(deleteSQ);

    }

    public <T> void deleteByWhere(Class<T> clazz, String where) {
        checkTableExist(clazz);
        checkFaildExist(clazz);
        String deleteSqlByWhere;
        if (TextUtils.isEmpty(tableName))
            deleteSqlByWhere = SQLBuilder.getDeleteSqlByWhere(clazz, where);
        else
            deleteSqlByWhere = SQLBuilder.getDeleteSqlByWhere(clazz, where, tableName);

        execSQL(deleteSqlByWhere);

    }

    public <T> void deleteAll(Class<T> clazz) {
        checkTableExist(clazz);
        
        execSQL(SQLBuilder.getDeletAllSQL(clazz));
    }

    public <T> void deleteAll() {
        checkTableExist(tableName.getClass());
        execSQL(SQLBuilder.getDeletAllSQL(tableName.getClass()));
    }

    public void update(Object entity) {
        checkTableExist(entity.getClass());
        checkFaildExist(entity.getClass());
        String updateSQL;
        if (TextUtils.isEmpty(tableName))
            updateSQL = SQLBuilder.getUpdateSQL(entity);
        else
            updateSQL = SQLBuilder.getUpdateSQL(entity, tableName);

        execSQL(updateSQL);
    }

    public void update(Object entity, String strWhere) {
        checkTableExist(entity.getClass());
        checkFaildExist(entity.getClass());
        String updateSQLByWhere;
        if (TextUtils.isEmpty(tableName))
            updateSQLByWhere = SQLBuilder.getUpdateSQLByWhere(entity, strWhere);
        else
            updateSQLByWhere = SQLBuilder.getUpdateSQLByWhere(entity, strWhere, tableName);
        execSQL(updateSQLByWhere);
    }


    public <T> void dropTable() {
        Class<T> clazz = (Class<T>) tableName.getClass();
        checkTableExist(clazz);
        String dropTableSQL;
        if (TextUtils.isEmpty(tableName))
            dropTableSQL = SQLBuilder.getDropTableSQL(clazz);
        else
            dropTableSQL = SQLBuilder.getDropTableSQL(clazz, tableName);
        execSQL(dropTableSQL);
        TableInfo table = TableInfo.get(clazz);
        table.setCheckDatabese(false);
    }

    public <T> void dropTable(Class<T> clazz) {
        checkTableExist(clazz);
        String dropTableSQL;
        if (TextUtils.isEmpty(tableName))
            dropTableSQL = SQLBuilder.getDropTableSQL(clazz);
        else
            dropTableSQL = SQLBuilder.getDropTableSQL(clazz, tableName);
        execSQL(dropTableSQL);
        TableInfo table = TableInfo.get(clazz);
        table.setCheckDatabese(false);
    }


    public <T> List<T> findAll(Class<T> clazz) {
        return findAllByWhere(clazz, null);
    }

    public <T> List<T> findAllByWhere(Class<T> clazz, String where) {
        String select;
        checkTableExist(clazz);
        checkFaildExist(clazz);
        if (TextUtils.isEmpty(tableName)) {
            select = SQLBuilder.getSelectSQL(clazz, where);
        } else {
            select = SQLBuilder.getSelectSQL(clazz, where, tableName, false);
        }
        debugSql(select);
        List list = new ArrayList();
        Cursor cursor = this.db.rawQuery(select, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Object entity = CursorUtils.getEntity(cursor, clazz);
                list.add(entity);
            }
        }
        if (cursor != null)
            cursor.close();
        cursor = null;
        return list;
    }


    public <T> List<T> findAllByWhere(Class<T> clazz, String where, String order) {
        checkTableExist(clazz);
        checkFaildExist(clazz);
        String select;
        if (TextUtils.isEmpty(tableName))
            select = SQLBuilder.getSelectSQL(clazz, where, order);
        else
            select = SQLBuilder.getSelectSQL(clazz, where, tableName, order);
        debugSql(select);
        List list = new ArrayList();
        Cursor cursor = this.db.rawQuery(select, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    Object entity = CursorUtils.getEntity(cursor, clazz);
                    list.add(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        if (cursor != null)
            cursor.close();
        cursor = null;
        return list;
    }

    public void execSQL(String sql) {
        if ((this.db == null) || ((this.db != null) && (!this.db.isOpen()))) {
            this.db = sqliteHelpder.getWritableDatabase();
        }
        debugSql(sql);
        this.db.execSQL(sql);
    }

    private void debugSql(String sql) {
        Log.d("KK_ORM sql", sql);
    }

    public synchronized void closeDB() {
        if ((this.db != null) && (this.db.isOpen()) && (sqliteHelpder != null))
            sqliteHelpder.close();
    }

    private <T> boolean cloumIsExist(String tableName, String cloumName) {
        Cursor cursor = null;
        try {
            String sql = "select    count(*) from sqlite_master where type ='table' and name ='" + tableName.trim() + "' and sql like '%" + cloumName + "%'";
            debugSql(sql);
            cursor = this.db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                int anInt = cursor.getInt(0);
                if (anInt > 0) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public <T> boolean tableIsExist(Class<T> clazz) {
        if (TextUtils.isEmpty(tableName))
            return tableIsExist(clazz, ClassUtil.getTableName(clazz));
        else
            return tableIsExist(clazz, tableName);
    }

    HashMap<String, Boolean> checkdTableMap = new HashMap<String, Boolean>();

    private <T> boolean tableIsExist(Class<T> clazz, String tabName) {
        TableInfo table = TableInfo.get(clazz);
        if (isCheckTable(tabName)) {
            return true;
        }
        boolean result = false;
        if (tabName == null) {
            return false;
        }
        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='" + tabName.trim() + "' ";
            debugSql(sql);
            cursor = this.db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                    table.setCheckDatabese(true);
                    checkdTableMap.put(tabName, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (cursor != null)
                cursor.close();
            cursor = null;
        }
        return result;
    }

    /**
     * 判断某表里某字段是否存在
     */
    public <T> void checkFaildExist(Class<T> clazz) {

        String queryStr = "select sql from sqlite_master where type = 'table' and name = '%s'";

        if (!TextUtils.isEmpty(tableName))
            queryStr = String.format(queryStr, tableName);
        else
            queryStr = String.format(queryStr, ClassUtil.getTableName(clazz));

        Cursor c = db.rawQuery(queryStr, null);
        String tableCreateSql = null;
        try {
            if (c != null && c.moveToFirst()) {
                tableCreateSql = c.getString(c.getColumnIndex("sql"));
            }
        } finally {
            if (c != null)
                c.close();
        }
        ArrayList<Field> tablefields = new ArrayList<>();
        if (tableCreateSql != null) {
            Field[] fields = ClassUtil.getDeclaredFields(clazz);
            for (Field field : fields) {
                if (!tableCreateSql.contains(field.getName())) {
                    tablefields.add(field);
                }
            }
        }
        if (tablefields.size() > 0)
            createFields(clazz, tablefields);

    }

    private synchronized <T> void createFields(Class<T> clazz, ArrayList<Field> tablefields) {

        for (Field field : tablefields) {
            String tableSQL;
            if (!TextUtils.isEmpty(tableName)) {
                tableSQL = SQLBuilder.getFieldAtTableSQL(tableName, field);
            } else
                tableSQL = SQLBuilder.getFieldAtTableSQL(ClassUtil.getTableName(clazz), field);

            try {
                debugSql(tableSQL);
                db.execSQL(tableSQL);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private boolean isCheckTable(String tabName) {
        if (checkdTableMap.containsKey(tabName) && checkdTableMap.get(tabName))
            return true;
        return false;
    }

    public <T> void checkTableExist(Class<T> clazz) {
        if (!tableIsExist(clazz)) {
            String createSQL;
            if (TextUtils.isEmpty(tableName))
                createSQL = SQLBuilder.getCreatTableSQL(clazz);
            else
                createSQL = SQLBuilder.getCreatTableSQL(clazz, tableName);
            debugSql(createSQL);
            execSQL(createSQL);
        }
    }


    public void beginTrancation() {
        this.db.beginTransaction();

    }

    public void endTrancation() {
        this.db.setTransactionSuccessful();
        this.db.endTransaction();
    }

    private class SQLiteHelpder extends SQLiteOpenHelper {
        public SQLiteHelpder(Context context) {
            super(context, SQLiteHelpder.class.getName() + ".db", null, 1);
        }

        public SQLiteHelpder(Context context, String dbFolder) {
            super(context, dbFolder + "/" + SQLiteHelpder.class.getName() + ".db", null, 1);
        }

        public SQLiteHelpder(Context context, String dbFolder, String dbName) {
            super(context, dbFolder + "/" + dbName, null, 1);
        }

        public void onCreate(SQLiteDatabase db) {
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

    }

}


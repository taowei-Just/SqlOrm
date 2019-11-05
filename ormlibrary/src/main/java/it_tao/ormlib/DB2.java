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
import java.util.Map;

import it_tao.ormlib.util.ClassUtil;
import it_tao.ormlib.util.CursorUtils;
import it_tao.ormlib.util.SQLBuilder;

public class DB2<O> {

    private boolean DEBUG = false;
    private static final int VERSION = 1;
    private SQLiteHelpder sqliteHelpder;
    private Context mContext;
    private SQLiteDatabase db;
    private String dbName;
    private String tableName;
    private Class<O> clazz;
    int statue = 0;//0 未开启 ， 1 开启
    String transcationThread;

    public SQLiteDatabase getDb() {
        return db;
    }

    public void setDEBUG(boolean DEBUG) {
        this.DEBUG = DEBUG;
    }

    private static Map<String, DB2> db2Map = new HashMap<>();


    public static <O> DB2 getInstance(Context context, String dbFolder, String dbName, String tableName, Class<O> clazz) {
        String str = dbFolder + dbName + tableName;
        if (null == db2Map.get(str)) {
            synchronized (DB2.class) {
                try {
                    db2Map.put(str, new DB2(context, dbFolder, dbName, tableName, clazz));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return db2Map.get(str);
    }

    public static <O> DB2 getInstance(Context context, String dbName, String tableName, Class<O> clazz) {
        String data = context.getDatabasePath("data").getParent();
        String str = data + dbName + tableName;
        if (null == db2Map.get(str)) {
            synchronized (DB2.class) {
                try {
                    db2Map.put(str, new DB2(context, data, dbName, tableName, clazz));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return db2Map.get(str);
    }


    private DB2(Context context, String dbFolder, String dbName, String tableName, Class<O> clazz) throws Exception {
        if (TextUtils.isEmpty(dbName))
            throw new Exception("dbName not be null !");

        if (TextUtils.isEmpty(tableName))
            throw new Exception("tableName not be null !");

        if (TextUtils.isEmpty(dbName)) {
            dbName = context.getDatabasePath("data").getParent();
        }

        this.dbName = dbName.trim();
        this.tableName = tableName.trim();
        this.clazz = clazz;

        sqliteHelpder = new SQLiteHelpder(context.getApplicationContext(), dbFolder.trim(), this.dbName);
        this.db = sqliteHelpder.getWritableDatabase();
    }

    public DB2(Context context, String dbName, String tableName, Class<O> clazz) throws Exception {
        this(context, null, dbName, tableName, clazz);
    }


    /**
     * 增加一条记录
     *
     * @param entity
     */
    public synchronized void save(O entity) {
        checkTableExist();
        checkFaildExist();
        String sql = SQLBuilder.getInsertSQL(entity, tableName);
        execSQL(sql);
    }


    public synchronized void delete(O entity) {
        checkTableExist();
        checkFaildExist();
        String deleteSQ = SQLBuilder.getDeleteSQL(entity, tableName);
        execSQL(deleteSQ);
    }


    public synchronized void deleteByWhere(String where) {
        checkTableExist();
        checkFaildExist();
        String deleteSqlByWhere = SQLBuilder.getDeleteSqlByWhere(where, tableName);
        execSQL(deleteSqlByWhere);
    }


    public synchronized void deleteAll() {
        if (isCheckTable(tableName))
            execSQL(SQLBuilder.getDeletAllSQL(tableName));
    }

    public synchronized void update(O entity) {
        checkTableExist();
        checkFaildExist();
        String updateSQL = SQLBuilder.getUpdateSQL(entity, tableName);
        execSQL(updateSQL);
    }

    public synchronized void update(O entity, String strWhere) {
        checkTableExist();
        checkFaildExist();
        String updateSQLByWhere = SQLBuilder.getUpdateSQLByWhere(entity, strWhere, tableName);
        execSQL(updateSQLByWhere);
    }


    public synchronized void dropTable() {
        checkTableExist();
        String dropTableSQL = SQLBuilder.getDropTableSQL(clazz, tableName);
        execSQL(dropTableSQL);
        TableInfo table = TableInfo.get(clazz);
        table.setCheckDatabese(false);
    }


    public synchronized List<O> findAll() {
        return findAllByWhere(null);
    }

    public synchronized List<O> findAllByWhere(String where) {
        String select;
        checkTableExist();
        checkFaildExist();
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
        return list;
    }


    public synchronized List<O> findAllByWhere(String where, String order) {
        checkTableExist();
        checkFaildExist();
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

    public synchronized void execSQL(String sql) {
        if ((this.db == null) || ((this.db != null) && (!this.db.isOpen()))) {
            this.db = sqliteHelpder.getWritableDatabase();
        }
        debugSql(sql);
        this.db.execSQL(sql);
    }

    private synchronized void debugSql(String sql) {
        if (DEBUG)
            Log.d("KK_ORM sql", sql);
    }

    public synchronized void closeDB() {
        if ((this.db != null) && (this.db.isOpen()) && (sqliteHelpder != null))
            sqliteHelpder.close();
    }

    private synchronized boolean cloumIsExist(String tableName, String cloumName) {
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

    public synchronized boolean tableIsExist() {
        return tableIsExist(clazz, tableName);
    }

    HashMap<String, Boolean> checkdTableMap = new HashMap<>();

    private synchronized boolean tableIsExist(Class<O> clazz, String tabName) {
        boolean result = false;

        if (TextUtils.isEmpty(tabName)) {
            return false;
        }
        if (isCheckTable(tabName)) {
            return true;
        }

        TableInfo table = TableInfo.get(clazz);
        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='" + tabName.trim() + "' ";
            debugSql(sql);
            cursor = this.db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                    if (null != table)
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
        }
        return result;
    }

    private synchronized boolean tableIsExist(String tabName) {
        boolean result = false;

        if (TextUtils.isEmpty(tabName)) {
            return false;
        }
        if (isCheckTable(tabName)) {
            return true;
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

                    checkdTableMap.put(tabName, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return result;
    }

    /**
     * 判断某表里某字段是否存在
     */
    public synchronized void checkFaildExist() {

        String queryStr = "select sql from sqlite_master where type = 'table' and name = '%s'";

        queryStr = String.format(queryStr, tableName);
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

    // 创建字段
    private synchronized <T> void createFields(Class<T> clazz, ArrayList<Field> tablefields) {

        for (Field field : tablefields) {
            String tableSQL = SQLBuilder.getFieldAtTableSQL(ClassUtil.getTableName(clazz), field);
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

    public synchronized void checkTableExist() {
        if (!tableIsExist()) {
            String createSQL = SQLBuilder.getCreatTableSQL(clazz, tableName);
            execSQL(createSQL);
        }
    }


    public void setTranscationThread(String transcationThread) {
        this.transcationThread = transcationThread;
    }

    public synchronized void beginTransaction() throws Exception {
        if (db.inTransaction() || statue == 1)
            throw new Exception("last Transaction un_commit!");
        this.db.beginTransaction();
        transcationThread = Thread.currentThread().getName();
        statue = 1;
    }

    public synchronized void forceBeginTransaction() throws Exception {
        if (db.inTransaction() || statue == 0) {
            if (!Thread.currentThread().getName().equals(transcationThread))
                throw new Exception("The operation threads do not match !");

            try {
                this.db.endTransaction();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.db.beginTransaction();

    }

    public synchronized void commitTransaction() throws Exception {

        if (!db.inTransaction())
            return;

        if (!Thread.currentThread().getName().equals(transcationThread)) {

            throw new Exception("The operation threads do not match !");
        }

        this.db.setTransactionSuccessful();
        this.db.endTransaction();
        transcationThread = "";
        statue = 0;
    }

    public synchronized void close() {
        if (null == db || !db.isOpen())
            return;
        try {
            commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
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


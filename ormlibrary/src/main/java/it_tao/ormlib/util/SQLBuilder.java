/*     */
package it_tao.ormlib.util;
/*     */ 
/*     */


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/*     */ public class SQLBuilder
/*     */ {
    /*  10 */   public static String primary_key = "id";

    /*     */ 
/*     */
    public static String getSelectSQL(Class<?> clazz, String where)
/*     */ {

        return getSelectSQL(clazz, where, ClassUtil.getTableName(clazz)   , false);
/*     */
    }

    public static String getSelectSQL(Class<?> clazz, String where , String tableName , boolean isorder)
/*     */ {
/*  22 */
        StringBuilder sb = new StringBuilder();
/*     */ 
/*  24 */
        Object entity = new Object();
/*     */
        try {
/*  26 */
            entity = clazz.newInstance();
/*     */
        } catch (Exception e) {
/*  28 */
            e.printStackTrace();
/*     */
        }
/*     */ 
/*  31 */
        sb.append("SELECT * FROM ");
/*  32 */
        sb.append(tableName);
/*     */ 
/*  34 */
        if ((where != null) && (!where.equals(""))) {
/*  35 */
            sb.append(" where " + where);
/*     */
        }
/*  37 */
        sb.append(" ORDER BY id");
/*     */ 
/*  39 */
        return sb.toString();
/*     */
    }

    public static String getSelectSQL(Class<?> clazz, String where, String order)
/*     */ {

        Object entity = new Object();
/*     */
        try {
/*  26 */
            entity = clazz.newInstance();
/*     */
        } catch (Exception e) {
/*  28 */
            e.printStackTrace();
/*     */
        }

        return getSelectSQL(clazz, where, ClassUtil.getTableName(entity.getClass()) + " ", order);
/*     */
    }

    public static String getSelectSQL(Class<?> clazz, String where, String tableName, String order)
/*     */ {
/*  22 */
        StringBuilder sb = new StringBuilder();
/*     */
/*  24 */
        Object entity = new Object();
/*     */
        try {
/*  26 */
            entity = clazz.newInstance();
/*     */
        } catch (Exception e) {
/*  28 */
            e.printStackTrace();
/*     */
        }
/*     */
/*  31 */
        sb.append("SELECT * FROM ");
/*  32 */
        sb.append(tableName);
/*     */
/*  34 */
        if ((where != null) && (!where.equals(""))) {
/*  35 */
            sb.append(" where " + where);
/*     */
        }
/*  37 */
        if (order != null)
            sb.append(" ORDER BY " + order);
        else
            sb.append(" ORDER BY id");
/*  39 */
        return sb.toString();
/*     */
    }

    /*     */ 
/*     */
    public static String getSelectSQL(Class<?> clazz)
/*     */ {
/*  44 */
        return getSelectSQL(clazz, null,null);
/*     */
    }

    /*     */ 
/*     */
    public static String getCreatTableSQL(Class<?> clazz)
/*     */ {

        return getCreatTableSQL(clazz, ClassUtil.getTableName(clazz));
/*     */
    }

    public static String getCreatTableSQL(Class<?> clazz, String tableName)
/*     */ {
/*  59 */
        StringBuilder sql = new StringBuilder();
/*     */ 
/*  61 */
 
/*     */ 
/*  63 */
        sql.append("CREATE TABLE IF NOT EXISTS ");
/*  64 */
        sql.append(tableName);
/*  65 */
        sql.append("( ");
 
        Field[] fields = ClassUtil.getDeclaredFields(clazz);
 
        fields = removePrimaryKey(fields);
 
        sql.append("\"" + primary_key + "\" INTEGER PRIMARY KEY AUTOINCREMENT,");
 
        for (Field field : fields) {
/*  74 */
            sql.append(field.getName() + " ");
/*  75 */
            sql.append(FieldUtil.getSqlType(field));
/*  76 */
            sql.append(",");

//            LogUtil.e("append ", " name " + field.getName());
/*     */
        }
/*  78 */
        sql.deleteCharAt(sql.length() - 1);
/*  79 */
        sql.append(" );");
/*     */ 
/*  81 */
        return sql.toString();
/*     */
    }

    /*     */ 
/*     */
    public static String getInsertSQL(Object entity)
/*     */ {

        return getInsertSQL(entity, ClassUtil.getTableName(entity.getClass()));
/*     */
    }

    public static String getInsertSQL(Object entity, String tableName)
/*     */ {
/*  89 */
        Class clazz = entity.getClass();
/*     */ 
/*  91 */
        StringBuilder sql = new StringBuilder();
/*     */ 
/*  93 */
        sql.append("INSERT INTO ");
/*  94 */
        sql.append(tableName);
/*  95 */
        sql.append(getPropertiesAndBracket(clazz));
/*  96 */
        sql.append(" VALUES ");
/*  97 */
        sql.append(getValuesAndBracket(entity));
/*  98 */
        sql.append(";");
/*     */ 
/* 100 */
        return sql.toString();
/*     */
    }

    /*     */ 
/*     */
    public static <T> String getDropTableSQL(Class<T> clazz)
/*     */ {

        return getDropTableSQL(clazz, ClassUtil.getTableName(clazz));
/*     */
    }

    public static <T> String getDropTableSQL(Class<T> clazz, String taleName)
/*     */ {
/* 115 */
        StringBuilder sql = new StringBuilder();
/* 116 */
        sql.append("DROP TABLE ");
/* 117 */
        sql.append(taleName);
/* 118 */
        sql.append(";");
/*     */ 
/* 120 */
        return sql.toString();
/*     */
    }

    /*     */ 
/*     */
    private static String getValuesAndBracket(Object entity)
/*     */ {
/* 125 */
        Class clazz = entity.getClass();
/*     */ 
/* 127 */
        StringBuilder sb = new StringBuilder();
/* 128 */
        sb.append("(");
/*     */ 
/* 130 */
        Field[] fields = ClassUtil.getDeclaredFields(clazz);
/* 131 */
        fields = removePrimaryKey(fields);
/*     */ 
/* 133 */
        for (Field field : fields) {
/* 134 */
            field.setAccessible(true);
/*     */ 
/* 136 */
            Object value = new Object();
/*     */
            try {
/* 138 */
                value = field.get(entity);
/*     */
            } catch (Exception e) {
/* 140 */
                e.printStackTrace();
/*     */
            }
/*     */ 
/* 143 */
            sb.append("\"" + value + "\"");
/* 144 */
            sb.append(",");
/*     */
        }
/* 146 */
        sb.deleteCharAt(sb.length() - 1);
/* 147 */
        sb.append(")");
/*     */ 
/* 149 */
        return sb.toString();
/*     */
    }

    /*     */ 
/*     */
    public static String getPropertiesAndBracket(Class<?> clazz)
/*     */ {
/* 154 */
        StringBuilder sb = new StringBuilder();
/* 155 */
        sb.append("(");
/*     */ 
/* 157 */
        Field[] fields = ClassUtil.getDeclaredFields(clazz);
/* 158 */
        fields = removePrimaryKey(fields);
/*     */ 
/* 160 */
        for (Field field : fields) {
/* 161 */
            sb.append(field.getName());
/* 162 */
            sb.append(",");
/*     */
        }
/*     */ 
/* 165 */
        sb.deleteCharAt(sb.length() - 1);
/* 166 */
        sb.append(")");
/*     */ 
/* 168 */
        return sb.toString();
    }

    public static String getDeleteSQL(Object entity) {
        Object primaryKeyValue = FieldUtil.getValue(entity, primary_key);

        StringBuilder where = new StringBuilder();
        if ((primaryKeyValue != null) && (!primaryKeyValue.equals(""))) {
            where.append(primary_key);
            where.append(" = ");
            where.append("\"" + primaryKeyValue + "\"");
        }
        return getDeleteSqlByWhere(entity.getClass(), where.toString());
    }

    public static String getDeleteSQL(Object entity, String tableName) {
        Object primaryKeyValue = FieldUtil.getValue(entity, primary_key);
        StringBuilder where = new StringBuilder();
        if ((primaryKeyValue != null) && (!primaryKeyValue.equals(""))) {
            where.append(primary_key);
            where.append(" = ");
            where.append("\"" + primaryKeyValue + "\"");
        }
        return getDeleteSqlByWhere( where.toString(), tableName);
    }

    public static <T> String getDeleteSqlByWhere(Class<T> clazz, String where) {
        return getDeleteSqlByWhere(  where, ClassUtil.getTableName(clazz));

    }

    public static <T> String getDeleteSqlByWhere( String where, String tableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ");
        sql.append(tableName);

        sql.append(" WHERE ");

        if ((where != null) && (!where.equals("")))
            sql.append(where);
        else {
            sql.append(primary_key + "=\"\"");
        }
        sql.append(";");
        return sql.toString();

    }

    /*     */ 
/*     */
    public static <T> String getDeletAllSQL(Class<T> clazz) {
        return getDeletAllSQL(ClassUtil.getTableName(clazz));
    }

    public static <T> String getDeletAllSQL(String tableName)
/*     */ {
/* 222 */
        return "DELETE FROM " + tableName;
/*     */
    }

    public static String getUpdateSQL(Object entity)
/*     */ {
/* 227 */
        return getUpdateSQLByWhere(entity, null);
/*     */
    }

    public static String getUpdateSQL(Object entity, String tableName)
/*     */ {
/* 227 */
        return getUpdateSQLByWhere(entity, null, tableName);
/*     */
    }

    /*     */ 
/*     */
    public static String getUpdateSQLByWhere(Object entity, String where)
/*     */ {
        return getUpdateSQLByWhere(entity, where, ClassUtil.getTableName(entity.getClass()));
/*     */
    }

    public static String getUpdateSQLByWhere(Object entity, String where, String tableName)
/*     */ {
/* 237 */
        StringBuilder sql = new StringBuilder();
/* 238 */
        sql.append("UPDATE " + tableName + " SET ");
/*     */ 
/* 240 */
        Field[] fields = ClassUtil.getDeclaredFields(entity.getClass());
/* 241 */
        fields = removePrimaryKey(fields);
/*     */ 
/* 243 */
        for (Field field : fields) {
/* 244 */
            Object value = FieldUtil.getValue(entity, field.getName());
/*     */ 
/* 246 */
            sql.append(field.getName());
/* 247 */
            sql.append("=");
/* 248 */
            sql.append("\"" + value + "\"");
/* 249 */
            sql.append(",");
/*     */
        }
/* 251 */
        sql.deleteCharAt(sql.length() - 1);
/*     */ 
/* 253 */
        sql.append(" WHERE ");
/* 254 */
        if ((where != null) && (!where.equals(""))) {
/* 255 */
            sql.append(where);
/*     */
        } else {
/* 257 */
            Object primaryKeyValue = FieldUtil.getValue(entity, primary_key);
/*     */ 
/* 259 */
            sql.append(primary_key + "=");
/* 260 */
            sql.append("\"" + (primaryKeyValue == null ? "\"\"" : primaryKeyValue) + "\"");
/*     */
        }
/* 262 */
        sql.append(";");
/*     */ 
/* 264 */
        return sql.toString();
/*     */
    }

    /*     */ 
/*     */
    private static Field[] removePrimaryKey(Field[] fields)
/*     */ {
        List list = new ArrayList(Arrays.asList(fields));
        for (int i = 0; i < list.size(); i++) {
            Field field = (Field) list.get(i);
            String name = field.getName();
            if (name.equals(primary_key)) {
                list.remove(i--);
            }

        }
        return (Field[]) list.toArray(new Field[0]);
/*     */
    }

    public static String getFieldAtTableSQL(String tableName,  Field field ) {

        StringBuilder sbSQl = new StringBuilder();
        
        sbSQl.append("alter table " +tableName  + " add " +field.getName() + " "+FieldUtil.getSqlType(field));
       return sbSQl.toString(); 

    }
/*     */
}

/* Location:           F:\android\myDemo\JniCharTransition\app\libs\kkorm-library-1.0.jar
 * Qualified Name:     kk.orm.util.SQLBuilder
 * JD-Core Version:    0.6.0
 */
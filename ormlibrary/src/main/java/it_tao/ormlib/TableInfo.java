/*     */
package it_tao.ormlib;
/*     */ 
/*     */


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/*     */
/*     */
/*     */
/*     */

/*     */
/*     */ public class TableInfo
/*     */ {
    /*     */   private String className;
    /*     */   private String tableName;
    /*  18 */   private static final HashMap<Class<?>, TableInfo> tableInfoMap = new HashMap();
    /*     */
/*  24 */   public final HashMap<String, Property> propertyMap = new HashMap();
    /*     */   private boolean checkDatabese;

    /*     */
/*     */
    public static <T> TableInfo get(Class<T> clazz)
/*     */ {
/*  30 */
        TableInfo tableInfo = (TableInfo) tableInfoMap.get(clazz);
/*     */ 
/*  32 */
        if (tableInfo == null) {
/*  33 */
            tableInfo = new TableInfo();
/*     */ 
/*  35 */
            tableInfo.className = clazz.getName();
/*  36 */
            tableInfo.tableName = getTableName(clazz);
/*     */
          
/*  39 */
            List<Property> pList = getPropertyList(clazz);
/*  40 */
            for (Property p : pList) {
/*  41 */
                tableInfo.propertyMap.put(p.getFieldName(), p);
/*     */
            }
/*     */ 
/*  44 */
            tableInfoMap.put(clazz, tableInfo);
/*     */
        }
/*     */ 
/*  47 */
        return tableInfo;
/*     */
    }

    /*     */
/*     */
    public static <T> String getTableName(Class<T> clazz)
/*     */ {
        // 根据类生成表名
/*  52 */
//        Field tableName1 = clazz.getField("tableName");

        String tableName = clazz.getName();
/*  53 */
        tableName = tableName.replaceAll("\\.", "_");
/*     */ 
/*  55 */
        return tableName;
/*     */
    }

    /*     */
/*     */
    public static List<Property> getPropertyList(Class<? extends Object> clazz) {
/*  59 */
        List propertyList = new ArrayList();
/*     */ 
/*  61 */
        Field[] fields = getDeclaredFieldsJustReflect(clazz);
/*  62 */
        for (Field field : fields)
/*     */ {
/*  64 */
            Property property = new Property();
/*  65 */
            property.setField(field);
/*  66 */
            property.setFieldName(field.getName());
/*  67 */
            property.setDataType(field.getType());
/*     */ 
/*  69 */
            propertyList.add(property);
/*     */
        }
/*     */ 
/*  72 */
        return propertyList;
/*     */
    }

    /*     */
/*     */
    public static Field[] getDeclaredFieldsJustReflect(Class<? extends Object> clazz)
/*     */ {
/*  82 */
        List list = new ArrayList();
/*     */ 
/*  84 */
        Class tClazz = clazz;
/*     */ 
/*  86 */
        while (!tClazz.equals(Object.class)) {
/*  87 */
            Field[] declaredFields = tClazz.getDeclaredFields();
            
            
            list.addAll(Arrays.asList(declaredFields));
/*     */ 
/*  89 */
            for (int j = 0; j < list.size(); j++) {
/*  90 */
                Field field = (Field) list.get(j);
/*     */ 
///*  92 */LogUtil.e("field " , "field:" + declaredFields [j].getName());
                field.setAccessible(true);
/*     */
            }
/*     */ 
/* 100 */
            tClazz = tClazz.getSuperclass();
/*     */
        }
/* 102 */
        return (Field[]) list.toArray(new Field[0]);
/*     */
    }

    /*     */
/*     */
    public boolean isCheckDatabese() {
/* 106 */
        return this.checkDatabese;
/*     */
    }

    /*     */
/*     */
    public void setCheckDatabese(boolean checkDatabese) {
/* 110 */
        this.checkDatabese = checkDatabese;
/*     */
    }
/*     */
}

/* Location:           F:\android\myDemo\JniCharTransition\app\libs\kkorm-library-1.0.jar
 * Qualified Name:     kk.orm.TableInfo
 * JD-Core Version:    0.6.0
 */
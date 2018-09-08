/*    */ package it_tao.ormlib.util;
/*    */ 
/*    */


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it_tao.ormlib.Property;
import it_tao.ormlib.TableInfo;



/*    */
/*    */
/*    */
/*    */
/*    */
/*    */

/*    */
/*    */ public class ClassUtil
/*    */ {
/*    */   public static <T> String getTableName(Class<T> clazz)
/*    */   {
/* 14 */     return TableInfo.getTableName(clazz);
/*    */   }
/*    */ 
/*    */   public static <T> Field[] getDeclaredFields(Class<T> clazz)
/*    */   {
/* 23 */     List list = new ArrayList();
/*    */ 
/* 26 */     TableInfo table = TableInfo.get(clazz);
/*    */ 
/* 29 */     Set<String> keys = table.propertyMap.keySet();
/* 30 */     for (String key : keys) {
/* 31 */       Field field = ((Property)table.propertyMap.get(key)).getField();
/* 32 */       list.add(field);

//        LogUtil.e("class" , "key:" + key);
/*    */     }
/*    */ 
/* 35 */     return (Field[])list.toArray(new Field[0]);
/*    */   }
/*    */ }

/* Location:           F:\android\myDemo\JniCharTransition\app\libs\kkorm-library-1.0.jar
 * Qualified Name:     kk.orm.util.ClassUtil
 * JD-Core Version:    0.6.0
 */
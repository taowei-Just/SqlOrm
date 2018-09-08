/*    */ package it_tao.ormlib.util;
/*    */ 
/*    */

import android.database.Cursor;

/*    */
/*    */ public class CursorUtils
/*    */ {
/*    */   public static <T> T getEntity(Cursor cursor, Class<T> clazz)
/*    */   {
/* 10 */     Object entity = null;
/*    */     try {
/* 12 */       entity = clazz.newInstance();
/*    */ 
/* 15 */       for (int column = 0; column < cursor.getColumnCount(); column++) {
/* 16 */         int type = cursor.getType(column);

/* 17 */         String name = cursor.getColumnName(column);
/* 18 */         String value = cursor.getString(column);
/*    */ 
/* 20 */         FieldUtil.setValue(entity, name, value);
//            if (entity instanceof Data )
//                 Logg.e("getEntity" , " name "+name+" value "+value  );
//                  Logg.e("getEntity" ,  ((Data)entity).toString());
/*    */       }
/*    */ 
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 32 */       e.printStackTrace();
/*    */     }
/*    */ 
/* 35 */     return (T) entity;
/*    */   }
/*    */ }

/* Location:           F:\android\myDemo\JniCharTransition\app\libs\kkorm-library-1.0.jar
 * Qualified Name:     kk.orm.util.CursorUtils
 * JD-Core Version:    0.6.0
 */
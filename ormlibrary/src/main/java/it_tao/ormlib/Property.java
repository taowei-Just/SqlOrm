/*    */ package it_tao.ormlib;
/*    */ 
/*    */ import java.lang.reflect.Field;

/*    */
/*    */ public class Property
/*    */ {
/*    */   private String fieldName;
/*    */   private Class<?> dataType;
/*    */   private Field field;
/*    */ 
/*    */   public String getFieldName()
/*    */   {
/* 36 */     return this.fieldName;
/*    */   }
/*    */ 
/*    */   public void setFieldName(String fieldName) {
/* 40 */     this.fieldName = fieldName;
/*    */   }
/*    */ 
/*    */   public Class<?> getDataType() {
/* 44 */     return this.dataType;
/*    */   }
/*    */ 
/*    */   public void setDataType(Class<?> dataType) {
/* 48 */     this.dataType = dataType;
/*    */   }
/*    */ 
/*    */   public Field getField() {
/* 52 */     return this.field;
/*    */   }
/*    */ 
/*    */   public void setField(Field field) {
/* 56 */     this.field = field;
/*    */   }
/*    */ }

/* Location:           F:\android\myDemo\JniCharTransition\app\libs\kkorm-library-1.0.jar
 * Qualified Name:     kk.orm.Property
 * JD-Core Version:    0.6.0
 */
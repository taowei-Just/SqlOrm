/*     */ package it_tao.ormlib.util;
/*     */ 
/*     */

import android.annotation.SuppressLint;

import java.lang.reflect.Field;

/*     */

/*     */
/*     */ @SuppressLint({"UseValueOf"})
/*     */ public class FieldUtil
/*     */ {
/*     */   public static void setValue(Object entity, String fieldName, String value)
/*     */   {
/*     */     try
/*     */     {
/*  22 */       Field field = getField(fieldName, entity.getClass());
/*  24 */       if ((field != null) && (value != null)) {
/*  25 */         Class type = field.getType();
/*  26 */         String type_name = type.getName();
///*     */                 Logg.e("getEntity" , " fieldName " + fieldName+" type_name " +type_name);
/*  28 */         if (!value.equals("null")) {
/*  29 */           if ((type_name.equals("boolean")) || (type_name.equals("bool"))) {
/*  30 */             if (value.equals("false"))
/*  31 */               field.set(entity, Boolean.valueOf(false));
/*  32 */             else if (value.equals("true"))
/*  33 */               field.set(entity, Boolean.valueOf(true));
/*     */           }
/*  35 */           else if (type.equals(Boolean.class)) {
/*  36 */             if (value.equals("false"))
/*  37 */               field.set(entity, new Boolean(false));
/*  38 */             else if (value.equals("true"))
/*  39 */               field.set(entity, new Boolean(true));
/*     */           }
/*  41 */           else if ((type_name.equals("int")) || (type_name.equals(Integer.class.getName())))
/*  42 */             field.set(entity, new Integer(value));
/*  43 */           else if ((type_name.equals("long")) || (type_name.equals(Long.class.getName())))
/*  44 */             field.set(entity, new Long(value));
/*  45 */           else if ((type_name.equals("float")) || (type_name.equals(Float.class.getName())))
/*  46 */             field.set(entity, new Float(value));
/*  47 */           else if ((type_name.equals("double")) || (type_name.equals(Double.class.getName())))
/*  48 */             field.set(entity, new Double(value));
/*  49 */           else if ((type_name.equals("char")) || (type_name.equals(Character.class.getName()))) {
/*  50 */             field.set(entity, new Character(value.charAt(0)));
/*     */           } else if (type_name.equals(String.class.getName())) {

//                    Logg.e("getEntity" , " string value " +value);
/*  54 */           field.set(entity, new String(value));
/*     */         }
/*     */         }
///*  53 */         else if (type_name.equals(String.class.getName())) {
//                    Logg.e("getEntity" , " string value " +value);
///*  54 */           field.set(entity, new String(value));
///*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  62 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Object getValue(Object entity, String fieldName)
/*     */   {
/*  75 */     Object obj = "";
/*     */     try {
/*  77 */       Field field = getField(fieldName, entity.getClass());
/*     */ 
/*  79 */       if (field != null)
/*  80 */         obj = field.get(entity);
/*     */     }
/*     */     catch (Exception e) {
/*  83 */       e.printStackTrace();
/*     */     }
/*     */ 
/*  86 */     return obj;
/*     */   }
/*     */ 
/*     */   private static Field getField(String fieldName, Class<?> clazz)
/*     */   {
/*  98 */     Field field = null;
/*     */ 
/* 100 */     Field[] fields = ClassUtil.getDeclaredFields(clazz);
/* 101 */     for (Field f : fields) {
/* 102 */       if (f.getName().equals(fieldName)) {
/* 103 */         field = f;
/*     */       }
/*     */     }
/*     */ 
/* 107 */     return field;
/*     */   }
/*     */ 
/*     */   public static String getSqlType(Field field)
/*     */   {
/* 116 */     Class type = null;
/* 117 */     if (field != null)
/* 118 */       type = field.getType();
/*     */     try
/*     */     {
/* 121 */       if (type != null) {
/* 122 */         String type_name = type.getName();
/*     */ 
/* 124 */         if ((type_name.equals("int")) || (type_name.equals(Integer.class.getName())))
/* 125 */           return "INT";
/* 126 */         if ((type_name.equals("long")) || (type_name.equals(Long.class.getName())))
/* 127 */           return "LONG";
/* 128 */         if ((type_name.equals("float")) || (type_name.equals(Float.class.getName())))
/* 129 */           return "FLOAT";
/* 130 */         if ((type_name.equals("double")) || (type_name.equals(Double.class.getName())))
/* 131 */           return "Double";
/* 132 */         if ((type_name.equals("bool")) || (type_name.equals("boolean")) || (type_name.equals(Boolean.class.getName())))
/* 133 */           return "VARCHAR(6)";
/* 134 */         if ((type_name.equals("char")) || (type_name.equals(Character.class.getName())))
/* 135 */           return "CHAR(3)";
/* 136 */         if (type_name.equals(String.class.getName()))
/* 137 */           return "TEXT";
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 141 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 144 */     return "TEXT";
/*     */   }
/*     */ }

/* Location:           F:\android\myDemo\JniCharTransition\app\libs\kkorm-library-1.0.jar
 * Qualified Name:     kk.orm.util.FieldUtil
 * JD-Core Version:    0.6.0
 */
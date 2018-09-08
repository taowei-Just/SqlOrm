/*    */ package it_tao.ormlib.util;
/*    */ 
/*    */ public class TimeUtil
/*    */ {
/*    */   private static long start;
/*    */   private static long end;
/*    */ 
/*    */   public static void begin()
/*    */   {
/*  9 */     start = System.currentTimeMillis();
/*    */   }
/*    */ 
/*    */   public static void end()
/*    */   {
/* 14 */     end = System.currentTimeMillis();
/*    */   }
/*    */ 
/*    */   public static long time()
/*    */   {
/* 19 */     return end - start;
/*    */   }
/*    */ }

/* Location:           F:\android\myDemo\JniCharTransition\app\libs\kkorm-library-1.0.jar
 * Qualified Name:     kk.orm.util.TimeUtil
 * JD-Core Version:    0.6.0
 */
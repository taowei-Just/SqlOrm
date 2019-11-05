package it_tao.ormlib;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DBTransactionHelper {
    static  ExecutorService threadExecutor ;
    static {
          threadExecutor = Executors.newSingleThreadExecutor();
    }
    
    public  static  void  subTranstion(TranscationTask transcationTask){
            threadExecutor.submit(transcationTask);
    }
    
}

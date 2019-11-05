package reqdidcard.tao.com.sqlorm;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.util.List;

import it_tao.ormlib.DB2;
import it_tao.ormlib.DBTransactionHelper;
import it_tao.ormlib.TranscationCall;
import it_tao.ormlib.TranscationTask;

public class MainActivity extends AppCompatActivity {

    private DB2<Person> testDb;
    private Person selectidperson;
    private String dbFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbFolder = Environment.getExternalStorageDirectory() + "/testDb/";
        File file = new File(dbFolder);
        if (!file.exists())
            file.mkdirs();
        try {
            testDb = DB2.getInstance(this, dbFolder, "persion", "persion", Person.class);
            testDb.setDEBUG(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int n = 0;

    public void add(View view) {
        Person person = new Person();
        person.name = "name" + n++;
        person.age = n;
        person.sex = n % 2;
        testDb.save(person);
//        select_all(null);

    }

    public void updata_by_name1(View view) {
        Person person = new Person();
        person.name = "name1_01";
        person.age = 15;
        person.sex = n % 2;
        testDb.update(person, "name='name1'");
        select_all(null);
    }

    public void updata(View view) {
        Person person = new Person();
        person.id = 5;
        testDb.update(person);
        select_all(null);
    }

    public void delete_by_name4(View view) {
        testDb.deleteByWhere("name='name4'");
        select_all(null);

    }

    public void delete_all(View view) {
        testDb.deleteAll();
        select_all(null);

    }

    public void select_all(View view) {
        long timeMillis = System.currentTimeMillis();
        List<Person> all = testDb.findAll();
        System.err.println("耗时： " + (System.currentTimeMillis() - timeMillis) + " s :" + all.toString());
    }

    public void select_name5(View view) {
        List<Person> allByWhere = testDb.findAllByWhere("name='name5'");
        System.err.println(allByWhere.toString());
    }

    public void beginT(View view) {
        try {
            testDb.beginTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void commitT(View view) {
        try {
            testDb.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void force_beginT(View view) {
        try {
            testDb.forceBeginTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeT(View view) {
        if (testDb.getDb().inTransaction())
            testDb.getDb().endTransaction();
    }

    public void testT(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                long timeMillis = System.currentTimeMillis();
                for (int i = 0; i < 1000; i++) {
                    add(null);
                }
                long l = System.currentTimeMillis() - timeMillis;
                System.err.println("over==============================");
                timeMillis = System.currentTimeMillis();
                try {

                    testDb.beginTransaction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < 3000; i++) {
                    add(null);
                }
                try {
                    testDb.commitTransaction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.err.println("不开启事务耗时：" + l + " ms");
                System.err.println("开启事务耗时：" + (System.currentTimeMillis() - timeMillis) + " ms");
            }
        }).start();

    }

    public void testS(View view) {
        DB2 db2 = null;
        try {
            db2 = DB2.getInstance(MainActivity.this, dbFolder, "persion", "persion", Person.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DBTransactionHelper.subTranstion(new TranscationTask(db2, new TranscationCall() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Exception e) {
                
            }
        }) {
            @Override
            protected void taskRun(DB2 db) throws Exception {
                final long timeMillis = System.currentTimeMillis();
                for (int j = 0; j < 100; j++) {
                    for (int i = 0; i < 1000; i++) {
                        Person person = new Person();
                        person.name = "name" + n++;
                        person.age = n;
                        person.sex = n % 2;
                        db.save(person);
                    }
                }

                System.err.println("over==============================   ");
                long l = System.currentTimeMillis() - timeMillis;
                System.err.println("over开启事务耗时：" + l + " ms");
            }
        });


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                DB2 db2 = null;
//                try {
//                    db2 =  DB2.getInstance(MainActivity.this, dbFolder, "persion", "persion", Person.class);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                try {
//                    db2.beginTransaction();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                long timeMillis = System.currentTimeMillis();
//                for (int i = 0; i < 1000; i++) {
//                    Person person = new Person();
//                    person.name = "name" + n++;
//                    person.age = n;
//                    person.sex = n % 2;
//                    db2.save(person);
//                }
//                db2.commitTransaction();
//
//                long l = System.currentTimeMillis() - timeMillis;
//                System.err.println("over============================== 3 ");
//                System.err.println("开启事务耗时：" + l + " ms");
//
//            }
//        }).start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                DB2 db2 = null;
//                try {
//                    db2 =   DB2.getInstance(MainActivity.this, dbFolder, "persion", "persion", Person.class);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                    db2.beginTransaction();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                long timeMillis = System.currentTimeMillis();
//                for (int i = 0; i < 1000; i++) {
//                    Person person = new Person();
//                    person.name = "name" + n++;
//                    person.age = n;
//                    person.sex = n % 2;
//                    db2.save(person);
//                }
//                db2.commitTransaction();
//                long l = System.currentTimeMillis() - timeMillis;
//                System.err.println("over============================== 4 ");
//                System.err.println("开启事务耗时：" + l + " ms");
//
//            }
//        }).start();
    }

    public void testB(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    add(null);
                }
                System.err.println("over============================== 1");

            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    add(null);
                }
                System.err.println("over==============================2");
            }
        }).start();
    }
}


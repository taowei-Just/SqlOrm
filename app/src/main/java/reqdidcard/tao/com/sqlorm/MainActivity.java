package reqdidcard.tao.com.sqlorm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import it_tao.ormlib.DB;

public class MainActivity extends AppCompatActivity {

    private DB testDb;
    private Person selectidperson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File file = new File("/sdcard/dbtest/");
        if (!file.exists())
            file.mkdirs();
        testDb = new DB(this, DB.getDbDirPath(this), "info.db", "person");
    }

    int n = 0;

    public void add(View view) {
        Person person = new Person();
        person.name = "name" + (n++);
        person.age = n;
        person.sex = n % 2;
        testDb.save(person);

    }


    public void selectid(View view) {
        try {
            List<Person> people = testDb.findAllByWhere(Person.class, "name='name1'"  );
            selectidperson = people.get(0);
            System.err.println(people);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectall(View view) {
        System.err.println(testDb.findAll(Person.class).toString());
    }

    public void deletes1obj(View view) {
        if (selectidperson != null) {
            testDb.delete(selectidperson);
        }
    }

    public void deleteallby(View view) {
        testDb.deleteAll(Person.class);
    }

    public void deleteid(View view) {
        testDb.deleteByWhere(Person.class, "id>" + 1);
    }

    public void updata(View view) {
        selectidperson.age = 1000;
        selectidperson.aaa=120;
        testDb.update(selectidperson);
    }

    public void updataWhere(View view) {
        testDb.updateByWhere(selectidperson , "name='name2'");
    }
}


package reqdidcard.tao.com.sqlorm;

public class Person {
    int id ;
    public String name;
    public int age;
    public int sex;
    public  int aaa;

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                ", aaa=" + aaa +
                '}';
    }
}

package jackson.serial;

public class EasyTest {
    public static void main(String[] args) {
        TestObject2 ob = new TestObject2();
        //System.out.println(ob.inc());
        Easy.save(ob, "test.xml");
        TestObject2 back = Easy.load("test.xml",TestObject2.class);
        System.out.println("Loaded object back in");
        Easy.save(back, "back.xml");
        //System.out.println(back.inc());
    }
}

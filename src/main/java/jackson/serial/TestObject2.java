package jackson.serial;

public class TestObject2  {
    int[][] xxx = {{0,1},{2}};

    // Jackson seems to need the empty constructor
    public TestObject2() {}
}

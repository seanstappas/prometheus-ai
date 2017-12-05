package integration;

import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;

public class MiscTest {
    public static void main(String[] args) {
        new MiscTest().method();
    }

    private void method() {
        TreeMap<KeyClass, ValueClass> map = new TreeMap<>();
        KeyClass key1 = new KeyClass(1);
        KeyClass key2 = new KeyClass(2);
        KeyClass key3 = new KeyClass(3);

        ValueClass val1 = new ValueClass("1");
        ValueClass val2 = new ValueClass("2");
        ValueClass val3 = new ValueClass("3");

//        map.put(key1, val1);
//        map.put(key2, val2);
//        map.put(key3, val3);
//
//        System.out.println(map);
//
//        map.remove(key1);
//
//        key1.x = 400;
//
//        map.put(key1, val1);
//        System.out.println(map);


        TreeSet<KeyClass> set = new TreeSet<>();

        set.add(key2);
        set.add(key1);
        set.add(key3);

        System.out.println(set);

        System.out.println(set);

        set.remove(key1);
        key1.x = 4000000;
        set.add(key1);

        System.out.println(set);
    }

    class KeyClass implements Comparable<KeyClass> {
        long x;
        KeyClass(long x) {
            this.x = x;
        }

        long getX() {
            return x;
        }

        @Override
        public int compareTo(KeyClass o) {
            return Comparator.comparing(KeyClass::getX).compare(this, o);
        }

        @Override
        public String toString() {
            return "k" + String.valueOf(x);
        }
    }

    class ValueClass {
        String y;
        ValueClass(String y) {
            this.y = y;
        }

        @Override
        public String toString() {
            return "v" + y;
        }
    }
}

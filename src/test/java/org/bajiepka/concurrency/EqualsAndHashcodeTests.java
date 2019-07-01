package org.bajiepka.concurrency;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class EqualsAndHashcodeTests {

    @Test
    public void test_01_two_types_of_equals() {

        Integer oneHundred = new Integer(100);
        Integer oneThousand = new Integer(1000);

        Integer a = oneHundred;
        Integer b = oneHundred;
        Integer c = oneThousand;

        assertTrue("a == b", a == b);
        assertFalse("a == c", a == c);

        MyObject o1 = new MyObject(oneHundred);
        MyObject o2 = new MyObject(oneHundred);
        MyObject o3 = new MyObject(oneThousand);

        System.out.println(o1.hashCode());
        System.out.println(o2.hashCode());
        System.out.println(o3.hashCode());

        assertFalse("o1 == o2", o1 == o2);
        assertFalse("o2 == o3", o2 == o3);

        MyObjectEqualByValue oe1 = new MyObjectEqualByValue(oneHundred);
        MyObjectEqualByValue oe2 = new MyObjectEqualByValue(oneHundred);
        MyObjectEqualByValue oe3 = new MyObjectEqualByValue(oneThousand);

        System.out.println(oe1.hashCode());
        System.out.println(oe2.hashCode());
        System.out.println(oe3.hashCode());

        assertFalse("oe1 == oe2", oe1 == oe2);
        assertFalse("oe2 == oe3", oe2 == oe3);

        assertTrue("oe1.equals(oe2)", oe1.equals(oe2));
        assertFalse("oe2.equals(oe3)", oe2.equals(oe3));

        String first = "first";
        String second = "second";


    }

    class MyObject {

        Integer value;

        public MyObject(Integer value) {
            this.value = value;
        }
    }

    class MyObjectEqualByValue {

        Integer value;

        public MyObjectEqualByValue(Integer value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            return this.hashCode() == obj.hashCode();
        }

        @Override
        public int hashCode() {
            return this.value;
        }
    }

}

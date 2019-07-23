package org.bajiepka.concurrency.modernjavainaction.collections;

import org.junit.Test;

public class BinaryTreeTest {

    @Test
    public void test_01_customBinaryTreeImplementationTest() {

        //  TODO не работает имплементация дерева. Надо бы починить как нибудь.
        BinaryTreeImpl tree = new BinaryTreeImpl(null);
        tree.add(1);
        tree.add(2);
        tree.add(3);
        tree.add(4);
        tree.add(5);

        System.out.println(tree);

    }
}

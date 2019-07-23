package org.bajiepka.concurrency.modernjavainaction.collections;

public class BinaryTreeImpl {

    Node root;

    public BinaryTreeImpl(Node root) {
        this.root = root;
    }

    public void add(int value) {
        root = root.addRecursive(root, value);
    }

    class Node {
        int value;
        Node left;
        Node right;

        public Node(int value) {
            this.value = value;
            right = null;
            left = null;
        }

        private Node addRecursive(Node n, int i) {

            if (n == null) {
                return new Node(i);
            }

            if (i < n.value) {
                n.left = addRecursive(n.left, i);
            } else if (i > n.value) {
                n.right = addRecursive(n.right, i);
            } else {
                return n;
            }
            return n;
        }
    }
}

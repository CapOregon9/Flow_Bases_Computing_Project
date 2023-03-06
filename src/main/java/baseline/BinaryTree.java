package baseline;

public class BinaryTree {

    treeNode root;

    public static class treeNode{
        int data;
        treeNode left;
        treeNode right;
        treeNode nullChild;

        public treeNode(int data) {
            this.data = data;
            this.left = null;
            this.right = null;
            this.nullChild = null;
        }
    }

    public BinaryTree() {
        root = null;
    }

    public BinaryTree(int key) {
        root = new treeNode(key);
    }


}

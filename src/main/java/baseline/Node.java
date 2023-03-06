package baseline;

public class Node { //Class to store a BDD node's information
    private int nodeID;
    private int leftChild;
    private int rightChild;
    private int decisionID;

    public Node(int nodeID, int leftChild, int rightChild, int decisionID) {
        this.nodeID = nodeID;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.decisionID = decisionID;
    }

    public Node(Node node) {
        this.nodeID = node.nodeID;
        this.leftChild = node.leftChild;
        this.rightChild = node.rightChild;
        this.decisionID = node.decisionID;
    }

    public int getNodeID() {
        return nodeID;
    }

    public int getLeftChild() {
        return leftChild;
    }

    public int getRightChild() {
        return rightChild;
    }

    public int getDecisionID() {
        return decisionID;
    }
}

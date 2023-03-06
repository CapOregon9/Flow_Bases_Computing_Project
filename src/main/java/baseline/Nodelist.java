package baseline;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Nodelist { //A class used to evaluate both BDD and Crossbars
    private int numOfVars;
    private int numOfNodes; //Used for BDD
    private int numOfRows; //Used for Crossbar
    private int numOfColumns; //Used for Crossbar
    private List<Node> nodes = new ArrayList<>(); //Stores all BDD Nodes
    private List<List<Integer>> xbar = new ArrayList<>(); //Stores Crossbar's design
    ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<>(); //Stores Crossbar's related adjacency list
    LinkedList<Integer> queue = new LinkedList<>(); //Queue used in BFS algorithm

    public Nodelist() {
    }

    public Nodelist(Nodelist nodeList) {
        this.numOfVars = nodeList.numOfVars;
        this.numOfNodes = nodeList.numOfNodes;
        this.numOfRows = nodeList.numOfRows;
        this.numOfColumns = nodeList.numOfColumns;
        for (Node node :nodeList.nodes) {
            this.nodes.add(new Node(node));
        }
        int counter = 0;
        for (List<Integer> row: nodeList.xbar) {
            this.xbar.add(new ArrayList<>());
            for (int value:row) {
                this.xbar.get(counter).add(value);
            }
            counter++;
        }
    }

    public void setNumOfVars(int numOfVars) {
        this.numOfVars = numOfVars;
    }

    public void setNumOfNodes(int numOfNodes) {
        this.numOfNodes = numOfNodes;
    }

    public void setNumOfRows(int numOfRows) {
        this.numOfRows = numOfRows;
    }

    public void setNumOfColumns(int numOfColumns) {
        this.numOfColumns = numOfColumns;
    }

    public void addNode(int nodeID, int leftChild, int rightChild, int decisionID) { //Adds BDD node to list
        nodes.add(new Node(nodeID, leftChild, rightChild, decisionID));
    }

    public void addRow(String[] splitLine) { //Adds row to crossbar array
        ArrayList<Integer> row = new ArrayList<>();
        for (String string : splitLine) {
            try {
                row.add(Integer.parseInt(string));
            } catch (NumberFormatException e) {
                System.out.println(String.format("Invalid node in crossbar. Value is %s", string));
            }
        }
        xbar.add(row);
    }

    public void addRow(ArrayList<Integer> row) {
        xbar.add(row);
    }

    public void addToRow(int column, int row, int i) { //Adds a specific value to a row in the crossbar design at a specific index. It also verifies that the index is within it's size constraints
        try {
            xbar.get(row);
        } catch (IndexOutOfBoundsException e) {
            xbar.add(row, new ArrayList<>());
        }
        for (int j = xbar.get(row).size(); j <= column; j++) {
            xbar.get(row).add(j, 0);
        }
        xbar.get(row).set(column, i);
    }

    public void finalizeCrossbarRows() { //Goes through each row and adds 0s to the end until its the same size as the final row size
        numOfRows = xbar.size();
        for (List<Integer> row:xbar) {
            for (int i = row.size(); i < numOfColumns; i++) {
                row.add(i, 0);
            }
        }
        Collections.reverse(xbar);
    }

    public void printBDDList() { //Prints BDD design to screen
        System.out.println(String.format("vars %d", numOfVars));
        System.out.println(String.format("nodes %d", numOfNodes));
        for (Node node : nodes) {
            System.out.println(String.format("%d %d %d %d", node.getNodeID(), node.getLeftChild(), node.getRightChild(), node.getDecisionID()));
        }
    }

    public void printCrossbarList() { //Prints crossbar design to screen
        System.out.println(String.format("vars %d", numOfVars));
        System.out.println(String.format("rows %d", numOfRows));
        System.out.println(String.format("cols %d", numOfColumns));

        for (List<Integer> row : xbar) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int value : row) {
                stringBuilder.append(value).append(" ");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            System.out.println(stringBuilder.toString());
        }
    }

    public void printCrossbarListToFile(File file) { //Using formatter to write crossbar to file
        try (Formatter output = new Formatter(file)) {
            output.format("vars %d%n", numOfVars);
            output.format("rows %d%n", numOfRows);
            output.format("cols %d%n", numOfColumns);
            for (List<Integer> row : xbar) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int value : row) {
                    stringBuilder.append(value).append(" ");
                }
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                stringBuilder.append("\n");
                output.format(stringBuilder.toString());
            }
        } catch (IOException e) {
            System.out.println("Could not write xbar file");
        }
    }

    public void addValuesToCrossbar(List<Integer> evaluationList) { //Changes variable values of crossbar to either 0 or 99 depending if it should be connected or not
        int counter = 0;
        for (int input : evaluationList) {
            counter++;
            for (int i = 0; i < xbar.size(); i++) {
                for (int j = 0; j < xbar.get(i).size(); j++) {
                    if (xbar.get(i).get(j) == counter) {
                        xbar.get(i).set(j, input * 99);
                    } else if (Math.abs(xbar.get(i).get(j)) == counter) {
                        int temp = input ^ 1;
                        xbar.get(i).set(j, temp * 99);
                    }
                }
            }
        }
    }

    public void createGraphRepresentation() { //Create Adjacency list using crossbar design after values have been adjusted in the crossbar
        adjacencyList = new ArrayList<>(numOfRows + numOfColumns);
        for (int i = 0; i < numOfRows + numOfColumns; i++) {
            adjacencyList.add(new ArrayList<Integer>());
        }
        for (int i = 0; i < xbar.size(); i++) {
            for (int j = 0; j < xbar.get(i).size(); j++) {
                if (xbar.get(i).get(j) == 99) {
                    addEdge(i, j + numOfRows);
                }
            }
        }
    }

    private void addEdge(int row, int column) { //Adds an edge to the adjacency list
        adjacencyList.get(row).add(column);
        adjacencyList.get(column).add(row);
    }

    public void printAdjacencyList() { //Used for debugging and testing
        for (int i = 0; i < adjacencyList.size(); i++) {
            System.out.println(String.format("Adjacency list of %d", i));
            for (int j = 0; j < adjacencyList.get(i).size(); j++) {
                System.out.println(adjacencyList.get(i).get(j) + " ");
            }
            System.out.println();
        }
    }

    public boolean bfsReachable() { //modified BFS algorithm to work for crossbar design. Determines if there is a path from the Start to the end of the crossbar. This means the first row node to the last row node
        int start = numOfRows - 1;
        int end = 0;
        boolean[] visited = new boolean[numOfRows + numOfColumns];

        visited[start] = true;
        queue.add(start);

        Iterator<Integer> i;
        while (queue.size() != 0) {
            start = queue.poll();

            int newOne;
            i = adjacencyList.get(start).listIterator();

            while (i.hasNext()) {
                newOne = i.next();

                if (newOne == end){
                    return true;
                }

                if (!visited[newOne]) {
                    visited[newOne] = true;
                    queue.add(newOne);
                }
            }
        }
        return false;
    }

    public int evaluateBDD(List<Integer> evaluationList) { //Evaluating a BDD using a binary traversal to determine the result of the BDD
        int variable = 0;
        int decision;
        int node = 0;
        while(nodes.get(node).getLeftChild() != -1 && nodes.get(node).getRightChild() != -1) {
            decision = evaluationList.get(variable);
            if (decision == 1) {
                node = nodes.get(node).getLeftChild() - 1;
                variable = nodes.get(node).getDecisionID() - 1;
            } else {
                node = nodes.get(node).getRightChild() - 1;
                variable = nodes.get(node).getDecisionID() - 1;
            }
        }
        return nodes.get(node).getDecisionID();
    }

    public long countMemristors() {
        long count = 0;
        for (List<Integer> row:xbar) {
            for (int i = 0; i < row.size(); i++) {
                if (row.get(i) != 0 && row.get(i) != 99) {
                    count++;
                }
            }
        }
        return count;
    }
}

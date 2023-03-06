package baseline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Crossbar {
    private File file;
    private int numOfVars;
    private int numOfRows;
    private int numOfColumns;
    private Nodelist nodeList = new Nodelist(); //Stores the 2-d Array of the crossbar design
    private List<Integer> evaluationList = new ArrayList<>(); //List of inputs generated either by user or function for truth table
    private Nodelist evaluatedNodeList = new Nodelist(); //Copy of the node list in order to keep the original list unaffected to use for other evaluations
    private List<Integer> truthTableResults = new ArrayList<>(); //List of results from truth table
    private BinaryTree myTree = new BinaryTree();

    private int column;
    private int row;
    private ArrayList<Integer> finalRow = new ArrayList<>();

    public Crossbar(){}

    public Crossbar(File file) {
        this.file = file;
    }

    public List<Integer> getTruthTableResults() { //Getter to pass truth table results to the main function to use in evaluation
        return truthTableResults;
    }

    public void readFile() { //Reads the file using a scanner
        String fullLine;
        try (Scanner inputLine = new Scanner(file)){
            fullLine = inputLine.nextLine();
            String[] splitLine = fullLine.split(" ");
            numOfVars = Integer.parseInt(splitLine[1]);
            fullLine = inputLine.nextLine();
            splitLine = fullLine.split(" ");
            numOfRows = Integer.parseInt(splitLine[1]);
            fullLine = inputLine.nextLine();
            splitLine = fullLine.split(" ");
            numOfColumns = Integer.parseInt(splitLine[1]);
            nodeList.setNumOfVars(numOfVars);
            nodeList.setNumOfRows(numOfRows);
            nodeList.setNumOfColumns(numOfColumns);
            while(inputLine.hasNextLine()) {
                fullLine = inputLine.nextLine();
                splitLine = fullLine.split(" ");
                nodeList.addRow(splitLine);

            }
        } catch (IOException e) {
            System.out.println("Could not read Crossbar file.");
        }
    }

    public void printToScreen() { //Wrapper function to allow access to the main function
        nodeList.printCrossbarList();
    }

    public void printToFile(File file) { //Wrapper function to allow access to the main function
        nodeList.printCrossbarListToFile(file);
    }

    public int evaluateCrossbar() { //Copies nodelist and then calls the evaluation function within the new nodelist and returns the result to the main function call. Uses a BFS graphing algorithm to determine the result
        evaluatedNodeList = new Nodelist(nodeList);
        evaluatedNodeList.addValuesToCrossbar(evaluationList);
        evaluatedNodeList.createGraphRepresentation();
        boolean result = evaluatedNodeList.bfsReachable();
        //evaluatedNodeList.printAdjacencyList();
        if (result) {
            return 1;
        } else {
            return 0;
        }
    }

    public void getInputVariables() { //This functions takes in the input of each variable from the user
        evaluationList.clear();
        Scanner scanner = new Scanner(System.in);
        int value;
        for (int i = 0; i < numOfVars; i++) {
            System.out.println(String.format("What is the value of variable %d(1 or 0)", i + 1));
            while (true) {
                try {
                    value = Integer.parseInt(scanner.nextLine());
                    if (value == 1 || value == 0) {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Incorrect Value. Enter a numeric value of 1 or 0");
                }
            }
            evaluationList.add(value);
        }
    }

    public void generateTruthTable() { //Generates evaluation list and evaluates the Crossbar using the previous function. This repeats for all lines in the truth table
        truthTableResults.clear();
        for (int i = 0 ; i != (1<<numOfVars) ; i++) {
            evaluationList.clear();
            String set = Integer.toBinaryString(i);
            while (set.length() != numOfVars) {
                set = '0' + set;
            }
            for (int j = 0; j < set.length(); j++) {
                evaluationList.add(Integer.parseInt(String.valueOf(set.charAt(j))));
            }
            int rowResult = evaluateCrossbar();
            truthTableResults.add(rowResult);
        }

    }

    public void printTruthTable() { //Prints truth table using same evaluation list generation as before and the results from the truth table
        int counter = 1;
        for (int j = numOfVars - 1; j >= 0; j--) {
            System.out.print(counter);
            counter++;
        }
        System.out.println("\tResult");
        for (int i = 0 ; i != (1<<numOfVars) ; i++) {
            String set = Integer.toBinaryString(i);
            while (set.length() != numOfVars) {
                set = '0' + set;
            }
            System.out.println(set + "\t" + truthTableResults.get(i));
        }
    }

    public void generateTree(ArrayList<ArrayList<Integer>> maxTerms){ //Takes the list of max terms and converts the list to a binary-like tree form except it has 3 children which is then read in the crossbar design algorithm
        numOfVars = maxTerms.get(0).size();
        myTree.root = new BinaryTree.treeNode(1);
        BinaryTree.treeNode temp = myTree.root;
        for (ArrayList<Integer> term:maxTerms) {
            for (int i = 0; i < numOfVars; i++) {
                if (term.get(i) == 1) {
                    if (temp.left == null && i != numOfVars - 1) {
                        temp.left = new BinaryTree.treeNode(i+2);
                    } else if (temp.left == null) {
                        temp.left = new BinaryTree.treeNode(99);
                    }
                    temp = temp.left;
                } else if(term.get(i) == 0){
                    if (temp.right == null && i != numOfVars - 1) {
                        temp.right = new BinaryTree.treeNode(i+2);
                    } else if (temp.right == null) {
                        temp.right = new BinaryTree.treeNode(99);
                    }
                    temp = temp.right;
                } else {
                    if (temp.nullChild == null && i != numOfVars - 1) {
                        temp.nullChild = new BinaryTree.treeNode(i+2);
                    } else if (temp.nullChild == null) {
                        temp.nullChild = new BinaryTree.treeNode(99);
                    }
                    temp = temp.nullChild;
                }
            }
            temp = myTree.root;
        }
    }

    public void treeToCrossbarDesign(int prevColumn, BinaryTree.treeNode temp) { //Recursively adds maxterm representation to crossbar design
        if (prevColumn == -100) {
            nodeList = new Nodelist();
            finalRow = new ArrayList<>();
            temp = myTree.root;
            nodeList.setNumOfVars(numOfVars);
            column = 0;
            row = 0;
            prevColumn = 0;
        } else if (temp.data != 99) { //Adds 99s to crossbar to connect decisions
            nodeList.addToRow(prevColumn, row, 99);
            prevColumn = column;
        }
        if (temp.data != 99) { //7 different cases depending on what children each node has
            if (temp.left != null && temp.right != null && temp.nullChild != null) {
                nodeList.addToRow(column, row, temp.data);
                column++;
                nodeList.addToRow(column, row, -1 * temp.data);
                column++;
                nodeList.addToRow(column, row, 99);//
                column++;//
                row++;
                treeToCrossbarDesign(prevColumn, temp.left);
                treeToCrossbarDesign(prevColumn + 1, temp.right);
                treeToCrossbarDesign(prevColumn + 2, temp.nullChild); //
            } else if (temp.left != null && temp.right != null) {
                nodeList.addToRow(column, row, temp.data);
                column++;
                nodeList.addToRow(column, row, -1 * temp.data);
                column++;
                row++;
                treeToCrossbarDesign(prevColumn, temp.left);
                treeToCrossbarDesign(prevColumn + 1, temp.right);
            } else if (temp.left != null && temp.nullChild != null) {
                nodeList.addToRow(column, row, temp.data);
                column++;
                nodeList.addToRow(column, row, 99); //
                column++; //
                row++;
                treeToCrossbarDesign(prevColumn, temp.left);
                treeToCrossbarDesign(prevColumn + 1, temp.nullChild); //
            } else if (temp.left != null) {
                nodeList.addToRow(column, row, temp.data);
                column++;
                row++;
                treeToCrossbarDesign(prevColumn, temp.left);
            } else if (temp.right != null && temp.nullChild != null) {
                nodeList.addToRow(column, row, -1 * temp.data);
                column++;
                nodeList.addToRow(column, row, 99); //
                column++; //
                row++;
                treeToCrossbarDesign( prevColumn, temp.right); //
                treeToCrossbarDesign(prevColumn + 1, temp.nullChild); //
            } else if (temp.right != null) {
                nodeList.addToRow(column, row, -1 * temp.data);
                column++;
                row++;
                treeToCrossbarDesign( prevColumn, temp.right);
            } else if (temp.nullChild != null) {
                nodeList.addToRow(column, row, 99); //
                column++; //
                row++;
                treeToCrossbarDesign(prevColumn, temp.nullChild); //
            }
        } else {
            for (int j = finalRow.size(); j <= prevColumn; j++) { //List that stores the 99s to be in the final row of the crossbar design
                finalRow.add(j, 0);
            }
            finalRow.set(prevColumn, 99);
        }
    }

    public void finalizeCrossbar() { //Adds the final row to the crossbar and call the finalizeCrossbarRows function
        nodeList.addRow(finalRow);
        nodeList.setNumOfColumns(finalRow.size());
        nodeList.finalizeCrossbarRows();
    }


    public long countMemristors() {
        return nodeList.countMemristors();
    }
}

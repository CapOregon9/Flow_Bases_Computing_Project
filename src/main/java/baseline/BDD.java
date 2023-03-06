package baseline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BDD {
    private File file;
    private int numOfVars;
    private int numOfNodes;
    private Nodelist nodeList = new Nodelist(); //List of all nodes from the BDD
    private List<Integer> evaluationList = new ArrayList<>(); //List of inputs generated either by user or function for truth table
    private Nodelist evaluatedNodeList = new Nodelist(); //Copy of the node list in order to keep the original list unaffected to use for other evaluations
    private List<Integer> truthTableResults = new ArrayList<>(); //List of results from truth table
    private ArrayList<ArrayList<Integer>> maxTerms = new ArrayList<>();

    public BDD(File file) {
        this.file = file;
    }

    public BDD() {}

    public ArrayList<ArrayList<Integer>> getMaxTerms() {
        return maxTerms;
    }

    public void readFile() { //Reads the file using a scanner
        String fullLine;
        try (Scanner inputLine = new Scanner(file)){
            fullLine = inputLine.nextLine();
            String[] splitLine = fullLine.split(" "); //Split used to create an array of the arguments that are in a tabular design
            numOfVars = Integer.parseInt(splitLine[1]);
            fullLine = inputLine.nextLine();
            splitLine = fullLine.split(" ");
            numOfNodes = Integer.parseInt(splitLine[1]);
            nodeList.setNumOfVars(numOfVars);
            nodeList.setNumOfNodes(numOfNodes);
            while(inputLine.hasNextLine()) {
                fullLine = inputLine.nextLine();
                splitLine = fullLine.split(" ");
                nodeList.addNode(Integer.parseInt(splitLine[0]), Integer.parseInt(splitLine[1]), Integer.parseInt(splitLine[2]), Integer.parseInt(splitLine[3]));

            }
        } catch (IOException e) {
            System.out.println("Could not read BDD file.");
        }
    }

    public void generateMaxTerms() { //Goes through the results and adds the inputs if the result is 1
        int maxTermIndex = 0;
        for (int i = 0 ; i != (1<<numOfVars) ; i++) {
            if (truthTableResults.get(i) == 1) {
                String set = Integer.toBinaryString(i);
                while (set.length() != numOfVars) {
                    set = '0' + set;
                }
                maxTerms.add(new ArrayList<Integer>());
                for (int j = 0; j < set.length(); j++) {
                    maxTerms.get(maxTermIndex).add(Integer.parseInt(String.valueOf(set.charAt(j))));
                }
                maxTermIndex++;
            }
        }
    }

    public void minimizeMaxTerms() { //Iterative consunsus algorithm as well as using a boolean list to know which terms have been used to be able to remove them
        int differences = 0;
        ArrayList<ArrayList<Integer>> newTerms;
        do {
            newTerms = new ArrayList<>();
            boolean []termsUsed = new boolean[maxTerms.size()];
            for (int i = 0; i < maxTerms.size(); i++) {
                for (int j = i + 1; j < maxTerms.size(); j++) {
                    for (int k = 0; k < maxTerms.get(i).size(); k++) {
                        if (maxTerms.get(i).get(k) != maxTerms.get(j).get(k)){
                            differences++;
                        }
                    }
                    if (differences < 2) {
                        termsUsed[i] = true;
                        termsUsed[j] = true;
                        ArrayList<Integer> newTerm = new ArrayList<>();
                        for (int k = 0; k < maxTerms.get(i).size(); k++) {
                            if (maxTerms.get(i).get(k) != maxTerms.get(j).get(k)){
                                newTerm.add(k, -1);
                            } else {
                                newTerm.add(k, maxTerms.get(i).get(k));
                            }
                        }
                        boolean addToList = true;
                        for (int k = 0; k < newTerms.size(); k++) {
                            if (newTerms.get(k).equals(newTerm)) {
                                addToList = false;
                            }
                        }
                        if (addToList) {
                            newTerms.add(newTerm);
                        }
                    }
                    differences = 0;
                }
            }
            if (!newTerms.isEmpty()) {
                for (int i = 0; i < maxTerms.size(); i++) {
                    if (!termsUsed[i]) {
                        newTerms.add(maxTerms.get(i));
                    }
                }
                maxTerms = new ArrayList<>();
                maxTerms.addAll(newTerms);
            }
        } while (!newTerms.isEmpty());
    }

    public void printToScreen() {
        nodeList.printBDDList();
    } //Wrapper function to allow access to the main function

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

    public int evaluateBDD() { //Copies nodelist and then calls the evaluation function within the new nodelist and returns the result to the main function call
        evaluatedNodeList = new Nodelist(nodeList);
        int result = evaluatedNodeList.evaluateBDD(evaluationList);
        return result;
    }

    public void generateTruthTable() { //Generates evaluation list and evaluates the BDD using the previous function. This repeats for all lines in the truth table
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
            int rowResult = evaluateBDD();
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

    public int compareTruthTables(List<Integer> truthTableResults) { //Compares the results from the BDD truth table and the Crossbar truth table to determine if they are equivalent
        if (truthTableResults.size() != this.truthTableResults.size()) {
            return 0;
        }
        for (int i = 0; i < truthTableResults.size(); i++) {
            if (truthTableResults.get(i) != this.truthTableResults.get(i)) {
                return 0;
            }
        }
        return 1;
    }
}

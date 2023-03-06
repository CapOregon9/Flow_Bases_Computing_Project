package baseline;

import java.io.File;
import java.util.Scanner;

public class FlowComputing { //Main class that does the menu executing

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int decision;
        String myFile;
        File file;
        BDD myBDD = new BDD(); //Object to store BDD that is read in and execute related tasks
        Crossbar myCrossbar = new Crossbar(); //Object to store Crossbar that is read in and execute related tasks
        long start;
        long end;
        double execution;
        while (true) { //Input Validation for decision
            System.out.println(" Do you want to read a (1)BDD or a (2)Crossbar file?\n Print (3)BDD or (4)Crossbar to screen?\n (5)Save Crossbar to file.\n (6)Evaluate Crossbar giving inputs.\n (7)Generate Crossbar truth table after Evaluation.\n (8)Evaluate BDD giving inputs.\n (9)Generate BDD truth table after Evaluation.\n (10)Read in and Verify if a Crossbar and BDD are equivalent.\n (11)Generate Crossbar design from BDD file.\n (12)Determine amount of variablistic memristors in crossbar.\n (13) to exit.");
            try {
                decision = Integer.parseInt(scanner.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please input value 1,2, or 3.");
            }
        }
        while (decision != 13) { //Decision Menu used in application to execute certain tasks
            int result;
            switch (decision) {
                case 1: //Task 1.1
                    System.out.println("What file do you want to open?");
                    myFile = scanner.nextLine();
                    file  = new File(myFile);
                    myBDD = new BDD(file);
                    myBDD.readFile();
                    break;
                case 2: //Task 1.3
                    System.out.println("What file do you want to open?");
                    myFile = scanner.nextLine();
                    file  = new File(myFile);
                    myCrossbar = new Crossbar(file);
                    myCrossbar.readFile();
                    break;
                case 3: //Task 1.2
                    myBDD.printToScreen();
                    break;
                case 4: //Task 1.4
                    myCrossbar.printToScreen();
                    break;
                case 5: //Task 1.5
                    System.out.println("What do you want to save the crossbar as");
                    myFile = scanner.nextLine();
                    file  = new File(myFile);
                    myCrossbar.printToFile(file);
                    break;
                case 6: //Task 2.1
                    myCrossbar.getInputVariables();
                    result = myCrossbar.evaluateCrossbar();
                    if (result == 1) {
                        System.out.println("Top-most Row is connected to the bottom-most Row!");
                    } else {
                        System.out.println("There is not connection.");
                    }
                    break;
                case 7: //Task 2.2
                    myCrossbar.generateTruthTable();
                    myCrossbar.printTruthTable();
                    break;
                case 8: //Task 2.3
                    myBDD.getInputVariables();
                    result = myBDD.evaluateBDD();
                    System.out.println(String.format("The result is %d", result));
                    break;
                case 9: //Task 2.4
                    myBDD.generateTruthTable();
                    myBDD.printTruthTable();
                    break;
                case 10: //Task 3.1
                    System.out.println("What BDD file do you want to open?");
                    myFile = scanner.nextLine();
                    file  = new File(myFile);
                    myBDD = new BDD(file);
                    myBDD.readFile();
                    System.out.println("What Crossbar file do you want to open?");
                    myFile = scanner.nextLine();
                    file  = new File(myFile);
                    myCrossbar = new Crossbar(file);
                    myCrossbar.readFile();
                    start = System.nanoTime();
                    myCrossbar.generateTruthTable();
                    myBDD.generateTruthTable();
                    result = myBDD.compareTruthTables(myCrossbar.getTruthTableResults());
                    end = System.nanoTime();
                    if (result == 1) {
                        System.out.println("They are equivalent.");
                    } else {
                        System.out.println("They are not equivalent.");
                    }
                    execution = (end - start) / 1e9;
                    System.out.println("Execution time: " + execution + " seconds");
                    break;
                case 11: //Task 4.1
                    System.out.println("What do you want to save the crossbar as");
                    myFile = scanner.nextLine();
                    file  = new File(myFile);
                    start = System.nanoTime();
                    myBDD.generateTruthTable();
                    myBDD.generateMaxTerms(); //Take all the terms from the truth table that results in an output of 1
                    myBDD.minimizeMaxTerms(); //Iterative Consensus on maxterms
                    myCrossbar = new Crossbar();
                    myCrossbar.generateTree(myBDD.getMaxTerms()); //Create tree from minterms
                    myCrossbar.treeToCrossbarDesign(-100, null); //Convert tree to Crossbar algorithm
                    myCrossbar.finalizeCrossbar();
                    myCrossbar.printToFile(file);
                    end = System.nanoTime();
                    execution = (end - start) / 1e9;
                    System.out.println("Execution time: " + execution + " seconds");
                    break;
                case 12:
                    long amount = myCrossbar.countMemristors();
                    System.out.println("Memsristors: " + amount);
                    break;
                default:
                    System.out.println("Not a choice. Try again.");
                    break;
            }
            while (true) {
                System.out.println(" Do you want to read a (1)BDD or a (2)Crossbar file?\n Print (3)BDD or (4)Crossbar to screen?\n (5)Save Crossbar to file.\n (6)Evaluate Crossbar giving inputs.\n (7)Generate Crossbar truth table after Evaluation.\n (8)Evaluate BDD giving inputs.\n (9)Generate BDD truth table after Evaluation.\n (10)Read in and Verify if a Crossbar and BDD are equivalent.\n (11)Generate Crossbar design from BDD file.\n (12)Determine amount of variablistic memristors in crossbar.\n (13) to exit.");
                try {
                    decision = Integer.parseInt(scanner.nextLine());
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please input value 1,2, or 3.");
                }
            }
        }


    }
}

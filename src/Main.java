import java.io.File;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        // opening the Directory containing all the TSP Instances
        File dir = new File("TSPinstances");
        File[] directoryListing = dir.listFiles();

        // iterating through all the TSP Instances in the directory
        for (File tspInstance : directoryListing) {
            // ArrayList of cities (City) which is initialised by the readInput method
            ArrayList<City> cities = InputReader.readInput(tspInstance.getAbsolutePath());
            //printCities(cities);

            System.out.print("GA:  " + tspInstance + "\t");
            // call the genetic algorithm path finder with the arraylist of cities
            long GAstartTime = System.nanoTime();
            GeneticAlgorithm.GA(cities);
            long GAendTime = System.nanoTime();
            System.out.println("Time taken by GA:  " + (int)((GAendTime-GAstartTime)/1000000) + "ms");

            System.out.print("ACO: " + tspInstance + "\t");
            // call the ant colony optimisation path finder with with the arraylist of cities
            long ACOstartTime = System.nanoTime();
            AntColonyOptimisation.ACO(cities);
            long ACOendTime = System.nanoTime();
            System.out.println("Time taken by ACO: " + (int)((ACOendTime-ACOstartTime)/1000000) + "ms");

            // print a new line to separate instances from one another
            System.out.println();
        }
    }



    // Methods used for debugging:
    public static void printCities(ArrayList<City> cities) {
        for (City city : cities) {
            System.out.println(city);
        }
    }
}

import java.util.Arrays;

public class Chromosome implements Comparable<Chromosome> {

    int[] path;
    double fitness;

    public Chromosome(int pathLength) {
        this.path = new int[pathLength];
        // fill the path with null values (-1)
        Arrays.fill(this.path, -1);
        this.fitness = -1;
    }

    // method which defines the >, < and == for Chromosomes, based off of their fitness
    @Override
    public int compareTo(Chromosome c){
        int comparison;

        if (c.fitness > this.fitness){
            comparison = 1;
        } else if (c.fitness < this.fitness) {
            comparison = -1;
        } else {
            comparison = 0;
        }

        return comparison;
    }


    // method which returns the path of a chromosome as a string
    public String getPath() {
        StringBuilder string = new StringBuilder();
        for (int city : path) {
            string.append((city+1) + " ");
        }
        return string.toString();
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for (int city : path) {
            string.append((city+1) + " ");
            //string.append(city + " ");
        }
        string.append(": " + fitness);

        return string.toString();
    }
}

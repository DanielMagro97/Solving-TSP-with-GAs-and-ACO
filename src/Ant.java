import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Ant {
    // ArrayList of CityIDs which will store the order of cities visited
    ArrayList<Integer> path;
    // double which stores the length of the path followed by the ant
    double pathDistance;
    // int which stores on which City the Ant is currently on
    int onCityID;
    // Set of Integers which stores which cities the ant has already visited during its current tour.
    // the arraylist 'path' can be used for this same purpose, however with the set, this can be checked in constant time
    Set<Integer> visitedCities;

    // constructor for the Ant class
    public Ant(int startingCity) {
        // set the length of the path the ant has travelled to -1, a 'null' value
        pathDistance = -1;
        // set the city the ant is currently on to the one passed as an argument to the constructor,
        // i.e. the one the ant will start its tour from
        this.onCityID = startingCity;
        // initialise the 'path' arraylist to an empty array list, and then add the starting city to the path arraylist
        path = new ArrayList<>();
        this.path.add(startingCity);
        // initialise the visited cities set as an empty set, and add the starting city to it
        visitedCities = new HashSet<>();
        visitedCities.add(onCityID);
    }


    // method which returns the path of an Ant as a string
    public String getPath() {
        StringBuilder string = new StringBuilder();
        for (int city : path) {
            string.append((city+1) + " ");
        }
        return string.toString();
    }

    @Override
    public String toString() {
        return ( Arrays.toString(path.toArray()) + "\t" + pathDistance );
    }
}

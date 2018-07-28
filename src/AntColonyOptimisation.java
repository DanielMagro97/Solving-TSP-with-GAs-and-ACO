import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.DoubleStream;

public class AntColonyOptimisation {

    // 2D Array of the pheromone level between every pair of cities
    private static double[][] pheromoneLevels;
    // 2D-Array of distances between every pair of cities. Used as an optimisation,
    // in order not to calculate a distance every time it is needed.
    private static double[][] distanceMatrix;

    // parameters of ACO
    // the number of ants that will be simulated. This parameter is called 'm' in literature
    private static final int numberOfAnts = 10;
    // probability that next city will be chosen based off the highest score and not probabilistically
    // i.e. the method chosen to choose the next city will be:
    // (q_0*100)% (90%) that the next city chosen is the one with the highest pheromone level and shortest distance
    // (100-q_0*100)%  (10%) that the next city is chosen probabilistically, by a probability distribution which
    // favours edges with higher levels of pheromone and shorter distances
    private static final double q_0 = 0.9;
    // evaporation rate of pheromones
    private static final double alpha = 0.1;
    // tau_0 is the amount of pheromone that is deposited on edges when performing local updates
    private static double tau_0;
    // the importance given to the distance between two cities against the pheromone level
    private static final double beta = 2;


    // the main method of the ACO TSP algorithm. Given an ArrayList of Cities, this method will output the
    // shortest path found as well as its distance/length
    public static void ACO(ArrayList<City> cities) {
        // initialising the matrix which stores the distance between every pair of cities and
        // initialising the matrix which stores the pheromone level between every pair of cities
        initialiseDistanceAndPheromoneLevelMatrices(cities);

        // Declaring an array which will store all the ants being simulated,
        // and initialising it to have as many elements as specified by the numberOfAnts parameter
        Ant[] ants = new Ant[numberOfAnts];

        double L_nn = calculateL_nn(cities.size());
        // tau_0 = (n*L_nn)^-1
        tau_0 = 1.0 / ( (double) cities.size() * L_nn );

        // integer storing for how many times the ACO is performed
        int iterations = 1000;
        // for loop through all the iterations
        for (int i = 0; i < iterations; i++) {
            // generating a new set of ants, with a random starting city
            for (int j = 0; j < numberOfAnts; j++) {
                int startingCity = ThreadLocalRandom.current().nextInt(0, cities.size());
                Ant ant = new Ant(startingCity);
                ants[j] = ant;
            }

            // repeat this for as many cities need to be visited or 'traversed'
            for (int j = 1; j < cities.size(); j++) {
                // for every ant
                for (int k = 0; k < ants.length; k++) {
                    // move them to the next city using the nextCity method
                    int nextCity = nextCity(cities, ants[k]);

                    // perform a Local Pheromone Update on the path between the current city, and the next city
                    performLocalPheromoneUpdate(ants[k].onCityID, nextCity);

                    // update the city the ant is on to the next city in the path
                    ants[k].onCityID = nextCity;
                    // add the nextCity to the ant's path
                    ants[k].path.add(nextCity);
                    // add the nextCity to the set of the ant's visitedCities
                    ants[k].visitedCities.add(nextCity);
                }
            }
            // perform a Local Pheromone Update on the paths between each ant's last city and its first city
            for (int k = 0; k < ants.length; k++) {
                performLocalPheromoneUpdate(ants[k].path.get( cities.size()-1 ), ants[k].path.get(0));
            }

            // calculate path distance of every ant
            for (int j = 0; j < ants.length; j++) {
                ants[j].pathDistance = routeDistance(ants[j]);
            }

            // perform a Global Pheromone Update on all of the edges on the path of the best Ant
            // find the best ant:
            double minDistance = ants[0].pathDistance;
            int minAnt = 0;
            for (int j = 1; j < ants.length; j++) {
                if (ants[j].pathDistance < minDistance) {
                    minDistance = ants[j].pathDistance;
                    minAnt = j;
                }
            }
            // perform the Global Pheromone Update
            performGlobalPheromoneUpdate(ants[minAnt]);
        }

        // find the ant with the shortest path
        double minDistance = ants[0].pathDistance;
        int minAnt = 0;
        for (int j = 1; j < ants.length; j++) {
            if (ants[j].pathDistance < minDistance) {
                minDistance = ants[j].pathDistance;
                minAnt = j;
            }
        }
        // and output its path and total distance
        System.out.println(ants[minAnt].getPath());
        System.out.println("Total Route Distance: " + ants[minAnt].pathDistance);
    }


    // method which calculates the Euclidean Distance between every pair of cities,
    // such that the array of distances can be referred to in constant time
    // also initialise the pheromone level between every pair of cities to a minuscule amount
    private static void initialiseDistanceAndPheromoneLevelMatrices(ArrayList<City> cities) {
        // initialise the distanceMatrix as a new empty square 2D array, according to how many cities there are
        distanceMatrix = new double[cities.size()][cities.size()];
        // initialise the pheromoneLevels as a new empty square 2D array, according to how many cities there are
        pheromoneLevels = new double[cities.size()][cities.size()];

        for (int i = 0; i < cities.size(); i++) {
            for (int j = 0; j < cities.size(); j++) {
                // check if the distance between 2 cities has already been calculated
                if (distanceMatrix[i][j] == 0.0) {
                    // calculate the distance between cities i and j
                    distanceMatrix[i][j] = Location.distance(cities.get(i).location, cities.get(j).location);
                    // set the distance between cities j and i to the distance between cities i and j,
                    // as these will always be equal given that this is a Symmetric TSP
                    distanceMatrix[j][i] = distanceMatrix[i][j];

                    // initially set the pheromone level between every pair of cities to a very small level
                    pheromoneLevels[i][j] = 0.000000001;
                    pheromoneLevels[j][i] = 0.000000001;
                }
            }
        }
    }


    // method which returns which city the ant will go to next
    private static int nextCity(ArrayList<City> cities, Ant ant) {
        // int which will store the city that the ant will go to next
        int nextCity;

        // array which will store the score of every city that the ant has not yet visited
        double[] score = new double[cities.size()];

        // calculate the 'score' of each city
        for (int i = 0; i < cities.size(); i++) {
            // if the ant hasn't visited a city
            if (!ant.visitedCities.contains(i)) {
                // set the probability of going to a city as the pheromone level between the ant's
                // current city and that city
                double tau = pheromoneLevels[ant.onCityID][i];
                double eta = 1 / distanceMatrix[ant.onCityID][i];
                score[i] = tau * Math.pow(eta, beta);
            }
        }

        double q = ThreadLocalRandom.current().nextDouble();

        if (q <= q_0) {
            // choose the city with the highest 'score' as the next city the ant will visit
            double maxScore = score[0];
            nextCity = 0;
            for (int i = 1; i < score.length; i++) {
                if (score[i] > maxScore) {
                    maxScore = score[i];
                    nextCity = i;
                }
            }
        } else {
            // find the total score to use as a denominator
            double sumOfScores = DoubleStream.of(score).sum();

            // declaring an array which will store every city's cumulative probability
            double[] cumulativeProbability = new double[score.length];
            // set the cumulative probability of the first city as its score divided by the sum of scores
            cumulativeProbability[0] = score[0] / sumOfScores;
            // set the cumulative probability of every other city as the cumulative probability of the previous city
            // + that probability (i.e. its score divided by the total score)
            for (int i = 1; i < cumulativeProbability.length; i++) {
                cumulativeProbability[i] = cumulativeProbability[i-1] + (score[i] / sumOfScores);
            }

            // choose the next city based on the probabilities
            double random = ThreadLocalRandom.current().nextDouble();

            nextCity = 0;
            while (random > cumulativeProbability[nextCity]) {
                nextCity++;
            }
        }

        return nextCity;
    }


    // method which performs a local pheromone update given the ID of two cities
    private static void performLocalPheromoneUpdate(int cityA, int cityB){
        double updatedPheromoneLevel = (1.0-alpha) * pheromoneLevels[cityA][cityB];
        updatedPheromoneLevel += ( alpha * tau_0 );

        pheromoneLevels[cityA][cityB] = updatedPheromoneLevel;
        pheromoneLevels[cityB][cityA] = updatedPheromoneLevel;
    }


    // method which performs a local pheromone update given the ID of two cities
    private static void performGlobalPheromoneUpdate(Ant bestAnt){
        // perform the Global Pheromone Update on all the edges inside the best ant's path
        for (int j = 0; j < bestAnt.path.size()-1; j++) {
            int cityR = bestAnt.path.get(j);
            int cityS = bestAnt.path.get(j+1);

            pheromoneLevels[cityR][cityS] *= 1.0 - alpha;
            pheromoneLevels[cityR][cityS] += ( alpha * (1.0/bestAnt.pathDistance) );
            // since this is a symmetric TSP, perform the pheromone update both ways
            pheromoneLevels[cityS][cityR] = pheromoneLevels[cityR][cityS];
        }
        // perform the Global Pheromone Update on the edge from the last city to the first
        int cityR = bestAnt.path.get( bestAnt.path.size()-1 );
        int cityS = bestAnt.path.get(0);
        pheromoneLevels[cityR][cityS] *= 1.0 - alpha;
        pheromoneLevels[cityR][cityS] += ( alpha * (1.0/bestAnt.pathDistance) );
        pheromoneLevels[cityS][cityR] = pheromoneLevels[cityR][cityS];
    }


    // method which calculates the total distance travelled by an ant on its tour
    private static double routeDistance(Ant ant) {
        double totalDistance = 0.0;

        // the sum of the distances between every 2 successive cities in a path
        for (int i = 0; i < ant.path.size()-1; i++) {
            totalDistance += distanceMatrix[ant.path.get(i)][ant.path.get(i+1)];
        }
        // plus the distance between the last city in the path back to the first city in the path
        totalDistance += distanceMatrix[ant.path.get(ant.path.size()-1)][ant.path.get(0)];

        return totalDistance;
    }


    // Method which first calculates a path through all the cities using the Nearest Neighbour Algorithm
    // then returns the parameter L_nn, which is the inverse of the distance of that path
    private static double calculateL_nn(int pathLength){

        // declaring the city the Nearest Neighbour Algorithm Ant will start from
        int startingCity = ThreadLocalRandom.current().nextInt(0, pathLength);

        // set the currentCity as the Starting City
        int currentCity = startingCity;
        // declaring an Ant which will store the path followed by the nearest neighbour algorithm
        // initialise the first city in the path as the startingCity
        Ant nn = new Ant(startingCity);

        // Declare a new Set of unvisitedCities, and fill it with all the city IDs
        Set<Integer> unvisitedCities = new HashSet<>();
        for (int i = 0; i < pathLength; i++) {
            unvisitedCities.add(i);
        }
        // remove the startingCity from the set of unvisitedCities
        unvisitedCities.remove(startingCity);

        // for the length of the path
        for (int i = 1; i < pathLength; i++) {
            // initially set the closestCity to a random city from the set of unvisitedCities
            int closestCity = unvisitedCities.iterator().next();
            // loop through every unvisitedCity to find the closestCity
            for (Integer city : unvisitedCities) {
                // if the distance between the currentCity and the unvisitedCity is < the distance between
                // the currentCity and the closestCity
                if (distanceMatrix[currentCity][city] < distanceMatrix[currentCity][closestCity]) {
                    // set thet unvisitedCity as the closestCity
                    closestCity = city;
                }
            }
            // add the closestCity as the next city in the path
            nn.path.add(closestCity);
            // make the currentCity for the next iteration the current closestCity
            currentCity = closestCity;
            // remove the closestCity from the set of unvisitedCities
            unvisitedCities.remove(closestCity);
        }

        return routeDistance(nn);
    }
}

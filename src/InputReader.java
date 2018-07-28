import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class InputReader {

    // method which reads the input from one input TSP instance given its path and turns it into an ArrayList of Citiess
    public static ArrayList<City> readInput(String path) {
        Path filePath = Paths.get(path);

        try {
            List<String> lines = Files.readAllLines(filePath);
            return  parseCities(lines);
        } catch (IOException e) {
            //e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // method which parses each line inside the text file
    private static ArrayList<City> parseCities(List<String> lines) {
        ArrayList<City> cities = new ArrayList<>();

        for (final String line : lines) {
            // splitting each line by spaces
            final String[] details = line.trim().split("\\s+");
            City city = parseCity(details);
            if (city != null) {
                cities.add(city);
            } else {
                continue;
            }
        }
        return cities;
    }

    // method which parses each individual line in the text file and stores it in a City Data Structure
    private static City parseCity(String[] details) {
        // making sure exactly 3 values were extracted from the line of the input file
        if (details.length == 3) {
            try {
                int cityID = Integer.parseInt(details[0].trim());
                Location location = parseLocation(details[1].trim(), details[2].trim());

                return new City(cityID, location);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // method which parses the location in the text file as 2 co ordinates in the Location Data Structure
    private static Location parseLocation(String x, String y) throws NumberFormatException{
        return new Location(Double.parseDouble(x), Double.parseDouble(y));
    }
}
public class City {
    int cityID;
    Location location;

    public City(int nodeID, Location location) {
        this.cityID = nodeID;
        this.location = location;
    }

    @Override
    public String toString() {
        return cityID + "\t" + location;
    }
}

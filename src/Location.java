public class Location {
    double x;
    double y;

    public Location(double x, double y){
        this.x = x;
        this.y = y;
    }

    public static double distance(Location a, Location b) {
        return Math.sqrt(  Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2)  );
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
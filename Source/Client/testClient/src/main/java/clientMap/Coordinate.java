package clientMap;

public class Coordinate {

    private int x;
    private int y;

   public Coordinate(int x, int y){
        this.x = x;
        this.y = y;
    }

    public static Coordinate of(int x, int y) {
        return new Coordinate(x, y);
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public boolean equals(Object o){
        if(this == o){
            Coordinate that = (Coordinate) o;
            return x == that.x && y == that.y;
        }
        return false;
    }

    public String toString() {
        return "[x=" + x + ", y=" + y + "]";
    }

}

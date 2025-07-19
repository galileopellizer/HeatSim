package heatsim.simulation;

public class Position {

    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Position move(Direction dir) {
        return new Position(this.x + dir.dx, this.y + dir.dy);
    }



    public int x() {
        return x;
    }

    public int y() {
        return y;
    }
}

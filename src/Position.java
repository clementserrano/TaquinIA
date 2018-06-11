public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int distance(Position pos) {
        return Math.abs(pos.getX() - x) + Math.abs(pos.getY() - y);
    }

    @Override
    public boolean equals(Object obj) {
        Position pos = (Position) obj;
        return (pos.getX() == x && pos.getY() == y);
    }

    @Override
    public String toString() {
        return "{" + x + ", " + y + '}';
    }
}

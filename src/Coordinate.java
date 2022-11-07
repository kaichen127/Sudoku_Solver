public class Coordinate {
    private int row;
    private int col;
    private int value;
    

    public Coordinate()
    {
        this.row = 0;
        this.col = 0;
        this.value = 999;

    }

    public Coordinate(int x, int y)
    {
        this.row = x;
        this.col = y;
        this.value = 999;
    }

    public Coordinate(int x, int y, int z)
    {
        this.row = x;
        this.col = y;
        this.value = z;
    }

    public int getRow()
    {
        return this.row;
    }

    public int getCol()
    {
        return this.col;
    }

    public int getValue()
    {
        return this.value;
    }

    public void setRow(int x)
    {
        this.row = x;
    }

    public void setCol(int x)
    {
        this.col = x;
    }

    public void setValue(int x)
    {
        this.value = x;
    }

}

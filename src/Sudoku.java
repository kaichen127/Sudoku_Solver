public class Sudoku {
    private int[][] board;
    private boolean[][] rows;
    private boolean[][] cols;
    // private int[][] diagonals;
    private boolean[][] groups;
    private boolean[][][] remainingValues; // keep track of each coordinate's remaining values, and how many values they have left (maybe not most efficient?)
    private int[][] MRV;
    private Coordinate priority;
    private int[][] degree;
    private int tiles;

    public Sudoku()
    {
        this.board = new int[9][9];
        this.rows = new boolean[9][10];
        this.cols = new boolean[9][10];
        // this.diagonals = new int[2][9];
        this.groups = new boolean[9][10];
        this.remainingValues = new boolean[9][9][10];
        this.MRV = new int[9][9];
        this.priority = new Coordinate();
        this.degree = new int[9][9];
        this.tiles = 81;

        for (int i = 0; i < 9; i++)
        {
            java.util.Arrays.fill(board[i], 0);
            java.util.Arrays.fill(MRV[i], 9);
            java.util.Arrays.fill(rows[i], false);
            java.util.Arrays.fill(cols[i], false);
            java.util.Arrays.fill(groups[i], false);
            java.util.Arrays.fill(degree[i], 0);
            for (int j = 0; j < 9; j++)
            {
                java.util.Arrays.fill(remainingValues[i][j], true);
            }
        }
        // for (int i = 0; i < 2; i++)
        // {
        //     java.util.Arrays.fill(diagonals[i], 0);
        // }
    }

    public Sudoku(int version)
    {
        this(); // copy generic constructor
        if (version <= 1)
        {
            this.board[0][2] = 1;
            this.board[0][5] = 2;
            this.board[1][2] = 5;
            this.board[1][5] = 6;
            this.board[1][7] = 3;
            this.board[2][0] = 4;
            this.board[2][1] = 6;
            this.board[2][5] = 5;
            this.board[3][3] = 1;
            this.board[3][5] = 4;
            this.board[4][0] = 6;
            this.board[4][3] = 8;
            this.board[4][6] = 1;
            this.board[4][7] = 4;
            this.board[4][8] = 3;
            this.board[5][4] = 9;
            this.board[5][6] = 5;
            this.board[5][8] = 8;
            this.board[6][0] = 8;
            this.board[6][4] = 4;
            this.board[6][5] = 9;
            this.board[6][7] = 5;
            this.board[7][0] = 1;
            this.board[7][3] = 3;
            this.board[7][4] = 2;
            this.board[8][2] = 9;
            this.board[8][6] = 3;
        }
        if (version == 2)
        {
            this.board[0][2] = 5;
            this.board[0][4] = 1;
            this.board[1][2] = 2;
            this.board[1][5] = 4;
            this.board[1][7] = 3;
            this.board[2][0] = 1;
            this.board[2][2] = 9;
            this.board[2][6] = 2;
            this.board[2][8] = 6;
            this.board[3][0] = 2;
            this.board[3][4] = 3;
            this.board[4][1] = 4;
            this.board[4][6] = 7;
            this.board[5][0] = 5;
            this.board[5][5] = 7;
            this.board[5][8] = 1;
            this.board[6][3] = 6;
            this.board[6][5] = 3;
            this.board[7][1] = 6;
            this.board[7][3] = 1;
            this.board[8][4] = 7;
            this.board[8][7] = 5;
        }
        if (version >= 3)
        {
            this.board[0][0] = 6;
            this.board[0][1] = 7;
            this.board[1][1] = 2;
            this.board[1][2] = 5;
            this.board[2][1] = 9;
            this.board[2][3] = 5;
            this.board[2][4] = 6;
            this.board[2][6] = 2;
            this.board[3][0] = 3;
            this.board[3][4] = 8;
            this.board[3][6] = 9;
            this.board[4][6] = 8;
            this.board[4][8] = 1;
            this.board[5][3] = 4;
            this.board[5][4] = 7;
            this.board[6][2] = 8;
            this.board[6][3] = 6;
            this.board[6][7] = 9;
            this.board[7][7] = 1;
            this.board[8][0] = 1;
            this.board[8][2] = 6;
            this.board[8][4] = 5;
            this.board[8][7] = 7;

        }
        this.scanBoard();
    }

    public int getGroup(int row, int col)
    {
        //find group using truncation. ex: the final coordinate, (8, 8) would be placed in the final group, group 8 because 8/3 -> 2, so 2 + 2*3 = 8
        return (row/3 + (col/3)*3);
    }

    public void scanBoard()
    {
        int temp;
        int group;
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                temp = this.board[i][j];
                if (temp != 0) //if element is filled
                {
                    this.MRV[i][j] = 99;
                    this.tiles--;
                    this.rows[i][temp] = true; //check rows
                    this.cols[j][temp] = true; //check column
                    group = getGroup(i, j);
                    this.groups[group][temp] = true;//check group
                    //remove the value of temp from the possibilities of all its neighbors
                    for (int k = 0; k < 9; k++)
                    {
                        //increase degree of neighbors
                        this.degree[i][k]++;
                        this.degree[k][j]++; //this will double count any neighbors in the same row and group and column, but it gets the job done
                        //check anything in the same row as [i][j]
                        if (this.remainingValues[i][k][temp] == true)
                        {
                            this.remainingValues[i][k][temp] = false;
                            this.MRV[i][k]--;
                            //after decrementing the remaining values, if this coordinate has less remaining values than the current one in the queue, make it the front OR if it has equal remaining values but has a higher degree
                            if (this.MRV[i][k] < this.priority.getValue() || (this.MRV[i][k] == this.priority.getValue() && this.degree[this.priority.getRow()][this.priority.getCol()] < this.degree[i][k]))
                            {
                                this.priority.setValue(this.MRV[i][k]);
                                this.priority.setRow(i);
                                this.priority.setCol(k);
                            }
                        }
                        //check anything in the same column as [i][j]
                        if (this.remainingValues[k][j][temp] == true)
                        {
                            this.remainingValues[k][j][temp] = false;
                            this.MRV[k][j]--;
                            if (this.MRV[k][j] < this.priority.getValue() || (this.MRV[i][k] == this.priority.getValue() && this.degree[this.priority.getRow()][this.priority.getCol()] < this.degree[k][j]))
                            {
                                this.priority.setValue(this.MRV[k][j]);
                                this.priority.setRow(k);
                                this.priority.setCol(j);
                            }
                        }
                    }
                    // find groupmates
                    int groupX = (i/3)*3; //i values 0, 1, 2 map to 0, 3, 4, 5 map to 3, etc
                    int groupY = (j/3)*3;
                    //check anything in the same group as [i][j]
                    for (int x = groupX ; x < groupX + 3; x++)
                    {
                        for (int y = groupY; y < groupY + 3; y++)
                        {
                            this.degree[x][y]++; //increment groupmate's degree
                            if (this.remainingValues[x][y][temp] == true)
                            {
                                this.remainingValues[x][y][temp] = false;
                                this.MRV[x][y]--;
                                if (this.MRV[x][y] < this.priority.getValue() || (this.MRV[x][y] == this.priority.getValue() && this.degree[this.priority.getRow()][this.priority.getCol()] < this.degree[x][y]))
                                {
                                    this.priority.setValue(this.MRV[x][y]);
                                    this.priority.setRow(x);
                                    this.priority.setCol(y);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void printBoard()
    {
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9 ; j++)
            {
                if (i == this.priority.getRow() && j == this.priority.getCol())
                {
                    System.out.printf(" *%d", this.board[i][j]);
                }
                else
                {
                    System.out.printf("%3d", this.board[i][j]);
                }
            }
            System.out.println();

        }
    }

    public void printContents()
    {
        this.printBoard();
        System.out.println("ROWS: ");
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < this.rows[i].length; j++)
            {
                if (this.rows[i][j])
                {
                    System.out.printf("%d  ", j);
                }
                else
                {
                    System.out.printf("%d  ", 0);
                }
            }
            System.out.println();
        }
        System.out.println("COLUMNS: ");
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < this.cols[i].length; j++)
            {
                if (this.cols[i][j])
                {
                    System.out.printf("%d  ", j);
                }
                else
                {
                    System.out.printf("%d  ", 0);
                }
            }
            System.out.println();
        }
        System.out.println("GROUPS: ");
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < this.groups[i].length; j++)
            {
                if (this.groups[i][j])
                {
                    System.out.printf("%d  ", j);
                }
                else
                {
                    System.out.printf("%d  ", 0);
                }
            }
            System.out.println();
        }
        System.out.println("MRVs: ");
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < this.MRV[i].length; j++)
            {

                System.out.printf("%3d", this.MRV[i][j]);

            }
            System.out.println();
        }
        System.out.printf("lowest MRV is %d at (%d, %d)", this.priority.getValue(), this.priority.getRow(), this.priority.getCol());
        System.out.println();
        System.out.println("DEGREES: ");
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j<this.degree[i].length; j++)
            {
                System.out.printf("%3d", this.degree[i][j]);
            }
            System.out.println();
        }
        System.out.printf("need to solve %d tiles\n", this.tiles);

    }

    public boolean checkLegality(int row, int col, int value)
    {
        // printBoard();
        int group = getGroup(row, col);
        if (this.rows[row][value] == true || this.cols[col][value] == true || this.groups[group][value] == true )
        {
            // System.out.println("checkLegal is false with row,col " + row + "," + col + " with value " + value);
            return false;
        }
        else
        {
            // System.out.println("checkLegal is true with row,col " + row + "," + col + " with value " + value);
            return true;
        }
    }

    public boolean insert(int row, int col, int value) // should take a coordinate and a number, calls checkLegality
    {
        // check legality of insertion
        if (checkLegality(row, col, value) == true)
        {
            this.tiles--;
            this.board[row][col] = value;
            // after inserting, update MRV
            this.MRV[row][col] = 99;
            this.cols[col][value] = true;
            this.rows[row][value] = true;
            this.groups[getGroup(row, col)][value] = true;
            //update neighbor's MRV
            for (int i = 0; i < 9; i++)
            {
                this.degree[row][i]++;
                this.degree[i][col]++;
                //check neighbors on the same row
                if (this.remainingValues[row][i][value] == true)
                {
                    this.remainingValues[row][i][value] = false;
                    this.MRV[row][i]--;
                    if (this.MRV[row][i] < this.priority.getValue() || (this.MRV[row][i] == this.priority.getValue() && this.degree[this.priority.getRow()][this.priority.getCol()] < this.degree[row][i]))
                    {
                        this.priority.setValue(this.MRV[row][i]);
                        this.priority.setRow(row);
                        this.priority.setCol(i);
                    }
                }
                //check neighbors on the same column
                if (this.remainingValues[i][col][value] == true)
                {
                    this.remainingValues[i][col][value] = false;
                    this.MRV[i][col]--;
                    if (this.MRV[i][col] < this.priority.getValue() || (this.MRV[i][col] == this.priority.getValue() && this.degree[this.priority.getRow()][this.priority.getCol()] < this.degree[i][col]))
                    {
                        this.priority.setValue(this.MRV[i][col]);
                        this.priority.setRow(i);
                        this.priority.setCol(col);
                    }
                }
            }
            //check neighbors in the same group
            int groupX = (row/3) * 3;
            int groupY = (col/3) * 3;
            for (int x = groupX ; x < groupX + 3; x++)
            {
                for (int y = groupY; y < groupY + 3; y++)
                {
                    if (this.remainingValues[x][y][value] == true)
                    {
                        this.remainingValues[x][y][value] = false;
                        this.MRV[x][y]--;
                        if (this.MRV[x][y] < this.priority.getValue() || (this.MRV[x][y] == this.priority.getValue() && this.degree[this.priority.getRow()][this.priority.getCol()] < this.degree[x][y]))
                        {
                            this.priority.setValue(this.MRV[x][y]);
                            this.priority.setRow(x);
                            this.priority.setCol(y);
                        }
                    }
                    this.degree[x][y]++;
                }
            }
            this.MRV[row][col] = 99; //set MRV to an arbitrarily high number
            this.updatePriority(); // change to a new priority now that MRVs have changed
            // System.out.println("insert success!");
            return true;
        }
        else
        {
            return false;
        }
    }
    public void backtrackingSearch()
    {
        System.out.println("Before search:");
        printBoard();
        // printContents();
        System.out.println();
        boolean success = search(this);
        // System.out.println();
        if (success)
        {
            System.out.println("Success!");
            System.out.println("After search:");
            printBoard();
            // printContents();
        }
        else
        {
            System.out.println("Unable to find a solution.");
            printBoard();
            // printContents();
        }

    }
    public boolean search(Sudoku board)
    // DFS
    {
        if (board.tiles == 0)
        {
            return true;
        }
        //create a child process for every single possible insertion
        for (int i= 1; i < 10; i++)
        {
            // if true, create a child and insert i
            if (board.checkLegality(board.priority.getRow(), board.priority.getCol(), i) == true)
            {
                Sudoku child = new Sudoku();
                copyBoard(board, child);
 
                // System.out.println("inserting " + i + " at " + board.priority.getRow() + ", " + board.priority.getCol() + " with " + board.MRV[board.priority.getRow()][board.priority.getCol()] + " remaining value(s) and with a degree of " + board.degree[board.priority.getRow()][board.priority.getCol()] );

                //inserting
                if (child.insert(board.priority.getRow(), board.priority.getCol(), i) == false)
                {
                    System.out.println("problem occured");
                    return false; //safety net
                }
 
                boolean searchSuccess = search(child);
                if (searchSuccess == true) //recursively feed the DFS back up
                {
                    copyBoard(child, board);
                    return true; //ends the program early if a solution is found
                }
                else 
                {
                    //if false, do nothing and let i increment
                }
            }
        }
        return false;
    }
    public boolean updatePriority()
    {
        this.priority.setValue(99);
        boolean change = false;
        for (int i = 0; i < 9; i++ )
        {
            for (int j = 0; j < 9; j++)
            {
                if (this.MRV[i][j] < this.priority.getValue() || (this.MRV[i][j] == this.priority.getValue() && this.degree[this.priority.getRow()][this.priority.getCol()] < this.degree[i][j])  )
                {
                    this.priority.setValue(this.MRV[i][j]);
                    this.priority.setRow(i);
                    this.priority.setCol(j);
                    change = true;
                }
            }
        }
        return change;
    }
    public void copyBoard(Sudoku copy, Sudoku copier) // could be made to be more memory efficient
    {
        for (int i = 0; i < 9; i++)
        {
            System.arraycopy(copy.board[i], 0, copier.board[i], 0, copy.board[i].length);
            System.arraycopy(copy.MRV[i], 0, copier.MRV[i], 0, copy.MRV[i].length);
            System.arraycopy(copy.rows[i], 0, copier.rows[i], 0, copy.rows[i].length);
            System.arraycopy(copy.cols[i], 0, copier.cols[i], 0, copy.cols[i].length);
            System.arraycopy(copy.groups[i], 0, copier.groups[i], 0, copy.groups[i].length);
            System.arraycopy(copy.degree[i], 0, copier.degree[i], 0, copy.degree[i].length);
            for (int j= 0; j < 9; j++)
            {
                System.arraycopy(copy.remainingValues[i][j], 0, copier.remainingValues[i][j], 0, copy.remainingValues[i][j].length);
            }
        }
        copier.priority.copyCoordinate(copy.priority, copier.priority);
        copier.tiles = copy.tiles;
    }
}

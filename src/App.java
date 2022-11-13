public class App {
    public static void main(String[] args) throws Exception {
        // System.out.println("Hello, World!");
        Sudoku game = new Sudoku(1);
        Sudoku game2 = new Sudoku(2);
        Sudoku game3 = new Sudoku(3);

        long startTime = System.currentTimeMillis();
        System.out.println("Board 1:");
        game.backtrackingSearch();
        long endTime = System.currentTimeMillis();
        System.out.println("Board 1 execution time: " + (endTime - startTime) + " milliseconds");

        startTime = System.currentTimeMillis();
        System.out.println();
        System.out.println("Board 2:");
        game2.backtrackingSearch();
        endTime = System.currentTimeMillis();
        System.out.println("Board2 execution time: " + (endTime - startTime) + " milliseconds");

        startTime = System.currentTimeMillis();
        System.out.println();
        System.out.println("Board 3:");
        game3.backtrackingSearch();
        endTime = System.currentTimeMillis();
        System.out.println("Board3 execution time: " + (endTime - startTime) + " milliseconds");


        System.exit(0);

    }
}

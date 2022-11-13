public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        Sudoku game = new Sudoku(1);
        Sudoku game2 = new Sudoku(2);
        Sudoku game3 = new Sudoku(3);
        // game.backtrackingSearch();
        // game2.backtrackingSearch();
        game3.backtrackingSearch();

        System.exit(0);

    }
}

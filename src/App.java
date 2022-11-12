public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        Sudoku game = new Sudoku(1);
        // game.printBoard();
        // int a = 3;
        // int b = 2;
        // System.out.printf("2/3 is %d\n", b/a);
        // game.printContents();

        // boolean bool = game.insert(8, 8, 1);
        game.backtrackingSearch();
        // game.printContents();
        // System.out.println(bool);

        System.exit(0);

    }
}

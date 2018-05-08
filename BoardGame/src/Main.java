public class Main {

    public static void main(String arg[]){
        Game game =  new Game();
        if(game.findShortestPath(0,0)) {
            game.printBoard();
            System.out.println("Game finished successfully.");
        }
        else
            System.out.println("Game is not completed");
    }
}

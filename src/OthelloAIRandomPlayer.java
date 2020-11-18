import java.util.Random;

/**
 * AI player that uses random moves.
 */
public class OthelloAIRandomPlayer implements OthelloAI {
    @Override
    public OthelloMove chooseMove(OthelloGameState state) {
        System.out.println("Number of black tiles on the board = " + state.getBlackScore());
        System.out.println("Number of white tiles on the board = " + state.getWhiteScore());

        if(state.gameIsOver()) {
            System.out.println("Game is Over! Returning null");
            return null;
        }

        Random xRandom = new Random();
        Random yRandom = new Random();
        boolean isValidMove = false;
        OthelloMove move = null;
        while(!isValidMove) {
            int x = xRandom.nextInt(8);
            int y = yRandom.nextInt(8);
            if(state.isValidMove(x,y)) {
                System.out.println(String.format("Found valid move at co-ordination (%d, %d)", x, y));
                isValidMove = true;
                move = new OthelloMove(x, y);
            }
        }
        return move;
    }

    public int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }
}

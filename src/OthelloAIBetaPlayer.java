import java.util.*;

/**
 * Final AI Player implementation using MCTS.
 */
public class OthelloAIBetaPlayer implements OthelloAI {

    Mcts mcts = null;
    @Override
    public OthelloMove chooseMove(OthelloGameState state) {

//        System.out.println("Number of black tiles on the board = " + state.getBlackScore());
//        System.out.println("Number of white tiles on the board = " + state.getWhiteScore());
        long startTime = System.currentTimeMillis();
        if(state.gameIsOver()) {
            System.out.println("Game is Over!!! Returning null");
            return null;
        }
        if(mcts == null) {
            Player player = state.isBlackTurn() ? Player.BLACK : Player.WHITE;
            mcts = new Mcts(player, 4500);
        }
        OthelloMove move = mcts.findNextMove(state);
        System.out.println(String.format("Selecting next move as (%d, %d)", move.getRow(), move.getColumn()));
        System.out.println("Total time taken to choose move = " + (System.currentTimeMillis() - startTime) + " ms");
        return move;
    }

    /**
     * Enum class to track the player.
     */
    public enum Player {
        BLACK,
        WHITE;
    }

    /**
     * Represents a Tree Node in MCTS.
     */
    public class Node {
        OthelloGameState gameState;
        Node parent;
        List<Node> children;
        OthelloMove move;

        private int visits;
        private double winScore;

        public Node(OthelloGameState gameState) {
            this(gameState, null, null);
        }

        public Node(OthelloGameState gameState, Node parent, OthelloMove move) {
            this(gameState, parent, move, new ArrayList<>());
        }

        public Node(OthelloGameState gameState, Node parent, OthelloMove move, List<Node> children) {
            this.gameState = gameState;
            this.parent = parent;
            this.move = move;
            this.children = children;
        }

        public OthelloGameState getGameState() {
            return gameState;
        }

        public Node getParent() {
            return parent;
        }

        public List<Node> getChildren() {
            return children;
        }

        public OthelloMove getMove() {
            return move;
        }

        public int getVisits() {
            return visits;
        }

        public void addVisitCount(int visitCount) {
            this.visits += visitCount;
        }

        public double getWinScore() {
            return winScore;
        }

        public void addWinScore(double winScore) {
            this.winScore += winScore;
        }

        @Override
        public String toString() {
            return String.format("[%s -> visits=%d, score=%.2f, children=%s]",
                    move, visits, winScore, children);
        }
    }

    /**
     * Implements the MCTS algorithm for finding the next move.
     */
    public class Mcts {

        Player player;
        long maxExplorationTimeInMilliSecs;
        Random random = new Random();
        Node root = null;

        Mcts(Player player, long maxExplorationTimeInMilliSecs) {
            this.player = player;
            this.maxExplorationTimeInMilliSecs = maxExplorationTimeInMilliSecs;
        }

        public OthelloMove findNextMove(OthelloGameState state) {
            long end = System.currentTimeMillis() + maxExplorationTimeInMilliSecs;
            Node root = new Node(state.clone());
            int totalIterations = 0;
            while(System.currentTimeMillis() < end) {

                // Step 1: Selection
                Node selectedNode = selectNodeToExpand(root);

                // Step 2: Expansion
                if(!selectedNode.getGameState().gameIsOver()) {
                    expandNode(selectedNode);
                }

                // Step 3: Simulation
                Node simulationStartNode = getSimulationNode(selectedNode);
                OthelloGameState finalState = simulateRandomGameplay(simulationStartNode);

                // Step 4: Backpropagation
                backpropogation(simulationStartNode, finalState);
                totalIterations++;
            }
            System.out.println("Total number of MCTS iterations = " + totalIterations);
            //System.out.println("MCTS Tree = " + root);
            return getChildWithBestScore(root).getMove();
        }

        private int getHashCode(OthelloGameState state) {
            int[][] board = new int[8][8];
            for(int i = 0; i < 8; ++i) {
                for(int j = 0; j < 8; ++j) {
                    switch (state.getCell(i, j)) {
                        case NONE:
                            board[i][j] = 0;
                            break;
                        case BLACK:
                            board[i][j] = 1;
                            break;
                        case WHITE:
                            board[i][j] = 2;
                            break;
                        default:
                            // Not required to be handled.
                    }
                }
            }
            return Arrays.deepHashCode(board);
        }

        private Node selectNodeToExpand(Node rootNode) {
            Node node = rootNode;
            while (!node.getChildren().isEmpty()) {
                node = node.getChildren().stream()
                        .max(Comparator.comparing(c -> uctValue(c, rootNode.getVisits())))
                        .get();
            }
            return node;
        }

        private Node getSimulationNode(Node parent) {
            if(!parent.getChildren().isEmpty()) {
                return parent.getChildren().get(random.nextInt(parent.getChildren().size()));
            }
            return parent;
        }

        private void expandNode(Node parent) {
            List<OthelloMove> possibleMoves = getAllPossiblesMoves(parent.getGameState());
            possibleMoves.forEach(move -> {
                OthelloGameState childGameState = parent.getGameState().clone();
                childGameState.makeMove(move.getRow(), move.getColumn());
                Node child = new Node(childGameState, parent, move);
                parent.getChildren().add(child);
            });
        }

        private Node getChildWithBestScore(Node root) {
            return root.getChildren().stream().max(Comparator.comparing(n -> n.getWinScore())).get();
        }

        private double uctValue(Node child, int parentVisits) {
            if (child.getVisits() == 0) {
                return Integer.MAX_VALUE;
            }
            return (child.getWinScore()/ (double)child.getVisits())
                    + 1.41 * Math.sqrt(Math.log(parentVisits) / (double)child.getVisits());
        }

        private OthelloGameState simulateRandomGameplay(Node startNode) {
            OthelloMove move = startNode.getMove();

            OthelloGameState clonedState = startNode.getGameState().clone();
            //clonedState.makeMove(move.getRow(), move.getColumn());

            while(!clonedState.gameIsOver()) {
                List<OthelloMove> possiblesMoves = getAllPossiblesMoves(clonedState);
                int moveIndex = random.nextInt(possiblesMoves.size());
                clonedState.makeMove(possiblesMoves.get(moveIndex).getRow(),
                        possiblesMoves.get(moveIndex).getColumn());
            }
//            System.out.println("Final score for simulated game => Black =  " + clonedState.getBlackScore() +
//                    " and White = " + clonedState.getWhiteScore());
            return clonedState;
        }

        private void backpropogation(Node simulatedNode, OthelloGameState finalState) {
            Node tempNode = simulatedNode;
            int blackScore = finalState.getBlackScore();
            int whiteScore = finalState.getWhiteScore();
            while (tempNode != null) {
                tempNode.addVisitCount(1);
                if(blackScore > whiteScore && this.player == Player.BLACK) {
                    tempNode.addWinScore(1);
                } else if (whiteScore > blackScore && this.player == Player.WHITE) {
                    tempNode.addWinScore(1);
                } else {
                    tempNode.addWinScore(0.5); // draw
                }
                tempNode = tempNode.getParent();
            }
        }

        private List<OthelloMove> getAllPossiblesMoves(OthelloGameState state) {
            List<OthelloMove> possiblesMoves = new ArrayList();
            for(int i = 0; i < 8; ++i) {
                for(int j = 0; j < 8; ++j) {
                    if(state.isValidMove(i, j)) {
                        possiblesMoves.add(new OthelloMove(i,j));
                    }
                }
            }
            return possiblesMoves;
        }
    }
}

import java.util.*;

public class OthelloAIGammaPlayer implements OthelloAI {
    Mcts mcts = null;
    int turn = 0;
    @Override
    public OthelloMove chooseMove(OthelloGameState state) {
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
        //System.out.println(String.format("Selecting next move as (%d, %d)", move.getRow(), move.getColumn()));
        //System.out.println("Total time taken to choose move = " + (System.currentTimeMillis() - startTime) + " ms");
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
        private OthelloGameState gameState;
        private Node parent;
        private List<Node> children;
        private OthelloMove move;
        private int visits;
        private double winScore;
        private int hash;

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
            this.hash = ZobristHashing.getHash(gameState);
        }

        public OthelloGameState getGameState() {
            return gameState;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
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

        public int getHash() {
            return hash;
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
        Node root = null; // used to keep track for root

        Mcts(Player player, long maxExplorationTimeInMilliSecs) {
            this.player = player;
            this.maxExplorationTimeInMilliSecs = maxExplorationTimeInMilliSecs;
        }

        public OthelloMove findNextMove(OthelloGameState state) {
            long end = System.currentTimeMillis() + maxExplorationTimeInMilliSecs;

            root = getGameStateAsRootNode(state); // finds t
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
            System.out.println("[OthelloAIGammaPlayer] Total number of MCTS iterations = " + totalIterations);
            //System.out.println("[OthelloAIGammaPlayer] Total number of root visits = " + root.getVisits());

            //re-assign the root to the next move node
            root = getChildWithBestScore(root);
            return root.getMove();
        }

        private Node getGameStateAsRootNode(OthelloGameState state) {
            if(root == null) {
                return new Node(state.clone());
            }
            int hash = ZobristHashing.getHash(state);
            if(hash == root.getHash()) {
                return root;
            }
            Optional<Node> existingNode = root.getChildren().stream().filter(n -> n.getHash() == hash).findFirst();
            if(existingNode.isPresent()) {
//    Test code to verify that hash matches the right game state.
//                System.out.println("[Gamma] Found existing node with visits = " + existingNode.get().getVisits());
//                boolean stateMatched = true;
//                for(int i = 0; i < 8; ++i) {
//                    for (int j = 0; j < 8; ++j) {
//                        if(state.getCell(i, j) != existingNode.get().getGameState().getCell(i, j)) {
//                            stateMatched = false;
//                            System.out.println("!!! STATES DID NOT MATCH !!!!!!!!!");
//                            System.exit(0);
//                        }
//                    }
//                }
//                if(stateMatched) {
//                    System.out.println("FOUND MATCHING EXISTING NODE!!");
//                }
                Node newRoot = existingNode.get();
                newRoot.setParent(null); // remove reference of previous nodes
                return newRoot;
            }
            return new Node(state.clone());
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

    /**
     * Referenced Zobrist Hashing logic from the below GitHub code link -
     * https://github.com/avianey/bitboard4j/blob/master/bitboard4j/src/main/java/fr/avianey/bitboard4j/hash/ZobristHashing.java
     */
    public static final class ZobristHashing {
        private static final int[][] BIT_STRINGS = new int[3][64];

        static {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 64; j++) {
                    BIT_STRINGS[i][j] = (int) (((long) (Math.random() * Long.MAX_VALUE)) & 0xFFFFFFFF);
                }
            }
        }
        public static int getHash(OthelloGameState state) {
            int hash = 0;
            for(int i = 0; i < 8; ++i) {
                for(int j = 0; j < 8; ++j) {
                    switch (state.getCell(i, j)) {
                        case NONE:
                            hash = hash ^ BIT_STRINGS[0][(i*8) + j];
                            break;
                        case BLACK:
                            hash = hash ^ BIT_STRINGS[1][(i*8) + j];
                            break;
                        case WHITE:
                            hash = hash ^ BIT_STRINGS[2][(i*8) + j];
                            break;
                        default:
                            // Not required to be handled.
                    }
                }
            }
            return hash;
        }
    }
}

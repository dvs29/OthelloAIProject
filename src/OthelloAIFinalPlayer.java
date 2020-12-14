import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Final AI Player implementation using MCTS.
 */
public class OthelloAIFinalPlayer implements OthelloAI {

    Mcts mcts = null;
    @Override
    public OthelloMove chooseMove(OthelloGameState state) {

//        System.out.println("Number of black tiles on the board = " + state.getBlackScore());
//        System.out.println("Number of white tiles on the board = " + state.getWhiteScore());

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
        return move;
    }

    public enum Player {
        BLACK,
        WHITE;
    }

    public class Node {
        OthelloGameState gameState;
        Node parent;
        List<Node> children;
        OthelloMove move;

        private int visitCount;
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

        public int getVisitCount() {
            return visitCount;
        }

        public void addVisitCount(int visitCount) {
            this.visitCount += visitCount;
        }

        public double getWinScore() {
            return winScore;
        }

        public void addWinScore(double winScore) {
            this.winScore += winScore;
        }

    }

    public class Mcts {
        Player player;
        long maxExplorationTimeInMilliSecs;

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
                Player player = simulateRandomGameplay(simulationStartNode);

                // Step 4: Backpropagation
                backpropogation(simulationStartNode, this.player == player);
                totalIterations++;
            }
            System.out.println("Total Iterations = " + totalIterations);
            return getChildWithBestScore(root).getMove();
        }

        private Node selectNodeToExpand(Node rootNode) {
            Node node = rootNode;
            while (!node.getChildren().isEmpty()) {
                node = node.getChildren().stream()
                        .max(Comparator.comparing(c -> uctValue(c, rootNode.getVisitCount())))
                        .get();
            }
            return node;
        }

        private Node getSimulationNode(Node parent) {
            if(!parent.getChildren().isEmpty()) {
                Random random = new Random();
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

        public double uctValue(Node child, int parentVisits) {
            if (child.getVisitCount() == 0) {
                return Integer.MAX_VALUE;
            }
            return (child.getWinScore()/ (double)child.getVisitCount())
                    + 1.41 * Math.sqrt(Math.log(parentVisits) / (double)child.getVisitCount());
        }

        private Player simulateRandomGameplay(Node startNode) {
            OthelloMove move = startNode.getMove();

            OthelloGameState clonedState = startNode.getGameState().clone();
            //clonedState.makeMove(move.getRow(), move.getColumn());
            Random random = new Random();

            while(!clonedState.gameIsOver()) {
                List<OthelloMove> possiblesMoves = getAllPossiblesMoves(clonedState);
                int moveIndex = random.nextInt(possiblesMoves.size());
                clonedState.makeMove(possiblesMoves.get(moveIndex).getRow(),
                        possiblesMoves.get(moveIndex).getColumn());
            }
//            System.out.println("Final score for simulated game => Black =  " + clonedState.getBlackScore() +
//                    " and White = " + clonedState.getWhiteScore());
            if(clonedState.getWhiteScore() > clonedState.getBlackScore()) {
                return Player.WHITE;
            } else {
                return Player.BLACK;
            }
        }

        private void backpropogation(Node simulatedNode, boolean win) {
            Node tempNode = simulatedNode;
            while (tempNode != null) {
                tempNode.addVisitCount(1);
                if (win)
                    tempNode.addWinScore(10);
                tempNode = tempNode.getParent();
            }
        }

        private List<OthelloMove> getAllPossiblesMoves(OthelloGameState state) {
            List<OthelloMove> possiblesStates = new ArrayList();
            for(int i = 0; i < 8; ++i) {
                for(int j = 0; j < 8; ++j) {
                    if(state.isValidMove(i, j)) {
                        possiblesStates.add(new OthelloMove(i,j));
                    }
                }
            }
            return possiblesStates;
        }
    }
}
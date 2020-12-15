//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class OthelloFrame extends JFrame {
    private static final String NO_SCORE = "No game in progress";
    private static final String CLICK_HERE_TO_START_GAME = "Click here to start game";
    private JLabel topLabel;
    private OthelloFrame.BoardPanel boardPanel;
    private JLabel bottomLabel;
    private OthelloNewGameDialog othelloNewGameDialog;

    public OthelloFrame() {
        super("COMP 670 AI Game Project: Othello (**Multi-bot player**)");
        this.configureUI();
        this.buildUI();
    }

    private void configureUI() {
        Dimension dimension = new Dimension(500, 500);
        this.getContentPane().setMinimumSize(dimension);
        this.getContentPane().setPreferredSize(dimension);
        this.getContentPane().setBackground(Color.BLACK);
        this.setDefaultCloseOperation(2);
        this.setLayout((LayoutManager)null);
        this.setResizable(false);
        this.pack();
    }

    private void buildUI() {
        this.topLabel = new JLabel("No game in progress");
        this.topLabel.setFont(new Font("SansSerif", 1, 13));
        this.topLabel.setForeground(Color.WHITE);
        this.topLabel.setOpaque(false);
        this.topLabel.setHorizontalAlignment(0);
        this.topLabel.setBounds(10, 10, 480, 15);
        this.getContentPane().add(this.topLabel);
        this.boardPanel = new OthelloFrame.BoardPanel(10, 450, 10, 410, 55, 50);
        this.boardPanel.setBounds(20, 40, 460, 420);
        this.getContentPane().add(this.boardPanel);
        this.bottomLabel = new JLabel("");
        this.bottomLabel.setFont(new Font("SansSerif", 1, 13));
        this.bottomLabel.setForeground(Color.WHITE);
        this.bottomLabel.setOpaque(false);
        this.bottomLabel.setHorizontalAlignment(0);
        this.bottomLabel.setBounds(10, 475, 475, 15);
        this.bottomLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                OthelloFrame.this.boardPanel.bottomLabelClicked();
            }
        });
        this.getContentPane().add(this.bottomLabel);
    }

    private class BoardPanel extends JPanel implements OthelloBoardChangeListener {
        private int leftX;
        private int rightX;
        private int topY;
        private int bottomY;
        private int xStep;
        private int yStep;
        private OthelloGameState currentState;
        private OthelloAI blackAI;
        private OthelloAI whiteAI;
        private boolean lastBlackIsHuman;
        private boolean lastWhiteIsHuman;
        private boolean lastAnimateTiles;
        public int blackWins;
        public int whiteWins;
        public int draws;
        public int totalGames;

        public BoardPanel(int leftX, int rightX, int topY, int bottomY, int xStep, int yStep) {
            this.leftX = leftX;
            this.rightX = rightX;
            this.topY = topY;
            this.bottomY = bottomY;
            this.xStep = xStep;
            this.yStep = yStep;
            this.setBackground(new Color(0, 128, 0));
            this.currentState = null;
            this.blackAI = null;
            this.whiteAI = null;
            this.enableEvents(16L);
            this.addMouseListener(new OthelloFrame.BoardPanel.BoardPanelMouseAdapter());
            this.addMouseMotionListener(new OthelloFrame.BoardPanel.BoardPanelMouseAdapter());
            this.lastBlackIsHuman = true;
            this.lastWhiteIsHuman = true;
            this.lastAnimateTiles = true;
        }

        public void paint(Graphics graphics) {
            super.paint(graphics);
            Graphics2D graphics2D = (Graphics2D)graphics;
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (this.currentState == null) {
                graphics2D.setFont(new Font("SansSerif", 1, 14));
                graphics2D.setColor(Color.WHITE);
                graphics2D.drawString("Click here to start game", 150, 210);
            } else {
                OthelloFrame.this.topLabel.setText("Black: " + this.currentState.getBlackScore() +
                        "   White: " + this.currentState.getWhiteScore() +
                        "     (Total wins => Black: " + this.blackWins +
                        "   White: " + this.whiteWins + ")");
                int row;
                int col;
                if (this.currentState.gameIsOver()) {
                    row = this.currentState.getBlackScore();
                    col = this.currentState.getWhiteScore();
                    String result = row > this.currentState.getWhiteScore() ? "Black wins!" : (row < col ? "White wins!" : " it's a draw!");
                    if(row == col) draws++;
                    else if(row > col) blackWins++;
                    else whiteWins++;
                    totalGames++;
                    System.out.println(String.format("TotalGames = %d, blackWins = %d, whiteWins = %d",
                            totalGames, blackWins, whiteWins));
                    OthelloFrame.this.bottomLabel.setText("Game over; " + result + "  (Click here to start a new game.)");
                    try {
                        Thread.sleep(5000L);
                        OthelloFrame.this.bottomLabel.setText("Game over; " + result + "  (Automatically starting a new game...)");
                        Thread.sleep(1500L);
                    } catch(InterruptedException e) {

                    } finally {
                        automateNewGame();
                    }

                } else {
                    String whoseTurn = this.currentState.isBlackTurn() ? (this.blackAI == null ? "Black's turn" : "Black AI is considering its move") : (this.whiteAI == null ? "White's turn" : "White AI is considering its move");
                    OthelloFrame.this.bottomLabel.setText("Game in progress; " + whoseTurn);
                }

                graphics2D.setColor(Color.BLACK);

                for(row = 0; row <= 8; ++row) {
                    graphics2D.drawLine(this.leftX, this.topY + row * this.yStep, this.rightX, this.topY + row * this.yStep);
                }

                for(row = 0; row <= 8; ++row) {
                    graphics2D.drawLine(this.leftX + row * this.xStep, this.topY, this.leftX + row * this.xStep, this.bottomY);
                }

                for(row = 0; row < 8; ++row) {
                    for(col = 0; col < 8; ++col) {
                        OthelloCell cell = this.currentState.getCell(row, col);
                        if (cell == OthelloCell.WHITE) {
                            graphics2D.setColor(Color.WHITE);
                        } else if (cell == OthelloCell.BLACK) {
                            graphics2D.setColor(Color.BLACK);
                        }

                        if (cell != OthelloCell.NONE) {
                            graphics2D.fillOval(this.leftX + col * this.xStep + 5, this.topY + row * this.yStep + 5, this.xStep - 10, this.yStep - 10);
                        }
                    }
                }
            }

        }

        public void boardChanged(OthelloGameState othelloGameState) {
            this.currentState = othelloGameState;
            if (this.lastAnimateTiles) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        BoardPanel.this.repaint();
                    }
                });

                try {
                    Thread.sleep(400L);
                } catch (InterruptedException var3) {
                }
            }

        }

        public void newTurn(OthelloGameState othelloGameState) {
            this.currentState = othelloGameState;
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    BoardPanel.this.repaint();
                }
            });
            if (!this.currentState.gameIsOver()) {
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        if (BoardPanel.this.blackAI != null && BoardPanel.this.currentState.isBlackTurn()) {
                            this.aiMove(BoardPanel.this.blackAI);
                        } else if (BoardPanel.this.whiteAI != null && !BoardPanel.this.currentState.isBlackTurn()) {
                            this.aiMove(BoardPanel.this.whiteAI);
                        }

                    }

                    private void aiMove(OthelloAI othelloAI) {
                        try {
                            OthelloMove othelloMove = othelloAI.chooseMove(BoardPanel.this.currentState.clone());
                            if (othelloMove == null) {
                                JOptionPane.showMessageDialog(BoardPanel.this, "ERROR: AI returned null OthelloMove!\nchooseMove() is not permitted to return null.", "Null OthelloMove", 0);
                            } else {
                                BoardPanel.this.currentState.makeMove(othelloMove.getRow(), othelloMove.getColumn());
                            }
                        } catch (Exception var3) {
                            var3.printStackTrace();
                            JOptionPane.showMessageDialog(BoardPanel.this, "ERROR: AI threw an exception\nSee console for more information", "AI Exception", 0);
                        }

                    }
                });
                thread.setDaemon(true);
                thread.start();
            }

        }
        private void automateNewGame() {
            this.lastBlackIsHuman = othelloNewGameDialog.blackIsHuman();
            this.blackAI = this.lastBlackIsHuman ? null
                    : (othelloNewGameDialog.blackIsCPU1() ? this.createAI("CPU1") : this.createAI("CPU2"));
            this.lastWhiteIsHuman = othelloNewGameDialog.whiteIsHuman();
            this.whiteAI = this.lastWhiteIsHuman ? null
                    : (othelloNewGameDialog.whiteIsCPU1() ? this.createAI("CPU1"): this.createAI("CPU2"));
            this.lastAnimateTiles = othelloNewGameDialog.animateTiles();
            OthelloGameStateImpl othelloGameStateImpl = new OthelloGameStateImpl();
            othelloGameStateImpl.addBoardChangeListener(this);
            this.currentState = othelloGameStateImpl;
            this.repaint();
            this.newTurn(this.currentState);

        }

        private void startNewGame() {
            othelloNewGameDialog = new OthelloNewGameDialog(OthelloFrame.this, this.lastBlackIsHuman, this.lastWhiteIsHuman, this.lastAnimateTiles);
            othelloNewGameDialog.setLocationRelativeTo(this);
            othelloNewGameDialog.setVisible(true);
            if (othelloNewGameDialog.okPressed()) {
                this.lastBlackIsHuman = othelloNewGameDialog.blackIsHuman();
                this.blackAI = this.lastBlackIsHuman ? null
                        : (othelloNewGameDialog.blackIsCPU1() ? this.createAI("CPU1") : this.createAI("CPU2"));
                this.lastWhiteIsHuman = othelloNewGameDialog.whiteIsHuman();
                this.whiteAI = this.lastWhiteIsHuman ? null
                        : (othelloNewGameDialog.whiteIsCPU1() ? this.createAI("CPU1"): this.createAI("CPU2"));
                this.lastAnimateTiles = othelloNewGameDialog.animateTiles();
                OthelloGameStateImpl othelloGameStateImpl = new OthelloGameStateImpl();
                othelloGameStateImpl.addBoardChangeListener(this);
                this.currentState = othelloGameStateImpl;
                this.repaint();
                this.newTurn(this.currentState);
            }

        }

        private void bottomLabelClicked() {
            if (this.currentState != null && this.currentState.gameIsOver()) {
                this.startNewGame();
            }

        }

        private OthelloAI createAI() {
            OthelloAI othelloAI = (new OthelloAIFactory()).createOthelloAI();
            if (othelloAI == null) {
                JOptionPane.showMessageDialog(OthelloFrame.this, "Your OthelloAIFactory().createOthelloAI() method returned null.\nBe sure to write the line of code into that method that creates\nan object of your AI class.", "OthelloAIFactory not implemented", 0);
            }

            return othelloAI;
        }

        private OthelloAI createAI(String botName) {
            OthelloAI othelloAI = (new OthelloAIFactory()).createOthelloAIs().get(botName);
            if (othelloAI == null) {
                JOptionPane.showMessageDialog(OthelloFrame.this, "Your OthelloAIFactory().createOthelloAI() method returned null.\nBe sure to write the line of code into that method that creates\nan object of your AI class.", "OthelloAIFactory not implemented", 0);
            }

            return othelloAI;
        }

        private class BoardPanelMouseAdapter implements MouseListener, MouseMotionListener {
            private BoardPanelMouseAdapter() {
            }

            public void mouseClicked(MouseEvent mouseEvent) {
                if (BoardPanel.this.currentState == null) {
                    BoardPanel.this.startNewGame();
                } else {
                    if (BoardPanel.this.currentState.isBlackTurn() && BoardPanel.this.blackAI != null || !BoardPanel.this.currentState.isBlackTurn() && BoardPanel.this.whiteAI != null) {
                        return;
                    }

                    final int n2 = this.getRow(mouseEvent);
                    final int n;
                    if (this.validMovePosition(n2, n = this.getCol(mouseEvent))) {
                        Thread thread = new Thread(new Runnable() {
                            public void run() {
                                BoardPanel.this.currentState.makeMove(n2, n);
                            }
                        });
                        thread.setDaemon(true);
                        thread.start();
                    }
                }

            }

            public void mouseEntered(MouseEvent mouseEvent) {
                if (BoardPanel.this.currentState == null) {
                    BoardPanel.this.setCursor(Cursor.getPredefinedCursor(12));
                }

            }

            public void mouseExited(MouseEvent mouseEvent) {
                if (BoardPanel.this.currentState == null) {
                    BoardPanel.this.setCursor(Cursor.getPredefinedCursor(0));
                }

            }

            public void mouseMoved(MouseEvent mouseEvent) {
                if (BoardPanel.this.currentState != null) {
                    if (BoardPanel.this.currentState.isBlackTurn() && BoardPanel.this.blackAI == null || !BoardPanel.this.currentState.isBlackTurn() && BoardPanel.this.whiteAI == null) {
                        if (this.validMovePosition(this.getRow(mouseEvent), this.getCol(mouseEvent))) {
                            BoardPanel.this.setCursor(Cursor.getPredefinedCursor(12));
                        } else {
                            BoardPanel.this.setCursor(Cursor.getPredefinedCursor(0));
                        }
                    } else {
                        BoardPanel.this.setCursor(Cursor.getPredefinedCursor(0));
                    }

                }
            }

            private int getRow(MouseEvent mouseEvent) {
                int n = mouseEvent.getY() - BoardPanel.this.topY;
                int n2 = n / BoardPanel.this.yStep;
                int n3 = n % BoardPanel.this.yStep;
                return n2 >= 0 && n2 < 8 && n3 >= 5 && BoardPanel.this.yStep - n3 >= 5 ? n2 : -1;
            }

            private int getCol(MouseEvent mouseEvent) {
                int n = mouseEvent.getX() - BoardPanel.this.leftX;
                int n2 = n / BoardPanel.this.xStep;
                int n3 = n & BoardPanel.this.xStep;
                return n2 >= 0 && n2 < 8 && n3 >= 5 && BoardPanel.this.xStep - n3 >= 5 ? n2 : -1;
            }

            private boolean validMovePosition(int n, int n2) {
                return n != -1 && n2 != -1 && BoardPanel.this.currentState.isValidMove(n, n2);
            }

            public void mouseDragged(MouseEvent mouseEvent) {
            }

            public void mousePressed(MouseEvent mouseEvent) {
            }

            public void mouseReleased(MouseEvent mouseEvent) {
            }
        }
    }
}

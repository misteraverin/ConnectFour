import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*
    Connect-Four - a simple game with nice GUi
 */
@SuppressWarnings("serial")
public class ConnectFour extends JFrame {

    // constants for the board
    public static final int ROWS = 6;
    public static final int COLS = 7;
    public static final int CELL_SIZE = 100;
    public static final int CANVAS_WIDTH = CELL_SIZE * COLS;
    public static final int CANVAS_HEIGHT = CELL_SIZE * ROWS;
    public static final int GRID = 8;
    public static final int CELL_PADDING = CELL_SIZE / 6;
    public static final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2;
    public static final int SYMBOL_STROKE_WIDTH = 8;


    public enum GameState {
        PLAYING, DRAW, YELLOW_WIN, RED_WIN
    }

    public enum Seed {
        EMPTY, YELLOW, RED
    }

    private Seed[][] board;
    private Seed currentPlayer;
    private GameState currentState;
    private JLabel StatusBar;

    private DrawCanvas canvas;

    public void initGame() {
        for (int i = 0; i < ROWS; i++)
            for (int j = 0; j < COLS; j++)
                board[i][j] = Seed.EMPTY;
        currentState = GameState.PLAYING;
        currentPlayer = Seed.RED;
    }

    // constructor for GUI
    public ConnectFour() {
        canvas = new DrawCanvas();
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        // setting properties to StatusBar
        StatusBar = new JLabel(" ");
        StatusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
        StatusBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));




        // make a main layout and add elements on it
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(canvas, BorderLayout.CENTER);
        cp.add(StatusBar, BorderLayout.SOUTH);





        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();

                int rowSelected = mouseY / CELL_SIZE;
                int colSelected = mouseX / CELL_SIZE;


                if (currentState == GameState.PLAYING) {
                    System.out.print(colSelected + " ");
                    if (colSelected >= 0 && colSelected < COLS && rowSelected >= 0 && rowSelected < ROWS) {
                        for (int i = ROWS - 1; i >= 0; i--) {
                            if (board[i][colSelected] == Seed.EMPTY) {
                                board[i][colSelected] = currentPlayer; // make a move into cell
                                // change player
                                updateGame(currentPlayer, rowSelected, colSelected);
                                currentPlayer = (currentPlayer == Seed.RED) ? Seed.YELLOW : Seed.RED;
                                break;
                            }
                        }
                    }
                } else {
                    initGame(); // new game because of the end
                }
                repaint();
            }

        });


        setTitle("Connect Four");
        setVisible(true); // make JFrame visible
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        board = new Seed[ROWS][COLS];
        initGame();
    }

    class DrawCanvas extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);    // fill background
            setBackground(Color.WHITE);

            // draw horizontal and vertical lines between cells
            g.setColor(Color.LIGHT_GRAY);
            for (int row = 1; row < ROWS; row++)
                g.fillRoundRect(0, CELL_SIZE * row, CANVAS_WIDTH - 1, GRID, GRID, GRID);
            for (int cols = 1; cols < COLS; cols++)
                g.fillRoundRect(CELL_SIZE * cols, 0, GRID, CANVAS_HEIGHT - 1, GRID, GRID);

            //Draw players on a board
            Graphics2D g2d = (Graphics2D)g;
            g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));

            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    int x1 = col * CELL_SIZE + CELL_PADDING;
                    int y1 = row * CELL_SIZE + CELL_PADDING;
                    if (board[row][col] == Seed.RED) {
                        g2d.setColor(Color.RED);
                        g2d.drawOval(x1, y1, SYMBOL_SIZE, SYMBOL_SIZE);
                        g2d.fillOval(x1, y1, SYMBOL_SIZE, SYMBOL_SIZE);
                    } else if (board[row][col] == Seed.YELLOW) {
                        g2d.setColor(Color.YELLOW);
                        g2d.drawOval(x1, y1, SYMBOL_SIZE, SYMBOL_SIZE);
                        g2d.fillOval(x1, y1, SYMBOL_SIZE, SYMBOL_SIZE);
                    }
                }
            }

            // write text for StatusBar
            if (currentState == GameState.PLAYING) {
                StatusBar.setForeground(Color.BLACK);
                if (currentPlayer == Seed.RED)
                    StatusBar.setText("RED's turn to make a move");
                else
                    StatusBar.setText("Yellow's turn to make a move");
            } else if (currentState == GameState.YELLOW_WIN) {
                StatusBar.setForeground(Color.RED);
                StatusBar.setText("Game over! Yellow wins. Click to play new game.");
            } else if (currentState == GameState.RED_WIN) {
                StatusBar.setForeground(Color.RED);
                StatusBar.setText("Game over! Red wins. Click to play new game.");
            } else if (currentState == GameState.DRAW) {
                StatusBar.setForeground(Color.RED);
                StatusBar.setText("Game over! Draw. Click to play new game.");
            }


        }
    }

    public boolean isDraw() {
        for (int i = 0; i < ROWS; i++)
            for (int j = 0; j < COLS; j++)
                if (board[i][j] == Seed.EMPTY)
                    return false;
        return true;
    }


    public void updateGame(Seed player, int rowSelected, int colSelected) {
        if (isDraw()) {
            currentState = GameState.DRAW;
        } else if (hasWon(player, rowSelected, colSelected))
            currentState = (player == Seed.RED) ? GameState.RED_WIN : GameState.YELLOW_WIN;
    }


    public boolean hasWon(Seed player, int rowSelected, int colSelected) {
        //check rows for 4 in a row
        int currentScore = 0;
        for (int i = 0; i < COLS; i++)
            if (board[rowSelected][i] == player) {
                currentScore++;
                if (currentScore == 4) return true;
            } else {
                currentScore = 0;
            }
        // check cols for 4 in a col
        currentScore = 0;
        for (int i = 0; i < ROWS; i++)
            if (board[i][colSelected] == player) {
                currentScore++;
                if (currentScore == 4) return true;
            } else {
                currentScore = 0;
            }


        currentScore = 1;
        for (int up = 1; rowSelected + up < ROWS && colSelected + up < COLS; up++)
            if (board[rowSelected + up][colSelected + up] != player)
                break;
            else
                currentScore++;
        if (currentScore >= 4)
            return true;
        currentScore = 1;
        for (int down = 1; rowSelected - down >= 0&& colSelected - down >= 0; down++)
            if (board[rowSelected - down][colSelected - down] != player) break;
            else currentScore++;
        if (currentScore >= 4)
            return true;


        // check main-diagonal
        currentScore = 1;
        for (int up = 1; rowSelected - up >= 0 && colSelected + up < COLS; up++)
            if (board[rowSelected - up][colSelected + up] != player)
                break;
            else
                currentScore++;
        if(currentScore >= 4) return true;
        currentScore = 1;
        for (int down = 1; rowSelected + down < ROWS && colSelected - down >= 0; down++)
            if (board[rowSelected + down][colSelected - down] != player) break;
            else currentScore++;
        return currentScore >= 4;
    }


    public static void main(String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ConnectFour(); // constructs new game
            }
        });
    }
}

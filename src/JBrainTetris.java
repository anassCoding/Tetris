import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JBrainTetris extends JTetris {

    public JBrainTetris() {
        super(16);
        mBrain = new DefaultBrain();
    }

    public JComponent createControlPanel() {
        super.createControlPanel();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // COUNT
        countLabel = new JLabel("0");
        panel.add(countLabel);

        // SCORE
        scoreLabel = new JLabel("0");
        panel.add(scoreLabel);

        // TIME
        timeLabel = new JLabel(" ");
        panel.add(timeLabel);

        panel.add(Box.createVerticalStrut(12));

        // START button
        startButton = new JButton("Start");
        panel.add(startButton);
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        // STOP button
        stopButton = new JButton("Stop");
        panel.add(stopButton);
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopGame();
            }
        });

        enableButtons();

        JPanel row = new JPanel();

        // SPEED slider
        panel.add(Box.createVerticalStrut(12));
        row.add(new JLabel("Speed:"));
        speed = new JSlider(0, 200, 75); // min, max, current
        speed.setPreferredSize(new Dimension(100, 15));

        updateTimer();
        row.add(speed);

        panel.add(row);
        speed.addChangeListener(new ChangeListener() {
            // when the slider changes, sync the timer to its value
            public void stateChanged(ChangeEvent e) {
                updateTimer();
            }
        });

        testButton = new JCheckBox("Test sequence");
        panel.add(testButton);


        panel.add(new JLabel("Brain:"));
        brainMode = new JCheckBox("Brain active");
        panel.add(brainMode);

        JPanel row2 = new JPanel();
        row2.add(new JLabel("Adversaire"));
        adversaire = new JSlider(0, 100, 0);

        row2.add(adversaire);
        panel.add(row2);

        return panel;

    }

    @Override
    public Piece pickNextPiece(){

        Random random = new Random();
        int rdm = random.nextInt(100);
        Piece newPiece = new Piece(pieces[0]);

        if (rdm < adversaire.getValue()){ // on choisit aléatoirement
            rdm = random.nextInt(7);
            newPiece = new Piece(this.pieces[rdm]);
        } else { // on choisit la pire piece

            double max = -1;
            Board sandbox = new Board(board);

            // sandbox play for each piece and take the worst
            for (Piece p: pieces){

                Brain.Move bestMove = mBrain.bestMove(sandbox, p, sandbox.getHeight());

                this.newPiece = bestMove.piece;
                this.newX = bestMove.x;
                this.newY = bestMove.y;

                sandbox.commit();
                sandbox.place(newPiece, newX, newY);

                double rate = mBrain.rateBoard(sandbox);
                if (rate > max) { // bigger rate is bad
                    max = rate;
                    newPiece = new Piece(p);
                }

                sandbox = new Board(board);

            }

        }

        return newPiece;
    }

    public void tick(int verb) {

        // Etrangement le CheckButton semble inversé (testé sous linux)
        // et affiche une case cochée quand isSelected renvoie false.
        if (!brainMode.isSelected()){
            super.tick(verb);
            return;
        }

        if (currentPiece != null) {
            this.board.undo(); // remove the piece from its old position
        }

        newPiece = currentPiece;
        this.newX = currentX;
        this.newY = currentY;

        // On calcule la meilleur action
        Brain.Move bestMove = mBrain.bestMove(this.board, this.newPiece, this.board.getHeight());

        this.newPiece = bestMove.piece;
        this.newX = bestMove.x;
        this.newY = bestMove.y;

        // try out the new position (rolls back if it doesn't work)
        int result = setCurrent(newPiece, newX, newY);

        // if row clearing is going to happen, draw the
        // whole board so the green row shows up
        if (result == Board.PLACE_ROW_FILLED) {
            int cleared = board.clearRows();
            if (cleared > 0) {
                // score goes up by 5, 10, 20, 40 for row clearing
                // clearing 4 gets you a beep!
                switch (cleared) {
                    case 1:
                        score += 5;
                        break;
                    case 2:
                        score += 10;
                        break;
                    case 3:
                        score += 20;
                        break;
                    case 4:
                        score += 40;
                        Toolkit.getDefaultToolkit().beep();
                        break;
                    default:
                        score += 50; // could happen with non-standard pieces
                }
                updateCounters();
                repaint(); // repaint to show the result of the row clearing
            }
            this.repaint();
        }

        boolean failed = (result >= Board.PLACE_OUT_BOUNDS);

        // if it didn't work, put it back the way it was
        if (failed) {
            if (currentPiece != null) {
                board.place(currentPiece, currentX, currentY);
            }
            repaintPiece(currentPiece, currentX, currentY);
        }


        // if the board is too tall, we've lost
        if (this.board.getMaxHeight() > this.board.getHeight() - TOP_SPACE) {
            this.stopGame();
        } else {
            // Otherwise add a new piece and keep playing
            this.addNewPiece();
        }

    }

    public static void main (String[] args) {
        JBrainTetris newGame;

        // Set GUI Look And Feel Boilerplate.
        // Do this incantation at the start of main() to tell Swing
        // to use the GUI LookAndFeel of the native platform. It's ok
        // to ignore the exception.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        JBrainTetris tetris = new JBrainTetris();
        JFrame frame = JTetris.createFrame(tetris);
        frame.setVisible(true);
    }


    private DefaultBrain mBrain;
    private JCheckBox brainMode;
    private JSlider adversaire;
}
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

class GameOfLife {


    private final static String NAME_OF_GAME = "Conway's Game of Life";
    private final static int START_LOCATION = 200;
    private final static int LIFE_SIZE = 50;
    private final static int POINT_RADIUS = 10;
    private final static int SIZE = (LIFE_SIZE + 1) * POINT_RADIUS - 3;
    private final static int BTH_PANEL_HEIGHT = 58;
    private boolean[][] lifeGeneration = new boolean[LIFE_SIZE][LIFE_SIZE];
    private boolean[][] nextGeneration = new boolean[LIFE_SIZE][LIFE_SIZE];
    private Random random = new Random();
    private volatile boolean goNextGeneration = false;
    private final int SHOWDELAY = 200;


    private JFrame frame;
    private JPanel btnPanel;
    private Canvas canvasPanel;

    void go() {
        frame = new JFrame(NAME_OF_GAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(SIZE, SIZE + BTH_PANEL_HEIGHT);
        frame.setLocation(START_LOCATION, START_LOCATION);
        frame.setResizable(false);

        canvasPanel = new Canvas();
        canvasPanel.setBackground(Color.WHITE);

        JButton fillButton = new JButton("Fill");
        fillButton.addActionListener(new FillButtonListener());

        JButton stepButton = new JButton("Step");
        stepButton.addActionListener(e -> {
            processOfLife();
            canvasPanel.repaint();
        });

        final JButton goButton = new JButton("Play");
        goButton.addActionListener(e -> {
            goNextGeneration = !goNextGeneration;
            goButton.setText(goNextGeneration ? "Stop" : "Play");
        });
        btnPanel = new JPanel();

        btnPanel.add(fillButton);
        btnPanel.add(stepButton);
        btnPanel.add(goButton);


        frame.getContentPane().add(BorderLayout.CENTER, canvasPanel);
        frame.getContentPane().add(BorderLayout.SOUTH, btnPanel);
        frame.setVisible(true);

        while (true) {
            if (goNextGeneration) {
                processOfLife();
                canvasPanel.repaint();
                try {
                    Thread.sleep(SHOWDELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private class FillButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < LIFE_SIZE; i++) {
                for (int j = 0; j < LIFE_SIZE; j++) {
                    lifeGeneration[i][j] = random.nextBoolean();
                }
            }
            canvasPanel.repaint();
        }
    }

    private void processOfLife() {
        for (int x = 0; x < LIFE_SIZE; x++) {
            for (int y = 0; y < LIFE_SIZE; y++) {
                int count = countNeighbors(x, y);
                nextGeneration[x][y] = lifeGeneration[x][y];
                nextGeneration[x][y] = (count == 3) || nextGeneration[x][y];
                nextGeneration[x][y] = ((count >= 2) && (count <= 3)) && nextGeneration[x][y];
            }
        }
        for (int x = 0; x < LIFE_SIZE; x++) {
            System.arraycopy(nextGeneration[x], 0, lifeGeneration[x], 0, LIFE_SIZE);
        }
    }

    private int countNeighbors(int x, int y) {
        int count = 0;
        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                int nX = x + dx;
                int nY = y + dy;
                nX = (nX < 0) ? LIFE_SIZE - 1 : nX;
                nY = (nY < 0) ? LIFE_SIZE - 1 : nY;
                nX = (nX > LIFE_SIZE - 1) ? 0 : nX;
                nY = (nY > LIFE_SIZE - 1) ? 0 : nY;
                count += (lifeGeneration[nX][nY]) ? 1 : 0;
            }
        }
        if (lifeGeneration[x][y]) {
            count--;
        }
        return count;
    }


    private class Canvas extends JPanel {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (int i = 0; i < LIFE_SIZE; i++) {
                for (int j = 0; j < LIFE_SIZE; j++) {
                    if (lifeGeneration[i][j]) {
                        g.fillOval(i * POINT_RADIUS, j * POINT_RADIUS, POINT_RADIUS, POINT_RADIUS);
                    }
                }
            }
        }
    }
}

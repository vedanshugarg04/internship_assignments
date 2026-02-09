import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PongGame extends JFrame {

    public PongGame() {
        this.setTitle("Multithreaded Pong - Java Swing");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GamePanel panel = new GamePanel();
        this.add(panel);
        this.pack();

        this.setLocationRelativeTo(null);
        this.setVisible(true);

        panel.requestFocusInWindow();

        panel.startGame();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PongGame());
    }
}

class GamePanel extends JPanel implements Runnable, KeyListener {

    static final int WIDTH = 800;
    static final int HEIGHT = 500;
    static final int BALL_DIAMETER = 20;
    static final int PADDLE_WIDTH = 25;
    static final int PADDLE_HEIGHT = 100;

    private Thread gameThread;
    private volatile boolean isRunning = false;

    private int ballX = WIDTH / 2;
    private int ballY = HEIGHT / 2;
    private int ballXSpeed = -5;
    private int ballYSpeed = 4;

    private int player1Y = HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private int player2Y = HEIGHT / 2 - PADDLE_HEIGHT / 2;

    private boolean wPressed = false;
    private boolean sPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;

    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);
    }

    public void startGame() {
        gameThread = new Thread(this);
        isRunning = true;
        gameThread.start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 100.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1) {
                move();
                checkCollision();
                repaint();
                delta--;
            }
        }
    }

    private void move() {
        int paddleSpeed = 8;

        if (wPressed && player1Y > 0)
            player1Y -= paddleSpeed;
        if (sPressed && player1Y < HEIGHT - PADDLE_HEIGHT)
            player1Y += paddleSpeed;

        if (upPressed && player2Y > 0)
            player2Y -= paddleSpeed;
        if (downPressed && player2Y < HEIGHT - PADDLE_HEIGHT)
            player2Y += paddleSpeed;

        ballX += ballXSpeed;
        ballY += ballYSpeed;
    }

    private void checkCollision() {
        if (ballY <= 0 || ballY >= HEIGHT - BALL_DIAMETER) {
            ballYSpeed = -ballYSpeed;
        }

        Rectangle ballRect = new Rectangle(ballX, ballY, BALL_DIAMETER, BALL_DIAMETER);
        Rectangle p1Rect = new Rectangle(0, player1Y, PADDLE_WIDTH, PADDLE_HEIGHT);
        Rectangle p2Rect = new Rectangle(WIDTH - PADDLE_WIDTH, player2Y, PADDLE_WIDTH, PADDLE_HEIGHT);

        if (ballRect.intersects(p1Rect)) {
            ballXSpeed = Math.abs(ballXSpeed);
        }

        if (ballRect.intersects(p2Rect)) {
            ballXSpeed = -Math.abs(ballXSpeed);
        }

        if (ballX <= 0 || ballX >= WIDTH) {
            ballX = WIDTH / 2;
            ballY = HEIGHT / 2;
            ballXSpeed = -ballXSpeed;
            System.out.println("GOAL SCORRED!");
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Toolkit.getDefaultToolkit().sync();

        g.setColor(Color.WHITE);

        g.fillRect(0, player1Y, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillRect(WIDTH - PADDLE_WIDTH, player2Y, PADDLE_WIDTH, PADDLE_HEIGHT);

        g.fillOval(ballX, ballY, BALL_DIAMETER, BALL_DIAMETER);

        g.drawLine(WIDTH/2, 0, WIDTH/2, HEIGHT);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) wPressed = true;
        if (key == KeyEvent.VK_S) sPressed = true;
        if (key == KeyEvent.VK_UP) upPressed = true;
        if (key == KeyEvent.VK_DOWN) downPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) wPressed = false;
        if (key == KeyEvent.VK_S) sPressed = false;
        if (key == KeyEvent.VK_UP) upPressed = false;
        if (key == KeyEvent.VK_DOWN) downPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}

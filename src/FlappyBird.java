import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int frameWidth = 360;
    int frameHeight = 640;

    // Images
    Image backgroundImage;
    Image flappyBirdImage;
    Image topPipeImage;
    Image bottomPipeImage;

    int birdX = frameWidth / 8;
    int birdY = frameHeight / 2;
    int birdWidth = 32;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }

    }

    int pipeHeight = 540;
    int pipeWidth = 64;
    int pipeX = frameWidth;
    int pipeY = 0;

    boolean gameOver = false;

    class Pipe {
        int height = pipeHeight;
        int width = pipeWidth;
        int x = pipeX;
        int y = pipeY;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    Bird bird;
    Timer gameLoop;
    Timer placePipesTimer;
    int xVelocity = -4;
    int yVelocity = -9;
    int gravity = 1;
    ArrayList<Pipe> pipes;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        setFocusable(true);
        addKeyListener(this);
        backgroundImage = new ImageIcon(getClass().getResource("./Images/flappybirdbg.png")).getImage();
        flappyBirdImage = new ImageIcon(getClass().getResource("./Images/flappybird.png")).getImage();
        bottomPipeImage = new ImageIcon(getClass().getResource("./Images/bottompipe.png")).getImage();
        topPipeImage = new ImageIcon(getClass().getResource("./Images/toppipe.png")).getImage();

        bird = new Bird(flappyBirdImage);

        pipes = new ArrayList<Pipe>();

        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Draw(g);
    }

    public void Draw(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, frameWidth, frameHeight, null);
        g.drawImage(flappyBirdImage, bird.x, bird.y, birdWidth, birdHeight, null);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void placePipes() {
        int randomY = (int) (pipeY - pipeHeight / 4 - Math.random() * pipeHeight / 2);
        int passingGap = frameHeight / 4;

        Pipe topPipe = new Pipe(topPipeImage);
        topPipe.y = randomY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImage);
        bottomPipe.y = randomY + pipeHeight + passingGap;
        pipes.add(bottomPipe);
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width && // a's top left corner doesn't reach b's top right corner
                a.x + a.width > b.x && // a's top right corner passes b's top left corner
                a.y < b.y + b.height && // a's top left corner doesn't reach b's bottom left corner
                a.y + a.height > b.y; // a's bottom left corner passes b's top left corner
    }

    public void move() {
        yVelocity += gravity;
        bird.y += yVelocity;
        bird.y = Math.max(bird.y, 0);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += xVelocity;
            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                score = score + 0.5;
                pipe.passed = true;
            }
            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }
        if (bird.y > frameHeight) {
            gameOver = true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            yVelocity = -9;
            if (gameOver) {
                // restart game by resetting conditions
                bird.y = birdY;
                yVelocity = 0;
                pipes.clear();
                gameOver = false;
                score = 0;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}

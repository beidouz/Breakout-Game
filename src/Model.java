import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

public class Model {

    private ArrayList<Observer> observers;
    public Ball ball;
    public Paddle paddle;
    public ArrayList<Brick> brickList = new ArrayList<>();
    public ArrayList<BufferedImage> brickIcons = new ArrayList<>();
    private Dimension windowSize;
    public final int defaultBallWidth = 12;
    public final int defaultPaddleWidth = 60;
    public final int defaultPaddleHeight = 13;
    public final Dimension defaultWindowSize= new Dimension(550, 400);
    public int gapUnderPaddle = 7;
    public int score;
    public int lives;
    private BufferedImage powerUpBrick;
    private long powerUpTimeStamp;
    private int fps;
    private int ballSpeed;


    public Model(int fps, int speed) {
        this.observers = new ArrayList<>();
        this.windowSize = defaultWindowSize;
        this.fps = fps;
        this.ballSpeed = speed;
        // read in brick icons
        for (int i = 0; i < 4; ++i) {
            try {
                this.brickIcons.add(ImageIO.read(new File("src/icons/brick_" + (i + 1) + ".png")));
            } catch (IOException e) {
                System.out.println("Exception: failed to read in brick icons, make sure images exist");
            }
        }
        try {
            this.powerUpBrick = ImageIO.read(new File("src/icons/powerUpBrick.png"));
        } catch (IOException e) {
            System.out.printf("Exception: failed to read in powerUp brick icon, make sure image exist");
        }
        this.initializeState();
    }

    private void initializeState() {
        // initialize Paddle
        this.paddle = new Paddle(new Point((this.windowSize.width-this.defaultPaddleWidth)/2,
                                            this.windowSize.height-this.defaultPaddleHeight-this.gapUnderPaddle),
                                                this.defaultPaddleWidth, this.defaultPaddleHeight);
        // Initialize Ball
        this.ball = new Ball(new Point(this.paddle.getLocation().x+(this.paddle.getWidth()-this.defaultBallWidth)/2,
                                        this.paddle.getLocation().y-this.defaultBallWidth-1),
                                            this.defaultBallWidth, this.ballSpeed);
        // Initialize bricks
        for (int row = 0; row < 7; ++row) {
            for (int i = 0; i < 12; ++i) {
                Random rand = new Random();

                int health = rand.nextInt(4) + 1;   // 1 <= health <= 4
                BufferedImage icon = brickIcons.get(health - 1);
                Brick newBrick = new Brick(row, i, this.windowSize, health, icon);

                int powerUp = rand.nextInt(100);
                if (powerUp > 7 && powerUp < 10) { // just a random range
                    // powerUp brick -> increase paddle length
                    newBrick.setIcon(this.powerUpBrick);
                    newBrick.setSpecial(true);
                }
                this.brickList.add(newBrick);
            }
        }
        // Initialize the display text
        this.score = 0;
        this.lives = 3;
    }


    public void resizeWindow(Dimension windowSize) {
        // reposition the objects in the window
        // resize in the obj.draw()
        int paddleShift = (windowSize.width - this.windowSize.width) / 2;
        this.windowSize = windowSize;
        this.paddle.setLocation(new Point(this.paddle.getLocation().x+paddleShift, windowSize.height-this.paddle.getHeight()-this.gapUnderPaddle));
        if (!this.ball.isLaunched()) {
            Double w = this.defaultPaddleWidth * this.windowSize.getWidth() / this.defaultWindowSize.getWidth();
            this.ball.setLocation((new Point(this.paddle.getLocation().x+(w.intValue()-this.ball.getWidth())/2, this.paddle.getLocation().y-this.ball.getHeight())));
            int a = this.ball.getDx() + paddleShift;
            a = a > this.ballSpeed? this.ballSpeed : a;
            this.ball.setVelocity(a, a);
        }
        for (Brick b : brickList) {
            b.setBrick(b.getRow(), b.getColumn(), this.windowSize);
        }
        this.notifyObservers();
    }

    public Dimension getWindowSize() {
        return this.windowSize;
    }

    public void travelBall() {

        if (!ball.isLaunched()) return;

        if (this.ball.top() + this.ball.getWidth() > this.paddle.bottom() + 1) {
            System.out.println("Failed to catch ball, reset\n");
            this.ball.setVelocity(0, 0);
            this.ball.setLocation(new Point(this.paddle.getLocation().x+(this.paddle.getWidth()-this.ball.getWidth())/2, this.paddle.getLocation().y-this.ball.getWidth()-1));
            this.ball.launched = false;
            this.lives -= 1;
            if (lives == 0) {
                System.out.println("You Lost, Play again?");
                int result = JOptionPane.showConfirmDialog(null, "You Lost!\nPlay Again?","", JOptionPane.YES_NO_OPTION);
                // Yes:0 No:1
                if (result == 0) {
                    System.out.println("Starting new game ....");
                    this.initializeState();
                } else { // pressing ESC key also quits game
                    System.out.println("Quiting game ....");
                    System.exit(0);
                }
            }

            this.notifyObservers();
            return;

        } else if (this.ball.left() >= this.paddle.getLocation().x &&
                this.ball.right() <= this.paddle.right() &&
                this.ball.top()+ this.ball.getWidth() >= this.paddle.getLocation().y) {
            System.out.println("ball hit top of the paddle");
            this.ball.setVelocity(this.ball.getDx(), this.ball.getDy()*(-1));
        } else if (this.ball.top() + this.ball.getWidth() >= this.paddle.getLocation().y &&
                this.ball.bottom() <= this.paddle.bottom() &&
                this.ball.left() + this.ball.getWidth()/2 >= this.paddle.getLocation().x &&
                this.ball.left() <= this.paddle.getLocation().x) {
            System.out.println("ball hit left side of the paddle");
            if (this.ball.getDx() > 0)  this.ball.setVelocity(this.ball.getDx()*(-1), this.ball.getDy());
            this.ball.setVelocity(this.ball.getDx(), this.ball.getDy()*(-1));
        } else if (this.ball.bottom() >= this.paddle.getLocation().y &&
                this.ball.bottom() <= this.paddle.bottom() &&
                this.ball.left() + this.ball.getWidth()/2 <= this.paddle.right() &&
                this.ball.right() >= this.paddle.right()) {
            System.out.println("ball hit right side of the paddle");
            if (this.ball.getDx() < 0) this.ball.setVelocity(this.ball.getDx()*(-1), this.ball.getDy());
            this.ball.setVelocity(this.ball.getDx(), this.ball.getDy()*(-1));
        } else if ((this.ball.left() <= 0 && this.ball.getDx() < 0) ||
                   (this.ball.left() >= (this.windowSize.width - this.ball.getWidth()) && this.ball.getDx() > 0)) {
            System.out.println("ball hit side of the window");
            this.ball.setVelocity(this.ball.getDx()*(-1), this.ball.getDy());
        } else if (this.ball.top() <= 0 && this.ball.getDy() < 0){ // ball needs to be going up, or else might stuck
            System.out.println("ball hit top of the window");
            this.ball.setVelocity(this.ball.getDx(), this.ball.getDy()*(-1));
        } else {
            // ball is traveiling -> check if it hits a brick
            if (brickList.isEmpty()) {
                System.out.println("You Win! Play again?");
                int result = JOptionPane.showConfirmDialog(null, "You Win!\nPlay Again?","", JOptionPane.YES_NO_OPTION);
                // Yes:0 No:1
                if (result == 0) {
                    System.out.println("Starting another game ....");

                    this.initializeState();
                    this.notifyObservers();
                    return;

                } else { // pressing ESC key also quits game
                    System.out.println("Quiting game ....");
                    System.exit(0);
                }
            }

            ListIterator<Brick> iter = brickList.listIterator(brickList.size());
            while (iter.hasPrevious()) {
                Brick currBrick = iter.previous();

                boolean hit = false;

                if (this.ball.left()+this.ball.getWidth()/2 >= currBrick.left() &&
                        this.ball.left()+this.ball.getWidth()/2 <= currBrick.right() &&
                        this.ball.top() <= currBrick.bottom() &&
                        this.ball.bottom() > currBrick.bottom() &&
                        this.ball.getDy() < 0) {
                    System.out.println("ball hit bottom of the brick");
                    this.ball.setVelocity(this.ball.getDx(), this.ball.getDy()*(-1));
                    hit = true;
                } else if (this.ball.left()+this.ball.getWidth()/2 >= currBrick.left() &&
                        this.ball.left()+this.ball.getWidth()/2 <= currBrick.right() &&
                        this.ball.bottom() >= currBrick.top() &&
                        this.ball.top() < currBrick.top() &&
                        this.ball.getDy() > 0) {
                    System.out.println("ball hit top of the brick");
                    this.ball.setVelocity(this.ball.getDx(), this.ball.getDy()*(-1));
                    hit = true;
                } else if (this.ball.top()+this.ball.getHeight()/2 >= currBrick.top() &&
                        this.ball.top()+this.ball.getWidth()/2 <= currBrick.bottom() &&
                        this.ball.right() >= currBrick.left() &&
                        this.ball.left() < currBrick.left() &&
                        this.ball.getDx() > 0) {
                    System.out.println("ball hit left of the brick");
                    this.ball.setVelocity(this.ball.getDx()*(-1), this.ball.getDy());
                    hit = true;
                } else if (this.ball.top()+this.ball.getHeight()/2 >= currBrick.top() &&
                        this.ball.top()+this.ball.getHeight()/2 <= currBrick.bottom() &&
                        this.ball.left() <= currBrick.right() &&
                        this.ball.right() > currBrick.right() &&
                        this.ball.getDx() < 0) {
                    System.out.println("ball hit right of the brick");
                    this.ball.setVelocity(this.ball.getDx()*(-1), this.ball.getDy());
                    hit = true;
                }
                if (hit) {
                    if (currBrick.getSpecial()) {
                        // special brick ->  extend the paddle
                        System.out.println("Hit powerUp block, increase paddle length!");
                        this.paddle.expand();
                        this.powerUpTimeStamp = System.currentTimeMillis();
                        currBrick.setSpecial(false);
                    }

                    this.score += 1;
                    int newHealth = currBrick.getHealth() - 1;
                    if (newHealth == 0) {
                        iter.remove();
                    } else {
                        currBrick.setHealth(newHealth);
                        currBrick.setIcon(brickIcons.get(newHealth - 1));
                        Random rand = new Random();
                        int powerUp = rand.nextInt(10);
                        if (powerUp < 2) { // just a random range
                            // powerUp brick -> increase paddle length
                            currBrick.setIcon(this.powerUpBrick);
                            currBrick.setSpecial(true);
                        }
                    }
                }
            }
        }

        this.ball.setLocation(new Point(this.ball.left() + this.ball.getDx(),
                                            this.ball.top() + this.ball.getDy()));

        this.notifyObservers();
    }

    public void launchBall() {
        this.ball.launch(-6, -6);
        this.notifyObservers();
    }

    public void movePaddle(int xLocation) {
        int xMax = this.windowSize.width - this.paddle.getWidth();;
        this.paddle.movePaddle(Math.min(xLocation, xMax));
        if (!this.ball.isLaunched()) {
            // ball stick in the middle of the paddle
            this.ball.setLocation(new Point(this.paddle.getLocation().x+this.paddle.getWidth()/2-this.ball.getWidth()/2, this.paddle.getLocation().y-this.ball.getWidth()-1));
        }
        this.notifyObservers();
    }

    private void drawString(Graphics2D g2, String s, Color color, Point location, String type, int style, int size) {
        Double scaledFactor = (this.windowSize.getWidth() * this.windowSize.getHeight()) /
                                (this.defaultWindowSize.getWidth() * this.defaultWindowSize.getHeight());
        int scaledSize = new Double(size * scaledFactor).intValue();
        scaledSize = (scaledSize < 11) ? 11: scaledSize;
        scaledSize = (scaledSize > 17) ? 17: scaledSize;

        Double scaledX = location.getX() * this.windowSize.getWidth() / this.defaultWindowSize.getWidth();
        Double scaledY = location.getY() * this.windowSize.getHeight() / this.defaultWindowSize.getHeight();
        Point scaledLocation = new Point(scaledX.intValue(), scaledY.intValue());

        g2.setColor(color);
        g2.setFont(new Font(type, style, scaledSize));
        g2.drawString(s, scaledLocation.x, scaledLocation.y);
    }

    public void draw(Graphics2D g2) {
        if (this.paddle.isPowerUp()) {
            long currTime = System.currentTimeMillis();
            if (currTime - this.powerUpTimeStamp >= 5000) {
                System.out.println("5 seconds! Shrink paddle length to normal.");
                this.paddle.shrink();
            }
        }
        this.paddle.draw(g2, this.windowSize, this.defaultWindowSize);
        this.ball.draw(g2, this.windowSize, this.defaultWindowSize);
        for (Brick brick : this.brickList) {
            brick.draw(g2);
        }
        //TODO   take in FPS as arg
        this.drawString(g2, "FPS: " + this.fps, Color.YELLOW, new Point(5,18),"IMPACT", Font.PLAIN, 14);
        this.drawString(g2, "Score: " + this.score, Color.WHITE, new Point(5,32),"IMPACT", Font.TYPE1_FONT, 14);
        this.drawString(g2, "Lives: " + this.lives, Color.WHITE, new Point(5,46),"IMPACT", Font.PLAIN, 14);

    }

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    public void notifyObservers() {
        for (Observer observer : this.observers) {
            observer.update(this);
        }
    }

}

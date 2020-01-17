import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Paddle {

    private Point location;
    private int width;
    private int height;
    public int defaultPaddleWidth = 60;
    public final int defaultPaddleHeight = 13;
    private BufferedImage icon;
    private boolean powerUp = false;

    public Paddle(Point location, int width, int height) {
        this.location = location;
        this.width = width;
        this.height = height;
        try {
            this.icon = ImageIO.read(new File("src/icons/paddle.png"));
        } catch (IOException e) {
            System.out.println("Exception: failed to read in paddle icon, make sure all 5 icon files exist");
        }
    }

    public boolean isPowerUp() {
        return this.powerUp;
    }

    public Point getLocation() {
        return this.location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public int right() {
        return this.location.x + this.width;
    }

    public int bottom() {
        return this.location.y + this.height;
    }

    public void movePaddle(int xLocation) {
        this.location.x = xLocation;
    }

    public void expand() {
        if (!this.powerUp) {
            this.width *= 2;
            this.defaultPaddleWidth *= 2;
            this.powerUp = true;
        }
    }

    public void shrink() {
        if (this.powerUp) {
            this.width /= 2;
            this.defaultPaddleWidth /= 2;
            this.powerUp = false;
        }
    }

    protected void draw(Graphics2D g2, Dimension windowSize, Dimension defaultWindowSize) {
        Double w = this.defaultPaddleWidth * windowSize.getWidth() / defaultWindowSize.getWidth();
        Double h = this.defaultPaddleHeight * windowSize.getHeight() / defaultWindowSize.getHeight();
        this.width = w.intValue();
        this.height = h.intValue();
        g2.drawImage(this.icon, this.location.x, this.location.y, w.intValue(), h.intValue(), null);
    }

}

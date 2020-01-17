import java.awt.*;
import java.awt.image.BufferedImage;

public class Brick {
    private int row;
    private int column;
    private Point location;
    private int width;
    private int height;
    private int health;
    private BufferedImage icon;
    private boolean special;


    public Brick(int row, int column, Dimension windowSize, int health, BufferedImage icon) {
        this.row = row;
        this.column = column;
        this.setBrick(row, column, windowSize);
        this.health = health;
        this.icon = icon;
        this.special = false;
    }

    public void setBrick(int row, int column, Dimension windowSize) {
        int leftGap = windowSize.width/10;
        int topGap = windowSize.height/8;
        this.width = (windowSize.width - 2 * leftGap) / 12;
        this.height = (windowSize.height/3 - topGap) / 7;
        this.location = new Point(leftGap + column * (width + 1), topGap + row * (height + 1));
    }

    public boolean getSpecial() {
        return this.special;
    }

    public void setSpecial(boolean special) {
        if (special) this.health = 1;
        this.special = special;
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }

    public int getHealth() {
        return this.health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setIcon(BufferedImage icon) {
        this.icon = icon;
    }

    public int left() {
        return this.location.x;
    }

    public int right() {
        return this.location.x + this.width;
    }

    public int top() {
        return this.location.y;
    }

    public int bottom() {
        return this.location.y +this.height;
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(this.icon, this.location.x, this.location.y, this.width, this.height, null);
    }
}

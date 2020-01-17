import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {

    public MainView(int fps, int speed, int temp) {
        String[] choices = {"Play"};
        String info = "BREAKOUT\n\nBeidou Zhang\n20624465\n\nRules:\n" +
                      "    - Make sure the ball doesn't fall, clear all bricks!\n" +
                      "    - Click anywhere to launch the ball\n    - Use mouse cursor to control the paddle\n" +
                      "    - Hitting the special bricks will double paddle length for 5 seconds\n" +
                      "    - Press Q to pause/quit the game";
        int result = JOptionPane.showOptionDialog(null, info, "Canvas",
                                     JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                                null, choices, null);

        if (result == -1) System.exit(0);
        this.setTitle("Breakout Game");

        Model model = new Model(fps, speed);
        Canvas canvas = new Canvas(model, fps, temp);
        canvas.getPreferredSize();
        canvas.setBackground(Color.black);

        this.add(canvas);
        this.pack(); // set everything to preferred size
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}
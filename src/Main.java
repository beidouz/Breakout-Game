import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // TODO: speed and fps args
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int fps = 0;
                int speed = 0;
                int temp = 0;
                if (args.length == 2) {
                    try {
                        fps = Integer.parseInt(args[0]);
                        speed = Integer.parseInt(args[1]);
                        if (fps < 25 || fps > 60) {
                            System.err.println("fps value needs to be the 25 - 55 range");
                            System.exit(0);
                        }
                        if (speed == 1) {
                            temp = 35;
                        } else if (speed ==2) {
                            temp = 45;
                        } else if (speed == 3) {
                            temp = 60;
                        } else {
                            System.out.println("speed must be 1 or 2 or 3 (slow medium fast)");
                            System.exit(0);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("fps and spped arguments must be an integers.");
                        System.exit(0);
                    }
                } else if (args.length == 0) {
                    // default case
                    fps = 45;
                    speed = 6;
                    temp = 45;
                } else {
                    System.err.println("Must provide 2 integer arguments: fps & speed");
                    System.exit(0);
                }
                System.out.println(temp);
                MainView mainView = new MainView(fps, speed, temp);
            }
        });
    }
}
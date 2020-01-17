import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class Canvas extends JPanel implements Observer {

    private Timer fpsTimer;
    private Timer modelTimer;
    private Model model;
    private Dimension defaultSize;
    private int temp;

    public Canvas(Model model, int fps, int temp) {
        this.temp = temp;
        this.model = model;
        model.addObserver(this);
        this.defaultSize = model.getWindowSize();
        startGame();
        drawGame(fps);
        setupListeners(model);

    }

    public void startGame() {
        // timer ticks every time we want to advance a frame
        // scheduled to run every 1000/FPS ms
        this.modelTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                model.travelBall(); // this func notifies observers
            }
        };
        this.modelTimer.schedule(task, 0, (1000/this.temp));
    }

    public void drawGame(int fps) {
        // timer ticks every time we want to advance a frame
        // scheduled to run every 1000/FPS ms
        this.fpsTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                repaint(); // this func notifies observers
            }
        };
        this.fpsTimer.schedule(task, 0, (1000/fps));
    }

    private void setupListeners(Model model) {
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                System.out.println("Window resized");
                model.resizeWindow(event.getComponent().getSize());
            }
        });

        this.addMouseMotionListener(new MouseInputAdapter() {
            @Override
            public void mouseMoved(MouseEvent e){
                model.movePaddle(e.getX());
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!model.ball.isLaunched()) {
                    model.launchBall();
                }
            }
        });

        this.setFocusable(true); // doesn't work without this
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_Q) {
                    System.out.println("Pressed key: Q (quit game)");
                    int temp_dx = model.ball.getDx();
                    int temp_dy = model.ball.getDy();
                    model.ball.setVelocity(0, 0);  // pause the game
                    int result = JOptionPane.showConfirmDialog(null, "Are you sure?","Quit Game", JOptionPane.YES_NO_OPTION);
                    // Yes:0 No:1
                    if (result == 0) {
                        System.out.println("Quiting game ...");
                        System.exit(0);
                    } else { // pressing ESC key also resumes game
                        System.out.println("Resume game ...");
                        model.ball.setVelocity(temp_dx, temp_dy);
                    }
                }
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        // default size
        return this.defaultSize;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        model.draw(g2);
    }

    public void update(Object observable) {
        repaint(); // calls paint component
    }
}

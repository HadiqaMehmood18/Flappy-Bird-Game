/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package game;
import com.sun.jdi.connect.spi.Connection;
import javax.swing.*;
import java.awt.*;
import java.sql.DriverManager;
import static java.awt.Color.black;
import static java.awt.Color.white;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.Border;

// Bird class is used to create a bird object in the game and to move it around the screen
class Bird extends GameObject {
    
private boolean isRunning = false; 
    private ProxyImage proxyImage; 
    private Image background;
    private Bird bird; 
    private TubeColumn tubeColumn;
    private int score;
    private int highScore;
    private JPanel scoreboardPanel;
    private JLabel scoreboardLabel;
    private JLabel scoreboardLabel2;
   
    private Tube[] tube; 
    // Constructor

    public Bird(int x, int y) {
        super(x, y);
        if (proxyImage == null) {
            proxyImage = new ProxyImage("bird2.png");
        }
        this.image = proxyImage.loadImage().getImage();
        this.width = image.getWidth(null); 
        this.height = image.getHeight(null);
        this.x -= width; 
        this.y -= height; 
        tube = new Tube[1]; 
        tube[0] = new Tube(900, Window.HEIGHT - 60); 
        this.dy = 2; 
    }

    // Method used to move the bird
    public void tick() {
        if (dy < 5) { 
            dy += 2; 
        }
        this.y += dy; 
        tube[0].tick(); 
    try {
        checkWindowBorder(); 
    } catch (SQLException ex) {
        Logger.getLogger(Bird.class.getName()).log(Level.SEVERE, null, ex);
    }
       
    }
    

    public void jump() {
        if (dy > 0) { 
            dy = 0;
        }
        dy -= 15; 
    }

    // Method used to check if the bird has hit the top or bottom of the screen
    private void checkWindowBorder() throws SQLException {
       if (this.x > Window.WIDTH) { 
        this.x = Window.WIDTH; 
    }
    if (this.x < 0) {
        this.x = 0; 
    }
    if (this.y > Window.HEIGHT - 50) { 
        this.y = Window.HEIGHT - 50; 
        Game.getInstance().endGame();
    }
    if (this.y < 0) { 
        this.y = 0; 
    }
}

    // Method used to check if the bird has hit the wall
    public void render(Graphics2D g, ImageObserver obs) {
        g.drawImage(image, x, y, obs); 
        tube[0].render(g, obs); 
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}

// Tube class is used to create a wall object in the game and to move it around the screen
class TubeColumn {

    private int base = Window.HEIGHT - 60;

    private List<Tube> tubes;
    private Random random;
    private int points = 0; 
    private int speed = 5; 
    private int changeSpeed = speed;

    public TubeColumn() {
        tubes = new ArrayList<>();
        random = new Random();
        initTubes();
    }

    // Method used to create the wall
    private void initTubes() {

        int last = base;
        int randWay = random.nextInt(10);

        for (int i = 0; i < 20; i++) {

            Tube tempTube = new Tube(900, last); 
            tempTube.setDx(speed); 
            last = tempTube.getY() - tempTube.getHeight(); 
            if (i < randWay || i > randWay + 4) {  
                tubes.add(tempTube); 
            }
        }
    }

    // Method used to check the position of the walls and to create new walls
    public void tick() {

        for (int i = 0; i < tubes.size(); i++) {  
            tubes.get(i).tick(); 

            if (tubes.get(i).getX() < 0) {
                tubes.remove(tubes.get(i)); 
            }
        }
        if (tubes.isEmpty()) { 
            this.points += 1;
            if (changeSpeed == points) {
                this.speed += 1; 
                changeSpeed += 5;
            }
            initTubes(); 
        }
    }

    // Method used to draw the walls
    public void render(Graphics2D g, ImageObserver obs) {
        for (int i = 0; i < tubes.size(); i++) { 
            tubes.get(i).render(g, obs);  
        }
    }

    public List<Tube> getTubes() {
        return tubes;
    }

    public void setTubes(List<Tube> tubes) {
        this.tubes = tubes;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
interface IStrategy {

    public void controller(Bird bird, KeyEvent kevent);

    public void controllerReleased(Bird bird, KeyEvent kevent);
}
// Controller class is used to control the movement of the bird

class Controller implements IStrategy {

    public void controller(Bird bird, KeyEvent kevent) {
    }

    public void controllerReleased(Bird bird, KeyEvent kevent) {
        if (kevent.getKeyCode() == KeyEvent.VK_SPACE) { 
            bird.jump();
        }
    }
}
interface IImage {

    public ImageIcon loadImage();
}

// ProxyImage class is used to load the image of all the objects
class ProxyImage implements IImage {

    private final String src;
    private RealImage realImage;

    public ProxyImage(String src) {
        this.src = src;
    }

    public ImageIcon loadImage() {
        if (realImage == null) { 
            this.realImage = new RealImage(src); 
        }

        return this.realImage.loadImage();
    }
}
class RealImage implements IImage {

    private final String src;
    private ImageIcon imageIcon;

    public RealImage(String src) {
        this.src = src;
    }

    @Override
    public ImageIcon loadImage() {
        if (imageIcon == null) {
            this.imageIcon = new ImageIcon(getClass().getResource(src));
        }
        return imageIcon;
    }

}

// this class is used to create the window for the game
abstract class GameObject {

    protected int x, y;
    protected int dx, dy;
    protected int width, height;
    protected Image image;

    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image getImage() {
        return image;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public abstract void tick();

    public abstract void render(Graphics2D g, ImageObserver obs);
}
// this class is used to create the walls for the game

class Tube extends GameObject {

    private ProxyImage proxyImage;

    public Tube(int x, int y) {
        super(x, y);
        if (proxyImage == null) { 
            proxyImage = new ProxyImage("TubeBody.png"); 

        }
        this.image = proxyImage.loadImage().getImage(); 
        this.width = image.getWidth(null); 
        this.height = image.getHeight(null); 
    }

    @Override
    public void tick() {
        this.x -= dx;
    }

    @Override
    public void render(Graphics2D g, ImageObserver obs) {
        g.drawImage(image, x, y, obs);

    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
// this class is used to create the background for the game 

class Game extends JPanel implements ActionListener {
    private static Game instance;
    private boolean isRunning = false; 
    private ProxyImage proxyImage; 
    private Image background;
    private Bird bird; 
    private TubeColumn tubeColumn; 
    private int score;
    private int highScore;
    private JPanel scoreboardPanel;
    private JLabel scoreboardLabel;
    private JLabel scoreboardLabel2;

    public Game() {
         instance = this;
        proxyImage = new ProxyImage("background.jpg"); 
        background = proxyImage.loadImage().getImage();
        setFocusable(true);
        setDoubleBuffered(false);
        addKeyListener(new GameKeyAdapter());
        Timer timer = new Timer(15, this);
        timer.start();

        scoreboardPanel = new JPanel();
        ImageIcon backgroundImageIcon = new ImageIcon("C:\\Users\\muddassir\\Documents\\NetBeansProjects\\Game\\src\\game\\gameover.jpg");
        JLabel backgroundLabel = new JLabel(backgroundImageIcon);
        scoreboardPanel.add(backgroundLabel);

        scoreboardPanel.setLayout(new BoxLayout(scoreboardPanel, BoxLayout.Y_AXIS));
        scoreboardPanel.setBackground(new Color(255, 219, 172));
        scoreboardLabel = new JLabel("Scoreboard");
        scoreboardLabel.setFont(new Font("Britannic Bold", Font.BOLD, 30));
        scoreboardLabel.setForeground(black);
        scoreboardPanel.add(scoreboardLabel);
        scoreboardPanel.setVisible(false);      
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Toolkit.getDefaultToolkit().sync(); 
        if (isRunning) {
            bird.tick(); 
            tubeColumn.tick(); 
            try {
                checkColision(); 
            } catch (SQLException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
            score++; 
        }
        repaint();
    }
    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(background, 0, 0, null);
        if (isRunning) {
            this.bird.render(g2, this);
            this.tubeColumn.render(g2, this);
            g2.setColor(Color.black);
            g.setFont(new Font("MV Boli", 1, 25));
            g2.drawString("Current score: " + this.tubeColumn.getPoints(), 10, 50);
            g2.setColor(Color.black);
            g.setFont(new Font("MV Boli", 1, 25));
            g2.drawString("High Score: " + highScore, Window.WIDTH - 230, 50);

        } else {
            g2.setColor(Color.black);
            g.setFont(new Font("MV Boli", 1, 30));
            g2.drawString("     Press ENTER key to start the game :) ", Window.WIDTH / 2 - 350, Window.HEIGHT / 2);
            g.setFont(new Font("Britannic Bold", 1, 45));
            g.drawString(" WELCOME TO FLAPPY BIRD GAME  ", Window.WIDTH / 2 - 350, Window.HEIGHT / 4);
            g2.setColor(Color.black);
            g.setFont(new Font("MV Boli", 1, 15));
        }

        g.dispose();
    }

    private void restartGame() {
        if (!isRunning) {
            this.isRunning = true;
            this.bird = new Bird(Window.WIDTH / 2, Window.HEIGHT / 2); 
            this.tubeColumn = new TubeColumn(); 
            this.scoreboardPanel.setVisible(true); 
        }
    }
     public static Game getInstance() {
        return instance;
    }
    void endGame() throws SQLException {
        this.isRunning = false;
        if (this.tubeColumn.getPoints() > highScore) { 
            this.highScore = this.tubeColumn.getPoints(); 
        }
        this.tubeColumn.setPoints(this.tubeColumn.getPoints()); 
        showScoreboard();
    }

    private void showScoreboard() throws SQLException {
        score = this.tubeColumn.getPoints();
        scoreboardLabel.setText("OOPS! \n Your score: " + score + ", \n High Score: " + highScore);

      
         int sc=score;
            try (var conn = DriverManager.getConnection("jdbc:ucanaccess://C:\\Users\\muddassir\\Documents\\NetBeansProjects\\Game\\flappybirdgame.accdb")) {
  
    String insertQuery = "INSERT INTO flappybirdd (Score) VALUES (?)";
    try (PreparedStatement insertStatement = conn.prepareStatement(insertQuery)) {
        insertStatement.setInt(1, sc);
        insertStatement.executeUpdate();
    }
    
   
} catch (SQLException ex) {
    ex.printStackTrace();
}
        this.setVisible(false);
        scoreboardPanel.setVisible(true);

        JButton restartButton = new JButton("Restart Game");

        restartButton.setBackground(new Color(82, 80, 82));
        restartButton.setForeground(white);
        restartButton.setFont(new Font("MV Boli", Font.BOLD, 18));
        scoreboardPanel.add(restartButton);
        restartButton.addActionListener(new RestartButtonListener());

        JButton exitButton = new JButton("Exit");
        exitButton.setBackground(new Color(82, 80, 82));
        exitButton.setForeground(Color.white);
        exitButton.setFont(new Font("MV Boli", Font.BOLD, 18));
        exitButton.addActionListener(e -> System.exit(0));
        scoreboardPanel.add(exitButton);

    }

    private class RestartButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // Restart the game
            setVisible(true);
            restartGame();
            scoreboardPanel.setVisible(false);
        }
    }
    private void checkColision() throws SQLException {
    Rectangle rectBird = this.bird.getBounds(); 
    Rectangle rectTube; 

    for (int i = 0; i < this.tubeColumn.getTubes().size(); i++) { 
        Tube tempTube = this.tubeColumn.getTubes().get(i); 
        rectTube = tempTube.getBounds(); 
        if ((rectBird.intersects(rectTube))) { 
            endGame(); 
        }
    }
}
    class GameKeyAdapter extends KeyAdapter {
        private final Controller controller;
        public GameKeyAdapter() {
            controller = new Controller();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                restartGame();
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            if (isRunning) {
                controller.controllerReleased(bird, e);
            }
        }
    }
    public JPanel getScoreboardPanel() {
        return scoreboardPanel;
    }
}
class Window {
    public static int WIDTH = 900; 
    public static int HEIGHT = 600; 

    public Window(int width, int height, String title, Game game) {

        JFrame frame = new JFrame();
        frame.add(game);
        frame.setTitle(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.setMaximumSize(new Dimension(width, height)); 
        frame.setPreferredSize(new Dimension(width, height)); 
        frame.setMinimumSize(new Dimension(width, height));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.add(game.getScoreboardPanel());
    }
    static String u = "", p = "";

    static private Border createRoundedBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );
    }

    public static void main(String[] args) {

        JFrame frame1 = new JFrame("Login and Sign-Up");
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setSize(400, 300);
 
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);
      
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBackground(new Color(255, 219, 172));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel loginLabel = new JLabel("Login");
        loginLabel.setFont(new Font("MV Boli", Font.BOLD, 50));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 20;
        loginPanel.add(loginLabel, gbc);

        JLabel loginUserLabel = new JLabel("Username:");
        loginUserLabel.setFont(new Font("MV Boli", Font.BOLD, 20));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        loginPanel.add(loginUserLabel, gbc);

        JLabel iconLabel = new JLabel(new ImageIcon("C:\\Users\\muddassir\\Documents\\NetBeansProjects\\Game\\src\\game\\login.png"));
        gbc.gridx = 20;
        gbc.gridy = 20;
        gbc.gridwidth = 120;
        loginPanel.add(iconLabel, gbc);
        JLabel iconLabel2 = new JLabel(new ImageIcon("C:\\Users\\muddassir\\Documents\\NetBeansProjects\\Game\\src\\game\\login1.png"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        loginPanel.add(iconLabel2, gbc);

        JTextField loginUsernameField = new JTextField(15);
        loginUsernameField.setBorder(createRoundedBorder());
        loginUsernameField.setBackground(new Color(23, 20, 23));
        loginUsernameField.setFont(new Font("MV Boli", Font.BOLD, 12));

        loginUsernameField.setForeground(white);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        loginPanel.add(loginUsernameField, gbc);

        JLabel loginPassLabel = new JLabel("Password:");
        loginPassLabel.setFont(new Font("MV Boli", Font.BOLD, 20));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        loginPanel.add(loginPassLabel, gbc);

        JPasswordField loginPasswordField = new JPasswordField(15);
        loginPasswordField.setBorder(createRoundedBorder());
        loginPasswordField.setBackground(new Color(23, 20, 23));
        loginPasswordField.setForeground(white);
        loginPasswordField.setFont(new Font("MV Boli", Font.BOLD, 12));

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        loginPanel.add(loginPasswordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setBorder(createRoundedBorder());
        loginButton.setBackground(new Color(82, 80, 82));
        loginButton.setForeground(white);
        loginButton.setFont(new Font("MV Boli", Font.BOLD, 18));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        loginPanel.add(loginButton, gbc);

        JButton goToSignUpButton = new JButton("Go to Sign-Up");
        goToSignUpButton.setFont(new Font("MV Boli", Font.BOLD, 18));
        goToSignUpButton.setBorder(createRoundedBorder());
        goToSignUpButton.setBackground(new Color(82, 80, 82));
        goToSignUpButton.setForeground(white);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        loginPanel.add(goToSignUpButton, gbc);

        JPanel signUpPanel = new JPanel();
        signUpPanel.setLayout(new GridBagLayout());
        signUpPanel.setBackground(new Color(255, 219, 172));

        JLabel signUpLabel = new JLabel("Sign-Up");
        signUpLabel.setFont(new Font("MV Boli", Font.BOLD, 54));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        signUpPanel.add(signUpLabel, gbc);

        JLabel signUpUserLabel = new JLabel("Username:");
        signUpUserLabel.setFont(new Font("MV Boli", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        signUpPanel.add(signUpUserLabel, gbc);

        JLabel iconLabel3 = new JLabel(new ImageIcon("C:\\Users\\muddassir\\Documents\\NetBeansProjects\\Game\\src\\game\\login.png"));
        gbc.gridx = 20;
        gbc.gridy = 20;
        gbc.gridwidth = 120;
        signUpPanel.add(iconLabel3, gbc);

        JLabel iconLabel4 = new JLabel(new ImageIcon("C:\\Users\\muddassir\\Documents\\NetBeansProjects\\Game\\src\\game\\login1.png"));
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        signUpPanel.add(iconLabel4, gbc);

        JTextField signUpUsernameField = new JTextField(15);
        signUpUsernameField.setBorder(createRoundedBorder());
        signUpUsernameField.setBackground(new Color(23, 20, 23));
        signUpUsernameField.setFont(new Font("MV Boli", Font.BOLD, 12));

        signUpUsernameField.setForeground(white);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 4;

        signUpPanel.add(signUpUsernameField, gbc);
        JLabel signupPassLabel = new JLabel("Password:");
        signupPassLabel.setFont(new Font("MV Boli", Font.BOLD, 20));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        signUpPanel.add(signupPassLabel, gbc);

        JPasswordField signUpPasswordField = new JPasswordField(15);
        signUpPasswordField.setBorder(createRoundedBorder());
        signUpPasswordField.setBackground(new Color(23, 20, 23));
        signUpPasswordField.setForeground(white);
        signUpPasswordField.setFont(new Font("MV Boli", Font.BOLD, 12));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        signUpPanel.add(signUpPasswordField, gbc);
        JLabel signUpEmailLabel = new JLabel("Email:");
        signUpEmailLabel.setFont(new Font("MV Boli", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        signUpPanel.add(signUpEmailLabel, gbc);

        JTextField signUpEmailField = new JTextField(15);
        signUpEmailField.setBorder(createRoundedBorder());
        signUpEmailField.setBackground(new Color(23, 20, 23));
        signUpEmailField.setForeground(white);
        signUpEmailField.setFont(new Font("MV Boli", Font.BOLD, 12));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        signUpPanel.add(signUpEmailField, gbc);
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setBorder(createRoundedBorder());
        signUpButton.setFont(new Font("MV Boli", Font.BOLD, 18));
        signUpButton.setBorder(createRoundedBorder());
        signUpButton.setBackground(new Color(82, 80, 82));
        signUpButton.setForeground(white);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        signUpPanel.add(signUpButton, gbc);

        JButton goToLoginButton = new JButton("Go to Login");
        goToLoginButton.setFont(new Font("MV Boli", Font.BOLD, 18));
        goToLoginButton.setBorder(createRoundedBorder());
        goToLoginButton.setBackground(new Color(82, 80, 82));
        goToLoginButton.setForeground(white);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        signUpPanel.add(goToLoginButton, gbc);

        
        goToSignUpButton.addActionListener(e -> cardLayout.show(mainPanel, "signUpPanel"));
        goToLoginButton.addActionListener(e -> cardLayout.show(mainPanel, "loginPanel"));
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = signUpUsernameField.getText();
                String password = new String(signUpPasswordField.getPassword());
                String email = signUpEmailField.getText();

                if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter Username, Password, and Email.");
                } else {
                    try (var conn = DriverManager.getConnection("jdbc:ucanaccess://C:\\Users\\muddassir\\Documents\\NetBeansProjects\\Game\\flappybirdgame.accdb")) {
                        String checkQuery = "SELECT COUNT(*) FROM flappybirdd WHERE Username = ?";
                        try (PreparedStatement pst = conn.prepareStatement(checkQuery)) {
                            pst.setString(1, username);
                            try (ResultSet rs = pst.executeQuery()) {
                                if (rs.next() && rs.getInt(1) > 0) {
                                    JOptionPane.showMessageDialog(null, "Username already exists. Please choose another.");
                                    return;
                                }
                            }
                        }
                        String query = "INSERT INTO flappybirdd (Username,Password,Email) VALUES (?, ?, ?)";
                        try (PreparedStatement pst = conn.prepareStatement(query)) {
                            pst.setString(1, username);
                            pst.setString(2, password);
                            pst.setString(3, email);
                            pst.executeUpdate();
                            JOptionPane.showMessageDialog(null, "Sign up successful!");
                            cardLayout.show(mainPanel, "loginPanel");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame1, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        Game game = new Game();
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = loginUsernameField.getText();
                String password = new String(loginPasswordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter Username and Password.");

                } else {

                    try (var conn = DriverManager.getConnection("jdbc:ucanaccess://C:\\Users\\muddassir\\Documents\\NetBeansProjects\\Game\\flappybirdgame.accdb")) {
                        String query = "SELECT * FROM flappybirdd WHERE Username = ? AND Password = ?";
                        try (PreparedStatement pst = conn.prepareStatement(query)) {
                            pst.setString(1, username);
                            pst.setString(2, password);
                            try (ResultSet rs = pst.executeQuery()) {
                                if (rs.next()) {
                                    JOptionPane.showMessageDialog(null, "Login successful!");
                                    frame1.dispose();
                                    // Continue to the game window here
                                    Game game = new Game();
                                    Window window = new Window(WIDTH, HEIGHT, "Flappy Bird", game);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Invalid Username or Password.");
                                }
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame1, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Add panels to main panel
        mainPanel.add(loginPanel, "loginPanel");
        mainPanel.add(signUpPanel, "signUpPanel");

        cardLayout.show(mainPanel, "loginPanel");
        frame1.add(mainPanel);

        frame1.setVisible(true);
    }
}

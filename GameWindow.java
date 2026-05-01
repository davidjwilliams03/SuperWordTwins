import java.awt.*;			// need this for GUI objects
import java.awt.event.*;			// need this for Layout Managers
import javax.swing.*;		// need this to respond to GUI events
	
public class GameWindow extends JFrame 
				implements ActionListener,
					   KeyListener,
					   MouseListener,
					   FocusListener
{
	// declare instance variables for user interface objects

	// declare labels 

	private JLabel statusBarL;
	private JLabel keyL;
	private JLabel mouseL;
	private JLabel collectedL;
	private JLabel clueL;
	private JLabel ansL;
	private JLabel coinsL;
	private JLabel levelL;
	private JLabel healthL;
	private JLabel progressL;

	// declare text fields

	private JTextField statusBarTF;
	private JTextField keyTF;
	private JTextField mouseTF;
	private JTextField collectedTF;
	private JTextField clueTF;
	private JTextField answerTF;
	private JTextField coinsTF;
	private JTextField levelTF;
	private JTextField healthTF;
	private JTextField progressTF;

	// declare buttons

	private JButton startB;
	private JButton pauseB;
	private JButton endB;
	private JButton startNewB;
	private JButton focusB;
	private JButton exitB;

	private Container c;

	private JPanel mainPanel;
	private GamePanel gamePanel;

	private java.util.Set<Character> guessedLetters = new java.util.HashSet<>();

	private static final boolean[] keys = new boolean[1024];

	public static boolean isKeyPressed(int keyCode) {
		if (keyCode >= 0 && keyCode < keys.length) return keys[keyCode];
		return false;
	}

	@SuppressWarnings({"unchecked"})
	public GameWindow() {
 
		setTitle ("Tiled Bat and Ball Game: Ordinary Windowed Mode");
		setSize (1350, 900);

		// create user interface objects

		// create labels

		statusBarL = new JLabel ("Application Status: ");
		keyL = new JLabel("Key Pressed: ");
		mouseL = new JLabel("Location of Mouse Click: ");
		collectedL = new JLabel ("Number of Coins Collected: ");
		clueL = new JLabel("Clue: ");
		ansL = new JLabel("Answer: ");
		coinsL = new JLabel("Coins Collected: ");
		levelL = new JLabel("Current Level: ");
		healthL = new JLabel("Player Health: ");
		progressL = new JLabel("Map Progress: ");

		// create text fields and set their colour, etc.

		statusBarTF = new JTextField (25);
		keyTF = new JTextField (25);
		mouseTF = new JTextField (25);
		collectedTF = new JTextField(2);
		clueTF = new JTextField(100);
		answerTF = new JTextField(20);
		coinsTF = new JTextField (25);
		levelTF = new JTextField (25);
		healthTF = new JTextField (25);
		progressTF = new JTextField (25);

		// Set all text fields to non-editable
		statusBarTF.setEditable(false);
		keyTF.setEditable(false);
		mouseTF.setEditable(false);
		collectedTF.setEditable(false);
		clueTF.setEditable(false);
		answerTF.setEditable(false);
		coinsTF.setEditable(false);
		levelTF.setEditable(false);
		healthTF.setEditable(false);
		progressTF.setEditable(false);

		//align
		clueTF.setHorizontalAlignment(JTextField.CENTER);
		answerTF.setHorizontalAlignment(JTextField.CENTER);

		// Set background colors
		statusBarTF.setBackground(Color.CYAN);
		keyTF.setBackground(Color.YELLOW);
		mouseTF.setBackground(Color.GREEN);
		collectedTF.setBackground(Color.WHITE);
		clueTF.setBackground(Color.WHITE);
		answerTF.setBackground(Color.WHITE);
		coinsTF.setBackground(Color.YELLOW);
		levelTF.setBackground(Color.WHITE);
		healthTF.setBackground(Color.GREEN);
		progressTF.setBackground(Color.LIGHT_GRAY);

		// create buttons

	        startB = new JButton ("Start Game");
	        pauseB = new JButton ("Pause Game");
	        endB = new JButton ("End Game");
		startNewB = new JButton ("Start New Game");
	        focusB = new JButton ("Show Animation");
		exitB = new JButton ("Exit");


		// add listener to each button (same as the current object)

		startB.addActionListener(this);
		pauseB.addActionListener(this);
		endB.addActionListener(this);
		startNewB.addActionListener(this);
		focusB.addActionListener(this);
		exitB.addActionListener(this);
		
		// create mainPanel

		mainPanel = new JPanel();
		FlowLayout flowLayout = new FlowLayout();
		mainPanel.setLayout(flowLayout);

		GridLayout gridLayout;

		// create the gamePanel for game entities

		gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(1300, 600));

		// create infoPanel

		JPanel infoPanel = new JPanel();
		gridLayout = new GridLayout(3, 4);
		infoPanel.setLayout(gridLayout);
		infoPanel.setBackground(Color.ORANGE);

		// add user interface objects to infoPanel
	
		infoPanel.add (levelL);
		infoPanel.add (levelTF);
		infoPanel.add (progressL);
		infoPanel.add (progressTF);

		infoPanel.add (healthL);
		infoPanel.add (healthTF);
		infoPanel.add (keyL);
		infoPanel.add (keyTF);		

		infoPanel.add (coinsL);
		infoPanel.add (coinsTF);
		infoPanel.add (statusBarL);
		infoPanel.add (statusBarTF);

		
		// create buttonPanel

		JPanel buttonPanel = new JPanel();
		gridLayout = new GridLayout(2, 3);
		buttonPanel.setLayout(gridLayout);

		// add buttons to buttonPanel

		buttonPanel.add (startB);
		buttonPanel.add (pauseB);
		buttonPanel.add (endB);
		buttonPanel.add (startNewB);
		buttonPanel.add (focusB);
		buttonPanel.add (exitB);

		//create cluePanel
		JPanel cluePanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(cluePanel, BoxLayout.Y_AXIS);
		cluePanel.setBackground(Color.ORANGE);

		// add user interface objects to infoPanel
	
		//cluePanel.add (clueL);
		cluePanel.add (clueTF);
		//cluePanel.add(ansL);
		cluePanel.add(Box.createVerticalStrut(10));
		cluePanel.add(answerTF);

		// add sub-panels with GUI objects to mainPanel and set its colour

		mainPanel.add(infoPanel);
		mainPanel.add(cluePanel);
		mainPanel.add(gamePanel);
		mainPanel.add(buttonPanel);
		
		mainPanel.setBackground(Color.PINK);
		mainPanel.setFocusable(true);
		mainPanel.addFocusListener(this);

		// set up mainPanel to respond to keyboard and mouse

		gamePanel.addMouseListener(this);
		mainPanel.setFocusable(true);
		mainPanel.addFocusListener(this);
		mainPanel.addKeyListener(this);

		gamePanel.setWindow(this);

		// add mainPanel to window surface

		mainPanel.requestFocus();

		c = getContentPane();
		c.add(mainPanel);

		// set properties of window

		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setVisible(true);

		// set status bar message

		statusBarTF.setText("Application started.");
	}


	// implement single method in ActionListener interface

	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		
		statusBarTF.setText(command + " button clicked.");

		if (command.equals(startB.getText())) {
			gamePanel.loadRiddles();
			clueTF.setText(gamePanel.chooseClue());
			for(int i = 0; i < gamePanel.numCharAns(); i++){
				answerTF.setText(answerTF.getText() + "_ ");
			}

			collectedTF.setText("0");

			gamePanel.startGame();
		}

		if (command.equals(pauseB.getText())) {
			gamePanel.pauseGame();
			if (command.equals("Pause Game"))
				pauseB.setText ("Resume");
			else
				pauseB.setText ("Pause Game");

		}
		
		if (command.equals(endB.getText())) {
			gamePanel.endGame();
		}

		if (command.equals(startNewB.getText()))
			gamePanel.startNewGame();

		if (command.equals(focusB.getText()))
			gamePanel.showAnimation();

		if (command.equals(exitB.getText()))
			System.exit(0);

		mainPanel.requestFocus();
	}

	public void updateLevel(int level) {
		levelTF.setText("" + level);
	}
	public void updateHealth(int health) {
		healthTF.setText("" + health);
	}
	public void updateCoins(int coins) {
		coinsTF.setText("" + coins);
	}
	public void updateProgress(int progress) {
		progressTF.setText(progress + "%");
	}

	public void resetGuessedLetters() {
		guessedLetters.clear();
	}

	public int updateAns(char c){
		guessedLetters.add(c);
		char[] answer = gamePanel.getAnswer().toCharArray();
		StringBuilder updated = new StringBuilder();
		int count = 0; 
		
		for (int i = 0; i < answer.length; i++){
			char current = answer[i];
			if (current == ' ') {
				updated.append("  "); // Double space for visual gap
			} else if (guessedLetters.contains(current)) {
				updated.append(current).append(" ");
				// Only return the count for the NEWLY found letters this turn
				if (current == c) {
					count++;
				}
			} else {
				updated.append("_ ");
			}
		}
		answerTF.setText(updated.toString().trim());
		return count;
	}

	public void focusGained(FocusEvent e) {}

	public void focusLost(FocusEvent e) {
		// Reset all keys when focus is lost to prevent "sticky" keys
		for (int i = 0; i < keys.length; i++) {
			keys[i] = false;
		}
	}

	// implement methods in KeyListener interface

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		String keyText = e.getKeyText(keyCode);
		keyTF.setText(keyText + " pressed.");
		
		if (keyCode >= 0 && keyCode < keys.length) {
			keys[keyCode] = true;
		}
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();

		int coinsCollected = gamePanel.getCoinsCollected();
		coinsTF.setText(String.valueOf(coinsCollected));
		
		if (keyCode >= 0 && keyCode < keys.length) {
			keys[keyCode] = false;
		}
	}

	public void keyTyped(KeyEvent e) {

	}


	// implement methods in MouseListener interface

	public void mouseClicked(MouseEvent e) {
		// Mouse click location tracking removed to favor coin collection count
	}


	public void mouseEntered(MouseEvent e) {
	
	}

	public void mouseExited(MouseEvent e) {
	
	}

	public void mousePressed(MouseEvent e) {
	
	}

	public void mouseReleased(MouseEvent e) {
	
	}

}
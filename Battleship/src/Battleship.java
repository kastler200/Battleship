import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

public class Battleship extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;

	//Variables
	int NUMROWS = 10;
	int NUMTURN = 1;
	int PCSCORE = 0;
	int PLAYSCORE = 0;
	int WINS;
	int LOSSES;
	int oneX;
	int oneY;
	String[] shipStrings = { "Carrier", "Battleship", "Destroyer", "Submarine", "Patrol Boat" };
	int[] shipSizes = {5, 4, 3, 3, 2};
	Boolean[] shipsAreSet = {false, false, false, false, false};
	int selectedShip;
	Boolean gridIsSet = false;
	Boolean coordOneSet = false;
	Boolean coordTwoSet = false;
	Boolean PCTURN = false;
	Random random = new Random();
	Gameboard pcBoard;
	Gameboard playerBoard;
	String saveFile = "bin/save-file.txt";

	//AI variables
	int pcFocusX;
	int pcFocusY;
	int pcTryX;
	int pcTryY;
	int pcMissX;
	int pcMissY;
	int parallelIndex;
	int parallelCount;
	int[] pcStoreX = new int[NUMROWS];
	int[] pcStoreY = new int[NUMROWS];
	int difficulty = 1;
	boolean up = true;
	boolean down = true;
	boolean left = true;
	boolean right = true;
	boolean pcHasMiss = true;
	boolean pcHasHit = false;
	int hasPMiss = 0;
	int pcHasDir = 0;
	boolean pcShipSunk = false;

	//GUI Variables
	Dimension bPanelSize;
	JPanel bPanel;
	JPanel labelPanel;
	JPanel centerPanel = new JPanel(new BorderLayout());
	JPanel topPanel = new JPanel();
	JPanel botPanel = new JPanel();
	JLabel shipLabel = new JLabel("Ships: ");
	JLabel botLabel = new JLabel("Setup your board, bitch.");
	JLabel turnLabel = new JLabel();
	JPanel sidePanel = new JPanel(new BorderLayout());
	JPanel sideTopPanel = new JPanel();
	JPanel sideBotPanel = new JPanel();
	JLabel sideBotLabel = new JLabel();
	JLabel sideTopLabel = new JLabel();
	JComboBox<Object> shipButtons;
	JButton startButton;
	JButton resetButton;
	JMenuBar mBar = new JMenuBar();
	JMenu menu;
	JMenuItem newGameButton = new JMenuItem("New Game");
	JMenuItem recordButton = new JMenuItem("Show Record");
	JMenuItem resetRecordButton = new JMenuItem("Reset Record");
	JMenuItem mildMenuButton = new JMenuItem("Mild");
	JMenuItem medMenuButton = new JMenuItem("Medium");
	JMenuItem hotMenuButton = new JMenuItem("Hot!");
	ArrayList<ArrayList<JButton>> buttonGrid;
	ArrayList<ArrayList<JLabel>> iconGrid;
	Border blackB = BorderFactory.createLineBorder(Color.GRAY, 3);
	Border emptyB = BorderFactory.createEmptyBorder(3,3,3,3);
	Border compB = BorderFactory.createCompoundBorder(blackB, emptyB);
	ImageIcon redTile = new ImageIcon(ImageIO.read(new File("bin/tiles/red.png")));
	ImageIcon redXTile = new ImageIcon(ImageIO.read(new File("bin/tiles/redx.png")));
	ImageIcon blueTile = new ImageIcon(ImageIO.read(new File("bin/tiles/blue.png")));
	ImageIcon blueXTile = new ImageIcon(ImageIO.read(new File("bin/tiles/bluex.png")));
	ImageIcon blueOTile = new ImageIcon(ImageIO.read(new File("bin/tiles/blueo.png")));
	ImageIcon bluePressedTile = new ImageIcon(ImageIO.read(new File("bin/tiles/bluePressed.png")));


	public Battleship() throws IOException {
		readSaveFile();
		setupButtonGrid();
		startNewGame();

		shipButtons = new JComboBox<Object>(shipStrings);
		shipButtons.addActionListener(this);

		menu = new JMenu("Game");
		mBar.add(menu);
		newGameButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		newGameButton.addActionListener(this);
		recordButton.addActionListener(this);
		resetRecordButton.addActionListener(this);
		menu.add(newGameButton);
		menu.add(recordButton);
		menu.add(resetRecordButton);

		menu = new JMenu("Difficulty");
		menu.add(mildMenuButton);
		menu.add(medMenuButton);
		menu.add(hotMenuButton);
		mildMenuButton.addActionListener(this);
		medMenuButton.addActionListener(this);
		hotMenuButton.addActionListener(this);
		mBar.add(menu);

		topPanel.add(shipLabel, BorderLayout.WEST);
		topPanel.add(shipButtons, BorderLayout.CENTER);
		botPanel.add(botLabel);

		startButton = new JButton("Start!");
		resetButton = new JButton("Reset");
		resetButton.addActionListener(this);
		startButton.addActionListener(this);
		topPanel.add(startButton);
		topPanel.add(resetButton);

		sideBotLabel.setText("Something will go here");
		sideTopPanel.add(sideTopLabel);
		sideBotPanel.add(sideBotLabel);


		centerPanel.add(topPanel, BorderLayout.NORTH);
		centerPanel.add(botPanel, BorderLayout.SOUTH);

		//set menu bar and finalize frame
		setTitle("Battleship");
		setJMenuBar(mBar);
		add(centerPanel, BorderLayout.CENTER);
		setVisible(true);
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	//set new buttons grid
	public void newButtonGrid() {

		//sets graphic for each buttons and adds action listener
		for (int i = 0; i < NUMROWS; ++i) {
			for (int j = 0; j < NUMROWS; ++j) {
				buttonGrid.get(i).get(j).setIcon(blueTile);
				buttonGrid.get(i).get(j).setPressedIcon(bluePressedTile);
				buttonGrid.get(i).get(j).setBorder(null);
				bPanel.add(buttonGrid.get(i).get(j));
			}
		}
	}

	//update the side panel with a label grid
	public void updateLabelGrid() {
		//this bitch keeps bpanel it's same size
		if (bPanelSize == null) {
			bPanelSize = bPanel.getSize();
			bPanel.setPreferredSize(bPanelSize);
		}


		//sets graphic for the label grid - 
		for (int i = 0; i < NUMROWS; ++i) {
			for (int j = 0; j < NUMROWS; ++j) {
				iconGrid.add(new ArrayList<JLabel>());
				iconGrid.get(i).add(new JLabel());
				if (playerBoard.containsShip(i, j)) {
					if (playerBoard.getTileStatus(i, j) == Gameboard.Status.empty) {
						iconGrid.get(i).get(j).setIcon(redTile);
					}else if (playerBoard.getTileStatus(i, j) == Gameboard.Status.hit) {
						iconGrid.get(i).get(j).setIcon(redXTile);
					}
				}else {
					if (playerBoard.getTileStatus(i, j) == Gameboard.Status.empty) {
						iconGrid.get(i).get(j).setIcon(blueTile);
					}else if (playerBoard.getTileStatus(i, j) == Gameboard.Status.miss) {
						iconGrid.get(i).get(j).setIcon(blueOTile);
					}
				}

				iconGrid.get(i).get(j).setBorder(null);
				labelPanel.add(iconGrid.get(i).get(j));
			}
		}

		sideBotLabel.setText("This is your board.");
		botLabel.setText("This is your opponent's board now.");
		sidePanel.add(sideTopPanel, BorderLayout.NORTH);
		sidePanel.add(sideBotPanel, BorderLayout.SOUTH);
		sidePanel.add(labelPanel, BorderLayout.CENTER);
		sideTopPanel.setPreferredSize(topPanel.getSize());
		sideBotPanel.setPreferredSize(botPanel.getSize());
		labelPanel.setPreferredSize(bPanel.getSize());
		add(sidePanel, BorderLayout.EAST);
		pack();

	}

	//create button grid and label grid and set tiles
	public void setupButtonGrid() {
		if (bPanel != null) {
			centerPanel.remove(bPanel);
		}

		bPanel = new JPanel(new GridLayout(NUMROWS, NUMROWS));
		bPanel.setBorder(compB);
		buttonGrid = new ArrayList<ArrayList<JButton>>();
		labelPanel = new JPanel(new GridLayout(NUMROWS, NUMROWS));
		labelPanel.setBorder(compB);
		iconGrid = new ArrayList<ArrayList<JLabel>>();

		//add buttons to the grid
		for (int q = 0; q < NUMROWS; ++q) {
			for (int w = 0; w < NUMROWS; ++w) {
				buttonGrid.add(new ArrayList<JButton>());
				buttonGrid.get(q).add(new JButton());
				buttonGrid.get(q).get(w).addActionListener(this);
			}
		}
		newButtonGrid();
		centerPanel.add(bPanel, BorderLayout.CENTER);
	}

	//you get it
	public void startNewGame() {

		remove(sidePanel);
		botLabel.setText("Setup your board first.");
		playerBoard = new Gameboard(NUMROWS);
		pcBoard = new Gameboard(NUMROWS);
		gridIsSet = false;
		coordOneSet = false;
		coordTwoSet = false;

		//AI vars
		pcShipSunk = false;
		pcHasMiss = true;
		pcHasHit = false;
		pcHasDir = 0;
		up = true;
		down = true;
		left = true;
		right = true;
		pcMissX = 0;
		pcMissY = 0;
		parallelCount = 0;
		parallelIndex = 0;
		hasPMiss = 0;
		NUMTURN = 1;
		PCSCORE = 0;
		PLAYSCORE = 0;
		selectedShip = 0;

		for (int x = 0; x < pcStoreX.length; x++) {
			pcStoreX[x] = -1;
			pcStoreY[x] = -1;
		}


		for (int i = 0; i < shipsAreSet.length; i++) {
			shipsAreSet[i] = false;
		}

		newButtonGrid();
		if (iconGrid.size() > 0) {
			pack();
		}
	}

	//resets first tile choice
	public void quickTileReset() {
		buttonGrid.get(oneX).get(oneY).setIcon(blueTile);
		coordOneSet = false;
		coordTwoSet = false;
	}

	//prompt the player to hit enter after setting ships
	public void askPlayerToEnter() {
		int total = shipsAreSet.length;
		int m = 0;
		for (int k = 0; k < total; k++) {
			if (shipsAreSet[k]) {
				m++;
			}
		}

		//m adds how many ships are set. if equal to total number of ships, can set the grid
		if (m == total) {
			int n = (int)JOptionPane.showConfirmDialog(centerPanel, "Are you ready to play? Hit yes to continue.", "Ready?", JOptionPane.YES_NO_OPTION);

			if (n == JOptionPane.YES_OPTION) {
				gridIsSet = true;
				updateLabelGrid();
				setupPCShips();
				newButtonGrid();
			}
		}
	}

	//take input from button grid on start of game to set up their ships
	public void setupPlayerShips(int i, int j) {
		//if statement for if i and j already are taken from grid
		if (playerBoard.containsShip(i, j)) {
			botLabel.setText("This tile is already taken.");
		}else {

			if (shipsAreSet[selectedShip] == false) {
				if (coordOneSet == false) {
					buttonGrid.get(i).get(j).setIcon(bluePressedTile);
					oneX = i;
					oneY = j;
					coordOneSet = true;
				}else if (coordOneSet && coordTwoSet == false){
					//on the second coord. ensure it is adjacent to first coord.
					//ship will go down
					Boolean checkShip = true;
					if (i == (oneX + 1) && j == oneY) {
						if (oneX + shipSizes[selectedShip] <= NUMROWS) {
							//check for ship in line of requested position
							for (int u = oneX; u < (oneX + shipSizes[selectedShip]); u++) {
								if (playerBoard.containsShip(u, oneY)) {
									checkShip = false;
								}
							}

							//set boat on grid
							if (checkShip) {
								coordTwoSet = true;
								shipsAreSet[selectedShip] = true;
								for (int q = oneX; q < (oneX + shipSizes[selectedShip]); q++) {
									buttonGrid.get(q).get(oneY).setIcon(redTile);
									playerBoard.setShip(q, oneY, selectedShip);

								}
								if ((selectedShip+1) < shipStrings.length) {
									shipButtons.setSelectedIndex(selectedShip+1);
								}else {
									shipButtons.setSelectedIndex(0);
								}

							}else {
								botLabel.setText("Pick a spot that will not overlap another ship.");
								quickTileReset();
							}
						}else {
							botLabel.setText("This boat is too long for this spot.");
							quickTileReset();
						}

						//ship will go up
					}else if (i == (oneX - 1) && j == oneY) {
						if ((1 + oneX - shipSizes[selectedShip]) >= 0) {
							//check for ship in line of requested position
							for (int q = 1 + (oneX - shipSizes[selectedShip]); q <= oneX; q++) {
								if (playerBoard.containsShip(q, oneY)) {
									checkShip = false;
								}
							}

							//set boat on grid
							if (checkShip) {
								coordTwoSet = true;
								shipsAreSet[selectedShip] = true;
								for (int q = 1 + (oneX - shipSizes[selectedShip]); q <= oneX; q++) {
									buttonGrid.get(q).get(oneY).setIcon(redTile);
									playerBoard.setShip(q, oneY, selectedShip);
								}
								if ((selectedShip+1) < shipStrings.length) {
									shipButtons.setSelectedIndex(selectedShip+1);
								}else {
									shipButtons.setSelectedIndex(0);
								}

							}else {
								botLabel.setText("Pick a spot that will not overlap another ship.");
								quickTileReset();
							}
						}else {
							botLabel.setText("This boat is too long for this spot.");
							quickTileReset();
						}

						//ship will go right
					}else if (i == oneX && j == (oneY + 1)) {
						if ((oneY + shipSizes[selectedShip]) <= NUMROWS) {
							//check for ship in line of requested position
							for (int q = oneY; q < (oneY + shipSizes[selectedShip]); q++) {
								if (playerBoard.containsShip(oneX, q)) {
									checkShip = false;
								}
							}

							//set boat on grid
							if (checkShip) {
								coordTwoSet = true;
								shipsAreSet[selectedShip] = true;
								for (int q = oneY; q < (oneY + shipSizes[selectedShip]); q++) {
									buttonGrid.get(oneX).get(q).setIcon(redTile);
									playerBoard.setShip(oneX, q, selectedShip);

								}
								if ((selectedShip+1) < shipStrings.length) {
									shipButtons.setSelectedIndex(selectedShip+1);
								}else {
									shipButtons.setSelectedIndex(0);
								}

							}else {
								botLabel.setText("Pick a spot that will not overlap another ship.");
								quickTileReset();
							}
						}else {
							botLabel.setText("This boat is too long for this spot.");
							quickTileReset();
						}

						//ship will go left
					}else if (i == oneX && j == (oneY - 1)) {
						if ((1 + oneY - shipSizes[selectedShip]) >= 0) {
							//check for ship in line of requested position
							for (int q = 1 + (oneY - shipSizes[selectedShip]); q <= oneY; q++) {
								if (playerBoard.containsShip(oneX, q)) {
									checkShip = false;	
								}
							}
							//set boat on grid
							if (checkShip) {
								coordTwoSet = true;
								shipsAreSet[selectedShip] = true;
								for (int q = 1 + (oneY - shipSizes[selectedShip]); q <= oneY; q++) {
									buttonGrid.get(oneX).get(q).setIcon(redTile);
									playerBoard.setShip(oneX, q, selectedShip);

								}
								if ((selectedShip+1) < shipStrings.length) {
									shipButtons.setSelectedIndex(selectedShip+1);
								}else {
									shipButtons.setSelectedIndex(0);
								}

							}else {
								botLabel.setText("Pick a spot that will not overlap another ship.");
								quickTileReset();
							}

						}else {
							botLabel.setText("This boat is too long for this spot.");
							quickTileReset();
						}

						//incorrect choice
					}else {
						botLabel.setText("Must chose an adjacent tile to your first coordinate.");
					}
					askPlayerToEnter();
				}else if (coordOneSet && coordTwoSet) {
					//if both coords are set

				}

			}else {
				botLabel.setText("This ship is already set. Please choose another one.");
			}
		}
	}

	//decide which spots for PC to choose
	public void runAI() {
		//pcHasDir: 0 = no, 1 = yes, 2 = no direction with parallel ships, 3 = has direction with parallels

		//pchasmiss means the random pick last turn was a miss, same for hit
		if (pcHasDir == 0 && playerBoard.getTileStatus(pcFocusX, pcFocusY) == Gameboard.Status.miss) {
			pcHasMiss = true;
			pcHasHit = false;
		}else if (pcHasDir == 0 && playerBoard.getTileStatus(pcFocusX, pcFocusY) == Gameboard.Status.hit) {
			pcHasMiss = false;
			pcHasHit = true;
			pcStoreX[parallelCount] = pcFocusX;
			pcStoreY[parallelCount] = pcFocusY;
		}



		//if surrounding tile was ticked as hit, pc has a direction to check
		if ((pcHasDir == 0 || pcHasDir == 2) && pcTryX != pcFocusX && playerBoard.getTileStatus(pcTryX, pcTryY) == Gameboard.Status.hit) {
			if (pcHasDir == 0) {

				parallelCount++;
				pcStoreX[parallelCount] = pcTryX;
				pcStoreY[parallelCount] = pcTryY;
			}
			pcHasDir++;

		}else if ((pcHasDir == 0 || pcHasDir == 2) && pcTryY != pcFocusY && playerBoard.getTileStatus(pcTryX, pcTryY) == Gameboard.Status.hit) {
			if (pcHasDir == 0) {
				parallelCount++;
				pcStoreX[parallelCount] = pcTryX;
				pcStoreY[parallelCount] = pcTryY;
			}
			pcHasDir++;

		}else if (pcHasDir == 1 && hasPMiss <= 1 && playerBoard.getTileStatus(pcTryX, pcTryY) == Gameboard.Status.hit) {
			parallelCount++;
			pcStoreX[parallelCount] = pcTryX;
			pcStoreY[parallelCount] = pcTryY;

		}

		//checks ends of ships for 2 misses
		if (pcShipSunk == false && (pcHasDir == 1 || pcHasDir == 3)) {
			for (int l = 0; l <= parallelCount; l++) {
				if (pcStoreX[0] != pcStoreX[1]) {
					int g = pcStoreX[l] - 1;
					int h = pcStoreX[l] + 1;
					if (g >= 0) {
						if (playerBoard.getTileStatus(g, pcStoreY[l]) == Gameboard.Status.miss) {
							hasPMiss++;
						}
					}else {
						hasPMiss++;
					}
					if (h <= NUMROWS) {
						if (playerBoard.getTileStatus(h, pcStoreY[l]) == Gameboard.Status.miss) {
							hasPMiss++;
						}
					}else {
						hasPMiss++;
					}
				}else if (pcStoreY[0] != pcStoreY[1])  {
					int g = pcStoreY[l] - 1;
					int h = pcStoreY[l] + 1;
					if (g >= 0) {
						if (playerBoard.getTileStatus(pcStoreX[l], g) == Gameboard.Status.miss) {
							hasPMiss++;
						}
					}else {
						hasPMiss++;
					}
					if (h <= NUMROWS) {
						if (playerBoard.getTileStatus(pcStoreX[l], h) == Gameboard.Status.miss) {
							hasPMiss++;
						}
					}else {
						hasPMiss++;
					}
				}
			}

			if (hasPMiss <= 1) {
				hasPMiss = 0;
			}else if (hasPMiss >= 2) {
				hasPMiss = 2;
				pcHasDir++;
			}
		}

		//see if parallel ships were found and index through them with the guessing code below
		if (pcHasDir >= 2 && hasPMiss >= 2 || parallelCount >= 6) {
			//if the PC finds 6 or more spots in a row and gets a sink, this will confuse the PC and it will attempt one random space to check for the unfound boat - 
			if (parallelCount >= 6 && pcShipSunk) {
				int z = random.nextInt(pcStoreX.length);
				pcStoreX[0] = pcStoreX[z];
				pcStoreY[0] = pcStoreY[z];
				parallelCount = 0;
			}
			//end of index if statement - pc can go back to random picks
			if (parallelIndex > parallelCount) {
				pcShipSunk = false;
				pcHasMiss = true;
				pcHasHit = false;
				pcHasDir = 0;
				up = true;
				down = true;
				left = true;
				right = true;
				parallelCount = 0;
				parallelIndex = 0;
				hasPMiss = 0;
				pcMissX = 0;
				pcMissY = 0;
			}else {

				if (pcShipSunk) {
					parallelIndex++;
					pcShipSunk = false;
					pcHasDir = 2;
					up = true;
					down = true;
					left = true;
					right = true;
				}

				pcFocusX = pcStoreX[parallelIndex];
				pcFocusY = pcStoreY[parallelIndex];

				//makes pc solve perpendicular ships first
				if (pcStoreX[0] != pcStoreX[1]) {
					up = false;
					down = false;
				}else if (pcStoreY[0] != pcStoreY[1]) {
					left = false;
					right = false;
				}
			}

		}

		//if PC sank a ship and we are not checking parallel ships, reset the shit
		if (pcShipSunk && pcHasDir <= 1 && parallelCount < 6) {
			pcShipSunk = false;
			pcHasMiss = true;
			pcHasHit = false;
			pcHasDir = 0;
			up = true;
			down = true;
			left = true;
			right = true;
			parallelCount = 0;
			parallelIndex = 0;
			hasPMiss = 0;
			pcMissX = 0;
			pcMissY = 0;
		}


		//Below is the PC guess code
		//first turn or last tile checked was a miss
		if (pcHasMiss) {
			pcRandomTile();
			//pc has hit but no direction
		}else if (pcHasHit && (pcHasDir == 0 || pcHasDir == 2)) {
			pcCheckTiles();

			//pc has hit and direction - pcHasDir of 0 or 3 mean a direction has not been chosen
		}else if (pcHasHit) {

			//vertical
			if (pcTryX != pcFocusX) {
				int trial = 1;
				while (trial <= NUMROWS) {
					try {
						if (up) {
							if (playerBoard.getTileStatus(pcFocusX - trial, pcFocusY) == Gameboard.Status.miss) {
								up = false;

							}else {
								if (playerBoard.getTileStatus(pcFocusX - trial, pcFocusY) == Gameboard.Status.empty) {
									pcTryX = pcFocusX - trial;
									pcTryY = pcFocusY;
									break;
								}
							}
						}
					}catch (Exception e) {
						up = false;
					}
					try {
						if (down) {
							if (playerBoard.getTileStatus(pcFocusX + trial, pcFocusY) == Gameboard.Status.miss) {
								down = false;

							}else {
								if (playerBoard.getTileStatus(pcFocusX + trial, pcFocusY) == Gameboard.Status.empty) {
									pcTryX = pcFocusX + trial;
									pcTryY = pcFocusY;
									break;
								}
							}
						}
					}catch (Exception e){
						down = false;
					}
					trial++;
				}

				//horizontal
			}else if (pcTryY != pcFocusY) {
				int trial = 1;
				while (trial <= NUMROWS) {
					try {
						if (left) {
							if (playerBoard.getTileStatus(pcFocusX, pcFocusY - trial) == Gameboard.Status.miss) {
								left = false;

							}else {
								if (playerBoard.getTileStatus(pcFocusX, pcFocusY - trial) == Gameboard.Status.empty) {
									pcTryY = pcFocusY - trial;
									pcTryX = pcFocusX;
									break;
								}
							}
						}
					}catch (Exception e) {
						left = false;
					}
					try {
						if (right) {
							if (playerBoard.getTileStatus(pcFocusX, pcFocusY + trial) == Gameboard.Status.miss) {
								right = false;

							}else {
								if (playerBoard.getTileStatus(pcFocusX, pcFocusY + trial) == Gameboard.Status.empty) {
									pcTryY = pcFocusY + trial;
									pcTryX = pcFocusX;
									break;
								}
							}
						}
					}catch (Exception e){
						right = false;
					}
					trial++;
				}
			}
		}

	}

	//ensure random pick is an empty tile
	public void pcRandomTile() {
		pcTryX = random.nextInt(NUMROWS);
		pcTryY = random.nextInt(NUMROWS);

		while(playerBoard.getTileStatus(pcTryX, pcTryY) != Gameboard.Status.empty) {
			pcTryX = random.nextInt(NUMROWS);
			pcTryY = random.nextInt(NUMROWS);
		}
		pcFocusX = pcTryX;
		pcFocusY = pcTryY;
	}

	public void pcCheckTiles() {
		boolean spotFound = false;

		//will check up, down, left, right - if each spot is taken, checks a random spot
		while (spotFound == false) {

			try {
				if (playerBoard.getTileStatus(pcFocusX - 1, pcFocusY) == Gameboard.Status.empty) {
					pcTryX = pcFocusX-1;
					pcTryY = pcFocusY;
					break;
				}
			}catch (Exception e) {}
			try {
				if (playerBoard.getTileStatus(pcFocusX + 1, pcFocusY) == Gameboard.Status.empty) {
					pcTryX = pcFocusX+1;
					pcTryY = pcFocusY;
					break;
				}
			}catch (Exception e){}
			try {
				if (playerBoard.getTileStatus(pcFocusX, pcFocusY - 1) == Gameboard.Status.empty) {
					pcTryY = pcFocusY-1;
					pcTryX = pcFocusX;
					break;
				}
			}catch (Exception e){}
			try {
				if (playerBoard.getTileStatus(pcFocusX, pcFocusY + 1) == Gameboard.Status.empty) {
					pcTryY = pcFocusY+1;
					pcTryX = pcFocusX;
					break;
				}
			}catch (Exception e){}
			pcRandomTile();
			spotFound = true;
		}


	}

	//input player choice and check if hit or miss - will have to make the AI part pick a correct tile prior to this step
	public void runGamePhase(int i, int j, Gameboard g) throws IOException {
		NUMTURN++;
		if (g.getTileStatus(i, j) == Gameboard.Status.empty) {
			//set tile to X
			if (g.containsShip(i, j)) {
				if (g == playerBoard) {
					iconGrid.get(i).get(j).setIcon(redXTile);
				}else {
					buttonGrid.get(i).get(j).setIcon(blueXTile);
				}
				g.setTileStatus(i, j, Gameboard.Status.hit);


				//check win
				if (playerBoard.getTotalHitSpots() >= playerBoard.getTotalShipSpots()) {
					JOptionPane.showMessageDialog(centerPanel, "Sorry, you lose.");
					LOSSES++;
					writeSaveFile();
					startNewGame();
				}else if (pcBoard.getTotalHitSpots() >= pcBoard.getTotalShipSpots()) {
					JOptionPane.showMessageDialog(centerPanel, "You Win!");
					WINS++;
					writeSaveFile();
					startNewGame();
				}else if (g.checkForSink(i, j)) {
					if (g == pcBoard) {
						JOptionPane.showMessageDialog(centerPanel, "Sink!");
						PCSCORE++;
					}else {
						JOptionPane.showMessageDialog(centerPanel, "They sunk your ship!");
						pcShipSunk = true;
						PLAYSCORE++;
					}
				}

				//set tile to O
			}else {
				if (g == playerBoard) {
					iconGrid.get(i).get(j).setIcon(blueOTile);
				}else {
					buttonGrid.get(i).get(j).setIcon(blueOTile);
				}
				g.setTileStatus(i, j, Gameboard.Status.miss);
			}

			sideTopLabel.setText("Player Ships Sunk: " + PLAYSCORE + "   PC Ships Sunk: " + PCSCORE);
			
			if (g == pcBoard) {
			PCTURN = true;
			}
		}else if (g == pcBoard){
			JOptionPane.showMessageDialog(centerPanel, "Please pick an empty tile.");
		}
	}

	//set random spots for PC ships
	public void setupPCShips() {
		int dX;
		int dY;
		Boolean axis;

		//over each ship - axis = true is vertical
		for (int i = 0; i < shipSizes.length; i++) {
			dX = random.nextInt(NUMROWS);
			dY = random.nextInt(NUMROWS);
			axis = random.nextBoolean();
			Boolean xSet = false;
			Boolean ySet = false;

			//vertical
			if (axis) {
				while (xSet == false) {
					Boolean checkShip = true;

					//in bounds
					if ((dX + shipSizes[i]) <= NUMROWS) {
						//check each requested ship position if it will overlap
						for (int j = 0; j < shipSizes[i]; j++) {
							if (pcBoard.containsShip(dX + j, dY)) {
								checkShip = false;
							}
						}

						if (checkShip) {
							for (int j = 0; j < shipSizes[i]; j++) {
								pcBoard.setShip(dX + j, dY, i);
								buttonGrid.get(dX+j).get(dY).setIcon(redTile);
								xSet = true;
							}
						}else {
							dX = random.nextInt(NUMROWS);
						}
					}else {
						dX = random.nextInt(NUMROWS);

					}
				}

				//horizontal
			}else {
				while (ySet == false) {
					Boolean checkShip = true;

					//in bounds
					if ((dY + shipSizes[i]) <= NUMROWS) {
						//check each requested ship position if it will overlap
						for (int j = 0; j < shipSizes[i]; j++) {
							if (pcBoard.containsShip(dX, dY + j)) {
								checkShip = false;
							}
						}

						if (checkShip) {
							for (int j = 0; j < shipSizes[i]; j++) {
								pcBoard.setShip(dX, dY + j, i);
								buttonGrid.get(dX).get(dY+j).setIcon(redTile);
								ySet = true;
							}
						}else {
							dY = random.nextInt(NUMROWS);
						}
					}else {
						dY = random.nextInt(NUMROWS);
					}
				}
			}

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(newGameButton)) {

			startNewGame();

		}else if (e.getSource().equals(recordButton)) {
			JOptionPane.showMessageDialog(centerPanel, "Wins: " + WINS + "\n" + "Losses: " + LOSSES);
		}else if (e.getSource().equals(resetRecordButton)) {
			WINS = 0;
			LOSSES = 0;
			try {
				writeSaveFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if (e.getSource().equals(shipButtons)) {

			int n = shipButtons.getSelectedIndex();

			switch(n) {
			case 0:
				selectedShip = 0;
				botLabel.setText("The carrier takes " + shipSizes[selectedShip] + " spaces.");
				coordOneSet = false;
				coordTwoSet = false;
				break;
			case 1:
				selectedShip = 1;
				botLabel.setText("The battleship takes " + shipSizes[selectedShip] + " spaces.");
				coordOneSet = false;
				coordTwoSet = false;
				break;
			case 2:
				selectedShip = 2;
				botLabel.setText("The destroyer takes " + shipSizes[selectedShip] + " spaces.");
				coordOneSet = false;
				coordTwoSet = false;
				break;
			case 3:
				selectedShip = 3;
				botLabel.setText("The submarine takes " + shipSizes[selectedShip] + " spaces.");
				coordOneSet = false;
				coordTwoSet = false;
				break;
			case 4:
				selectedShip = 4;
				botLabel.setText("The patrol boat takes " + shipSizes[selectedShip] + " spaces.");
				coordOneSet = false;
				coordTwoSet = false;
				break;
			}
		}else if (e.getSource().equals(startButton)) {
			if (gridIsSet == false) {
				int total = shipsAreSet.length;
				int m = 0;
				for (int k = 0; k < total; k++) {
					if (shipsAreSet[k]) {
						m++;
					}
				}

				//m adds how many ships are set. if equal to total number of ships, can set the grid
				if (m == total) {
					gridIsSet = true;
					updateLabelGrid();
					setupPCShips();
					newButtonGrid();
				}else {
					botLabel.setText("Please set your ships before starting the game.");
				}
			}
		}else if (e.getSource().equals(resetButton)) {
			if (coordOneSet && coordTwoSet == false) {
				buttonGrid.get(oneX).get(oneY).setIcon(blueTile);
			}
			coordOneSet = false;
			coordTwoSet = false;

		}else if (e.getSource().equals(mildMenuButton)) {
			difficulty = 1;
		}else if (e.getSource().equals(medMenuButton)) {
			difficulty = 2;
		}else if (e.getSource().equals(hotMenuButton)) {
			difficulty = 3;
		}
		else {
			//check button grid
			for (int i = 0; i < NUMROWS; i++) {
				for (int j = 0; j < NUMROWS; j++) {
					if (e.getSource() == buttonGrid.get(i).get(j)) {
						//options for grid setup
						if (gridIsSet == false) {
							setupPlayerShips(i, j);
						}

						//this is during the game phase, after setup
						else {
							try {
								runGamePhase(i, j, pcBoard);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							//PC turn
							if (PCTURN) {
								for (int q = 0; q < difficulty; q ++) {
									runAI();
									try {
										runGamePhase(pcTryX, pcTryY, playerBoard);
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								PCTURN = false;
							}
						}
					}
				}
			}
		}
	}



	//get the save file
	public void readSaveFile() throws IOException {
		ArrayList<String> inList = new ArrayList<String>();
		try {
			Scanner s = new Scanner(new FileReader(saveFile));

			int i = 0;
			while (s.hasNextLine()) {
				inList.add(s.nextLine());
				if (inList.get(i).contains("wins")) {
					String st[] = inList.get(i).split("= ");
					WINS = Integer.parseInt(st[1]);
				}else if (inList.get(i).contains("losses")) {
					String st[] = inList.get(i).split("= ");
					LOSSES = Integer.parseInt(st[1]);
				}
				i++;
			}

			s.close();
		}catch (FileNotFoundException e) {
			System.out.println("Unable to open file.");
		}
	}

	public void writeSaveFile() throws IOException {
		try {
			FileWriter w = new FileWriter(saveFile);

			w.write("wins = " + WINS + "\n");
			w.write("losses = " + LOSSES);

			w.close();
		}catch (FileNotFoundException e) {
			System.out.println("Unable to open file.");
		}
	}

	public static void main(final String[] args) throws IOException {
		new Battleship();
	}
}

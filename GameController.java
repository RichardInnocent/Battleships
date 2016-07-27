package data;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class GameController {
	
	private double textHeight = 100, mapMargin = 50;
	private Image backgroundImage;
	private double mapTopLeftX = 0, mapTopLeftY = 0, mapBottomRightX = 0, mapBottomRightY = 0;
	private GameShip shipToBePlaced;
	private Player currentPlayer, otherPlayer;
	private int[] currentCoordinates = new int[2];
	String headerText = "Player 1: Place your battleships! Left click to place, right click to rotate.";
	String supportText = "";
	boolean turnOfPlayer1 = true;
	
	// Overlay
	private double overlayWidth = 0, overlayHeight = 0;
	private double overlayX = 0, overlayY = 0;
	private Color overlayColor = new Color(1,1,1,0.5);
	
	// Mouse
	double mouseX, mouseY;
	
	/** Setting up window **/
	public GameController(Canvas canvas) {
		backgroundImage = new Image("images/sea.jpg");
		currentPlayer = GameData.getPlayer1();
		otherPlayer = GameData.getPlayer2();
		shipToBePlaced = currentPlayer.getNextShipToBePlaced();
		canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseClick) {
				
				// During setup phase
				if (GameData.getGamePhase() == GamePhase.SETUP) {
					
					// On left click
					if (mouseClick.getButton() == MouseButton.PRIMARY) {
						if (newShipPositionIsValid(currentCoordinates[0], currentCoordinates[1])) {
							shipToBePlaced.setMapPositionOfOrigin(currentCoordinates[0], currentCoordinates[1]);
							currentPlayer.addShip(shipToBePlaced);
						}
						
						// Updating the next ship that should be placed in the map:
						if (!GameData.getPlayer1().hasFinishedSetup()) {
							currentPlayer = GameData.getPlayer1();
							shipToBePlaced = currentPlayer.getNextShipToBePlaced();
							overlayColor = new Color(1,1,1,0.5);
							headerText = currentPlayer.getName() + ": Place your battleships! Left click to place, right click to rotate.";
						} else if (!GameData.getPlayer2().hasFinishedSetup()) {
							currentPlayer = GameData.getPlayer2();
							shipToBePlaced = currentPlayer.getNextShipToBePlaced();
							overlayColor = new Color(1,1,1,0.5);
							headerText = currentPlayer.getName() + ": Place your battleships! Left click to place, right click to rotate.";
						} else {
							currentPlayer = GameData.getPlayer1();
							GameData.setGamePhase(GamePhase.BATTLE);
							headerText = currentPlayer.getName() + ": Click to fire on the enemy! A red circle indicated a hit. A white circle indicates a miss.";
						}
						
					} else { // On right click
						// Change orientation of ship to be placed
						if (GameData.getGamePhase() == GamePhase.SETUP) {
							switch (shipToBePlaced.getOrientation()) {
								case NORTH:
									shipToBePlaced.setOrientation(Orientation.EAST);
									break;
								case EAST:
									shipToBePlaced.setOrientation(Orientation.SOUTH);
									break;
								case SOUTH:
									shipToBePlaced.setOrientation(Orientation.WEST);
									break;
								case WEST:
									shipToBePlaced.setOrientation(Orientation.NORTH);
									break;
							}
						}
					}
					
				} else { // During battle phase
					overlayColor = new Color(1,1,1,0.5);
					overlayWidth = overlayHeight = 0;
					
					updateOverlay();
					
					if (mouseIsWithinMap()) {
						
						int [] mapCoordinates = getMapCoordinatesFromMousePosition(mouseX, mouseY);
						
						if (currentPlayer.getFiredAtState(mapCoordinates[0], mapCoordinates[1]) == MapFiredUponState.UNTESTED && !GameData.gameHasBeenWon()) {
							
							MapFiredUponState stateOfAttack = MapFiredUponState.MISS;
							
							currentPlayer.fireAt(mapCoordinates[0], mapCoordinates[1], stateOfAttack);
							otherPlayer.enemyFireHasHit(mapCoordinates[0], mapCoordinates[1]);

							if (otherPlayer.hasShipAtCoordinates(mapCoordinates[0], mapCoordinates[1])) {
								stateOfAttack = MapFiredUponState.HIT;
								currentPlayer.fireAt(mapCoordinates[0], mapCoordinates[1], stateOfAttack);
								if (otherPlayer.getNumberOfShipsRemaining() == 0) {
									GameData.setGameWon(true);
								}
							} else {
								if (turnOfPlayer1) {
									currentPlayer = GameData.getPlayer2();
									otherPlayer = GameData.getPlayer1();
									turnOfPlayer1 = false;
								} else {
									currentPlayer = GameData.getPlayer1();
									otherPlayer = GameData.getPlayer2();
									turnOfPlayer1 = true;
								}
							}
							
							updateTextFields();
							
						}
					}
				}
				
			}
		});
		canvas.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseMove) {
				mouseX = mouseMove.getX();
				mouseY = mouseMove.getY();
				currentCoordinates = getMapCoordinatesFromMousePosition(mouseX, mouseY);
			}
		});
	}
	
	/** This method calculates the position of the overlay **/
	private void updateOverlay() {
		
		// If not in map, set overlay dimensions to 0
		if (mouseX < mapTopLeftX || mouseX >= mapBottomRightX || mouseY < mapTopLeftY || mouseY >= mapBottomRightY) {
			overlayWidth = overlayHeight = 0;
			return;
		}

		double mapLength = mapBottomRightX - mapTopLeftX;
		if (GameData.getGamePhase() == GamePhase.SETUP) {
			switch (shipToBePlaced.getOrientation()) {

			case NORTH:
				overlayWidth = mapLength / GameData.getMapWidth();
				overlayHeight = shipToBePlaced.getLength() * (mapLength / GameData.getMapWidth());
				overlayX = currentCoordinates[0] * (mapLength / GameData.getMapWidth()) + mapTopLeftX;
				overlayY = currentCoordinates[1] * (mapLength / GameData.getMapWidth()) + mapTopLeftY;
				break;
			case EAST:
				overlayWidth = shipToBePlaced.getLength() * (mapLength / GameData.getMapWidth());
				overlayHeight = mapLength / GameData.getMapWidth();
				overlayX = currentCoordinates[0] * (mapLength / GameData.getMapWidth()) + mapTopLeftX + mapLength / GameData.getMapWidth()
						- shipToBePlaced.getLength() * (mapLength / GameData.getMapWidth());
				overlayY = currentCoordinates[1] * (mapLength / GameData.getMapWidth()) + mapTopLeftY;
				break;
			case SOUTH:
				overlayWidth = mapLength / GameData.getMapWidth();
				overlayHeight = shipToBePlaced.getLength() * (mapLength / GameData.getMapWidth());
				overlayX = currentCoordinates[0] * (mapLength / GameData.getMapWidth()) + mapTopLeftX;
				overlayY = currentCoordinates[1] * (mapLength / GameData.getMapWidth()) + mapTopLeftY + (mapLength / GameData.getMapWidth())
						- shipToBePlaced.getLength() * (mapLength / GameData.getMapWidth());
				break;
			case WEST:
				overlayWidth = shipToBePlaced.getLength() * (mapLength / GameData.getMapWidth());
				overlayHeight = mapLength / GameData.getMapWidth();
				overlayX = currentCoordinates[0] * (mapLength / GameData.getMapWidth()) + mapTopLeftX;
				overlayY = currentCoordinates[1] * (mapLength / GameData.getMapWidth()) + mapTopLeftY;
				break;

			}
			int[] mapCoordinatesOfMouse = getMapCoordinatesFromMousePosition(mouseX, mouseY);
			if (!newShipPositionIsValid(mapCoordinatesOfMouse[0], mapCoordinatesOfMouse[1])) {
				overlayColor = new Color(1, 0, 0, 0.5);
			} else {
				overlayColor = new Color(1, 1, 1, 0.5);
			}
		} else {
			overlayWidth = overlayHeight = mapLength / GameData.getMapWidth();
			overlayX = mapTopLeftX + (mapLength / GameData.getMapWidth()) * currentCoordinates[0];
			overlayY = mapTopLeftY + (mapLength / GameData.getMapWidth()) * currentCoordinates[1];
		}

		
	}
	
	/** This method determines whether a ship can be placed at the current position **/
	private boolean newShipPositionIsValid(int xCoordinate, int yCoordinate) {
		
		int xIterator = 0, yIterator = 0;
		int originX = xCoordinate;
		int originY = yCoordinate;
		int endX, endY;
		
		switch (shipToBePlaced.getOrientation()) {
			case NORTH:
				yIterator = 1;
				break;
			case EAST:
				xIterator = -1;
				break;
			case SOUTH:
				yIterator = -1;
				break;
			case WEST:
				xIterator = 1;
				break;
		}
		
		endX = originX + (xIterator * (shipToBePlaced.getLength()-1));
		endY = originY + (yIterator * (shipToBePlaced.getLength()-1));
		
		// Checking the ship fits on the map
		if (originX < 0 || originX >= GameData.getMapWidth() || originY < 0 || originY >= GameData.getMapHeight() ||
				endX < 0 || endX >= GameData.getMapWidth() || endY < 0 || endY >= GameData.getMapHeight()) {
			return false;
		}
		
		// Checking if the ship is obstructed by other ships
		int[] currentShipPartCoordinates = {originX, originY};
		
		for (int shipPart = 0; shipPart < shipToBePlaced.getLength(); shipPart++) {
			if (currentPlayer.hasShipAtCoordinates(currentShipPartCoordinates[0], currentShipPartCoordinates[1])) {
				return false;
			}
			currentShipPartCoordinates[0] += xIterator;
			currentShipPartCoordinates[1] += yIterator;
		}
		
		return true;
		
	}
	
	/** Draws the window to the given graphics context **/
	public void draw(GraphicsContext drawer) {
		
		updateOverlay();
		
		// Background image
		drawer.drawImage(backgroundImage, 0, 0);
		
		// Background for the text
		drawer.setFill(new Color(1,1,1,0.5));
		drawer.fillRect(0, 0, GameData.getWindowWidth(), textHeight);
		drawer.setFill(Color.BLACK);
		drawer.fillText(headerText, 20, 30);
		drawer.fillText(supportText, 20, 70);
		
		// Drawing the map
		drawMap(drawer);
		
		if (GameData.getGamePhase() == GamePhase.SETUP) {
			drawSetupState(drawer);
		} else {
			drawBattleState(drawer);
		}
		
		// Drawing the overlay, showing how the mouse interacts with the map
		drawer.setFill(overlayColor);
		drawer.fillRect(overlayX, overlayY, overlayWidth, overlayHeight);
		
	}
	
	private void drawMap(GraphicsContext drawer) {
		// Drawing the map
		double mapTopLeftXPotential = mapMargin;
		double mapTopLeftYPotential = textHeight + mapMargin;
		double mapBottomRightXPotential = GameData.getWindowWidth() - mapMargin;
		double mapBottomRightYPotential = GameData.getWindowHeight() - mapMargin;
		
		double mapMaximumWidth = mapBottomRightXPotential - mapTopLeftXPotential;
		double mapMaximumHeight = mapBottomRightYPotential - mapTopLeftYPotential;
		
		// Generating a square dimension for 
		double mapLength = (mapMaximumWidth <= mapMaximumHeight) ? mapMaximumWidth : mapMaximumHeight;
		
		mapTopLeftX = (GameData.getWindowWidth() - mapLength) / 2;
		mapTopLeftY = textHeight + ((GameData.getWindowHeight() - textHeight) - mapLength)/2;
		mapBottomRightX = GameData.getWindowWidth() - mapTopLeftX;
		mapBottomRightY = GameData.getWindowHeight() - (mapTopLeftY - textHeight);
		
		drawer.setStroke(Color.WHITESMOKE);
		drawer.setLineWidth(3);
		
		// Drawing map segment lines
		int numberOfHorizontalLines = GameData.getMapHeight() + 1;
		int numberOfVerticalLines = GameData.getMapWidth() + 1;
		
		double currentPosition = mapTopLeftY;
		
		// Drawing horizontal lines
		for (int i = 0; i < numberOfHorizontalLines; i++) {
			drawer.strokeLine(mapTopLeftX, currentPosition, mapBottomRightX, currentPosition);
			currentPosition += mapLength / ((double) (numberOfHorizontalLines-1));
		}
		
		currentPosition = mapTopLeftX;
		
		// Drawing vertical lines
		for (int i = 0; i < numberOfVerticalLines; i++) {
			drawer.strokeLine(currentPosition, mapTopLeftY, currentPosition, mapBottomRightY);
			currentPosition += mapLength / ((double) (numberOfHorizontalLines-1));
		}
	}
	
	/** Draws the map during the setup state **/
	private void drawSetupState(GraphicsContext drawer) {
		// Drawing ships for the current player
		drawer.setFill(Color.DARKGRAY);
		double mapLength = mapBottomRightX - mapTopLeftX;
		for (int x = 0; x < GameData.getMapWidth(); x++) {
			for (int y = 0; y < GameData.getMapWidth(); y++) {
				if (currentPlayer.hasShipAtCoordinates(x, y)) {
					drawer.fillRect(mapTopLeftX+(mapLength/GameData.getMapWidth())*x, mapTopLeftY+(mapLength/GameData.getMapWidth())*y,
							mapLength/GameData.getMapWidth(), mapLength/GameData.getMapWidth());
				}
			}
		}
	}
	
	/** Draws the map during the battle state **/
	private void drawBattleState(GraphicsContext drawer) {
		double mapWidth = mapBottomRightX - mapTopLeftX;
		if (turnOfPlayer1) {
			currentPlayer = GameData.getPlayer1();
		} else {
			currentPlayer = GameData.getPlayer2();
		}
		
		for (int x = 0; x < GameData.getMapWidth(); x++) {
			for (int y = 0; y < GameData.getMapWidth(); y++) {
				
				double xCoordinate = mapTopLeftX + (mapWidth/GameData.getMapWidth())*x;
				double yCoordinate = mapTopLeftY + (mapWidth/GameData.getMapWidth())*y;
				
				switch (currentPlayer.getFiredAtState(x, y)) {
				case HIT:
					drawer.drawImage(new Image("images/fireball2.png"), xCoordinate, yCoordinate, mapWidth/GameData.getMapWidth(), mapWidth/GameData.getMapWidth());
					break;
				case MISS:
					drawer.drawImage(new Image("images/splash.png"), xCoordinate, yCoordinate, mapWidth/GameData.getMapWidth(), mapWidth/GameData.getMapWidth());
					break;
				default:
					break;
				}
			}
		}
	}
	
	/** Returns the map coordinate location of the click. If the click is not within the map, {-1, -1} is returned. **/
	private int [] getMapCoordinatesFromMousePosition(double mouseX, double mouseY) {
		int [] mapCoordinates = {-1,-1};
		// If inside map
		if (mouseX >= mapTopLeftX && mouseX < mapBottomRightX && mouseY >= mapTopLeftY && mouseY < mapBottomRightY) {
			// Getting x coordinate:
			mapCoordinates[0] = (int) (((mouseX - mapTopLeftX) / (mapBottomRightX - mapTopLeftX)) * GameData.getMapWidth());
			mapCoordinates[1] = (int) (((mouseY - mapTopLeftY) / (mapBottomRightY - mapTopLeftY)) * GameData.getMapHeight());
		}
			
		return mapCoordinates;
	}

	/** This method determines whether the mouse is within the map **/
	private boolean mouseIsWithinMap() {
		if (mouseX < mapTopLeftX || mouseX >= mapBottomRightX || mouseY <= mapTopLeftY || mouseY > mapBottomRightY) {
			return false;
		} else {
			return true;
		}
	}
	
	/** This method updates the text fields to display appropriate user instructions **/
	private void updateTextFields() {
		
		// Change the text fields depending on the state of the game
		switch (GameData.getGamePhase()) {
		
		case SETUP:
			headerText = currentPlayer.getName() + ": Place your battleships! Left click to place, right click to rotate.";
			supportText = "";
			break;
		
		case BATTLE:
			if (GameData.gameHasBeenWon()) {
				headerText = "Game over!";
				supportText = currentPlayer.getName() + " has won the game!";
			} else {
				headerText = currentPlayer.getName() + ", fire at the enemy! Explosion represents a hit.";
				supportText = "Number of enemy ships remaining: " + otherPlayer.getNumberOfShipsRemaining();
			}
			break;
		}
		
	}
}

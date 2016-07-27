package data;

public class Player {
	
	private String name;
	GameShip [] ships = new GameShip[7];
	MapFiredUponState[][] areasFiredUpon;
	private int numberOfPatrolBoats = 0, numberOfBattleships = 0, numberOfSubmarines = 0, numberOfDestroyers = 0, numberOfCarriers = 0;
	private GameShip nextShipToBePlaced;
	private boolean setupFinished = false;
	private int numberOfShipsPlaced = 0;
	private int numberOfShipsRemaining = 7;
	private boolean isAI = false;
	
	public Player(String _name) {
		
		name = _name;
		
		// Initialising all ships to null
		for (int i = 0; i < 7; i++) {
			ships[i] = null;
		}

		updateNextShipToBePlaced();
		
		// Filling the areas fired upon with an untested state
		areasFiredUpon = new MapFiredUponState[GameData.getMapWidth()][GameData.getMapWidth()];
		for (int x = 0; x < GameData.getMapWidth(); x++) {
			for (int y = 0; y < GameData.getMapWidth(); y++) {
				areasFiredUpon[x][y] = MapFiredUponState.UNTESTED;
			}
		}
		
	}
	
	/** This method returns the name of the player. **/
	public String getName() {
		return name;
	}
	
	/** This method sets the name of the player. **/
	public void setName(String newName) {
		name = newName;
	}
	
	/** Returns whether or not the player is AI. **/
	public boolean isAI() {
		return isAI;
	}
	
	/** Sets the player to be an AI character. **/
	public void setAI() {
		isAI = true;
	}
	
	/** This method adds a new ship to the player's roster. **/
	public void addShip(GameShip ship) {
		
		// Checking the ship can be added:
		if (ship instanceof PatrolBoat) {
			if (numberOfPatrolBoats >= 2) {
				System.out.println("Maximum number of patrol boats already reached.");
				return;
			} else {
				numberOfPatrolBoats++;
			}
		} else if (ship instanceof Battleship) {
			if (numberOfBattleships >= 2) {
				System.out.println("Maximum number of battleships already reached.");
				return;
			} else {
				numberOfBattleships++;
			}
		} else if (ship instanceof Submarine) {
			if (numberOfSubmarines >= 1) {
				System.out.println("Maximum number of submarines already reached.");
				return;
			}
		} else if (ship instanceof Destroyer) {
			if (numberOfDestroyers >= 1) {
				System.out.println("Maximum number of destroyers already reached.");
				return;
			} else {
				numberOfDestroyers++;
			}
		} else {
			if (numberOfCarriers >= 1) {
				System.out.println("Maximum number of carriers already reached.");
				return;
			} else {
				numberOfCarriers++;
			}
		}
		
		// Adding ship to empty slot. No need for validation check as the total number of ships is verified above.
		ships[numberOfShipsPlaced++] = ship;
		updateNextShipToBePlaced();
		
	}
	
	/** This method returns the number of ships that the player has currently placed on the map during the setup phase **/
	public int getNumberOfShipsPlaced() {
		return numberOfShipsPlaced;
	}
	
	/** This method changes the next ship that should be added to the map **/
	private void updateNextShipToBePlaced() {
		switch (numberOfShipsPlaced) {
		case 0:
		case 1:
			nextShipToBePlaced = new PatrolBoat();
			break;
		case 2:
		case 3:
			nextShipToBePlaced = new Battleship();
			break;
		case 4:
			nextShipToBePlaced = new Submarine();
			break;
		case 5:
			nextShipToBePlaced = new Destroyer();
			break;
		case 6:
			nextShipToBePlaced = new Carrier();
			break;
		default:
			setupFinished = true;
			nextShipToBePlaced = null;
			break;
		}
	}
	
	/** This method returns the next ship that should be added to the map **/
	public GameShip getNextShipToBePlaced() {
		return nextShipToBePlaced;
	}
	
	/** This method returns whether the player has placed all 7 ships **/
	public boolean hasFinishedSetup() {
		return setupFinished;
	}
	
	/** This method returns the ship at the specified index **/
	public GameShip getShip(int index) {
		return ships[index];
	}
	
	/** This method whether or not the player has a ship located at the given coordinates **/
	public boolean hasShipAtCoordinates(int xCoordinate, int yCoordinate) {
		GameShip shipAtCoordinates = getShipAtCoordinates(xCoordinate, yCoordinate);
		if (shipAtCoordinates == null) {
			return false;
		} else {
			return true;
		}
	}
	
	/** This method returns the ship that is located at the given position. A null pointer is provided if there is no ship at the location. **/
	public GameShip getShipAtCoordinates(int xCoordinate, int yCoordinate) {
		for (int i = 0; i < 7; i++) {
			
			// Must have looped through all ships that the player owns
			if (ships[i] == null) {
				return null;
			}
			
			int xIterator = 0, yIterator = 0;
			
			switch (ships[i].getOrientation()) {
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
			
			int [] currentCoordinates = {ships[i].getXCoordinate(), ships[i].getYCoordinate()};
			
			// Looping through all coordinates of the ship to see if it intersects the given coordinates
			for (int shipPart = 0; shipPart < ships[i].getLength(); shipPart++) {
				if (currentCoordinates[0] == xCoordinate && currentCoordinates[1] == yCoordinate) {
					return ships[i];
				}
				currentCoordinates[0] += xIterator;
				currentCoordinates[1] += yIterator;
			}
						
		}
		return null;
	}
	
	/** Registers that the player has fired at the following coordinates. **/
	public void fireAt(int xCoordinate, int yCoordinate, MapFiredUponState mapFiredUponState) {
		areasFiredUpon[xCoordinate][yCoordinate] = mapFiredUponState;
	}
	
	/** Returns whether or not the player has hit, missed or untested a spot at a particular coordinate. **/
	public MapFiredUponState getFiredAtState(int xCoordinate, int yCoordinate) {
		return areasFiredUpon[xCoordinate][yCoordinate];
	}
	
	/** This method should be called when the other player fires upon the map of this class. It will return a boolean as to whether or not a ship has been hit. **/
	public boolean enemyFireHasHit(int xCoordinate, int yCoordinate) {
		GameShip gameShipAtCoordinates = getShipAtCoordinates(xCoordinate, yCoordinate);
		if (gameShipAtCoordinates != null) {
			gameShipAtCoordinates.hit();
			if (gameShipAtCoordinates.isDestroyed()) {
				numberOfShipsRemaining--;
			}
			return true;
		}
		return false;
	}
	
	/** This method returns the number of ships that the player has still standing. **/
	public int getNumberOfShipsRemaining() {
		return numberOfShipsRemaining;
	}
	
}
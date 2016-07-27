package data;

public abstract class GameShip {
	
	protected int length;
	protected Orientation orientation = Orientation.NORTH;
	protected int xCoordinate, yCoordinate;
	protected int numberOfTimesHit = 0;
	
	/** Sets the position of the origin of the ship on the map **/
	public void setMapPositionOfOrigin(int _xCoordinate, int _yCoordinate) {
		xCoordinate = _xCoordinate;
		yCoordinate = _yCoordinate;
	}
	
	/** Returns the x coordinate of the ship on the map **/
	public int getXCoordinate() {
		return xCoordinate;
	}
	
	/** Returns the y coordinate of the ship on the map **/
	public int getYCoordinate() {
		return yCoordinate;
	}
	
	/** Sets the orientation of the ship **/
	public void setOrientation(Orientation _orientation) {
		orientation = _orientation;
	}
	
	/** Returns the orientation of the ship **/
	public Orientation getOrientation() {
		return orientation;
	}
	
	/** Returns the length of the ship **/
	public int getLength() {
		return length;
	}
	
	/** Returns whether or not all of the elements of the ship have been hit and hence the ship has been destroyed **/
	public boolean isDestroyed() {
		if (numberOfTimesHit >= length) {
			return true;
		} else {
			return false;
		}
	}
	
	/** This method should be called when the ship is hit. **/
	public void hit() {
		numberOfTimesHit++;
		if (isDestroyed()) {
			System.out.println("Ship destroyed");
		}
	}
	
}

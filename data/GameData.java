package data;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameData extends Application {
	
	private static boolean gameRunning = true, gameWon = false;
	private static GamePhase gamePhase = GamePhase.SETUP;
	private static Player player1, player2;
	private static double windowWidth = 1000, windowHeight = 600;
	private static GameController gameWindow;
	private static int mapWidth = 12, mapHeight = 12;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// Setting scene
		primaryStage.setTitle("Battleships");
		StackPane backgroundPane = new StackPane();
		primaryStage.setScene(new Scene (backgroundPane, windowWidth, windowHeight));
		
		// Opening scene
		primaryStage.show();
		
		// Initialising game components
		player1 = new Player("Player 1");
		player2 = new Player("Player 2");

		// Adding canvas
		Canvas canvas = new Canvas(windowWidth, windowHeight);
		backgroundPane.getChildren().add(canvas);
		gameWindow = new GameController(canvas);
		
		// Listen for the width of the window to change
		primaryStage.getScene().widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldWidth, Number newWidth) {
				windowWidth = (double) newWidth;
				canvas.setWidth(windowWidth);
			}
		});
		
		// Listen for the height of the window to change
		primaryStage.getScene().heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldHeight, Number newHeight) {
				windowHeight = (double) newHeight;
				canvas.setHeight(windowHeight);
			}
		});
		
		// Starting drawing thread
		new Thread(new GameWindow(canvas.getGraphicsContext2D())).start();
		
	}
	
	/** Returns whether or not the game is running at the current moment **/
	public static boolean isGameRunning() {
		return gameRunning;
	}
	
	/** Terminates the game **/
	public static void stopRunning() {
		gameRunning = false;
	}
	
	/** Returns player 1 **/
	public static Player getPlayer1() {
		return player1;
	}
	
	/** Returns player 2 **/
	public static Player getPlayer2() {
		return player2;
	}
	
	/** Returns the current width of the window **/
	public static double getWindowWidth() {
		return windowWidth;
	}
	
	/** Returns the current height of the window **/
	public static double getWindowHeight() {
		return windowHeight;
	}
	
	/** Returns the phase of the current game, i.e. in setup or in game **/
	public static GamePhase getGamePhase() {
		return gamePhase;
	}
	
	/** Sets the current phase of the game **/
	public static void setGamePhase(GamePhase _gamePhase) {
		gamePhase = _gamePhase;
	}
	
	/** Returns the game window **/
	public static GameController getGameWindow() {
		return gameWindow;
	}
	
	/** Returns whether or not the game has been won by a player. **/
	public static boolean gameHasBeenWon() {
		return gameWon;
	}
	
	/** Sets whether the game has been won by a player. **/
	public static void setGameWon(boolean _gameWon) {
		gameWon = true;
	}
	
	/** Returns the height of the map **/
	public static int getMapHeight() {
		return mapHeight;
	}
	
	public static int getMapWidth() {
		return mapWidth;
	}

}

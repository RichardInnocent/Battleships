package data;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

public class GameWindow implements Runnable {
	
	private GraphicsContext drawer;
	
	/** Creates a drawing tool to draw the battleships game on any given graphics context **/
	public GameWindow(GraphicsContext _drawer) {
		drawer = _drawer;
	}

	@Override
	/** Draws the game repeatedly while the game is running **/
	public void run() {
		
		GameController gameWindow = GameData.getGameWindow();

		// Draws at reasonable refresh rate
		new AnimationTimer() {
			@Override
			public void handle(long arg0) {
				
				// If the game is running, draw the correct phase
				if (GameData.isGameRunning()) {
					gameWindow.draw(drawer);
				}
				
			}
		}.start();
	}

}

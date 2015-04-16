package tinywings;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicStampedReference;

import javax.swing.JFrame;

/**
 * This class is responsible for interacting with the human player to
 * determine the action of the player controlled bird.
 * Rather than making decisions like an AI bird, it displays to the player and
 * asks for input.
 */
public class PlayerBird extends Bird implements KeyListener {

	public PlayerBird(AtomicStampedReference<BufferedImage> screen) {
		super(screen);
	}

	@Override
	public void run() {
		// Set up a window.
		JFrame frame = new JFrame();
		frame.addKeyListener(this);
		PlayerScreen playerScreen = new PlayerScreen(screen);
		frame.add(playerScreen);
		// Not super important for the references to be consistent here since the dimensions should be constant.
		frame.setSize(screen.getReference().getWidth(), screen.getReference().getHeight());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		// Request a screen redraw when it appears the image was updated.
		int stamp = screen.getStamp();
		while (true) {
			int newStamp = screen.getStamp();
			if (stamp <= newStamp) {
				stamp = newStamp;
				playerScreen.repaint();
			} else {
				// Shutdown code, end thread.
				break;
			}
		}
		frame.setVisible(false);
		frame.dispose();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == ' ') {
			pressed.set(true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyChar() == ' ') {
			pressed.set(false);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}
}

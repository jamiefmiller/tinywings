package tinywings;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.atomic.AtomicStampedReference;

import javax.swing.JFrame;

/**
 * This class is responsible for interacting with the human player to
 * determine the action of the player controlled bird.
 * Rather than making decisions like an AI bird, it displays to the player and
 * asks for input.
 */
public class PlayerBird extends Bird implements KeyListener {
	
	private int stamp;
	private PlayerScreen playerScreen;
	private JFrame frame;
	//private boolean lookedForBetterstampOnce = false;

	public PlayerBird(AtomicStampedReference<BufferedImage> screen, List<AtomicStampedReference<BufferedImage>> screens) {
		super(screen, screens);
		playerScreen = new PlayerScreen(screen);
		stamp = screen.getStamp();
		
		frame = new JFrame();
		frame.addKeyListener(this);
		frame.add(playerScreen);
		// Not super important for the references to be consistent here since the dimensions should be constant.
		frame.setSize(screen.getReference().getWidth(), screen.getReference().getHeight());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	@Override
	public void run() {		
		// Request a screen redraw when it appears the image was updated.
		while (computeFrame()) {}
		frame.setVisible(false);
		frame.dispose();
	}

	@Override
	boolean computeFrame() {
		int newStamp = screen.getStamp();
		if (stamp <= newStamp) {
			stamp = newStamp;
			/*for (AtomicStampedReference<BufferedImage> otherscreen : screens) {
				while(otherscreen.getStamp() < stamp && otherscreen.getStamp() > 0){}
				if (otherscreen.getStamp() > stamp && !lookedForBetterstampOnce) {
					lookedForBetterstampOnce = true;
					return true;
				}
			}*/
			playerScreen.repaint();
			return true;
		} else {
			// Shutdown code, end thread.
			return false;
		}
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

package tinywings;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicStampedReference;

import javax.swing.JPanel;

public class PlayerScreen extends JPanel {
	private static final long serialVersionUID = -6890052749851639929L;
	private AtomicStampedReference<BufferedImage> screen;

	public PlayerScreen(AtomicStampedReference<BufferedImage> screen) {
		this.screen = screen;
	}

	@Override
	public void paint(Graphics graphics)
	{
		// Not interested in the stamp, just give user the latest image available.
		BufferedImage image = screen.getReference();
		graphics.drawImage(image, 0, 0, null);
	}
}

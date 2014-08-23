import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

public class RandomTree {
	public final static int MAX_DEPTH = 200;
	public final static int defaultCircleRadius = 10;

	public final static int screenWidth = 1600;
	public final static int screenHeight = 900;
	
	public static TreeLeaf firstLeaf;
	
	public static int currentDrawDepth = 1;
	
	public static final TreePanel treePanel = new TreePanel();
	
	public static Button makeButton() {
		return new Button();
	}

	public static void main(final String[] args) {
		final JPanel mainPanel = new JPanel();
		

		final JFrame window = new JFrame("RandomTree");
		window.setContentPane(mainPanel);
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent evt) {
				System.out.println("Exiting...");
				System.exit(0);
			}
		});

		// window.setContentPane(mainPanel);
		window.setSize(screenWidth, screenHeight);
		window.setVisible(true);

		final GridBagLayout layout = new GridBagLayout();
		final GridBagConstraints c = new GridBagConstraints();
		mainPanel.setLayout(layout);

		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		treePanel.setPreferredSize(new Dimension(screenWidth - 20,
				screenHeight - 90));
		mainPanel.add(treePanel, c);

		c.gridy = 1;
		final ReloadButton reloadButton = new ReloadButton();
		mainPanel.add(reloadButton, c);
		
		SlowDrawingThread thread = new SlowDrawingThread();
		thread.start();
		
	}
	

	

}

class SlowDrawingThread extends Thread {
	/* 
	 * This object modifies global state and is NOT thread-safe. Only one of this thread may run at a time.
	 * If several threads are started, the object itself ensures that they do not run simultaneously
	 * They will run in the order that they were started
	*/
	
	// Stores the currently running thread, and the thread that is last in the running queue.
	// lastQueuedThread may be the running thread and may be dead.
	public static SlowDrawingThread runningThread;
	public static SlowDrawingThread lastQueuedThread;
	
	private boolean willTerminate = false;
	
	public void terminate() {
		willTerminate = true;
	}
	
	public void run() {
		if (runningThread == null) {
			runningThread = this;
			lastQueuedThread = this;
		}
		else {
			System.out.println("A drawing thread was already running, please wait for it to terminate");
			try {
				SlowDrawingThread threadToJoin = lastQueuedThread;
				lastQueuedThread = this;
				threadToJoin.join();
				runningThread = this; // Old thread has now finished running
			} 
			catch (InterruptedException e) {
			}
		}
		// Start creating a new tree
		RandomTree.currentDrawDepth = 1;
		RandomTree.firstLeaf = new TreeLeaf();
		drawSlowly();
		runningThread = null;
	}
	
	/*
	 * Slowly unveils the tree on the screen. 
	 * The whole tree has already been created in memory at this point
	 */
	public void drawSlowly() {
		
		if (RandomTree.currentDrawDepth <= 1) {
			System.out.println("Leaf count: " + RandomTree.firstLeaf.getLeafCount());
		}
		
		while (RandomTree.currentDrawDepth <= RandomTree.MAX_DEPTH) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			if (willTerminate) {
				return;
			}
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					RandomTree.treePanel.repaint();
				}
			});
			
			if (RandomTree.currentDrawDepth % 10 == 0) {
				System.out.println("Draw depth = " + RandomTree.currentDrawDepth);
			}
			RandomTree.currentDrawDepth++;

		}
	}
}


class ReloadButton extends JButton {

	private static final long serialVersionUID = 4239444440174687845L;

	public ReloadButton() {
		super("Reload");
		addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Creating new firstLeaf");
				SlowDrawingThread thread = new SlowDrawingThread();
				SlowDrawingThread.lastQueuedThread.terminate();
				thread.start();
			}
		});
	};
}

class TreePanel extends JPanel {
	private static final long serialVersionUID = -8146966673803489602L;

	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		drawLeaf(RandomTree.firstLeaf, g);
	}

	// Recursively draws the leaf and its children.
	public void drawLeaf(final TreeLeaf leaf, final Graphics g) {
		if (leaf != null && leaf.depth < RandomTree.currentDrawDepth) {
			g.setColor(new Color(flatten(leaf.red), flatten(leaf.green),
					flatten(leaf.blue)));

			g.fillOval(leaf.xPos, leaf.yPos, (int) (leaf.xRadius),
					(int) (leaf.yRadius));

			drawLeaf(leaf.leftLeaf, g);
			drawLeaf(leaf.topLeaf, g);
			drawLeaf(leaf.rightLeaf, g);

		}
	}

	// "Flattens" the color value, placing it within 0-255 with any input.
	public static int flatten(final int color) {
		final int absColor = Math.abs(color);
		if ((int) (absColor / 256) % 2 == 0) {
			return absColor - (absColor / 256) * 256;
		} else {
			return ((absColor / 256) * 256 + 255 - absColor);
		}
	}

	public static boolean testFlatten() {
		for (int i = -100000; i < 100000; i++) {
			int flattened = flatten(i);
			if (flattened > 255 || flattened < 0) {
				System.out.println("Failed at input \"" + i + "\", returned "
						+ flattened);
				return false;
			}
		}
		return true;
	}

}

enum RelPos {LEFT, TOP, RIGHT}

public class TreeLeaf {
	
	/* 
	 * Represents an oval leaf on the random tree
	 * May recursively contain other leaves
	 * This object is immutable and does not modify global state
	 * It is thread-safe once created
	 */
	
	public final int depth;
	
	// Stores whether the leaf was positioned on any of the 
	// tree's extremities upon creation.
	public final boolean isLeftMost;
	public final boolean isRightMost;
	public final boolean isCentre;
	
	//Each leaf may have up to three child leaves.
	public final TreeLeaf leftLeaf;
	public final TreeLeaf topLeaf;
	public final TreeLeaf rightLeaf;
	
	// Pixel coordinates on the screen
	public final int xPos;
	public final int yPos;
	
	public final int xRadius;
	public final int yRadius;
	
	public final int red;
	public final int green;
	public final int blue;
	
	/* This constructor is only meant to be called on the first leaf.
	 * It will then recursively create all other leaves in the tree.
	 */
	public TreeLeaf () {
		depth = 0;
		xRadius = RandomTree.defaultCircleRadius;
		yRadius = RandomTree.defaultCircleRadius;
		
		xPos = RandomTree.screenWidth / 2;
		yPos = RandomTree.screenHeight - RandomTree.defaultCircleRadius;
		
		red = 128;
		green = 0;
		blue = 0;
		
		isRightMost = true;
		isLeftMost= true;
		isCentre = true;
		
		if (depth < RandomTree.MAX_DEPTH) {
			leftLeaf = new TreeLeaf(this, RelPos.LEFT);
			topLeaf = new TreeLeaf(this, RelPos.TOP);
			rightLeaf = new TreeLeaf(this, RelPos.RIGHT);
		}
		else {
			leftLeaf = null;
			topLeaf = null;
			rightLeaf = null;
		}
	}
	
	
	private TreeLeaf (TreeLeaf parent, RelPos relPos) {
		
		xRadius = (int)Math.round(parent.xRadius * getBiasedRandom(50));
		yRadius = (int)Math.round(parent.xRadius * getBiasedRandom(50));
		
		red = parent.red + 4;
		green = parent.green + 7;
		blue = parent.blue + 5;
		
		//
		//
		//
		
		//Sets the position of TreeLeaf adjacent to its parent.
		xPos = (relPos == RelPos.RIGHT) ? parent.xPos + xRadius / 2 + parent.xRadius / 2 : 
			(relPos == RelPos.LEFT) ? parent.xPos - xRadius / 2 - parent.xRadius / 2 : parent.xPos; 
		yPos = ((relPos == RelPos.TOP)  ? parent.yPos - yRadius / 2 - parent.yRadius / 2 : parent.yPos);
		
		depth = parent.depth + 1;
		
		//Ensures that leaves only create new leaves on the edges
		isLeftMost = (relPos == RelPos.LEFT || relPos == RelPos.TOP) ? parent.isLeftMost : false;
		isRightMost = (relPos == RelPos.RIGHT || relPos == RelPos.TOP) ? parent.isRightMost : false;
		isCentre = (relPos == RelPos.TOP) ? parent.isCentre : false;
		
		//If the circle is off screen, don't give it any more children
		if (depth > RandomTree.MAX_DEPTH || xPos < 0 || xPos > RandomTree.screenWidth 
				|| yPos < 0 || yPos > RandomTree.screenHeight ) {
			
			leftLeaf = null;
			topLeaf = null;
			rightLeaf = null;
		}
		else {
			leftLeaf = isLeftMost ? new TreeLeaf(this, RelPos.LEFT) : null;
			topLeaf = isCentre ? new TreeLeaf(this, RelPos.TOP) : null;
			rightLeaf = isRightMost ? new TreeLeaf(this, RelPos.RIGHT) : null;
		}
	}
	
	public int getLeafCount() {
		int count = 1;
		count += leftLeaf != null ? leftLeaf.getLeafCount() : 0;
		count += topLeaf != null ? topLeaf.getLeafCount() : 0;
		count += rightLeaf != null ? rightLeaf.getLeafCount() : 0;
		return count;
	}
	
	// Returns a pseudorandom number biased towards the middle
	// Higher input int means heavier bias
	public static double getBiasedRandom(int n) {
		double sum = 0;
		int i = n;
		while (i > 0) {
			sum = sum + Math.random();
			i--;
		}
		return sum * 2 / n;
	}
}

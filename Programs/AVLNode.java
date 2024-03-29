// Aum Amma

public class AVLNode {
    protected int data;
    protected AVLNode left;
    protected AVLNode right;

    protected int balanceFactor;

    public AVLNode() { };
    public AVLNode(int d) {
        data = d;
        balanceFactor = 0;
    }

    public AVLNode insert(int key) {
        // Two cases - decide left or right
        if (key < data)  { // 1. go leftwards
            // Two sub cases - left slot is empty or not
            if (left == null)   // left is available for insertion
                left = new AVLNode(key);
            else  // left is not available, so delegate insert to left child
                left = left.insert(key);
        }
        else {  // 2. go rightwards
            // Two sub cases - right slot is empty or not
            if (right == null)  // right is available for insertion
                right = new AVLNode(key);
            else  // right is not available, so delegate insert to right child
                right = right.insert(key);
        }

		/* Compute balanceFactor and trigger appropriate rotation */
        balanceFactor = computeBalance();
        switch(balanceFactor) {
        case 2:  // Left imbalance 
        	if (left.balanceFactor >= 0)  // Left-left imbalance
        		return rotateWithLeftChild();
        		/*        15 (2)        10 (0)           15 (2)           10 (-1)
                         /             /  \              /                /  \
                       10 (1)    ==>  5    15          10 (0)    ==>     5    15 (1)
                      /                               /  \                   /
                     5                               5    12                12
                               CASE 1                          CASE 2
                (triggered after insertion of 5)  (triggered after deletion of 15's right)

        	    */
        	else {                        // Left-right imbalance
        		left = left.rotateWithRightChild();
       			return rotateWithLeftChild();
        		/*      15 (2)         15 (2)         12 (0)         15 (2)         15 (3)         12 (1) 
                       /              /              /  \           /              /              /  \
                     10 (-1)  ==>   12 (1)   ==>  10    15        10 (0)  ==>     12 (2)   ==>  10    15
                       \            /                            /   \           /             /
                        12        10                            5     12        10 (1)        5
                                                                               /
                                                                              5
                                    CASE 1                                    CASE 2
 	                   (triggered after insertion of 12)        (triggered after deletion of 15's right)
        		*/
         	}

       	case -2:  // Right imbalance
       		if (right.balanceFactor <= 0)  // Right-right imbalance
       			return rotateWithRightChild();
       		else {                         // Right-left imbalance
       			right = right.rotateWithLeftChild();
       			return rotateWithRightChild();
       		}
        }
        return this;
    }

    public void inorder() {
        // Inorder traversal - left, this, right order
        if (left != null)  // traverse leftwards only if left subtree exists
            left.inorder();
        System.out.print(data + " ");
        if (right != null)  // traverse rightwards only if right subtree exists
            right.inorder();
    }

    public void preorder() {
        // Preorder traversal - this, left, right order
        System.out.print(data + " ");
        if (left != null)  // traverse leftwards only if left subtree exists
            left.preorder();
        if (right != null)  // traverse rightwards only if right subtree exists
            right.preorder();
    }

    public void postorder() {
        // Postorder traversal - left, right, this order
        if (left != null)  // traverse leftwards only if left subtree exists
            left.postorder();
        if (right != null)  // traverse rightwards only if right subtree exists
            right.postorder();
        System.out.print(data + " ");
    }

    public boolean search(int key) {
        if (key == data)
            return true;
        else if (key < data && left != null)
            return left.search(key);
        else if (key > data && right != null)
            return right.search(key);
        else
            return false;
    }

    public void delete(int key) {
    	if (left != null && left.data == key) { // left to be deleted
    		if (left.isLeaf())             // 1. It is a leaf
    			left = null;
    		else if (left.hasOnlyLeft())   // 2. It has only left subtree underneath
    			left = left.left;
    		else if (left.hasOnlyRight())  // 3. It has only right subtree underneath
    			left = left.right;
    		else {                         // 4. It has both left and right subtree
    			// 4a. Find predecessor. left.left is the potential candidate
    			if (left.left.hasOnlyLeft() || left.left.isLeaf()) { // left.left does not have right
    				left.data = left.left.data;  // Copy left.left.data to left.data
    				left.left = left.left.left;  // Reassign left.left to left.left.left
    			}
    			else { // 4b. Find predecessor. The rightmost from left.left
    				AVLNode predParent = left.left;
    				AVLNode pred = predParent.right;
    				while (pred.right != null) {   // Slide along the right direction
    					predParent = pred;  // Parent of the predecessor
    					pred = pred.right;  // The predecessor
    				}
    				left.data = pred.data;  // Copy predecessor data to left
    				predParent.right = pred.left;  // Remove predecessor node
    			}    			
    		}
    	}
    	else if (right != null && right.data == key) {  // Analogous case
    		if (right.isLeaf())
    			right = null;
    		else if (right.hasOnlyLeft())
    			right = right.left;
    		else if (right.hasOnlyRight())
    			right = right.right;
    		else {
    			if (right.left.hasOnlyLeft() || right.left.isLeaf()) {
    				right.data = right.left.data;
    				right.left = right.left.left;
    			}
    			else {
    				AVLNode predParent = right.left;
    				AVLNode pred = predParent.right;
    				while (pred.right != null) {
    					predParent = pred;
    					pred = pred.right;
    				}
    				right.data = pred.data;
    				predParent.right = pred.left;
    			}    			
    		}
    	}
    	else if (left != null && key < data)
    		left.delete(key);
    	else if (right != null && key >= data)
    		right.delete(key);
    }

    public boolean isLeaf() {
        if (left == null && right == null)
            return true;
        else
            return false;
    }

    public boolean hasOnlyLeft() {
    	if (left != null && right == null)
    		return true;
    	else
    		return false;
    }

    public boolean hasOnlyRight() {
    	if (left == null && right != null)
    		return true;
    	else
    		return false;
    }

    public int height() {
    	if ( isLeaf() )
    		return 0;
    	else if (hasOnlyLeft()) 
    		return left.height() + 1;
    	else if (hasOnlyRight())
    		return right.height() + 1;
		else
    		return max(left.height(), right.height()) + 1;
    }

    public int max(int a, int b) {
    	if (a > b)
    		return a;
    	else
    		return b;
    }

    public AVLNode rotateWithLeftChild() {
    	AVLNode lc = left;
    	left = lc.right;
    	lc.right = this;
    	computeBalance(); lc.computeBalance();
    	return lc;
    }

    public AVLNode rotateWithRightChild() {
    	AVLNode rc = right;
    	right = rc.left;
    	rc.left = this;
    	computeBalance(); rc.computeBalance();
    	return rc;
    }
 
    public int computeBalance() {
        if (isLeaf()) return 0;
        else if (hasOnlyLeft()) return left.height()+1;
        else if (hasOnlyRight()) return -1 * (right.height()+1);
        else	return left.height() - right.height();
    }
}

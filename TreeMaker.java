/*
 * TreeMaker.java  
 */
package treedrawapp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;


public class TreeMaker {

    /* defaults for drawing graphics */
    private static BufferedImage def_image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
    private static Graphics2D def_g2D = def_image.createGraphics();
    private static AffineTransform def_at = new AffineTransform();    
    private static FontRenderContext def_frc = new FontRenderContext(def_at, true, true);    
    private static Font def_font = new Font("Tahoma", Font.PLAIN, 12);      
    /* offsets and spacing for layout and drawing */
    public static int maxLevel = 0; // this becomes set to the max depth of the tree
    static double xOffsetText = 0.0; // 20.0; // 10.0;
    static double yOffsetText = 40.0; // 20.0; // 10.0;
    static double xOffsetLine = 0.0;
    static double yOffsetLine = 30.0; // 8.0;
    private static double xTopAdjustment = 40.0; // 8.0;
    private static double yTopAdjustment = 40.0; // 8.0;   
    /* internal spacing for tree layout */
    private static double levelSeparation = 40.0; 
    private static double siblingSeparation = 8.0; 
    private static double subtreeSeparation = 40.0;
    private static double textBoxSpacer = 4.0;  // the spacing between text and lines
    /* tree characteristics */
//    private static Node levelZeroPtr = null;
    public static int nodeCount = 0;
   
    public static void main(String[] args) throws Exception {
        Node treeRoot = TreeMaker.makeTestTree2(); 
        String tree_filename = "C:\\Apps\\tree.png"; 
        TreeMaker.drawTree(tree_filename, treeRoot);
    }
    
    public static void drawTree(String filename, Node tree) {
        int width = 1200;
        int height = 800;
        tree.setX(600); // set initial root x point
        tree.setY(8);   // set initial root y point

        positionTree(tree); 
        BufferedImage image;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = image.createGraphics();
        g2D.setColor(Color.WHITE);
        g2D.fillRect(0, 0, width, height); // g2D.fillRect(0, 0, width, height);
        g2D.setColor(Color.BLACK);
        float[] dashingPattern1 = {2f, 2f};
        Stroke stroke1 = new BasicStroke(2f, BasicStroke.CAP_BUTT,
                                             BasicStroke.JOIN_MITER,
                                         1.0f, dashingPattern1, 2.0f);
 
        g2D.setStroke(stroke1);
        g2D.setFont(def_font);
        g2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                              RenderingHints.VALUE_ANTIALIAS_ON);
        paintTree(tree, g2D);
        try {
            File outputfile = new File(filename);
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
        }
    }
   
    static void paintTree(Node aNode, Graphics2D gc) {
        double leftSideXTextCoord = aNode.getX() - aNode.getWidth() / 2.0;
        if (aNode.hasKids()) {
            double bottomYParent = aNode.getY() + aNode.getHeight() + textBoxSpacer;
            gc.drawString(aNode.getValue(), (float)(leftSideXTextCoord + xOffsetText), (float)(aNode.getY() + yOffsetText));
            double midPointXParent = aNode.getX();            
            for (Node kid : aNode.getKids()) {
                double midPointXKid = kid.getX();
                double topYKid = kid.getY() - textBoxSpacer;
                paintTree(kid, gc);
                gc.drawLine(roundInt((midPointXParent + xOffsetLine)), roundInt((bottomYParent + yOffsetLine)),
                            roundInt((midPointXKid + xOffsetLine)), roundInt((topYKid + yOffsetLine)));
            }
        } else {
            gc.drawString(aNode.getValue(), (float)(leftSideXTextCoord + xOffsetText), (float)(aNode.getY()+ yOffsetText));
        }
     
    }
    /* start of Walker's algorithm. */
    public static void positionTree(Node root) {       
        initPrevNodeList(root);        
        firstWalk(root, 0);       
        xTopAdjustment = root.getX() - root.getPrelim(); 
        yTopAdjustment = root.getY();         
        secondWalk(root, 0, 0);
    }

    private static void initPrevNodeList(Node tree) {
        tree.setLeftNeighbor(null); // start at root // set nearest neighbor to null   // there is no neighbor to the left
        ArrayList<Node> kids = tree.getKids();
        recursePrevNodeList(kids);
    }

    private static void recursePrevNodeList(ArrayList<Node> nodes) {
        if (nodes == null || nodes.isEmpty()) return; // if there are no more nodes
        ArrayList<Node> kids = new ArrayList<Node>();
        Node leftMostNode = nodes.get(0);   // get first node in list
        leftMostNode.setLeftNeighbor(null); // for the 1st child, the prior is null
        if (leftMostNode.hasKids()) {
            kids.addAll(leftMostNode.getKids());
            }
        Node tempSibling = null;     // get tempSibling to iterate across all nodes
        int nodeListSize = nodes.size();  // get total num of siblings at this level
        for (int i = 1; i < nodeListSize; i++) { // iterate across all but 1st
            tempSibling = nodes.get(i);
            tempSibling.setLeftNeighbor(leftMostNode); // set left neighbor to current node
            if (tempSibling.hasKids()) {
                kids.addAll(tempSibling.getKids());
        }            
            leftMostNode = tempSibling; // use leftMostNode to iterate across the list
    }
        recursePrevNodeList(kids);
    }
    
    private static void firstWalk(Node aNode, int level) {
        aNode.setMod(0.0);
        Node prevSib = null;
        if (! aNode.hasKids() || (level > maxLevel)) { // if a leaf; o level check
            prevSib = aNode.getPrevSibling();
            if (prevSib != null){ // if a left sibling exists, set the x prelim based on the left sibling's prelim
                aNode.setPrelim(prevSib.getPrelim() +
                                siblingSeparation +
                                getMeanNodeWidth(prevSib, aNode));
            } else {
                aNode.setPrelim(0); // a leaf with no left sibling has a prelim of 0 (zero0
            }
        } else { // aNode is not a leaf--it has at least 1 child
            Node leftChild = aNode.getFirstChild();
            Node rightChild = leftChild; // initialize, then update below
            firstWalk(leftChild, level + 1); // recurse and increment the level
            while (rightChild.getNextSibling() != null) { // handle all siblings to the right
                rightChild = rightChild.getNextSibling();
                firstWalk(rightChild, level + 1);    // recurse for each right sibling
            } // on while loop exit, right child will be the right most child of aNode
            double midpoint = 0.5
                    * (leftChild.getPrelim() + rightChild.getPrelim()); // get the midpoint of the kids for the parent's modifier
            prevSib = aNode.getPrevSibling(); // get the left sibling if it exists
            if (prevSib != null) { // if aNode has a left sibling
                aNode.setPrelim(prevSib.getPrelim() +
                                siblingSeparation +
                                getMeanNodeWidth(prevSib, aNode)); // set Preliminary
                aNode.setMod(aNode.getPrelim() - midpoint); // set Modifier
                apportion(aNode, level);// Apportion. aNode has kids                
            } else {
                aNode.setPrelim(midpoint);
            }
        }
    }
   
    private static void apportion(Node aNode, int level) {        
        Node leftChild = aNode.getFirstChild();
        Node neighbor = leftChild.getLeftNeighbor();  // gets either previous sibling node or neighbor
        int compareLevel = 1;
        int depthToStop = maxLevel - level;

        while (leftChild != null &&
               neighbor  != null &&
               compareLevel <= depthToStop ) {
            double leftModSum  = 0.0;   
            double rightModSum = 0.0;               
            Node ancestorLeftMost = leftChild;
            Node ancestorNeighbor = neighbor;
            for (int i = 0; i < compareLevel; i++) {
                ancestorLeftMost = ancestorLeftMost.getParent();
                ancestorNeighbor = ancestorNeighbor.getParent();
                leftModSum  = leftModSum + ancestorNeighbor.getMod();
                rightModSum = rightModSum + ancestorLeftMost.getMod();
            }
            // Find move distance & apply it to Node's subtree
            // Add approporiate portions to smaller interior subtrees
            double moveDistance = neighbor.getPrelim() +
                                  leftModSum +
                                  subtreeSeparation +
                                  getMeanNodeWidth(leftChild, neighbor) -
                                  (leftChild.getPrelim() +  rightModSum);

            if (moveDistance > 0.0) {
                // count interior sibling and itself subtrees in leftSiblings
                Node tempNode = aNode;
                String tempNodeValue = (tempNode != null ? tempNode.getValue() : "null");
                String ancNeighborValue = (ancestorNeighbor != null ? ancestorNeighbor.getValue() : "null");
                int leftSiblings = 0;  
                while (tempNode != null &&
                       tempNode != ancestorNeighbor) {
                    leftSiblings = leftSiblings + 1;
                    tempNode = tempNode.getPrevSibling();
                    tempNodeValue = (tempNode != null ? tempNode.getValue() : "null");
                      
                }

                if (tempNode != null) {
                    
                    double movePortion = moveDistance / leftSiblings;
                    tempNode = aNode; // resetting the counting place
                    while (tempNode != ancestorNeighbor) {
                        tempNode.setPrelim(tempNode.getPrelim() + moveDistance);
                        tempNode.setMod(tempNode.getMod() + moveDistance);
                        moveDistance = moveDistance - movePortion;
                        tempNode = tempNode.getPrevSibling();
                    }
                } else { // no need to move additional. Ancestors need to do that
                    return;
                }                
            } // end if (moveDistance > 0.0) {
            // Determine leftmost descendent of Node at the next lower level
            // to compare its positioning against its neighbor
            compareLevel = compareLevel + 1;
            if (! leftChild.hasKids()) { // if leftChild is leaf
                leftChild = getLeftMost(aNode, 0, compareLevel);
            } else {
                leftChild = leftChild.getFirstChild();
            }
            if (leftChild != null) neighbor = leftChild.getLeftNeighbor(); 
        } // end while (leftChild ... neighbor ... compareLevel ...
    } // end apportion

    private static Node getLeftMost(Node aNode, int level, int compareLevel) {
        if (level >= compareLevel) {
            return aNode;
        } else if (! aNode.hasKids()) { // if aNode is leaf
            return null;            
        } else {
            Node rightMost = aNode.getFirstChild();
            Node leftMost = getLeftMost(rightMost, level + 1, compareLevel);
            // do post order walk of subtree below aNode
            while ((leftMost != null) && (rightMost.getNextSibling() != null)) {
                rightMost = rightMost.getNextSibling();
                leftMost = getLeftMost(rightMost, level + 1, compareLevel);
            }
            return leftMost;
        }
    }
   
    private static double getMeanNodeWidth(Node nodeA, Node nodeB) {
        double nodeWidth = 0.0;
        if (nodeA != null) {
            nodeWidth = nodeWidth + nodeA.getWidth()/2.0;
        }
        if (nodeB != null) {
            nodeWidth = nodeWidth + nodeB.getWidth()/2.0;
        }
        return nodeWidth;
    }
   
    private static boolean validExtentCheck(double x, double y){
        return ((x > 0.0) && (x < 2400.0) &&
                (y > 0.0) && (y < 1800.0) );
    }
           
    private static boolean secondWalk(Node aNode, int level, double modSum) {
        boolean result = true;
        if (level <= maxLevel) {
            double xTemp = xTopAdjustment + aNode.getPrelim() + modSum;
            double yTemp = yTopAdjustment + (level * levelSeparation);
            if (validExtentCheck(xTemp, yTemp)) {
                aNode.setX(xTemp);
                aNode.setY(yTemp);
                if (aNode.hasKids()) {
                    Node leftChild = aNode.getFirstChild();
                    result = result && secondWalk(leftChild, level + 1, (modSum + aNode.getMod()));
                    Node rightSibling = leftChild.getNextSibling();
                    while (rightSibling != null) {
                        result = result && secondWalk(rightSibling, level + 1, (modSum + aNode.getMod()));
                        rightSibling = rightSibling.getNextSibling();
                    }
                }
            } else {
            return false; // drawing was outside drawable extents
            }
        }
        return true;
    }


    static Node makeTestTree2() {
        Node root = new Node("O", null);  
        root.setLevel(0);
        Node b = new Node("E", root);
        root.addAKid(b);
        b.setLevel(1);
        Node c = new Node("A", b);
        b.addAKid(c);
        c.setLevel(2);
        Node d = new Node("D", b);
        b.addAKid(d);
        d.setLevel(2);
        Node e = new Node("B", d);
        d.addAKid(e);
        e.setLevel(3);
        Node f = new Node("C", d);
        d.addAKid(f);
        f.setLevel(3);
        b = new Node("F", root);
        root.addAKid(b);
        b.setLevel(1);
       
        b = new Node("N", root);
        root.addAKid(b);
        b.setLevel(1);
        c = new Node("G", b);
        b.addAKid(c);
        c.setLevel(2);
        c = new Node("M", b);
        b.addAKid(c);        
        c.setLevel(2);
       
        d = new Node("H", c);
        c.addAKid(d);
        d.setLevel(3);
        d = new Node("I", c);
        c.addAKid(d);
        d.setLevel(3);
        d = new Node("J", c);
        c.addAKid(d);
        d.setLevel(3);
        d = new Node("K", c);
        c.addAKid(d);
        d.setLevel(3);
        d = new Node("L", c);
        c.addAKid(d);
        d.setLevel(3);
        maxLevel = 4;
        return root;
    }
    public static Graphics2D getDefaultG2D() {
        return def_g2D;
    }

    public static FontRenderContext getDefaultFRC() {
        return def_frc;
    }

    public static Font getDefaultFont() {
        return def_font;
    }

    static int roundInt(double b) {
        return (int) (b + 0.5);
    }
   
}

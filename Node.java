/*
 * Node.java  
 */
package treedrawapp;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Node {

    private double prelim;
    private double modify; // 
    private int level; // level in tree. root is at 0. Level increases downward
    private String value;
    private ArrayList<Node> kids = null; 

    private double width;
    private double height;
    private double x;
    private double y;
    private Node parent;     
    private Node nextSibling;
    private Node prevSibling;
    private Node nearestNeighbor; // This node's nearest neighbor to the left, at the same level. Set dynamically after tree creation

    Node()  {
    };

    Node(String aValue) {
        value = aValue;
        TreeMaker.nodeCount = TreeMaker.nodeCount + 1;
    }    

    Node(String aValue, Node theParent) {
        value = aValue;
        parent = theParent;
    }    

    Node(String aValue, Node theParent, ArrayList<Node> someKids) {
        value = aValue;
        kids = someKids;
    }    

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getMod() {
        return modify;
    }

    public void setMod(double mod) {
        this.modify = mod;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getNextSibling() {
        return nextSibling;
    }

    public void setNextSibling(Node nextSibling) {
        this.nextSibling = nextSibling;
    }

    public Node getPrevSibling() {
        return prevSibling;
    }

    public void setPrevSibling(Node prevSibling) {
        this.prevSibling = prevSibling;
    }

    public double getPrelim() {
        return prelim;
    }

    public void setPrelim(double prelim) {
        this.prelim = prelim;
    }

    public double getHeight() {
        if (height < 0.001) {
           height = calcNodeHeightDefault();
        } 
        return height;
    }

    public double getWidth() {
        if (width < 0.001) {
           width = calcNodeWidthDefault();
        } 
        return width;
    }
    
    public Node getFirstChild() {
        if (hasKids()) {
            return kids.get(0);
        }
        return null;
    }

    public Node getLastChild() {
        if (hasKids()) {
            return kids.get(kids.size() - 1);
        }
        return null;
    }

    public ArrayList<Node> getKids() {
        return kids;
    }

    public void setKids(ArrayList<Node> kids) {
        this.kids = kids;
    }

    public ArrayList<Node> addAKid(Node newKid) {
        if (kids == null) {
            kids = new ArrayList<Node>();
        } else { //  kids already exists, so siblings for this kid exist. Set siblings prior to adding kid
            Node lastKidSib = kids.get(kids.size()-1); // this kid will be nextSibling for old lastKid
            lastKidSib.setNextSibling(newKid);
            newKid.setPrevSibling(lastKidSib);
        }
        kids.add(newKid);
        return kids;
    }

    public Node getFirstSibling() {
        if (parent != null){ // parent exists
            ArrayList<Node> siblings = parent.getKids();
            if (siblings != null && siblings.size() > 0) {
                return siblings.get(0);
            }
        }
        return null; // if no siblings exist
    }

    public Node getLastSibling() {
        if (parent != null) { // parent exists
            ArrayList<Node> siblings = parent.getKids();
            if ((siblings != null) && (siblings.size() > 1)) {
                return siblings.get(siblings.size() - 1);
            }
        }
        return null; // if no siblings exist
    }
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    boolean hasKids() {
        return ((kids != null) && (kids.size() > 0)); 
    }

    boolean hasRightSibling() {
        return (nextSibling != null);        
    }

    public void print(){
        System.out.println("Node value: " + value);
        if (kids != null) {
            for (Node kid : kids ) {
                kid.print();
            }
        }
    }

    public double calcNodeWidthDefault() {
        Graphics2D g = TreeMaker.getDefaultG2D();
        Font font = TreeMaker.getDefaultFont();
        FontRenderContext frc = TreeMaker.getDefaultFRC(); //
        Point2D p2d = calcNodeSize( g, font, frc);
        return p2d.getX();
    }
    
    
    public double calcNodeHeightDefault() {
        Graphics2D g = TreeMaker.getDefaultG2D();
        Font font = TreeMaker.getDefaultFont();
        FontRenderContext frc = TreeMaker.getDefaultFRC(); //
        Point2D p2d = calcNodeSize( g, font, frc);
        return p2d.getY();
    }
    
    public Point2D calcNodeSize(Graphics2D g, Font font, FontRenderContext frc) {
        Point2D result = new Point2D.Double();
        double textwidth = (font.getStringBounds(value, frc).getWidth());
        double textheight = (font.getStringBounds(value, frc).getHeight());
        result.setLocation(textwidth, textheight);
        return result;        
    }

    public Rectangle2D getBounds() {
        Rectangle2D.Double r;
        r = new Rectangle2D.Double();
        r.width = getWidth(); 
        r.height = getHeight();
        r.x      = getX();
        r.y      = getX();        
        return r;
    }

    public void setLeftNeighbor(Node prevNodeAtLevel) {
        nearestNeighbor = prevNodeAtLevel;
    }
    
    public Node getLeftNeighbor() {
        if (prevSibling != null) {
            return prevSibling;
        }
        return nearestNeighbor;
    }

}


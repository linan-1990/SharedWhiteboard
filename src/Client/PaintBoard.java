/*
 * The University of Melbourne
 * COMP90015 Assignment 2 – Shared White Board
 * Author: Run Cao
 * Student ID: 614233
 *
 * */

package Client;

import java.awt.Point;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import Remote.WhiteBoardServerInterface;


public class PaintBoard extends JComponent {
	private static final long serialVersionUID = 1L;
	private String clientName;
	private boolean isManager;
	private BufferedImage image;
	private BufferedImage lastImage;
	private Graphics2D g2;
	
	private Point lastPt, currPt;
	private Color currColor;
	private String currMode;
	private String text;
	private WhiteBoardServerInterface server;
 
	public PaintBoard(String name, WhiteBoardServerInterface RemoteInterface, boolean isManager) {
		this.clientName = name;
	    this.server = RemoteInterface;
	    this.isManager = isManager;
	    this.currColor = Color.black;
	    this.currMode = "point";
	    this.text = "";
	    
	    setDoubleBuffered(false);
	    //when the mouse pressed, the coordinate of the mouse pointer is stored as the start point of the current action
	    //send the mouse "start" coordinate and info to the server 
	    addMouseListener(new MouseAdapter() {
	    	public void mousePressed(MouseEvent e) {
	    		//System.out.println(clientName + " started drawing");
		        lastPt = e.getPoint();
				saveLastImage();
				
		        try {
					DrawItemWrapper item = new DrawItemWrapper("start", clientName, currMode, currColor, lastPt, text);
					server.sendDrawInfo(item);
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
	    	}
	    });
	    //listen to the mouse motion on the white board. 
	    //Draw the shape: line/straight line/rectangle/ circle/oval/ eraser/text
	    //send the shape and its ,e.g. coordinates, color, mode, etc to the server. The server will synchronize the info to the other clients.
	    addMouseMotionListener(new MouseMotionAdapter() {
	    	public void mouseDragged(MouseEvent e) {
	    		//System.out.println(clientName + " is drawing");
		        currPt = e.getPoint();
		        CreateShape newShape = new CreateShape();
		        
		        if (g2 != null) {
		        	if (currMode.compareTo("point") == 0) {
		        		newShape.makeLine(lastPt, currPt);
						lastPt = currPt;
						try {
							DrawItemWrapper item = new DrawItemWrapper("drawing", clientName, currMode, currColor, currPt, "");
							server.sendDrawInfo(item);
						} catch (RemoteException e1) {
							e1.printStackTrace();
						}
		        	} else if (currMode.compareTo("eraser") == 0) {
		        		newShape.makeLine(lastPt, currPt);
						lastPt = currPt;
						g2.setPaint(Color.white);
						g2.setStroke(new BasicStroke(15.0f));
						try {
							DrawItemWrapper item = new DrawItemWrapper("drawing", clientName, currMode, Color.white, currPt, "");
							server.sendDrawInfo(item);
						} catch (RemoteException e1) {
							e1.printStackTrace();
						}
		        	} else if (currMode.compareTo("line") == 0) {
		        		clear();
						drawLastImage();
						newShape.makeLine(lastPt, currPt);
		        	} else if (currMode.compareTo("rect") == 0) {
		        		clear();
						drawLastImage();
						newShape.makeRect(lastPt, currPt);
		        	} else if (currMode.compareTo("circle") == 0) {
		        		clear();
						drawLastImage();
		        		newShape.makeCircle(lastPt, currPt);
		        	} else if (currMode.compareTo("oval") == 0) {
		        		clear();
						drawLastImage();
		        		newShape.makeOval(lastPt, currPt);
		        	} else if (currMode.compareTo("text") == 0) {
		        		clear();
						drawLastImage();
						g2.setFont(new Font("TimesRoman", Font.PLAIN, 20));
						g2.drawString("Enter text here", currPt.x, currPt.y);
						newShape.makeTextField(currPt);
						Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1, new float[]{3}, 0);
						g2.setStroke(dashed);
		        	}
		        	g2.draw(newShape.getShape());
					repaint();
		        }
	    	}
	    });
	    
	    addMouseListener(new MouseAdapter() {
	    	public void mouseReleased(MouseEvent e) {
	    		//System.out.println(clientName + " has stopped drawing");
	    		currPt = e.getPoint();
	    		CreateShape newShape = new CreateShape();
	    		
	    		if (g2 != null) {
	    			if (currMode.compareTo("point") == 0 || currMode.compareTo("line") == 0) {
	    				newShape.makeLine(lastPt, currPt);
	    			} else if (currMode.compareTo("eraser") == 0) {
						g2.setPaint(currColor);
						g2.setStroke(new BasicStroke(1.0f));
	    			} else if (currMode.compareTo("rect") == 0) {
						newShape.makeRect(lastPt, currPt);
	    			} else if (currMode.compareTo("circle") == 0) {
						newShape.makeCircle(lastPt, currPt);
	    			} else if (currMode.compareTo("oval") == 0) {
						newShape.makeOval(lastPt, currPt);
	    			} else if (currMode.compareTo("text") == 0) {
	    				text = JOptionPane.showInputDialog("What text you want to add?");
	    				if (text == null)
	    					text = "";
	    				clear();
						drawLastImage();
	    				g2.setFont(new Font("TimesRoman", Font.PLAIN, 20)); 
	    				g2.drawString(text, currPt.x, currPt.y);
	    				g2.setStroke(new BasicStroke(1.0f));
	    			}
	    			if (currMode.compareTo("text") != 0 && currMode.compareTo("eraser") != 0)
	    				g2.draw(newShape.getShape());
	    			repaint();
					lastPt = currPt;
		    		try {
						DrawItemWrapper item = new DrawItemWrapper("end", clientName, currMode, currColor, currPt, text);
						server.sendDrawInfo(item);
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
	    		}
	    	}
	    });
	}
 //The method for painting the shape on the white board. 
// initialize the white board to synchronize with the manager's image when the client join the shared white board
	protected void paintComponent(Graphics g) {
		if (image == null) {
			if (isManager) {
			    image = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_RGB);
			    g2 = (Graphics2D) image.getGraphics();
			    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			    clear();
			} else {
				try {
					byte[] rawImage = server.sendCurrImage();
					image = ImageIO.read(new ByteArrayInputStream(rawImage));
					g2 = (Graphics2D) image.getGraphics();
				    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				    g2.setPaint(currColor);
				} catch (IOException e) {
					System.err.println("Fail receiving image!");
				}
			}
	    }
	    g.drawImage(image, 0, 0, null);
	}
	
	public Color gerCurrColor() {
		return currColor;
	}
	
	public String gerCurrMode() {
		return currMode;
	}
	
	public Graphics2D getGraphic() {
		return g2;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void clear() {
		g2.setPaint(Color.white);
		g2.fillRect(0, 0, getSize().width, getSize().height);
		g2.setPaint(currColor);
		repaint();
	}
	
	public void saveLastImage() {
		ColorModel cm = image.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = image.copyData(null);
		lastImage = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	public void drawLastImage() {
		drawImage(lastImage);
	}
	
	public void drawImage(BufferedImage img) {
		g2.drawImage(img, null, 0, 0);
		repaint();
	}
 
	public void red() {
		currColor = Color.red;
		g2.setPaint(currColor);
	}
 
	public void black() {
		currColor = Color.black;
		g2.setPaint(currColor);
	}
 
	public void green() {
		currColor = Color.green;
		g2.setPaint(currColor);
	}
 
	public void blue() {
		currColor = Color.blue;
		g2.setPaint(currColor);
	}
	
	public void orange() {
		currColor = Color.orange;
		g2.setPaint(currColor);
	}
	
	public void yellow() {
		currColor = Color.yellow;
		g2.setPaint(currColor);
	}
	
	public void cyan() {
		currColor = Color.cyan;
		g2.setPaint(currColor);
	}
	
	public void point() {
		currMode = "point";
	}
 
	public void line() {
		currMode = "line";
	}
 
	public void rect() {
		currMode = "rect";
	}
 
	public void circle() {
		currMode = "circle";
	}
 
	public void oval() {
		currMode = "oval";
	}
	
	public void text() {
		currMode = "text";
	}
	
	public void eraser() {
		currMode = "eraser";
	}
	
	public void notifyClear() throws RemoteException {
		server.sendMsg("new");
	}
}

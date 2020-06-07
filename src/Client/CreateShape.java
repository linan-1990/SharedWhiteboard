/*
 * The University of Melbourne
 * COMP90015 Assignment 2 – Shared White Board
 * Author: Run Cao
 * Student ID: 614233
 *
 * */

package Client;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

//define and create the shape of drawing mode
public class CreateShape {
	private Shape shape;
	
	public CreateShape() {
		super();
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public void makeLine(Point start, Point end) {
		shape = new Line2D.Double(start.x, start.y, end.x, end.y);
	}
	
	public void makeRect(Point start, Point end) {
		int x = Math.min(start.x, end.x);
		int y= Math.min(start.y, end.y);
		int width = Math.abs(start.x - end.x);
		int height = Math.abs(start.y - end.y);
		shape = new Rectangle2D.Double(x, y, width, height);
	}
	
	public void makeCircle(Point start, Point end) {
		int x = Math.min(start.x, end.x);
		int y= Math.min(start.y, end.y);
		int width = Math.abs(start.x - end.x);
		int height = Math.abs(start.y - end.y);
		width = Math.max(width, height);
		shape = new Ellipse2D.Double(x, y, width, width);
	}
	
	public void makeOval(Point start, Point end) {
		int x = Math.min(start.x, end.x);
		int y= Math.min(start.y, end.y);
		int width = Math.abs(start.x - end.x);
		int height = Math.abs(start.y - end.y);
		shape = new Ellipse2D.Double(x, y, width, height);
	}
	
	public void makeTextField(Point start) {
		int x = start.x - 5;
		int y= start.y - 20;
		int width = 130;
		int height = 25;
		shape = new RoundRectangle2D.Double(x, y, width, height, 10, 10);
	}
}

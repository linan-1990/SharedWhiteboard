/*
 * Description: Communication message wrapper about drawing
 * Author: Nan Li
 * Since 2020 May
 * Contact: linan.lqq0@gmail.com
 * */

package Client;

import java.awt.Color;
import java.awt.Point;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import Remote.WhiteBoardMsgInterface;

//wrap the drawing information messages as the communication protocol between server and client
public class DrawItemWrapper extends UnicastRemoteObject implements WhiteBoardMsgInterface {
	private static final long serialVersionUID = 1L;
	private String drawState;
	private String clientName;
	private String mode;
	private Color color;
	private Point point;
	private String text;
	
	public DrawItemWrapper(String state, String name, String mode, Color color, Point pt, String text) throws RemoteException {
		this.drawState = state;
		this.clientName = name;
		this.mode = mode;
		this.color = color;
		this.point = pt;
		this.text = text;
	}

	public String getState() {
		return this.drawState;
	}
	
	public String getName() {
		return this.clientName;
	}
	
	public String getMode() {
		return this.mode;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public Point getPoint() {
		return this.point;
	}
	
	public String getText() {
		return this.text;
	}
}

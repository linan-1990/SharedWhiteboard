/*
 * The University of Melbourne
 * COMP90015 Assignment 2 – Shared White Board
 * Author: Run Cao
 * Student ID: 614233
 *
 * */

package Remote;

import java.awt.Color;
import java.awt.Point;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WhiteBoardMsgInterface extends Remote {
	public String getState() throws RemoteException;
	public String getName() throws RemoteException;
	public String getMode() throws RemoteException;
	public Color getColor() throws RemoteException;
	public Point getPoint() throws RemoteException;
	public String getText() throws RemoteException;
}

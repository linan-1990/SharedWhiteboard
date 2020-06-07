/*
 * Description: Interface of communication message (implemented in DrawItemWrapper.java)
 * Author: Nan Li
 * Since 2020 May
 * Contact: linan.lqq0@gmail.com
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

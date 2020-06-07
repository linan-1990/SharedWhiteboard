/*
 * Description: Interface of RMI on client side (implemented in StartClient.java)
 * Author: Nan Li
 * Since 2020 May
 * Contact: linan.lqq0@gmail.com
 * */

package Remote;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface WhiteBoardClientInterface extends Remote {
	public void drawBoard(WhiteBoardServerInterface server) throws RemoteException;
    public void sendMessage(String message) throws RemoteException;
    public boolean updateWithOthers(WhiteBoardMsgInterface msg) throws RemoteException;
    public String getName() throws RemoteException;
    public void setName(String name) throws RemoteException;
    public void reName(String name) throws RemoteException;
    public void resyncClientNameList() throws RemoteException;
    public double sendResponse() throws RemoteException;
    public void addUser(String name) throws RemoteException;
    public void removeUser(String name) throws RemoteException;
    public void assignManager() throws RemoteException;
    public void clearBoard() throws RemoteException;
    public boolean isManager() throws RemoteException;
    public byte[] sendImage() throws RemoteException, IOException;
    public void drawOpenedImage(byte[] rawImage) throws IOException;
	public void setRemoved() throws IOException;
	public boolean isRemoved() throws RemoteException;
	public void closeApp() throws IOException;
}
/*
 * Description: Interface of RMI on server side (implemented in WhiteBoardServer.java)
 * Author: Nan Li
 * Since 2020 May
 * Contact: linan.lqq0@gmail.com
 * */

package Remote;

import java.util.ArrayList;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface WhiteBoardServerInterface extends Remote {
	public void register(WhiteBoardClientInterface client) throws RemoteException;
    public ArrayList<String> getClientNameList() throws RemoteException;
    public void globalClientNameListResync() throws RemoteException;
    public void sendDrawInfo(WhiteBoardMsgInterface msg) throws RemoteException;
    public void sendMsg(String msg) throws RemoteException;
    public void removeMe(String name) throws RemoteException;
    public byte[] sendCurrImage() throws RemoteException, IOException;
    public void sendOpenedImage(byte[] rawImage) throws IOException;
	public void removeClient(String selectName) throws IOException;
	public void removeAll() throws IOException;
}

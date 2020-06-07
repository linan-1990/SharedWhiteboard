/*
 * The University of Melbourne
 * COMP90015 Assignment 2 – Shared White Board
 * Author: Run Cao
 * Student ID: 614233
 *
 * */

package Server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.io.IOException;
import java.io.Serializable;

import Remote.WhiteBoardServerInterface;
import Remote.WhiteBoardClientInterface;
import Remote.WhiteBoardMsgInterface;


public class WhiteBoardServer extends UnicastRemoteObject implements WhiteBoardServerInterface, Serializable {
	private static final long serialVersionUID = 1L;
	private ClientManager clientManager;
	Set<String> removedList;
	
	protected WhiteBoardServer() throws RemoteException {
		super();
		this.clientManager = new ClientManager(this);
		removedList = new HashSet<String>();
	}
	//register the client. check if the client has duplicated name, assign a random number to this client
	public void register(WhiteBoardClientInterface client) throws RemoteException {
        if(this.clientManager.contains(client))
            throw new IllegalStateException(String.format("Client %s has already been registered", client.toString()));
        
        if (this.clientManager.clientCount() == 0) {
        	client.assignManager();
        }
        
        for (WhiteBoardClientInterface c : this.clientManager) {
            if (c.getName().compareTo(client.getName()) == 0) {
            	int min = 1000;
				int max = 9999;
				int random_int = (int)(Math.random() * (max - min + 1) + min);
				String new_name = client.getName() + "_" + Integer.toString(random_int);
            	System.out.println(client.getName() + " has been used, changing it to " + new_name);
            	client.reName(new_name);
            }
        }
        //publish the the new joined client name to all other clients
        for (WhiteBoardClientInterface otherClient : this.clientManager) {
        	if (otherClient.isManager())
        		client.addUser("*" + otherClient.getName());
        	else
        		client.addUser(otherClient.getName());
    	}
        
        this.clientManager.addClient(client);
        client.sendMessage("Hello " + client.getName());
        //printClients();
        updateUserList("add", client.getName());
    }
	
	public ArrayList<String> getClientNameList() throws RemoteException{
        ArrayList<String> clientNames = new ArrayList<String>();
        for(WhiteBoardClientInterface client : this.clientManager){
            clientNames.add(client.getName());
        }

        return clientNames;
    }
	
	public void globalClientNameListResync() {
        for(WhiteBoardClientInterface client : clientManager){
            try{
                client.resyncClientNameList();
            }catch(RemoteException err){
                err.printStackTrace();
            }
        }
    }
	
	public void printClients() throws RemoteException {
        if(this.clientManager.clientCount()>0){
            System.out.println(this.clientManager.clientCount() + " active clients");
            for (WhiteBoardClientInterface client : this.clientManager) {
                System.out.println("Client: " + client.getName());
            }
        }else{
            System.out.println("No active clients.");
        }
    }
	//receive the client update and publish the update to all other active clients
	public void sendDrawInfo(WhiteBoardMsgInterface msg) throws RemoteException {
		if (removedList.contains(msg.getName()))
			return;
		
		System.out.println(msg.getName() + " " + msg.getState() + " " + msg.getMode());
		
		for (WhiteBoardClientInterface client : this.clientManager) {
            client.updateWithOthers(msg);
        }
	}
	
	public void removeMe(String name) throws RemoteException {
		for (WhiteBoardClientInterface client : this.clientManager) {
            if (client.getName().compareTo(name) == 0) {
            	System.out.println(name + " has quit, removing it...");
            	this.clientManager.removeClient(client);
            	System.out.println(name + " removed!");
            }
        }
		updateUserList("remove", name);
		removedList.remove(name);
	}
	
	public void updateUserList(String action, String name) throws RemoteException {
        if(this.clientManager.clientCount() > 0){
            for (WhiteBoardClientInterface client : this.clientManager) {
                if (client.getName().compareTo(name) != 0) {
                	if (action.compareTo("add") == 0) {
                		client.addUser(name);
                	} else if (action.compareTo("remove") == 0) {
                		client.removeUser(name);
                	}
                }
            }
        } else{
            System.out.println("No active clients.");
        }
    }
	
	public void sendMsg(String msg) throws RemoteException {
		if (msg.compareTo("new") == 0) {
			for (WhiteBoardClientInterface client : this.clientManager) {
				client.clearBoard();
			}
		}
	}
	
	public byte[] sendCurrImage() throws IOException {
		byte[] currImage = null;
		for (WhiteBoardClientInterface client : this.clientManager) {
			if (client.isManager()) {
				currImage = client.sendImage();
			}
		}
		return currImage;
	}
	
	public void sendOpenedImage(byte[] rawImage) throws IOException {
		for (WhiteBoardClientInterface client : this.clientManager) {
			if (client.isManager() == false) {
				client.drawOpenedImage(rawImage);
			}
		}
	}
	
	public void removeClient(String selectName) throws IOException {
		for (WhiteBoardClientInterface client : this.clientManager) {
			if (client.getName().compareTo(selectName) == 0) {
				this.clientManager.removeClient(client);
				client.setRemoved();
				removedList.add(client.getName());
			}
		}
		updateUserList("remove", selectName);
	}
	
	public void removeAll() throws IOException {
		System.out.println("The manager has quit, close the application");
		removedList.clear();
    	
		for (WhiteBoardClientInterface client : this.clientManager) {
			this.clientManager.removeClient(client);
			if (client.isManager() == false) {
				client.closeApp();
			}
		}
	}
}

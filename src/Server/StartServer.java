/*
 * The University of Melbourne
 * COMP90015 Assignment 2 – Shared White Board
 * Author: Run Cao
 * Student ID: 614233
 *
 * */

package Server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import Remote.WhiteBoardServerInterface;

public class StartServer {
	
	public static void main(String[] args)  {
		//The main method of the server/register the Whiteboardserver in the RMI name registry
		try {
			WhiteBoardServerInterface server = new WhiteBoardServer();
			LocateRegistry.createRegistry(1099);
            Naming.rebind("WhiteBoardInterface", server);
            System.out.println("WhiteBoard server is ready");
            
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR happened. Server not running");
		}
	}
}

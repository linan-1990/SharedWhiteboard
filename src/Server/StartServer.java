/*
 * Description: Server main
 * Author: Nan Li
 * Since 2020 May
 * Contact: linan.lqq0@gmail.com
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

/*
 * Description: Client manager
 * Author: Nan Li
 * Since 2020 May
 * Contact: linan.lqq0@gmail.com
 * */

package Server;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import Remote.WhiteBoardClientInterface;
//store the client in a List and modify the list as per server request
public class ClientManager implements Iterable<WhiteBoardClientInterface>{
    private Set<WhiteBoardClientInterface> clients;
    private WhiteBoardServer server;


    public ClientManager(WhiteBoardServer server){
        this.clients = Collections.newSetFromMap(new ConcurrentHashMap<WhiteBoardClientInterface, Boolean>());
        this.server = server;
        new Thread(new ClientPing(this)).start();
    }

    public void addClient(WhiteBoardClientInterface client){
        this.clients.add(client);
        this.server.globalClientNameListResync();
    }

    public boolean contains(WhiteBoardClientInterface client){
        return this.clients.contains(client);
    }

    public int clientCount(){
        return this.clients.size();
    }

    public void removeClient(WhiteBoardClientInterface client){
        this.clients.remove(client);
        this.server.globalClientNameListResync();
    }

    public Iterator<WhiteBoardClientInterface> iterator(){
        return clients.iterator();
    }
    
    class ClientPing implements Runnable{
        ClientManager manager;

        public ClientPing(ClientManager manager){
            this.manager = manager;
        }

        public void run(){
            while(true) {
                if(manager.clientCount() > 0) {
                    //System.out.println("Pinging Clients:");
                    for (Iterator<WhiteBoardClientInterface> iterator =  this.manager.iterator(); iterator.hasNext();) {
                    	WhiteBoardClientInterface client = iterator.next();
                        try {
                            client.sendResponse();
                        } catch (RemoteException e) {
                            System.err.println("Lost connection, removing...");
                            manager.removeClient(client);
                            System.err.println("Removed");
                        }
                    }
                }else{
                    //System.out.println("No clients connected");
                }
                try {
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }
    }
}

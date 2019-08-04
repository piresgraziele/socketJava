package udc.edu.app.servidor;

import java.net.ServerSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import udc.edu.app.bean.FileMessage;

import java.util.HashMap;

public class Servidor {
	private ServerSocket serverSocket;
	private Socket socket;
	private Map<String, ObjectOutputStream> streamMap = new HashMap<String, ObjectOutputStream>();
	
	
	public Servidor() {
		try {
		serverSocket = new ServerSocket(5555);
		System.out.println("Servidor online");	
			while(true) {
				socket= serverSocket.accept();
				new Thread(new ListenerSocket(socket)).start();
			}
		
		} catch(IOException e){
			e.printStackTrace();
			
		}
		
		
	}
	
	private class ListenerSocket implements Runnable{
		private ObjectOutputStream outputStream;
		private ObjectInputStream inputStream;
		
		public ListenerSocket(Socket socket) throws IOException {
			this.outputStream = new ObjectOutputStream(socket.getOutputStream());
			this.inputStream = new ObjectInputStream(socket.getInputStream());
						
		}
		
		public void run() {
			FileMessage message = null;
			try {
				while((message = (FileMessage) inputStream.readObject()) != null) {
					streamMap.put(message.getCliente(), outputStream);
					if(message.getFile()!= null) {
						for(Map.Entry<String, ObjectOutputStream> kv: streamMap.entrySet()) {
							if(!message.getCliente().equals(kv.getKey())) {
								kv.getValue().writeObject(message);
							}
						}
					}
					
				}
			} catch (ClassNotFoundException e) {
				
				streamMap.remove(message.getCliente());
				System.out.println(message.getCliente()+ "está desconectado!");
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		new Servidor();
	}

}

package udc.edu.app.cliente;

import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFileChooser;

import udc.edu.app.bean.FileMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Cliente {
	private Socket socket;
	private ObjectOutputStream outputStream;
	
	public Cliente() throws IOException {
		this.socket = new Socket("localhost",5555);
		this.outputStream = new ObjectOutputStream(socket.getOutputStream());
		
		new Thread(new ListenerSocket(socket)).start();
		
		menu();
	}
	
	private void menu() throws IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Digite o nome do cliente:");
		String nome = scanner.nextLine();
		this.outputStream.writeObject(new FileMessage(nome));
		int option = 0;
		
		while(option!=1) {
			System.out.print("1- Sair | 2 - Enviar: ");
			option = scanner.nextInt();
			if(option ==2) {
				send(nome);
			} else if(option ==1) {
				System.exit(0);
			}
		}
		
	}
	
	private void send(String nome) throws IOException {
		FileMessage fileMessage = new FileMessage();
		
		JFileChooser fileChooser = new JFileChooser();
		int opt = fileChooser.showOpenDialog(null);
		
		if(opt == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			
			this.outputStream.writeObject(new FileMessage(nome, file));
		}
	}

	private class ListenerSocket implements Runnable{
		
		private ObjectInputStream inputStream;
		
		public ListenerSocket (Socket socket) throws IOException {
			this.inputStream = new ObjectInputStream(socket.getInputStream());
			
		}
		
		public void run() {
			FileMessage message = null;
			try {
				while((message=(FileMessage)inputStream.readObject()) != null) {
					System.out.print("\n Você recebeu um arquivo de " + message.getCliente());
					System.out.print("\n O arquivo é:" + message.getFile());
					
					//imprime(message);
					
					salvar(message);
					
					System.out.print("1- Sair | 2 - Enviar: ");
					
					
			}
				
			} catch(IOException e) {
				
			} catch(ClassNotFoundException e) {
				
			}
		
		
		}

		private void salvar(FileMessage message) {
			try {
				
				Thread.sleep(new Random().nextInt(1000));
				long time = System.currentTimeMillis();
				
				
				
				
				FileInputStream fileInputStream = new FileInputStream (message.getFile());
				FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\Graziele Pires\\Desktop" + time + "___" + message.getFile().getName());
				
				FileChannel fin = fileInputStream.getChannel();
				FileChannel fout = fileOutputStream.getChannel();
				
				long size = fin.size();
				
				fin.transferTo(0, size, fout);
				
			} catch (FileNotFoundException e) {				
				e.printStackTrace();
				
			}catch (IOException e) {				
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
			
		}

		private void imprime(FileMessage message) {
			try {
				FileReader fileReader = new FileReader(message.getFile());
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String linha;
				while((linha = bufferedReader.readLine()) != null) {
					System.out.print(linha);
				}
				
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}
	}
	
	public static void main(String [] args) {
		try {
			new Cliente();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}	
	

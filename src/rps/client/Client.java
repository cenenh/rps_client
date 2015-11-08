package rps.client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.GridLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.awt.event.ActionEvent;

import rps.client.action.ClientBehavior;
import rps.client.ref.ClientAction;

public class Client extends JFrame{

	JFrame frame;
	JLabel server_address_label, user_id_label;
	JPanel centerPanel, centerPanel2, centerPanel3, southPanel;
	JButton connect_to_server_button;
	JTextField server_address_textfield, user_id_textfield;
	
	Socket clientSocket;
	InputStream inStream;                 //서버가 보낸 데이터를 읽기 위한 입력 스트림 저장
	ObjectInputStream objectInStream;          //서버로부터 데이터를 전송받기 위한 스트림
	OutputStream outStream;                //서버로 메시지를 보내기 위한 출력 스트림 저장
	ObjectOutputStream objectOutStream;          //서버에 데이터를 전송하기 위한 스트림

	ClientDTO clientDTO;
	ClientBehavior clientBehavior;
	Thread thread;
	
	public Client() {
		clientDTO = new ClientDTO();
		clientBehavior = new ClientBehavior();
		frame = new JFrame("Client");
		server_address_label = new JLabel("Server Address  ");
		user_id_label = new JLabel("User ID  ");
		centerPanel = new JPanel();
		centerPanel2 = new JPanel();
		centerPanel3 = new JPanel();
		southPanel = new JPanel();

		connect_to_server_button = new JButton("Connect to Server");
		connect_to_server_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				clientDTO.setServerAddress(server_address_textfield.getText());
				clientDTO.setUserID(user_id_textfield.getText());
				clientDTO.setUserAction(ClientAction.CONNECT);
				
				System.out.println("server_address = " + clientDTO.getServerAddress());
				System.out.println("user_id = " + clientDTO.getUserID());
				System.out.println("will connect to Server...");
				
				// Do Server Connection
				int responseCode = connectToServer(clientDTO);
				//System.out.println(responseCode);
				if (responseCode == 200) {
					frame.setVisible(false);
					ConnectedClient connectedClient = new ConnectedClient(clientDTO);
					connectedClient.frame2GUI();
					
					clientBehavior.setClient(clientDTO);
					
					System.out.println("Start Client Thread...");
					thread = new Thread(clientBehavior);
					thread.start();

				} else {
					showMessage(responseCode);
				}
			}
		});
		server_address_textfield = new JTextField("127.0.0.1");
		user_id_textfield = new JTextField();

		server_address_textfield.setColumns(10);
		user_id_textfield.setColumns(10);
	}

	public void initialGUI() {
		frame.setSize(400, 150);
		frame.setLayout(new GridLayout(2, 1));
		centerPanel.setLayout(new BorderLayout());
		centerPanel2.setLayout(new GridLayout(2, 1));
		centerPanel3.setLayout(new GridLayout(2, 1));
		centerPanel2.add(server_address_label);
		centerPanel3.add(server_address_textfield);
		centerPanel2.add(user_id_label);
		centerPanel3.add(user_id_textfield);
		southPanel.add(connect_to_server_button);
		centerPanel.add(centerPanel2, BorderLayout.WEST);
		centerPanel.add(centerPanel3, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.add(centerPanel);
		frame.add(southPanel);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	public int connectToServer(ClientDTO clientDTO) {
		int responseCode = 0;
		System.out.println("try to connect to Server in Client.java");
		try {
			HashMap<String, String> request = new HashMap<>();
			//connect to server
			clientSocket = new Socket(clientDTO.getServerAddress(), 8888);
			
			System.out.println(clientSocket.getRemoteSocketAddress());
			System.out.println("My Client ID : "+clientDTO.getUserID());
			
			request.put("client_action", ClientAction.CONNECT);
			request.put("client_id", clientDTO.getUserID());
			request.put("client_address", clientSocket.getLocalSocketAddress().toString());
			
			outStream = clientSocket.getOutputStream();
			objectOutStream = new ObjectOutputStream(outStream);
			inStream = clientSocket.getInputStream();
			objectInStream = new ObjectInputStream(inStream);
			
			clientDTO.setClientSocket(clientSocket);
			clientDTO.setObjectInStream(objectInStream);
			clientDTO.setObjectOutStream(objectOutStream);
			
			System.out.println("write request to Server..."+request);
			objectOutStream.writeObject(request);
			objectOutStream.flush();
			responseCode = 200;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			responseCode = 500; // Not Found
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			responseCode = 400; // Connection refused
			e.printStackTrace();
		}
		return responseCode;
	}
	
	public void showMessage(int responseCode) {
		String ErrorMessage = null;
		if (responseCode == 400) { // Connection refused
			ErrorMessage = "Connection Refused";
		} else if (responseCode == 500) { // Not Found
			ErrorMessage = "UnknownHost";
		}
		
		JOptionPane.showMessageDialog(null, 
				ErrorMessage, "ERROR", 
				JOptionPane.ERROR_MESSAGE);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Client client = new Client();
		client.initialGUI();
		
	}
}

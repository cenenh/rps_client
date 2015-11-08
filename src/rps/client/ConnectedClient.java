package rps.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

import rps.client.action.ClientBehavior;
import rps.client.ref.ClientAction;

public class ConnectedClient extends Client{

	JFrame frame2;
	JPanel centerPan, eastPan, eastPan2, eastPan3;
	public static JTextArea clientLogArea, clientListArea;
	private static JLabel my_id_text;
	public static JButton readyBtn, rockBtn, scissorsBtn, paperBtn;
	JScrollPane scroll, scroll2;
	ClientBehavior clientBehavior;
	private static String meTag = " [ME] ";
	private static String newLine = "\n";
	
	public ConnectedClient(){
		
	}
	
	public ConnectedClient(ClientDTO client) {
		clientDTO = client;
		clientBehavior = new ClientBehavior();
		frame2 = new JFrame("Client");
		centerPan = new JPanel();
		eastPan = new JPanel();
		eastPan2 = new JPanel();
		eastPan3 = new JPanel();
		clientLogArea = new JTextArea();
		clientListArea = new JTextArea();
		clientLogArea.setEditable(false);
		clientListArea.setEditable(false);
		clientLogArea.append("My ID : " + clientDTO.getUserID()+" 접속."+newLine);
		clientListArea.append(clientDTO.getUserID()+meTag+newLine);
		
		DefaultCaret clientLogAreaCaret = (DefaultCaret)clientLogArea.getCaret();
		clientLogAreaCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		DefaultCaret clientListAreaCaret = (DefaultCaret)clientListArea.getCaret();
		clientListAreaCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		my_id_text = new JLabel("My USER ID : " + clientDTO.getUserID());
		readyBtn = new JButton("READY");
		rockBtn = new JButton("주먹");
		scissorsBtn = new JButton("가위");
		paperBtn = new JButton("보");
		rockBtn.setEnabled(false);
		scissorsBtn.setEnabled(false);
		paperBtn.setEnabled(false);

		frame2.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				int confirmed = JOptionPane.showConfirmDialog(null, 
				        "게임을 종료하시겠습니까?", "게임종료",
				        JOptionPane.YES_NO_OPTION);
				if(confirmed == JOptionPane.YES_OPTION){
					System.out.println("client close!, send disconnect message to server...");
					HashMap<Object, Object> message = new HashMap<Object, Object>();
					message.put("client_action", ClientAction.DISCONNECT);
					message.put("client_id", clientDTO.getUserID());
					clientBehavior.sendMessage(clientDTO, message);
					System.out.println(clientDTO.getClientSocket());
					System.out.println("종료합니다.");
					clientBehavior.setThreadStop(clientDTO);
					dispose();
				}
			}
		});
		
		readyBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//ready => 가위바위보 버튼 활성화.
				afterReady();
				clientDTO.setUserAction(ClientAction.READY);
				clientBehavior.doReady(clientDTO);
				clientLogArea.append("My ID : "+clientDTO.getUserID()+", I am READY!\n");
			}
		});

		rockBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				clientLogArea.append("당신은 바위를 내셨습니다.\n");
				afterDoAction();
				clientDTO.setRpsAction(ClientAction.ROCK_ACTION);
				clientBehavior.doRPS(clientDTO);
			}
		});
		
		scissorsBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				clientLogArea.append("당신은 가위 내셨습니다.\n");
				afterDoAction();
				clientDTO.setRpsAction(ClientAction.SCISSORS_ACTION);
				clientBehavior.doRPS(clientDTO);
			}
		});
		
		paperBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				clientLogArea.append("당신은 보를 내셨습니다.\n");
				afterDoAction();
				clientDTO.setRpsAction(ClientAction.PAPER_ACTION);
				clientBehavior.doRPS(clientDTO);
			}
		});
		
		
		scroll = new JScrollPane(clientLogArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setViewportView(clientLogArea);
		scroll2 = new JScrollPane(clientListArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll2.setViewportView(clientListArea);
		
	}
	
	public static String getMeTag(){
		return meTag;
	}
	
	public static String getNewLine(){
		return newLine;
	}
	
	public static void set_my_id_text(String new_user_id){
		my_id_text.setText("");
		my_id_text.setText(new_user_id);
	}
	
	public static void updateClientLog(String client_id, String client_action){
		clientLogArea.append(client_id + " : " + client_action+"\n");
	}
	
	public static void updateClientList(String client_id){
		clientListArea.append(client_id+'\n');
	}
	
	public static void afterConnected(){
		readyBtn.setEnabled(true);
		rockBtn.setEnabled(false);
		scissorsBtn.setEnabled(false);
		paperBtn.setEnabled(false);
	}
	
	public static void afterStartGame(){
		rockBtn.setEnabled(true);
		scissorsBtn.setEnabled(true);
		paperBtn.setEnabled(true);
	}
	
	public static void afterReady(){
		rockBtn.setEnabled(false);
		scissorsBtn.setEnabled(false);
		paperBtn.setEnabled(false);
		readyBtn.setEnabled(false);
		JOptionPane.showMessageDialog(null, 
				"모든 USER가 레디하면 게임이 시작됩니다!", "Wait", 
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void afterDoAction(){
		rockBtn.setEnabled(false);
		scissorsBtn.setEnabled(false);
		paperBtn.setEnabled(false);
	}
	
	public static void afterLose(){
		readyBtn.setEnabled(false);
		rockBtn.setEnabled(false);
		scissorsBtn.setEnabled(false);
		paperBtn.setEnabled(false);
	}
	
	public static void afterWin(){
		rockBtn.setEnabled(true);
		scissorsBtn.setEnabled(true);
		paperBtn.setEnabled(true);
	}
	
	public static void afterDraw(){
		rockBtn.setEnabled(true);
		scissorsBtn.setEnabled(true);
		paperBtn.setEnabled(true);
	}
	
	public static void gameFinish(){
		readyBtn.setEnabled(true);
		rockBtn.setEnabled(false);
		scissorsBtn.setEnabled(false);
		paperBtn.setEnabled(false);
	}
	
	public void frame2GUI() {
		frame2.setSize(600, 600);
		frame2.setLayout(null);
		frame2.add(readyBtn);
		frame2.add(rockBtn);
		frame2.add(scissorsBtn);
		frame2.add(paperBtn);
		frame2.add(scroll);
		frame2.add(scroll2);
		frame2.add(my_id_text);
		my_id_text.setBounds(200, 0, 500, 30);
		scroll.setBounds(10, 30, 400, 500);
		scroll2.setBounds(420, 30, 150, 200);
		readyBtn.setBounds(420, 240, 150, 50);
		rockBtn.setBounds(420, 300, 150, 50);
		scissorsBtn.setBounds(420, 360, 150, 50);
		paperBtn.setBounds(420, 420, 150, 50);
		frame2.setVisible(true);
	}
}

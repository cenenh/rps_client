package rps.client.action;

import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

import rps.client.ClientDTO;
import rps.client.ConnectedClient;
import rps.client.ref.ClientAction;

public class ClientBehavior implements Runnable {

	Socket clientSocket;
	InputStream inStream; // 서버가 보낸 데이터를 읽기 위한 입력 스트림 저장
	ObjectInputStream objectInStream; // 서버로부터 데이터를 전송받기 위한 스트림
	OutputStream outStream; // 서버로 메시지를 보내기 위한 출력 스트림 저장
	ObjectOutputStream objectOutStream; // 서버에 데이터를 전송하기 위한 스트림
	HashMap<Object, Object> message;
	ClientDTO clientDTO;
	boolean stop = false;

	public ClientBehavior() {
		message = new HashMap<>();
	}

	public ClientBehavior(ClientDTO client) {
		clientDTO = client;
	}

	public void setClient(ClientDTO cl) {
		clientDTO = cl;
	}

	public void setThreadStop(ClientDTO clientDTO) {
		stop = true;
		try {
			clientDTO.getObjectInStream().close();
			clientDTO.getObjectOutStream().close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void sendMessage(ClientDTO clientDTO, HashMap<Object, Object> message) {
		try {
			clientDTO.getObjectOutStream().writeObject(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doReady(ClientDTO client) {

		clientDTO = client;
		HashMap<Object, Object> request = new HashMap<>();
		String action = clientDTO.getUserAction();
		if (action.equals(ClientAction.READY)) {
			request.put("client_address", clientDTO.getClientSocket().getLocalSocketAddress().toString());
			request.put("client_id", clientDTO.getUserID());
			request.put("client_action", ClientAction.READY);
			sendMessage(clientDTO, request);
		}
	}

	public void doRPS(ClientDTO client) {
		clientDTO = client;
		HashMap<Object, Object> request = new HashMap<Object, Object>();
		String action = clientDTO.getUserAction();
		String userID = clientDTO.getUserID();
		int rps_action = clientDTO.getRpsAction();
		/*
		 * ROCK_ACTION = 1; SCISSORS_ACTION = 2; PAPER_ACTION = 3;
		 */
		request.put("client_action", ClientAction.DO_RPS);
		request.put("client_rps_action", rps_action);
		request.put("client_id", userID);
		sendMessage(clientDTO, request);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			System.out.println("This is Client Thread");
			while ((message = (HashMap<Object, Object>) (clientDTO.getObjectInStream().readObject())) != null) {
				Object Action = message.get("client_action");
				Object client_id = message.get("client_id");

				if (Action.equals("NEW_CONNECT")) {
					// 서버가 새로운 client를 알려줌.
					System.out.println("NEW CONNECT USER");
					System.out.println(message);
					String newClient = (String) message.get("client_id");
					if (!clientDTO.getUserID().equals(newClient)) {
						ConnectedClient.updateClientList(newClient);
						ConnectedClient.updateClientLog(newClient, (String) Action);
					}
				} else if (Action.equals("FIRST_CONNECT")) {
					// 서버가 새로운 client에게 client list를 보내줌.
					System.out.println("ALREADY CONNECT USER(S)");
					System.out.println(message);
					Object client_id_list = message.get("client_id_list");
					ArrayList<String> client_list = (ArrayList<String>) client_id_list;
					for (int i = 0; i < client_list.size(); i++) {
						if (!clientDTO.getUserID().equals(client_list.get(i))) {
							ConnectedClient.updateClientList(client_list.get(i));
						}
					}
				} else if(Action.equals(ClientAction.DUPLICATE_USERID)){
					String new_user_id = message.get(ClientAction.NEW_USERID).toString();
					String client_list = ConnectedClient.clientListArea.getText();
					client_list = client_list.replace(clientDTO.getUserID()+ConnectedClient.getMeTag()+ConnectedClient.getNewLine(), 
							new_user_id+ConnectedClient.getMeTag()+ConnectedClient.getNewLine());
					ConnectedClient.updateClientLog("[서버공지]", "User ID 중복! " + clientDTO.getUserID() + " 에서 " + new_user_id + "로 수정");
					clientDTO.setUserID(new_user_id);
					
					System.out.println(client_list);
					ConnectedClient.clientListArea.setText("");
					ConnectedClient.clientListArea.append(client_list);
					ConnectedClient.set_my_id_text("My USER ID : " + new_user_id);
					
					
				} else if (Action.equals(ClientAction.DISCONNECT)){
					String disconnect_user_id = message.get(ClientAction.DISCONNECT).toString();
					System.out.println(disconnect_user_id + " is disconnected..");
					ConnectedClient.updateClientLog("[서버공지]", disconnect_user_id+" 님이 나가셨습니다.");
					String client_list = ConnectedClient.clientListArea.getText();
					client_list = client_list.replace(disconnect_user_id+"\n", "");
					System.out.println(client_list);
					ConnectedClient.clientListArea.setText("");
					ConnectedClient.clientListArea.append(client_list);

				}else if (Action.equals("READY")) {
					System.out.println("set READY!");
					if (!clientDTO.getUserID().equals(client_id)) {
						ConnectedClient.updateClientLog((String) client_id, (String) Action);
					}
				} else if (Action.equals(ClientAction.GAME_START)) {
					ConnectedClient.afterStartGame();
					ConnectedClient.updateClientLog("[서버공지]", "GAME START! 30초 이내에 가위/바위/보 중 하나를 내세요.");
				} else if (Action.equals(ClientAction.GAME_RESULT)) {
					String gameResult = message.get(ClientAction.GAME_RESULT).toString();
					ConnectedClient.updateClientLog("[서버공지]", message.get(ClientAction.GAME_RESULT).toString());
					if (gameResult.equals(ClientAction.WIN)) {
						ConnectedClient.afterWin();
						ConnectedClient.updateClientLog("[서버공지]", "승리!");
					} else if (gameResult.equals(ClientAction.DRAW)) {
						ConnectedClient.afterDraw();
						ConnectedClient.updateClientLog("[서버공지]", "무승부!");
					} else if (gameResult.equals(ClientAction.LOSE)) {
						ConnectedClient.afterLose();
						ConnectedClient.updateClientLog("[서버공지]", "패배!");
						ConnectedClient.updateClientLog("[서버공지]", "당신은 패배하셨습니다.");
						ConnectedClient.updateClientLog("[서버공지]", "모든 게임이 끝날때 까지 기다려주세요.");
					}
				} else if (Action.equals(ClientAction.NOW_PLAYING_CLIENTS)) {
					ArrayList<String> now_playing_clients_ids = (ArrayList<String>) message.get(ClientAction.NOW_PLAYING_CLIENTS);
					int now_playing_clients = now_playing_clients_ids.size();
					
					if(now_playing_clients != 0)
						ConnectedClient.updateClientLog("[서버공지]", "이번 판의 승리자들");
					
					for (int i = 0; i < now_playing_clients_ids.size(); i++){
						ConnectedClient.clientLogArea.append(now_playing_clients_ids.get(i));
						if(i != now_playing_clients_ids.size()-1){
							ConnectedClient.clientLogArea.append(", ");
						}
					}
					ConnectedClient.clientLogArea.append("\n");
					if (now_playing_clients > 1 && now_playing_clients_ids.contains(clientDTO.getUserID())) {
						ConnectedClient.updateClientLog("[서버공지]", "아직 게임이 끝나지 않았습니다.");
						ConnectedClient.updateClientLog("[서버공지]", "30초 이내에 가위/바위/보 중 하나를 내세요.");
					}
				} else if (Action.equals(ClientAction.FINAL_GAME_RESULT)) {
					String winner = message.get("client_id").toString();
					ConnectedClient.updateClientLog("[서버공지]", "최종 승자 : " + winner);
					if (winner.equals(clientDTO.getUserID())) {
						ConnectedClient.updateClientLog("[서버공지]", "최종 승리를 축하드립니다!");
					}
					ConnectedClient.updateClientLog("[서버공지]", "게임이 종료되었습니다.");
					ConnectedClient.updateClientLog("[서버공지]", "READY를 누르면 재게임이 가능합니다.");
					ConnectedClient.afterConnected();
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		} 
	}
}

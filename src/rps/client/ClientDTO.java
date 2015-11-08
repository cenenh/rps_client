package rps.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientDTO {
	
	private String userID;
	private Socket clientSocket;
	private String userAction;
	private String serverAddress;
	private int rpsAction;
	private ObjectInputStream objectInStream;
	private ObjectOutputStream objectOutStream;
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public Socket getClientSocket() {
		return clientSocket;
	}
	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	public String getUserAction() {
		return userAction;
	}
	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}
	public String getServerAddress() {
		return serverAddress;
	}
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	public ObjectInputStream getObjectInStream() {
		return objectInStream;
	}
	public void setObjectInStream(ObjectInputStream objectInStream) {
		this.objectInStream = objectInStream;
	}
	public ObjectOutputStream getObjectOutStream() {
		return objectOutStream;
	}
	public void setObjectOutStream(ObjectOutputStream objectOutStream) {
		this.objectOutStream = objectOutStream;
	}
	public int getRpsAction(){
		return rpsAction;
	}
	public void setRpsAction(int rpsAction){
		this.rpsAction = rpsAction;
	}

}

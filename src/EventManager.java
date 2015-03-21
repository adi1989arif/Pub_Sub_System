/**
 * EventManager.java
 * This is the pub/sub server
 * 
 * @author Adiba Arif
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class EventManager extends Thread {
	ServerSocket listen;
	//to read the object i.e. for Topic
	ObjectInputStream in;
	String clientName;
	ObjectOutputStream out;
	//creating a list to store the topic on server side
	static ArrayList<Topic> NewList1 = new ArrayList<Topic>();
	//created a hashmap to store the topics vs subscriber
	static HashMap<Topic, ArrayList<String>> SubscriberHashMap = new HashMap<Topic, ArrayList<String>>();
	static HashMap<Event, ArrayList<String>> SubscriberEventHashMap = new HashMap<Event, ArrayList<String>>();

	/*
	 * methods which sets the name of the client/user
	 */
	public void setclientName(String n) {
		this.clientName=n;
	}

	/*
	 * parameterized constructor of EventManager
	 */
	public EventManager(int port) throws IOException {
		// TODO Auto-generated constructor stub
		listen = new ServerSocket(port);
		System.out.println ("Listening on port: " + listen.getLocalPort());
	}

	/*
	 * default constructor of EventManager
	 */
	public EventManager() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * Start the EventManager
	 */
	private void startService() {
		try { 
			listen = new ServerSocket(2424);
			System.out.println ("Listening on port: " + listen.getLocalPort());
			listenToPort();
		} catch(Exception e) {
			System.out.println(e);
		}
	}

	/*
	 * connection takes place here between the server and the client
	 */
	public void listenToPort()	{
		try {
			for(;;) {
				Socket clnt = listen.accept();
				System.out.println("Somebody connected ... ");
				EventManager NewServerSocket = new EventManager(0);
				NewServerSocket.start();
				System.out.println("offer ... " + NewServerSocket.listen.getLocalPort());
				PrintWriter out = new PrintWriter (clnt.getOutputStream (), true);
				out.println(NewServerSocket.listen.getLocalPort());
			}
		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	/*
	 * run method for NewServerSocket thread created in listenToPort method
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run()	{
		try {
			Socket clnt = listen.accept();
			System.out.println(clnt.toString());
			//reading the object of topic
			in = new ObjectInputStream(clnt.getInputStream());
			out = new ObjectOutputStream(clnt.getOutputStream ());
			//new thread created to read the data
			new Thread (new ReadingData(this)).start();
			out.writeObject(NewList1);
			NotifyUsers();
			while(true){
			}
		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	/*
	 * notify all subscribers of new event 
	 */
	public void notifySubscribers(Event event) {
		Set<Topic> keys = SubscriberHashMap.keySet();
		Object[] objs = keys.toArray();
		Topic t = event.gettopic();
		for (int index = 0; index < objs.length; index++) {
			t = (Topic) objs[index];
			if (event.gettopic().getName().equals(t.getName()))
				break;
		}
		ArrayList<String> subscriber = SubscriberHashMap.get(t);
		SubscriberEventHashMap.put(event, subscriber);
	}

	/*
	 * it continuously checks whether it can send an event to the user or not. When it encouters an events it sends it to the user
	 */
	private void NotifyUsers() {
		while(true){
			Set<Event> keys = SubscriberEventHashMap.keySet();
			Object[] objs = keys.toArray();
			Event t = null;
			for (int index = 0; index < objs.length; index++) {
				t = (Event) objs[index];
				ArrayList<String> subs = SubscriberEventHashMap.get(t);
				if (subs.contains(clientName)){
					try {
						out.writeObject(t);
					} catch (IOException e) {
						e.printStackTrace();
					}
					subs.remove(clientName);
				}
				SubscriberEventHashMap.put(t, subs);
			}
		}
	}

	/*
	 * add new topic when received advertisement of new topic
	 */
	void addTopic(Topic t){
		NewList1.add(t);
		SubscriberHashMap.put(t, new ArrayList());
	}

	/*
	 * add subscriber to the internal list
	 */
	void addSubscriber(Topic subscribertopic){
		Set<Topic> TopicKey=SubscriberHashMap.keySet();
		Object[] TopicObj = TopicKey.toArray();
		Topic temp = subscribertopic;
		for(int i=0; i<TopicObj.length;i++){
			temp = (Topic) TopicObj[i];
			if (subscribertopic.getName().equals(temp.getName()))
				break;
		}
		ArrayList AddSubscriber = SubscriberHashMap.get(temp);
		AddSubscriber.add(clientName);
		SubscriberHashMap.put(temp, AddSubscriber);
	}

	/*
	 * remove subscriber from the list
	 */
	void removeSubscriber(Topic unsubscribertopic){
		Set<Topic> TopicKey=SubscriberHashMap.keySet();
		Object[] TopicObj = TopicKey.toArray();
		Topic temp = unsubscribertopic;
		for(int i=0; i<TopicObj.length;i++){
			temp = (Topic) TopicObj[i];
			if (unsubscribertopic.getName().equals(temp.getName()))
				break;
		}
		ArrayList RemoveSubscriber = SubscriberHashMap.get(temp);
		RemoveSubscriber.remove(clientName);
		SubscriberHashMap.put(temp, RemoveSubscriber);
	}

	/*
	 * show the list of subscriber for a specified topic
	 */
	void showSubscribers(Topic topic){
		Set<Topic> TopicKey=SubscriberHashMap.keySet();
		Object[] TopicObj = TopicKey.toArray();
		Topic temp = topic;
		for(int i=0; i<TopicObj.length;i++){
			temp = (Topic) TopicObj[i];
			if (topic.getName().equals(temp.getName()))
				break;
		}
		ArrayList<String> subscriber = SubscriberHashMap.get(temp);
		System.out.println(subscriber);
		try {
			out.writeObject(subscriber);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	public static void main(String[] args) {
		new EventManager().startService();
	}
}

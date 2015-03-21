/**
 * PubSubAgent.java
 * This is the pub/sub client
 * 
 * @author Adiba Arif
 */

import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class PubSubAgent implements Publisher, Subscriber, Runnable{
	Scanner sc=new Scanner(System.in);
	int choice;
	String LocalHost;
	int EventId=1;
	int TopicId=1;
	ObjectOutputStream out;
	ObjectInputStream in;
	List<Topic> NewList;
	ArrayList<Event> notifications;

	//constructor
	public  PubSubAgent (){
		//creating a list to store the topic on client side
		NewList = new ArrayList<Topic>();
		notifications = new ArrayList<Event>();
	}

	/*
	 * forms the connection and creates thread to communicate
	 */
	public void ConnectingToServer()	{
		try {
			Socket socket = new Socket(LocalHost, 2424);
			BufferedReader din = new BufferedReader (new InputStreamReader (socket.getInputStream()));

			String newPort = din.readLine ();
			System.out.println ("Port being used from now: " + newPort);
			socket.close();
			din.close();

			socket = new Socket(LocalHost,new Integer(newPort).intValue());
			System.out.println(socket.toString());
			//to print the object of topic
			out = new ObjectOutputStream(socket.getOutputStream ());
			in = new ObjectInputStream(socket.getInputStream());
			new Thread(this).start();
			UserChoice();
		} catch (Exception e) {
			System.out.println (e);
		}
	}

	@Override
	public void subscribe(Topic topic) {
		try {
			out.writeInt(4);
			out.writeObject(topic);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void subscribe(String keyword) {
	}

	@Override
	public void unsubscribe(Topic topic) {
		try {
			out.writeInt(5);
			out.writeObject(topic);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void unsubscribe() {
	}

	@Override
	public void listSubscribedTopics() {
	}

	/*
	 * method which takes user input
	 */
	public void UserChoice() {
		String name, title, content, topic, username, subscribe, unsubscribe;
		System.out.println("Enter your name:");
		username = sc.nextLine();
		UserName(username);
		choice =1;
		while (choice!=0) {
			System.out.println("Select the activity of your choice:");
			System.out.println("0: Quit");
			System.out.println("1: Advertise");
			System.out.println("2: Publish");
			System.out.println("3: Subscribe");
			System.out.println("4: Unsubscribe");
			System.out.println("5: Show notification");
			//System.out.println("6: Show subscribers list");
			choice = Integer.parseInt(sc.nextLine());
			System.out.println("You entered string "+choice);
			String key;

			/*
			 * switch case. Whatever the user enters, depending on that case is executed
			 */
			switch (choice) {
			case 0:
				System.out.println("BYE BYE"); 
				break;
			case 1:
				System.out.println("ADVERTISE"); 
				System.out.println("Enter the keyword");
				ArrayList<String> keywords = new ArrayList<String>();
				key = sc.nextLine();
				keywords.add(key);
				System.out.println("Keyword chosen: "+keywords);

				System.out.println("Enter the name");
				name = sc.nextLine();
				System.out.println("Name chosen: "+name);

				System.out.println("Id:" +TopicId);
				Topic t1 = new Topic(TopicId, keywords, name);
				advertise (t1);
				TopicId++;
				break;

			case 2:
				System.out.println("PUBLISH"); 
				System.out.println("Enter the topic which you want to publish: ");
				topic = sc.nextLine();
				System.out.println("Topic chosen: "+topic);

				System.out.println("Enter the title: ");
				title = sc.nextLine();
				System.out.println("Title chosen: "+title);

				System.out.println("Enter the content: ");
				content = sc.nextLine();
				System.out.println("Content written: "+content);

				//stores the topic btained from the list in EventTopic object
				Topic EventTopic= getTopicFromList(topic);
				if (EventTopic!=null){
					Event e1 = new Event(EventId, EventTopic, title, content);
					publish (e1);
					EventId++;
				}
				else
					System.out.println("Topic does not exist");	
				break;

			case 3:
				System.out.println("SUBSCRIBE");
				System.out.println("Enter the topic which you want to subscribe: ");
				subscribe = sc.nextLine();
				System.out.println("Topic chosen: "+subscribe);
				Topic SubscribeTopic= getTopicFromList(subscribe);
				if (SubscribeTopic!=null){
					subscribe(SubscribeTopic);
				}
				break;

			case 4:
				System.out.println("UNSUBSCRIBE");
				System.out.println("Enter the topic which you want to unsubscribe: ");
				unsubscribe = sc.nextLine();
				System.out.println("Topic chosen: "+unsubscribe);
				Topic UnsubscribeTopic= getTopicFromList(unsubscribe);
				if (UnsubscribeTopic!=null){
					unsubscribe(UnsubscribeTopic);
				}
				break;

			case 5:
				System.out.println("SHOW NOTIFICATION");
				printNotification();
				break;

			case 6:
				System.out.println("SHOW SUBSCRIBERS LIST");
				System.out.println("Enter the topic of the subscriber you wish to see: ");
				subscribe = sc.nextLine();
				System.out.println("Topic and subscribers: "+subscribe);
				Topic ShowSubscribeTopic= getTopicFromList(subscribe);
				if (ShowSubscribeTopic!=null){
					Showsubscriber(ShowSubscribeTopic);
				}
				break;

			default:
				System.out.println("Invalid choice");
				break;
			}
		}
	}

	/*
	 * displays all the subscribers of a particular topic
	 */
	private void Showsubscriber(Topic showSubscribeTopic) {
		try {
			out.writeInt(6);
			out.writeObject(showSubscribeTopic);
			//System.out.println((ArrayList)in.readObject());
		} catch (IOException e) {
			e.printStackTrace();
		} //catch (ClassNotFoundException e) {
		//e.printStackTrace();
		//}
	}

	/*
	 * publish an event of a specific topic with title and content
	 * (non-Javadoc)
	 * @see Publisher#publish(Event)
	 */
	@Override
	public void publish(Event event) {
		//publish advertise
		try {
			out.writeInt(2);
			out.writeObject(event);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * advertise new topic
	 * (non-Javadoc)
	 * @see Publisher#advertise(Topic)
	 */
	@Override
	public void advertise(Topic newTopic) {
		try {
			out.writeInt(1);
			out.writeObject(newTopic);
			//topics are stored in this newlist
			NewList.add(newTopic);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * writes the username of the user
	 * (non-Javadoc)
	 * @see Publisher#publish(Event)
	 */
	public void UserName(String username) {
		try {
			out.writeInt(3);
			out.writeObject(username);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * fetch topic from the ArrayList
	 */
	public Topic getTopicFromList(String topic) {
		for (int i=0; i<NewList.size(); i++) {
			if (topic.equals(NewList.get(i).getName()));
			return NewList.get(i);
		}
		return null;
	}

	/*
	 * prints the events for the client
	 */
	private void printNotification() {
		for (int i = 0; i < notifications.size(); i++) {
			Event e = notifications.get(i);
			System.out.println("----------------------------");
			System.out.println("Event title: " + e.gettitle());
			System.out.println("Event content: " + e.getcontent());
			System.out.println("Event topic: " + e.gettopic().getName());
			System.out.println("----------------------------");
		}
	}

	/*
	 * reading the topics from the server
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Event responseEvent;
		try {
			ArrayList<Topic> fromServer = (ArrayList<Topic>)in.readObject();
			NewList.addAll(fromServer);
			while ((responseEvent = (Event)in.readObject()) != null) {
				notifications.add(responseEvent);
			}
		} catch (IOException e) {
			System.err.println("IOException:  " + e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String argv[]) {
		PubSubAgent Newclient = new PubSubAgent();
		Newclient.ConnectingToServer();
	}
}

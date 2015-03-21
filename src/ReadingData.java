/**
 * ReadingData.java
 * thread at the servers end that reads the data entered by the client
 * 
 * @author Adiba Arif
 */

import java.io.IOException;

public class ReadingData extends Thread {
	EventManager read;

	//constructor
	public ReadingData(EventManager eventManager) {
		this.read=eventManager;
	}

	/*
	 * run method for the new thread created in class EventManager to read the data
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run (){
		while (true){
			int i;
			try {
				i = read.in.readInt();
				switch (i) {
				case 1:
					Topic t = (Topic) read.in.readObject();
					read.addTopic(t);
					break;

				case 2:
					Event e = (Event) read.in.readObject();
					read.notifySubscribers(e);
					break;

				case 3:
					String n= (String)read.in.readObject();
					read.setclientName(n);
					break;

				case 4:
					Topic Subscribertopic = (Topic) read.in.readObject();
					read.addSubscriber(Subscribertopic);
					break;

				case 5:
					Topic Unsubscribertopic = (Topic) read.in.readObject();
					read.removeSubscriber(Unsubscribertopic);
					break;

				case 6:
					Topic ShowSubscribertopic = (Topic) read.in.readObject();
					read.showSubscribers(ShowSubscribertopic);
					break;

				default:
				}
			}
			catch (IOException e1) {
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		}
	}
}

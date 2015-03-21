/**
 * Event.java
 * Datastructure to store the data
 * 
 * @author Adiba Arif
 */

import java.util.List;

public class Event  implements java.io.Serializable{
	private int id;
	private Topic topic;
	private String title;
	private String content;

	public Event(int id, Topic topic, String title, String content) {
		this.id=id;
		this.topic=topic;
		this.title=title;
		this.content=content;
	}

	public void settopic(Topic topic) {
		this.topic=topic;
	}

	public Topic gettopic() {
		return topic;
	}

	public void settitle(String title) {
		this.title = title;
	}

	public String gettitle() {
		return title;
	}

	public void setcontent(String content) {
		this.content = content;
	}

	public String getcontent() {
		return content;
	}

	public void setid(int id) {
		this.id = id;
	}

	public int getid() {
		return id;
	}
}

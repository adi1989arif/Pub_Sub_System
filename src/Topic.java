/**
 * Topic.java
 * Datastructure to store the data
 * 
 * @author Adiba Arif
 */

import java.util.List;

public class Topic implements java.io.Serializable{
	private int id;
	private List<String> keywords;
	private String name;

	public Topic(int id, List<String> keywords, String name) {
		this.id=id;
		this.keywords=keywords;
		this.name=name;
	}

	public void setkeywords(List<String> keywords) {
		this.keywords=keywords;
	}

	public List<String> getkeywords() {
		return keywords;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setid(int id) {
		this.id = id;
	}

	public int getid() {
		return id;
	}
}
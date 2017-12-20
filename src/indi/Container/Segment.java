package indi.Container;

import java.util.ArrayList;

public class Segment {
			private     int					author		= 0;      		
			private    	ArrayList<String> 	sentences 	= null; 

			
	public int getAuthor() {
		return author;
	}

	public void setAuthor(int author) {
		this.author = author;
	}

	public Segment(ArrayList<String> sentences) {
		super();
		this.sentences = sentences;
	}

	public ArrayList<String> getSentences(){
		return sentences;
	}
	
}

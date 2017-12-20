package indi.Container;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Segments {
	public final int SENTENCE_COUNT = 5;
    private	ArrayList<Segment> segments = null;
    private Authors			   authors   = null;
	public Segments() throws Exception {
		// TODO Auto-generated constructor stub
		segments = new ArrayList<Segment>();
		authors = new Authors();
	}
	
	public Segment getSegment(int index){
		return segments.get(index); 
	}
	public ArrayList<Segment> getSegments(){
		return this.segments;
	}
	
	public int getSegmentCount(){
		return this.segments.size();
	}
	
	public ArrayList<String> getSentences(){
		Iterator<Segment> it = segments.iterator();
		ArrayList<String> sentenceList = new ArrayList<String>();
		while(it.hasNext()){
			Segment segment = it.next();
			ArrayList<String> sentences = segment.getSentences();
			sentenceList.addAll(sentences);
		}
		return sentenceList;
	}

	public void doCreateSegments() throws IOException{
			FileReader file1 =new FileReader("1.txt");
		    FileReader file2 =new FileReader("2.txt");	
		    File file=new File("merge.txt");
		    if(!file.exists()){
		    	file.createNewFile();
		    }
		    BufferedReader br1=new BufferedReader(file1);
		    BufferedReader br2=new BufferedReader(file2);
		    BufferedWriter wr=new BufferedWriter(new FileWriter(file));
		    List<BufferedReader> brList = new ArrayList<BufferedReader>();
		    brList.add(br1);
		    brList.add(br2);
		    
		    ArrayList<Boolean> flags = new ArrayList<Boolean>();
		    flags.add(new Boolean(true));
		    flags.add(new Boolean(true));
		    Random rand = new Random();
		    int authorCount = authors.getCount();
//		    System.out.println("作者个数有:"+authorCount);
		    while(flags.get(0)||flags.get(1)){
		    	int index = rand.nextInt(authorCount);
		    	BufferedReader br = brList.get(index);
		    	ArrayList<String> sentences = new ArrayList<String>();
		    	for(int i=0;i<SENTENCE_COUNT;++i){
		    		String sentence =  br.readLine();
//		    		System.out.println(index+"---"+sentence);
		    		if(sentence!=null){
		    		sentences.add(sentence);
		    		wr.write(sentence);
		    		wr.newLine();
		    		}else{
		    			flags.set(index, false);
		    			break;
		    		}
		    	}
		    	Segment segment = new Segment(sentences);
		    	if(sentences.size()!=0){
			    	segment.setAuthor(index);
			    	segments.add(segment);
		    	}
		    }
		    wr.flush();
	    	wr.close();
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Segments segments = new Segments();
		segments.doCreateSegments();
	}

}

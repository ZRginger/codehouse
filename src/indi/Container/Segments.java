package indi.Container;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Segments {
	private final String SEPERATE_SENTENCE_REGEX_PATTERN = "[。？！?.!]";
	public final int SENTENCE_COUNT = 8;
    private	ArrayList<Segment> segments = null;
    private Authors			   authors   = null;
    private int 			   authorCount = 0;
	public Segments() throws Exception {
		// TODO Auto-generated constructor stub
		segments = new ArrayList<Segment>();
		authors = new Authors();
		authorCount=authors.getCount();
	}
	
	public int getAuthorCount() {
		return authorCount;
	}

	public void setAuthorCount(int authorCount) {
		this.authorCount = authorCount;
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

	
	public String readToString(String fileName) {  
        String encoding = "UTF-8";  
        File file = new File(fileName);  
        Long filelength = file.length();  
        byte[] filecontent = new byte[filelength.intValue()];  
        try {  
            FileInputStream in = new FileInputStream(file);  
            in.read(filecontent);  
            in.close();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        try {  
            return new String(filecontent, encoding);  
        } catch (UnsupportedEncodingException e) {  
            System.err.println("The OS does not support " + encoding);  
            e.printStackTrace();  
            return null;  
        }  
    }  
	
	private void preprocess() throws IOException{
		String file1 = readToString("1.txt");
		String file2 = readToString("2.txt");
	    File f1=new File("1.txt");
	    BufferedWriter wr1=new BufferedWriter(new FileWriter(f1));
//	    File f2=new File("2.txt");
//	    BufferedWriter wr2=new BufferedWriter(new FileWriter(f2));
		
	    Pattern p =Pattern.compile(SEPERATE_SENTENCE_REGEX_PATTERN);  
        Matcher m = p.matcher(file1);
        /*按照句子结束符分割句子*/  
        String[] substrs = p.split(file1);  

        /*将句子结束符连接到相应的句子后*/  
        if(substrs.length > 0)  
        {  
            int count = 0;  
            while(count < substrs.length)  
            {  
                if(m.find())  
                {  
                    substrs[count] += m.group();  
                }  
                count++;  
            }  
        }
	    
        for(int i=0;i<substrs.length;++i){
        	wr1.write(substrs[i]);
        	wr1.newLine();
        }
//        Pattern p1 =Pattern.compile(SEPERATE_SENTENCE_REGEX_PATTERN);  
//        Matcher m1 = p1.matcher(file2);
//        /*按照句子结束符分割句子*/  
//        String[] substrs1 = p1.split(file2);  
//
//        /*将句子结束符连接到相应的句子后*/  
//        if(substrs1.length > 0)  
//        {  
//            int count = 0;  
//            while(count < substrs1.length)  
//            {  
//                if(m1.find())  
//                {  
//                    substrs1[count] += m1.group();  
//                }  
//                count++;  
//            }  
//        }
//	    
//        for(int i=0;i<substrs1.length;++i){
//        	wr2.write(substrs1[i]);
//        	wr2.newLine();
//        }
		wr1.close();
//		wr2.close();
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
//		segments.preprocess();
		segments.doCreateSegments();
	}

}

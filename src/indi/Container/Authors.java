package indi.Container;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Authors {
	private int count = 0;	//作者个数
	private ArrayList<String> authors = null;
	public Authors() throws Exception {
		super();
		authors = new ArrayList<String>();
		readAuthors();
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getAuthor(int index){
		return authors.get(index);
	}
	
	
	/**
	 * 从文件中读取作者信息
	 * @throws IOException 
	 */
	private void readAuthors() throws Exception {
		File file = new File("authors.txt");
		if(!file.exists()){
			throw new Exception();
		}
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		while((line=br.readLine())!=null){
			authors.add(line);
//			System.out.println(line);
		}
		br.close();
		count=authors.size();
	}
}

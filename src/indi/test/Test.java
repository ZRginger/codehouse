package indi.test;

import indi.measure.GMMMixtureModel;
import indi.util.GMMUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Test {
	
	
	
	static public void main(String[] args) throws IOException{
		File file = new File("iris.data");
		FileReader fr = new FileReader(file);
		BufferedReader br =new BufferedReader(fr);
		int line = 150;
		int width = 5;
		double[][] data = new double[line][width-1];
		String[]   label = new String[line];
		String str = " ";
		for(int i=0;i<150;++i){
			str=br.readLine();
			String[] splits = str.split(",");
			for(int j=0;j<width-1;++j){
				data[i][j] = Double.parseDouble(splits[j]);
			}
			label[i]=splits[4];
		}
		br.close();
		int[] input ={0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};  
		GMMMixtureModel gm = new GMMMixtureModel(data, line, 3, 4,input);
		gm.GMMCluster();
		
		
		
		
	}
}

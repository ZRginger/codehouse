package indi.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Util {

	static public void copyArray(double[] src,double[] des){
		for(int i=0;i<src.length;++i){
			des[i]=src[i];
		}
	}

	static public ArrayList<ArrayList<Double>> toArrayList(double[][] arr){
		ArrayList<ArrayList<Double>> res = new ArrayList<ArrayList<Double>>();
		for(int i=0;i<arr.length;++i){
			ArrayList<Double> tmp = new ArrayList<Double>();
			for(int j=0;j<arr[0].length;++j){
				tmp.add(arr[i][j]);
			}
			res.add(tmp);
		}
		return res;
	}
	static public void writeToFile(double[][] input) throws IOException{
		File file = new File("output.txt");
		FileWriter fw = new FileWriter(file);
		for(int i=0;i<input.length;++i){
			fw.write(Arrays.toString(input[i]));
		}
	}
}

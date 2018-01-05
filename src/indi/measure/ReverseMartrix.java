package indi.measure;

public class ReverseMartrix {
	private double[][] data = null;
//	static float[][] data = {
//		{17, 17, 5},
//		{21, 18, 21},
//		{2, 2, 19},
//	};
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		ReverseMartrix rm = new ReverseMartrix();
//		rm.getReverseMartrix(data);
	}
	
	


	public double[][] getReverseMartrix(double[][] data) {
		double[][] newdata = new double[data.length][data[0].length];
		double A = getMartrixResult(data);
		System.out.println("A="+A);
		for(int i=0; i<data.length; i++) {
			for(int j=0; j<data[0].length; j++) {
				if((i+j)%2 == 0) {
					newdata[i][j] = getMartrixResult(getConfactor(data, i+1, j+1)) / A;
				}else {
					newdata[i][j] = -getMartrixResult(getConfactor(data, i+1, j+1)) / A;
				}
				
			}
		}
		newdata = trans(newdata);
		
		showMaxtrix(newdata);
		return newdata;
	}

	private void showMaxtrix(double[][] newdata) {
		for(int i=0;i<newdata.length; i++) {
			for(int j=0; j<newdata[0].length; j++) {
				System.out.print(newdata[i][j]+ "   ");
			}
			System.out.println();
		}
	}
	
	private double[][] trans(double[][] newdata) {
		// TODO Auto-generated method stub
		double[][] newdata2 = new double[newdata[0].length][newdata.length];
		for(int i=0; i<newdata.length; i++) 
			for(int j=0; j<newdata[0].length; j++) {
				newdata2[j][i] = newdata[i][j];
			}
		return newdata2;
	}

	/*
	 * 计算行列式的值
	 */
	public double getMartrixResult(double[][] data) {
		/*
		 * 二维矩阵计算
		 */
		if(data.length == 2) {
			return data[0][0]*data[1][1] - data[0][1]*data[1][0];
		}
		/*
		 * 二维以上的矩阵计算
		 */
		double result = 0;
		int num = data.length;
		double [] nums = new double[num];
		for(int i=0; i<data.length; i++) {
			if(i%2 == 0) {
				nums[i] = data[0][i] * getMartrixResult(getConfactor(data, 1, i+1));
			}else {
				nums[i] = -data[0][i] * getMartrixResult(getConfactor(data, 1, i+1));
			}
		}
		for(int i=0; i<data.length; i++) {
			result += nums[i];
		}
		
		System.out.println("result="+result);
		return result;
	}
	
	/*
	 * 求(h,v)坐标的位置的余子式
	 */
	public double[][] getConfactor(double[][] data, int h, int v) {
		int H = data.length;
		int V = data[0].length;
		double[][] newdata = new double[H-1][V-1];
		for(int i=0; i<newdata.length; i++) {
			if(i < h-1) {
				for(int j=0; j<newdata[i].length; j++) {
					if(j < v-1) {
						newdata[i][j] = data[i][j];
					}else {
						newdata[i][j] = data[i][j+1];
					}
				}
			}else {
				for(int j=0; j<newdata[i].length; j++) {
					if(j < v-1) {
						newdata[i][j] = data[i+1][j];
					}else {
						newdata[i][j] = data[i+1][j+1];
					}
				}
			}
		}
		
//		for(int i=0; i<newdata.length; i ++)
//			for(int j=0; j<newdata[i].length; j++) {
//				System.out.println(newdata[i][j]);
//			}
		return newdata;
	}
	
	
}
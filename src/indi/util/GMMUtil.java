package indi.util;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

public class GMMUtil {  
	

	/** 
     *  
    * @Title: computeDet  
    * @Description: 计算行列式 
    * @return double 
    * @throws 
     */  
    public static double computeDet(double[][] mattrix) {  
        // 将list转化为array  
        RealMatrix rm = new Array2DRowRealMatrix(mattrix);
        LUDecomposition LU = new LUDecomposition(rm);
        double res = LU.getDeterminant();
        return res;  
    }  
	
	
	
    public static double computeDistanceOu(double[] d1,double[] d2){
    	double dist = 0;
    	for(int i=0;i<d1.length;++i){
    		double tmp = d1[i]-d2[i];
    		dist+=Math.pow(tmp, 2);
    	}
    	return Math.sqrt(dist);
    }
    
	
    /** 
     *  
    * @Title: computeDistance  
    * @Description: 计算任意两个节点间的距离 余弦距离表示法
    * @return double 
    * @throws 
     */  
    public static double computeDistance(double[] d1, double[] d2) {  
        double denaminator = 0;  
        double nominator = 0; 
        double sqrtSum1 = 0;
        double sqrtSum2 = 0;
        for(int i = 0; i < d1.length; i++) {  
        	nominator+=d1[i]*d2[i];
        	sqrtSum1+=Math.pow(d1[i], 2);
        	sqrtSum2+=Math.pow(d2[i], 2);
        }  
       	denaminator=Math.sqrt(sqrtSum1)+Math.sqrt(sqrtSum2);
       	return 1.0-nominator/denaminator;
    }  
      
    /** 
    * @Title: computeCov  
    * @Description: 计算协方差矩阵 
    * @return double[][] 
    * @throws 
     */  
    public static double[][] computeCov(ArrayList<ArrayList<Double>> dataSet) {  
        double[][] dataSet2 = toArray(dataSet);
        Covariance cov  = new Covariance(dataSet2);
        double[][] res = cov.getCovarianceMatrix().getData();
        return res;  
    }  
      
    public static double[][] computeCov(double[][] dataSet) {  
        Covariance cov  = new Covariance(dataSet);
        double[][] res = cov.getCovarianceMatrix().getData();
        return res;  
    } 
    
    /** 
     *  
    * @Title: computeInv  
    * @Description: 计算矩阵的逆矩阵 
    * @return double[][]  
    * @throws 
     */  
    public static double[][] computeInv(double[][] data) {  
    	RealMatrix result = null;
	    	try{
	    		RealMatrix mattrix = new Array2DRowRealMatrix(data);
	    		result = new LUDecomposition(mattrix).getSolver().getInverse();
	    	}catch(Exception e){
	    		double[][] tmpData = new double[data.length-1][data[0].length-1];
	    		for(int i=0;i<data.length-1;++i){
	    			for(int j=0;j<data[0].length-1;++j){
	    				tmpData[i][j]=data[i][j];
	    			}
	    		}
	    		RealMatrix mattrix = new Array2DRowRealMatrix(tmpData);
	    		result = new LUDecomposition(mattrix).getSolver().getInverse();
	    	}
        return result.getData(); 
    }  
      
    
      /**
       * 矩阵相乘
       * @param a
       * @param b
       * @return
       */
    public static double[][] matrixMultiply(double[][] a, double[][] b) {  
	       RealMatrix ma = new Array2DRowRealMatrix(a);
	       RealMatrix mb = new Array2DRowRealMatrix(b);
	       double[][] res = ma.multiply(mb).getData();
	       return res;  
    }  
      
    /** 
     *  
    * @Title: dotMatrixMultiply  
    * @Description: 矩阵的点乘，即对应元素相乘 
    * @return double[][] 
    * @throws 
     */  
    public static double[][] dotMatrixMultiply (double[][] a, double[][] b) {  
        double[][] res = new double[a.length][a[0].length];  
        for(int i = 0; i < a.length; i++) {  
            for(int j = 0; j < a[0].length; j++) {  
                res[i][j] = a[i][j] * b[i][j];  
            }  
        }  
        return res;  
    }  
      
    /** 
     *  
    * @Title: dotMatrixMultiply  
    * @Description: 矩阵的点除，即对应元素相除 
    * @return double[][] 
    * @throws 
     */  
    public static double[][] dotMatrixDivide(double[][] a, double[][] b) {  
        double[][] res = new double[a.length][a[0].length];  
        for(int i = 0; i < a.length; i++) {  
            for(int j = 0; j < a[0].length; j++) {  
                res[i][j] = a[i][j] / b[i][j];  
            }  
        }  
        return res;  
    }  
      
    /** 
     *  
    * @Title: repmat  
    * @Description: 对应matlab的repmat的函数，对矩阵进行横向或纵向的平铺 
    * @return double[][] 
    * @throws 
     */  
    public static double[][] repmat(double[] a, int row) {  
    	int rowCount = row;
    	int colCount = a.length;
        double[][] res = new double[rowCount][colCount];  
          for(int i=0;i<rowCount;++i){
        	  for(int j=0;j<colCount;++j){
        		  res[i][j]=a[j];
        	  }
          }
        return res;  
    }  
      
    /** 
     *  
    * @Title: matrixMinux  
    * @Description: 计算集合只差 
    * @return ArrayList<ArrayList<Double>> 
    * @throws 
     */  
    public static double[][]  matrixMinus(double[][] a1, double[][] a2) {  
    	double[][] res = new double[a1.length][a1[0].length];  
        for(int i = 0; i < a1.length; i++) {  
        	for(int j = 0;j < a1[0].length;++j){
        		res[i][j]=a1[i][j]-a2[i][j];
        	}
        }  
        return res;  
    }  
      
    /** 
     *  
    * @Title: matrixSum  
    * @Description: 返回矩阵每行之和(mark==2)或每列之和(mark==1) 
    * @return ArrayList<Double> 
    * @throws 
     */  
    public static double[] matrixSum(double[][] a, int mark) {  
        double res[] = new double[a.length];  
        if(mark == 1) { // 计算每列之和，返回行向量  
            res = new double[a[0].length];  
            for(int i = 0; i < a[0].length; i++) {  
                for(int j = 0; j < a.length; j++) {  
                    res[i] += a[j][i];  
                }  
            }  
        } else if (mark == 2) { // 计算每行之和， 返回列向量  
            for(int i = 0; i < a.length; i++) {  
                for(int j = 0; j < a[0].length; j++) {  
                    res[i] += a[i][j];  
                }  
        }  
          
    }  
    return res;  
}  
      
    public static double[][] toArray(ArrayList<ArrayList<Double>> a) {  
        int  dataNum = a.size();  
        int dataDimen= a.get(0).size();
        double[][] res = new double[dataNum][dataDimen];  
          
        for(int i = 0; i < dataNum; i++) {  
            for(int j = 0; j < dataDimen; j++) {  
                res[i][j] = a.get(i).get(j);  
            }  
        }  
          
        return res;  
    }  
      
      
    /** 
     *  
    * @Title: matrixReverse  
    * @Description: 矩阵专制 
    * @return double[][] 
    * @throws 
     */  
    public static double[][] matrixReverse(double[][] a) {  
        double[][] res = new double[a[0].length][a.length];  
        for(int i = 0; i < a.length; i++) {  
            for(int j = 0; j < a[0].length; j++) {  
                res[j][i] = a[i][j];  
            }  
        }  
        return res;  
    }  
      
    
    public static double[] getDiag(double[][] matrix){
    	double[] diag = new double[matrix.length];
    	for(int i=0;i<matrix.length;++i){
    		diag[i]=matrix[i][i];
    	}
    	return diag;
    }
    
    /** 
     *  
    * @Title: diag  
    * @Description: 向量对角化 
    * @return double[][] 
    * @throws 
     */  
    public static double[][] diag(double[] a) {  
        double[][] res = new double[a.length][a.length];  
        for(int i = 0; i < a.length; i++) {  
            for(int j = 0; j < a.length; j++) {  
                if(i == j) {  
                    res[i][j] = a[i];  
                }  
            }  
        }  
        return res;  
    }  
    public static void main(String[] args){
    	double[][] data = new double[][]{
    			{10,15,29},
    			{15,46,13},
    			{23,21,30},
    			{11,9,35}
    	};
    	
    	double[][] res=GMMUtil.computeCov(data);
    	for(int i=0;i<res.length;++i){
    		System.out.println(Arrays.toString(res[i]));
    	}
    }
}  
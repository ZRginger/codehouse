package indi.Container;

import indi.util.Util;

import java.util.ArrayList;
import java.util.Arrays;

public class Parameter {  
    private double[][] pMiu; // 均值参数k个分布的中心点，每个中心点d维  
    private double[] pPi = null; // k个GMM的权值  
    private double[][][] pSigma; // k类GMM的协方差矩阵,d*d*k  
      
	public double[][] getpMiu() {  
        return pMiu;  
    }  
	
	public double[] getpMiu(int index){
		return pMiu[index];
	}
	
    public void setpMiu(double[][] pMiu) {  
    	this.pMiu=pMiu;
    }  
    /**
     * 得到第k个gmm的权值
     * @return
     */
    public double[] getpPi() {  
        return pPi;  
    }  
    public double getpPi(int index) {  
        return pPi[index];  
    }  
    public void setpPi(double[] pPi) {  
        this.pPi = pPi;  
    }  
    
    
    public double[][] getpSigma(int index) {
    	return this.pSigma[index];
    }
    public double[][][] getpSigma() {  
        return pSigma;  
    }  
    public void setpSigma(double[][][] pSigma) {  
        this.pSigma = pSigma;  
    }  
}  
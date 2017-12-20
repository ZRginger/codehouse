package indi.measure;

import indi.Container.Parameter;
import indi.util.GMMUtil;
import indi.util.Util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.sound.sampled.DataLine;

public class GMMMixtureModel {  
	private int			 dataDimen = 0;	//数据维度
	private int			 dataNum = 0;	//数据个数
	private int			 k		 = 0;	//作者个数
	private Parameter    parameter = null;	//GMMMixtureModel的参数 
	private double[][]   dataSet = null;	//数据集
	private double[][]   cov     = null;	//k个高斯分布的协方差矩阵
	public GMMMixtureModel(double[][] dataSet, int dataNum, int k, int dataDimen){
		this.dataDimen=dataDimen;
		this.dataNum=dataNum;
		this.k=k;
		this.dataSet = dataSet;
		parameter = new Parameter();//初始化参数
		this.iniParameters();
	}
	
    /** 
    * @Title: GMMCluster  
    * @Description: GMM聚类算法的实现类，返回每条数据的类别(0~k-1) 
    * @return int[] 
    * @throws 
     */  
    public int[] GMMCluster() {  
        double Lpre = -1000000; // 上一次聚类的误差  
        double threshold = 0.0001;  
      for(int n=0;n<4;++n){  
            double[][] px = computeProbablity();  
            System.out.println("求得的概率为:");
            for(int i=0;i<px.length;++i){
            	System.out.println(Arrays.toString(px[i]));
            }
            //更新pGama的值
            double[][] pGama = new double[dataNum][k];  
            for(int i = 0; i < dataNum; i++) {  
                for(int j = 0; j < k; j++) {  
                    pGama[i][j] = px[j][i] * parameter.getpPi()[j];  
                }  
            }  
            double[] sumPGama = GMMUtil.matrixSum(pGama, 2);  
            for(int i = 0; i < dataNum; i++) {  
                for(int j = 0; j < k; j++) {  
                    pGama[i][j] = pGama[i][j] / sumPGama[i];  
                }  
            }  
              
            // 更新pMiu  
            double[] NK = GMMUtil.matrixSum(pGama, 1); // 第k个高斯生成每个样本的概率的和，所有Nk的总和为N  
            double[] NKReciprocal = new double[NK.length];  
            for(int i = 0; i < NK.length; i++) {  
                NKReciprocal[i] = 1 / NK[i];  
            }  
            double[][] tmpMiu = GMMUtil.matrixMultiply(
            		GMMUtil.matrixMultiply(GMMUtil.diag(NKReciprocal), GMMUtil.matrixReverse(pGama)), dataSet);  
              parameter.setpMiu(tmpMiu);
              
            // 更新pPie  Alpha
            double[] tmpPie = new double[k];  
            for(int i = 0; i < NK.length; i++) {  
            	tmpPie[i] = NK[i] / dataNum;  
            }  
            parameter.setpPi(tmpPie);  
            
            // 更新k个pSigma  
            double[][][] tmpSigma = new double[k][dataDimen][dataDimen];  
            for(int i = 0; i < k; i++) {  
            	double[][] shift = new double[dataNum][dataDimen];
            	for(int q=0;q<dataNum;++q){
            		for(int p=0;p<dataDimen;++p){
            			shift[q][p]=dataSet[q][p]-tmpMiu[i][p];
            		}
            	}
            	double[][] revShift = GMMUtil.matrixReverse(shift);
            	double[] para1 = new double[dataNum];
            	for(int p1=0;p1<dataNum;++p1){
            		para1[p1]=pGama[p1][i];
            	}
            	double[][] diagpGama = GMMUtil.diag(para1);
            	double[][] para2 = GMMUtil.matrixMultiply(shift, diagpGama);
            	tmpSigma[i]=GMMUtil.matrixMultiply(revShift,para2);
            	for(int q=0;q<dataDimen;++q){
            		for(int p=0;p<dataDimen;++p){
            			tmpSigma[i][q][p]/=NK[i];
            		}
            	}
            }  
//              
//            // 判断是否迭代结束  
//            double[][] a = GMMUtil.matrixMultiply(px, tmpPie);  
//            for(int i = 0; i < dataNum; i++) {  
//                a[i][0] = Math.log(a[i][0]);  
//            }  
//            double L = GMMUtil.matrixSum(a, 1)[0];  
//              
//            if(L - Lpre < threshold) {  
//                break;  
//            }  
//            Lpre = L;  
        }  
        return null;  
    }  
      
    
    private void preProcess(double[][] invMattrix){
    	double minValue = Math.pow(1,-1);
    	for(int i=0;i<dataDimen;++i){
    		invMattrix[i][i]+=minValue;
    	}
    }
    
    
    /** 
     *  
    * @Title: computeProbablity  
    * @Description: 计算每个节点（共n个）属于每个分布（k个）的概率 
    * @return double[][] 
    * @throws 
     */  
    public double[][] computeProbablity() {  
        double[][] px = new double[k][dataNum];
        for(int i=0;i<this.k;++i){
        	double[][] xShift = GMMUtil.matrixMinus(dataSet, GMMUtil.repmat(this.parameter.getpMiu(i),this.dataNum));
        	double[][] tmpSigma = GMMUtil.toArray(this.parameter.getpSigma(i));
        	preProcess(tmpSigma);
        	double[][] invSigma = GMMUtil.computeInv(tmpSigma);
        	double[][] res = GMMUtil.matrixMultiply(xShift, invSigma);
        	res = GMMUtil.dotMatrixMultiply(res, xShift);
        	double[] sum = GMMUtil.matrixSum(res, 2);
        	
        	double t = GMMUtil.computeDet(invSigma);
        	double coef = Math.pow((2 * Math.PI), -(double)dataDimen / 2d) * Math.sqrt(t);  
        	System.out.println("coef="+coef+" t="+t);
        	for(int p=0;p<this.dataNum;++p){
        			px[i][p]=coef*Math.pow(Math.E,-0.5*sum[p]);
        	}
        }
        return px;  
    }  
      
    /**
     *  
    * @Title: iniParameters  
    * @Description: 初始化参数Parameter 
    * @return void 
    * @throws 
     */  
    public void iniParameters() {  
          
        //计算数据的均值
        double[][] pMiuTmp = generateCentroids();  
        this.parameter.setpMiu(pMiuTmp);  
        
        // 对样本节点进行分类计数，进而初始化k个分布的权值  
        double[] pPiTmp = new double[this.dataNum];  
        int[] type = getTypes();  
        int[] typeNum = new int[k];  //每个高斯分布生成的样本点数量
        for(int i = 0; i < this.dataNum; i++) {  
            typeNum[type[i]]++;  
        }  
        for(int i = 0; i < k; i++) {  
        	pPiTmp[i]=typeNum[i]/this.dataNum;
        }  
        this.parameter.setpPi(pPiTmp);  
          
        // 计算k个分布的k个协方差  
        ArrayList<ArrayList<ArrayList<Double>>> pSigmaTmp = new ArrayList<ArrayList<ArrayList<Double>>>();  
        for(int i = 0; i < k; i++) {  
            ArrayList<ArrayList<Double>> tmp = new ArrayList<ArrayList<Double>>();  //第i个高斯分布的所有数据
            for(int j = 0; j < this.dataNum; j++) {  
                if(type[j] == i) {  
                	ArrayList<Double> a = new ArrayList<Double>();
                	for(int q=0;q<this.dataDimen;++q){
                		a.add(this.dataSet[j][q]);
                	}
                    tmp.add(a);  
                }  
            }  
            //计算属于各个高斯分布数据的协方差矩阵
            double[][] cov = GMMUtil.computeCov(tmp);
            ArrayList<ArrayList<Double>> covList = Util.toArrayList(cov);
            pSigmaTmp.add(covList);  
        }  
        this.parameter.setpSigma(pSigmaTmp);  
    }  
      
    /** 
     *  
    * @Title: generateCentroids  
    * @Description: 获取随机的k个中心点 
    * @return double[][]
    * @throws 
     */  
    public double[][] generateCentroids() {  
    		double[][] res = new double[this.k][this.dataDimen];  
            Random rand = new Random();
            boolean[] choose = new boolean[this.dataNum];
            Arrays.fill(choose, false);
            // 随机产生不重复的k个数  
            for(int i=0;i<this.k;++i){
	            int index = rand.nextInt(this.dataNum);
	            while(choose[index]==true){
	            	index = rand.nextInt(this.dataNum);
	            }
	            choose[index]=true;
	            Util.copyArray(this.dataSet[index], res[i]);
            }  
        return res;  
    }  
      
    /** 
     *  
    * @Title: getTypes  
    * @Description: 返回每条数据的类别 
    * @return int[] 
    * @throws 
     */  
    private int[] getTypes() {  
        int[] type = new int[dataNum];  
        for(int j = 0; j < dataNum; j++) {  
            double minDistance = GMMUtil.computeDistance(dataSet[j], parameter.getpMiu(0));  
//            System.out.println("dist="+minDistance);
            type[j] = 0; // 0作为该条数据的类别  
            for(int i = 1; i < k; i++) {  
            	double dis =  GMMUtil.computeDistance(dataSet[j], parameter.getpMiu(i));
//            	System.out.println("dis="+dis);
                if(dis < minDistance) {  
                    minDistance = GMMUtil.computeDistance(dataSet[j], parameter.getpMiu(i));  
                    type[j] = i;  
                }  
            }  
        }  
        return type;  
    }  
      
    
}  
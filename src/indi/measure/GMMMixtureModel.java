package indi.measure;

import indi.Container.Parameter;
import indi.test.ClusterEvaluation;
import indi.util.GMMUtil;
import indi.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GMMMixtureModel {  
	private int[]        input = null;	//已知分类结果
	private int			 dataDimen = 0;	//数据维度
	private int			 dataNum = 0;	//数据个数
	private int			 k		 = 0;	//作者个数
	private Parameter    parameter = null;	//GMMMixtureModel的参数 
	private double[][]   dataSet = null;	//数据集
	private int[] type = null;		//数据种类	
	double[][] px = null;
	public GMMMixtureModel(double[][] dataSet, int dataNum, int k, int dataDimen,int[] input){
		this.dataDimen=dataDimen;
		this.dataNum=dataNum;
		this.k=k;
		this.dataSet = dataSet;
		this.setInput(input);
		parameter = new Parameter();//初始化参数
		double[][] res =new KMeans().cluster();
		this.parameter.setpMiu(res);
		this.iniParameters();
	}
	
	/**
	 * 
	 */
	public void setInput(int[] input){
		this.input=input;
	}
    /**
     * @throws IOException  
    * @Title: GMMCluster  
    * @Description: GMM聚类算法的实现类，返回每条数据的类别(0~k-1) 
    * @return int[] 
    * @throws 
     */  
    public int[] GMMCluster() throws IOException {  
        double Lpre = -1000000; // 上一次聚类的误差  
        double threshold = 0.0001;  
      for(int n=0;n<8;++n){  
//    	  System.out.println("n="+n);
            px = computeProbablity();  
            outPut();
           
            //更新pie
            double[] tmpPie = new double[k];
            Arrays.fill(tmpPie, 0);
            for(int j=0;j<k;++j){
	            for(int i=0;i<dataNum;++i){
	            	tmpPie[j]+=px[j][i];
	            }
	            tmpPie[j]/=dataNum;
            }
            this.parameter.setpPi(tmpPie);
//            System.out.println("pie");
//            System.out.println(Arrays.toString(tmpPie));
            //更新pmiu
            double[][] tmpMiu = new double[k][dataDimen];
            for(int i=0;i<k;++i){
            	Arrays.fill(tmpMiu[i], 0);
            }
            for(int i=0;i<k;++i){
            	for(int j=0;j<dataDimen;++j){
            		for(int p=0;p<dataNum;++p){
            			tmpMiu[i][j]+=px[i][p]*dataSet[p][j];
            		}
            	}
            	for(int j=0;j<dataDimen;++j){
            		tmpMiu[i][j]/=tmpPie[i]*dataNum;
            	}
            }
            this.parameter.setpMiu(tmpMiu);
            
            
            // 更新k个pSigma  
            double[][][] tmpSigma = new double[k][dataDimen][dataDimen];  
            for(int i = 0; i < k; i++) {  
            	for(int j=0;j<dataNum;++j){
            		double[][] offset = new double[1][dataDimen];
            		for(int p=0;p<dataDimen;++p){
            			offset[0][p]=dataSet[j][p]-tmpMiu[i][p];
             		}
            		double[][] revOffset = GMMUtil.matrixReverse(offset);
            		double[][] tmp=GMMUtil.matrixMultiply(revOffset,offset);
            		for(int index=0;index<dataDimen;++index){
            			for(int index2=0;index2<dataDimen;++index2){
            				tmpSigma[i][index][index2]+=px[i][j]*tmp[index][index2];
            			}
            		}
            	}
            	for(int j=0;j<dataDimen;++j){
            		for(int p=0;p<dataDimen;++p){
            			tmpSigma[i][j][p]/=tmpPie[i]*dataNum;
            		}
            	}
            }  
            this.parameter.setpSigma(tmpSigma);
            
//            // 判断是否迭代结束  
            double[][] pie = new double[1][k];
            double[][] a = GMMUtil.matrixMultiply(pie,px);  
            System.out.println("a=");
            for(int index=0;index<a.length;++index){
            	System.out.println(Arrays.toString(a[index]));
            }
            for(int i = 0; i < dataNum; i++) {  
                a[0][i] = Math.log(a[0][i]);  
            }  
            double L = GMMUtil.matrixSum(a, 2)[0];  
              
            if(L - Lpre < threshold) {  
                break;  
            }  
            System.out.println("误差为:"+Lpre);
            Lpre = L;  
      }  
        return null;  
    }  
    
    private void preProcess(double[][] invMattrix){
    	double minValue = Math.pow(0.25,1);
    	for(int i=0;i<dataDimen;++i){
    		invMattrix[i][i]+=minValue;
    	}
    }
    
    /**
     * @throws IOException  
     *  
    * @Title: computeProbablity  
    * @Description: 计算每个节点（共n个）属于每个分布（k个）的概率 
    * @return double[][] 
    * @throws 
     */  
    public double[][] computeProbablity() throws IOException {  
        for(int i=0;i<this.dataNum;++i){
        	for(int j=0;j<this.k;++j){
        		//计算offset
        		double[][] offset = new double[1][this.dataDimen];
        		for(int p=0;p<this.dataDimen;++p){
        			offset[0][p]=this.dataSet[i][p]-this.parameter.getpMiu(j)[p];
        		}
        		//计算invSigma
        		double[][] tmp = this.parameter.getpSigma(j);
        		double[][] invSigma = new double[dataDimen][dataDimen];
        		for(int index=0;index<dataDimen;++index){
        			for(int index2=0;index2<dataDimen;++index2){
        				invSigma[index][index2]=tmp[index][index2];
        			}
        		}
        		//计算res
        		preProcess(invSigma);
        		invSigma=GMMUtil.computeInv(invSigma);
//        		for(int index=0;index<dataDimen;++index){
//        			System.out.println(Arrays.toString(invSigma[index]));
//        		}
        		double[][] res = GMMUtil.matrixMultiply(offset, invSigma);
        		double[][] revOffset = GMMUtil.matrixReverse(offset);
        		double[][] resM = GMMUtil.matrixMultiply(res, revOffset);
        		//计算sum
        		double[] sum = resM[0];
        		//计算coef
        		double coef = Math.pow((2 * Math.PI), -(double)dataDimen / 2d) * Math.sqrt(GMMUtil.computeDet(invSigma)); 
        		//计算px
        		px[j][i]=coef*Math.pow(Math.E, -0.5 * sum[0])*Math.pow(this.parameter.getpPi(j),1);  
        	}
        }
        double[] total = new double[dataNum];
        Arrays.fill(total, 0);
        for(int i=0;i<k;++i){
        	for(int j=0;j<dataNum;++j){
        		total[j]+=px[i][j];
        	}
        } 
        System.out.println("求得的概率为:");
       
        for(int i=0;i<k;++i){
        	for(int j=0;j<dataNum;++j){
        		px[i][j]=px[i][j]/total[j];
        		System.out.print(px[i][j]+" ");
        	}
        	System.out.println();
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
    	this.px=new double[k][dataNum];
    	this.type=new int[dataNum];
    	//初始化px
    	for(int i=0;i<k;++i){
    		for(int j=0;j<dataNum;++j){
    			px[i][j]=1.0/k;
    		}
    	}
    	
        int[] typeNum = new int[k];
        // 对样本节点进行分类计数，进而初始化k个分布的权值  
        double[] pPiTmp = new double[this.dataNum];  
        type = getTypes();  
        for(int i = 0; i < this.dataNum; i++) {  
            typeNum[type[i]]++;  
        }  
        System.out.println(Arrays.toString(typeNum));
        for(int i = 0; i < k; i++) {  
        	pPiTmp[i]=typeNum[i]/(double)this.dataNum;
        	System.out.println("均值为:"+pPiTmp[i]);
        }  
        this.parameter.setpPi(pPiTmp);  
        // 计算k个分布的k个协方差  
        double[][][] pSigmaTmp = new double[k][dataDimen][dataDimen];  
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
//            ArrayList<ArrayList<Double>> covList = Util.toArrayList(cov);
//            System.out.println("第"+i+"个协方差");
            for(int index=0;index<dataDimen;++index){
            	for(int index2=0;index2<dataDimen;++index2){
            		pSigmaTmp[i][index][index2]=cov[index][index2];
//            		System.out.print(pSigmaTmp[i][index][index2]+" ");
            	}
//            	System.out.println();
            }
        }  
        this.parameter.setpSigma(pSigmaTmp);  
    }  
    /** 
     *  
    * @Title: getTypes  
    * @Description: 返回每条数据的类别 
    * @return int[] 
    * @throws 
     */  
    private int[] getTypes() {  
        for(int j = 0; j < dataNum; j++) {  
            double minDistance = GMMUtil.computeDistance(dataSet[j], parameter.getpMiu(0));  
            type[j] = 0; // 0作为该条数据的类别  
            for(int i = 1; i < k; i++) {  
            	double dis =  GMMUtil.computeDistance(dataSet[j], parameter.getpMiu(i));
                if(dis < minDistance) {  
                    minDistance = GMMUtil.computeDistance(dataSet[j], parameter.getpMiu(i));  
                    type[j] = i;  
                }  
            }  
        }  
        System.out.println("type="+Arrays.toString(type));
        return type;  
    }  
      
    private void outPut(){
    	int[] typeNum = new int[k];
    	Arrays.fill(typeNum, 0);
    	for(int i=0;i<dataNum;++i){
    		int typeIndex = 0;
    		double maximun = Double.MIN_VALUE;
    		for(int j=0;j<k;++j){
    			if(px[j][i]>maximun){
    				maximun=px[j][i];
    				typeIndex=j;
    			}
    		}
    		type[i]=typeIndex;
    		typeNum[type[i]]++;
    	}
    	System.out.println("typr="+Arrays.toString(type));
    	System.out.println("typenum="+Arrays.toString(typeNum));
    }
    
    
    
   public class KMeans{
	   private double[][] centroids = null;			//数据的均值点
	   private double[][] preCentroid = null;		//保存的上一次迭代的均值点
	   private int[]      labelList     = null;		//数据所属的标签
	   private int[]      labelNum      = null;		//每个标签包含的数据个数
	   public KMeans(){
		   this.centroids=new double[k][dataDimen];
		   this.preCentroid=new double[k][dataDimen];
		   this.labelList=new int[dataNum];
		   this.labelNum =new int[k];
	   }
	   
	   //初始化中心点
	   private void initCentroids(){
		   Random rand = new Random();
		   for(int i=0;i<k;++i){
			   int index = rand.nextInt(dataNum);
			   System.out.println("index="+index);
			   for(int j=0;j<dataDimen;++j){
				   centroids[i][j]=dataSet[index][j];
				   preCentroid[i][j]=dataSet[index][j];
			   }
		   }
	   }
	   
	   //更新中心点
	   private void updateCentroids(){
		   double[][] tmpCentroid = new double[k][dataDimen];
		   for(int i=0;i<k;++i){
			   Arrays.fill(tmpCentroid[i], 0);
		   }
		   for(int i=0;i<dataNum;++i){
			   int label = labelList[i];
			   for(int j=0;j<dataDimen;++j){
				   tmpCentroid[label][j]+=dataSet[i][j];
			   }
		   }
		   for(int i=0;i<k;++i){
			   for(int j=0;j<dataDimen;++j){
				   tmpCentroid[i][j]/=labelNum[i];
				   this.centroids[i][j]=tmpCentroid[i][j];
			   }
		   }
	   }
	   
	   //检查是否收敛
	   public boolean checkFinished(){
		   for(int j=0;j<k;++j){
		    for(int i=0;i<dataDimen;++i){
		    	if(preCentroid[j][i]!=centroids[j][i]){
		    		return false;
		    	}
		    }
		   }
		     return true;
	   }
	   
	   
	   public double[][] cluster(){
		   initCentroids();
		   for(int gen=0;gen<40;++gen){
		   Arrays.fill(labelNum, 0);
		   for(int i=0;i<dataNum;++i){
			   double minDist = Double.MAX_VALUE;
			   int label = 0;
			   for(int j=0;j<k;++j){
				   double result = GMMUtil.computeDistance(dataSet[i], centroids[j]);
				   if(minDist>result){
					   minDist=GMMUtil.computeDistance(dataSet[i], centroids[j]);
					   label=j;
				   }else if(minDist==result){
					   Random rand = new Random();
					   double port = rand.nextDouble();
					   if(port<0.5){
						   label=j;
					   }
				   }
			   }
			   labelList[i]=label;
			   labelNum[label]++;
		   }
		   System.out.print("labelNum=");
		   System.out.println(Arrays.toString(labelNum));
		   System.out.print("labelList=");
		   System.out.println(Arrays.toString(labelList));
		   //更新中心点
		   System.out.println("中心点为:");
		   updateCentroids();
		   for(int i=0;i<k;++i){
			   System.out.println(Arrays.toString(centroids[i]));
		   }
		   if(checkFinished()){
			   System.out.println("finished");
			   break;
		   }else{
			   for(int i=0;i<k;++i){
				   for(int j=0;j<dataDimen;++j){
					   preCentroid[i][j]=centroids[i][j];
				   }
			   }
		   }
	   }
		   //测试聚类结果
		   ClusterEvaluation ce = new ClusterEvaluation();
		   ce.test(input, labelList);
		   return this.centroids;
	   }
	   }
}  
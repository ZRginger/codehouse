package indi.measure;

import indi.Container.Authors;
import indi.Container.Segment;
import indi.Container.Segments;
import indi.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.StringTokenizer;
/**
 *  @Description: 计算HMM的初始参数
 * @author scarlett
 *
 */
public class InitParameter {
	static private String SEPERATE_WORD_REGEX_PATTERN =" ,?.!:\"\"''\n";
	private int widthForWordsShowTwiceVector = 0;
	private int widthForWordsShowInVector = 0;
	private int height = 0;
	private Segments segments = null;
	private Hashtable<String, Integer> hTable = null;
	private ArrayList<String> wordsShowTwice = null;
	private ArrayList<String> wordsShowUp = null;
	private boolean[][] vectorForWordsShowTwice = null;
	private int[][]     vectorForCountWordsShowInSegment = null;
	//马尔科夫模型的初始参数
	private double[][]  convertMattrix = null;		//状态转移矩阵
	private double[]    stateInitMattrix = null; 	//初始状态矩阵
	private double[][]  transmitMattrix = null;	 	//发射矩阵
		
		public InitParameter() throws Exception {
			super();
			segments = new Segments();
			segments.doCreateSegments();
			this.hTable = new Hashtable<String, Integer>();
			this.wordsShowTwice = new ArrayList<String>();
			this.wordsShowUp = new ArrayList<String>();
		}
		/**
		 *  @Description: 创建记录文档单词出现次数的hashTable
		 */
		private void doCreateHTable(){
			ArrayList<String> sentences= segments.getSentences();
			Iterator<String> it = sentences.iterator();
			while(it.hasNext()){
				String sentence = it.next();
				StringTokenizer st=new StringTokenizer(sentence,SEPERATE_WORD_REGEX_PATTERN);
				while(st.hasMoreTokens()){
					String key = st.nextToken();
						Integer value = hTable.get(key);
						if(value!=null){
							value++;
							hTable.put(key, value);
						}else{
							hTable.put(key, 1);
						}
				}
			}
		}
		
		/**
		 *  @Description: 分别记录下出现一次和两次的单词
		 */
		private void doGetWords(){
			for(Iterator<String> iterator=hTable.keySet().iterator();iterator.hasNext();){
				String key=iterator.next();
				int value = hTable.get(key);
				if(value>1){
					wordsShowTwice.add(key);
				}
				wordsShowUp.add(key);
			}
		}
		
		/**
		 * @Title: computeTFIDF()  
	    * @Description: 计算每个Segment出现词加上TF-IDF权重的词频
	    * @return double
		 */
		private double[][] computeTFIDF(){
			//计算出现各个单词的文档数
			int[] countSegmentContainSpecificWord = new int[widthForWordsShowInVector];
			for(int i=0;i<widthForWordsShowInVector;++i){
				for(int j=0;j<height;++j){
					if(vectorForCountWordsShowInSegment[j][i]!=0){
						countSegmentContainSpecificWord[i]++;
					}
				}
			}
			double[][] TFMattrix = new double[height][widthForWordsShowInVector];
			for(int i=0;i<height;++i){
				for(int j=0;j<widthForWordsShowInVector;++j){
					double temp = Math.log10(height/(double)countSegmentContainSpecificWord[j]);
					TFMattrix[i][j]=vectorForCountWordsShowInSegment[i][j]*temp;
				}
			}
//			for(int i=0;i<height;++i){
//			System.out.println("tf="+Arrays.toString(TFMattrix[i]));
//			}
			return TFMattrix;
		}
		
		
		private void doCreateVectorForSegment(int i,Segment segment){
			ArrayList<String> sentence=segment.getSentences();
			Iterator<String> it= sentence.iterator();
			while(it.hasNext()){
				String str = it.next();
				StringTokenizer tokenizer = new StringTokenizer(str);  
				String word;  
			    while (tokenizer.hasMoreTokens()) {  
			        word = tokenizer.nextToken(" ,?.!:\"\"''\n");  
					for(int j=0;j<widthForWordsShowTwiceVector;++j){
						if(word.equals(wordsShowTwice.get(j))){
							vectorForWordsShowTwice[i][j]=true;
							break;
						}
					}
					for(int j=0;j<widthForWordsShowInVector;++j){
						if(word.equals(wordsShowUp.get(j))){
							vectorForCountWordsShowInSegment[i][j]++;
							break;
						}
					}
			    }
			}
		}
		
		public void doCreateVector(){
			//得到hTable
			this.doCreateHTable();
			//得到words
			this.doGetWords();
			//初始化vector
			widthForWordsShowTwiceVector=wordsShowTwice.size();
			widthForWordsShowInVector=wordsShowUp.size();
			height=segments.getSegmentCount();
			vectorForWordsShowTwice = new boolean[height][widthForWordsShowTwiceVector];
			vectorForCountWordsShowInSegment = new int[height][widthForWordsShowInVector];
			for(int i=0;i<height;++i){
			Arrays.fill(vectorForWordsShowTwice[i], false);
			Arrays.fill(vectorForCountWordsShowInSegment[i],0);
			}
			//为每个Segment构造vector
			ArrayList<Segment> segmentList = this.segments.getSegments();
			Iterator<Segment> it = segmentList.iterator();
			int i=0;
			while(it.hasNext()){
				Segment segment = it.next();
				this.doCreateVectorForSegment(i,segment);
				++i;
			}
		}
		
		/**
		 *  @Description:利用GMM给每个Segment指派最可能属于的作者
		 */
		private void doAssignAuthorToSegment(){
			double[][] TFMattrix = computeTFIDF();
			
			//判断错误
			for(int i=0;i<widthForWordsShowInVector;++i){
				double total =0;
				for(int j=0;j<height;++j){
					total+=TFMattrix[j][i];
				}
				if(total==0){
					System.out.println("**********wrong*************");
				}
			}
			
			//随机选择K个均值点pmiu
			int k=this.convertMattrix.length;
			GMMMixtureModel gmm = new GMMMixtureModel(TFMattrix, height, k, TFMattrix[0].length);
			int[] res=gmm.GMMCluster();
		}
		
		/**
		 *  @Description:初始化状态转移矩阵
		 * @throws Exception
		 */
		public void setConvertMattrix() throws Exception{
			Iterator<Segment> it = segments.getSegments().iterator();
			Authors authors = new Authors();
			int stateCount = authors.getCount();
			int[][] tempArr = new int[stateCount][stateCount];
			for(int i=0;i<stateCount;++i){
				Arrays.fill(tempArr[i],0);
			}
			int author1 = 0;
			int author2 = 0;
			if(it.hasNext()){
				author1 =it.next().getAuthor();
			}
			while(it.hasNext()){
				Segment segment = it.next();
				author2 = segment.getAuthor();
				tempArr[author1][author2]+=1;
				author1=author2;
			}
			//计算状态转换矩阵
			double[] totalCount = new double[stateCount];
			for(int i=0;i<stateCount;++i){
				totalCount[i]=0;
				for(int j=0;j<stateCount;++j){
					totalCount[i]+=tempArr[i][j];
				}
			}
			this.convertMattrix=new double[stateCount][stateCount];
			for(int i=0;i<stateCount;++i){
				for(int j=0;j<stateCount;++j){
					this.convertMattrix[i][j]=tempArr[i][j]/totalCount[i];
				}
			}
			System.out.println("状态转移矩阵为:");
			for(int i=0;i<stateCount;++i){
					System.out.println(Arrays.toString(this.convertMattrix[i]));
			}
		}
		
		/**
		 *  @Description:设置状态初始概率
		 * @throws Exception
		 */
		public void setStateInitMAttrix() throws Exception{
			int authorCount=new Authors().getCount();
			int[] authors = new int[authorCount];
			ArrayList<Segment> segmentList = this.segments.getSegments();
			Iterator<Segment> it = segmentList.iterator();
			while(it.hasNext()){
				int authorIndex = it.next().getAuthor();
				authors[authorIndex]++;
			}
			this.stateInitMattrix=new double[authorCount];
			for(int i=0;i<authorCount;++i){
				this.stateInitMattrix[i]=authors[i]/(double)height;
			}
			System.out.println("初始状态概率:"+Arrays.toString(this.stateInitMattrix));
		}

		/**
		 *  @Description:设置发射概率
		 * @throws Exception
		 */
		public void setTransmitMattrix() throws Exception{
			int authorCount=new Authors().getCount();
			int sentenceCount = this.segments.getSentences().size();
			double[][] portionMattrix = new double[authorCount][widthForWordsShowInVector];
			int[][]    vectorForWordsShowInAuthor = new int[authorCount][widthForWordsShowInVector];
			int[][]    vectorForWordShowInSentence = new int[sentenceCount][widthForWordsShowInVector];
			int[] totalCount = new int[authorCount];
			Arrays.fill(totalCount, 0);
			for(int j=0;j<sentenceCount;++j){
				Arrays.fill(vectorForWordShowInSentence[j], 0);
			}
			ArrayList<String> sentences = this.segments.getSentences();
			for(int i=0;i<sentenceCount;++i){
				for(int j=0;j<widthForWordsShowInVector;++j){
					String sentence =sentences.get(i);
					StringTokenizer st = new StringTokenizer(sentence,SEPERATE_WORD_REGEX_PATTERN);
					while(st.hasMoreTokens()){
						String word = st.nextToken();
						if(word.equals(wordsShowUp.get(j))){
							vectorForWordShowInSentence[i][j]++;
						}
					}
				}
			}
			for(int i=0;i<height;++i){
				int k=segments.getSegment(i).getAuthor();
				for(int j=0;j<widthForWordsShowInVector;++j){
					vectorForWordsShowInAuthor[k][j]+=vectorForCountWordsShowInSegment[i][j];
				}
			}
			transmitMattrix=new double[authorCount][sentenceCount];
			for(int i=0;i<authorCount;++i){
				Arrays.fill(transmitMattrix[i], 0);
			}
			
			int[] total = new int[authorCount];//在每个作者中单词出现的次数
			Arrays.fill(total, 0);
			for(int i=0;i<authorCount;++i){
				for(int j=0;j<widthForWordsShowInVector;++j){
					total[i]+=vectorForWordsShowInAuthor[i][j];
				}
			}
			for(int i=0;i<authorCount;++i){
				for(int j=0;j<widthForWordsShowInVector;++j){
					//total[j] 第j个单词出现的总次数
					//vectorForWordsShowInAuthor[i][j] 第j个单词在第i个author出现的次数
					//portionMattrix[i][j] 第j个单词出现在第i个作者的概率
					portionMattrix[i][j]=vectorForWordsShowInAuthor[i][j]/(double)total[i];
				} 
			}
			
			for(int i=0;i<authorCount;++i){
				for(int j=0;j<sentenceCount;++j){
					double temp = 0;
					for(int z=0;z<widthForWordsShowInVector;++z){
						//vectorForWordShowInSentence[i][z] 代表第z个单词出现在第j个句子的次数
						//portionMattrix[j][z] 代表第z个单词出现在第i个作者的次数
						temp+=vectorForWordShowInSentence[j][z]*portionMattrix[i][z];
					}
					transmitMattrix[i][j]=temp;
				}
			}
			System.out.println("发射概率矩阵为:");
			for(int i=0;i<authorCount;++i){
				System.out.println(Arrays.toString(transmitMattrix[i]));
			}
		}
		
		
		
		public static void main(String[] args) throws Exception{
			InitParameter ip = new InitParameter();
			ip.doCreateVector();
			ip.setConvertMattrix();
			ip.doAssignAuthorToSegment();
			ip.setStateInitMAttrix();
			ip.setTransmitMattrix();
		}
		
}
;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;

public class LogisticRegressionTrainWOStopWords {

	static TreeSet<String> V=new TreeSet<String>();
	static Integer spamFC=0,hamFC=0,FC=0,classes=0;
	static double learningRate;
	static double lambda;
	static int iterations;
	static Double[] weights;
	static double weight0;
	
	static ArrayList<String> features=new ArrayList<String>();
	static ArrayList<String> testFeatures=new ArrayList<String>();
	
	static ArrayList<String[]> vocab;
	static int noOfTrainingExamples,hamExamples,spamExamples;
	static int[][] matrix;
	static int[] testMatrix;
	static ArrayList<Integer> classValue=new ArrayList<Integer>();
	static ArrayList<TreeSet<String>> hamText;
	static TreeSet<String> testTree;
	static HashMap<String,String> stopText=new HashMap<String,String>();
	static HashMap<Integer,HashMap<String, Integer>> fileFeatureValueTrain=new HashMap<Integer, HashMap<String,Integer>>();
	static String hamTrainFile,spamTrainFile,hamTestFile,spamTestFile,stopWordsFile;
	public static void main(String[] args) throws Exception {

		
		learningRate=Double.parseDouble(args[0]);
		lambda=Double.parseDouble(args[1]);
		iterations=Integer.parseInt(args[2]);
		hamTrainFile=args[3];
		spamTrainFile=args[4];
		hamTestFile=args[5];
		spamTestFile=args[6];
		stopWordsFile=args[7];
		
		
		/*
		hamTrainFile= "C:/Users/Bhargavi/Desktop/Assignment2/train/ham";
		spamTrainFile= "C:/Users/Bhargavi/Desktop/Assignment2/train/spam";
		hamTestFile= "C:/Users/Bhargavi/Desktop/Assignment2/test/ham";
		spamTestFile= "C:/Users/Bhargavi/Desktop/Assignment2/test/spam";
		stopWordsFile= "C:/Users/Bhargavi/Desktop/Assignment2/stopwords.txt";
		*/
		
		featureExtaction();
		createFeatureMatrix();
		noOfTrainingExamples=vocab.get(0).length+vocab.get(1).length;
		matrix=new int[noOfTrainingExamples][V.size()];
		createExampleMatrix();

		initializeWeights();//done
		
		learnWeightsDummy1();

		int count=getTestFilecount(0);
		int posCounter=0;
		for (int i = 0; i < count; i++) {
			extractTestSetFeatures(i,0);
			buildTestMatrix();
			double d=predictValue();
			if(d>0)
				posCounter++;
		}
		System.out.println("Ham Accuracy is :"+(double)posCounter/count);

		count=getTestFilecount(1);
		posCounter=0;
		for (int i = 0; i < count; i++) {
			extractTestSetFeatures(i,1);
			buildTestMatrix();
			double d=predictValue();
			if(d<0)
				posCounter++;
		}
		System.out.println("Spam Accuracy is :"+(double)posCounter/count);


	}


	private static double predictValue() {
		double temp=0.0;
		for (int i = 0; i < weights.length; i++) {
			temp+=weights[i]*testMatrix[i];
		}
		temp+=weight0;
		return temp;



	}
	private static void buildTestMatrix() {
		testMatrix=new int[features.size()];
		Arrays.fill(testMatrix,0);
		for (int i = 0; i < features.size(); i++) {
			Iterator<String> itr=testTree.iterator();
			while(itr.hasNext()){
				if(itr.next().contentEquals(features.get(i)))
				{
					testMatrix[i]=1;
				}
			}
			
		}
	}
	private static int getTestFilecount(int val) {
		File testFolder;
		if(val==0)
			testFolder = new File(hamTestFile);
		else
			testFolder = new File(spamTestFile);

		File[] testlistOfFiles = testFolder.listFiles();
		return testlistOfFiles.length;
	}
	private static void extractTestSetFeatures(int i,int j) throws Exception {
		File testFolder ;
		if(j==0)
			testFolder = new File(hamTestFile);
		else
			testFolder = new File(spamTestFile);

		
		testTree=new TreeSet<String>();
	//	File[] testlistOfFiles = testFolder.listFiles();
		FileInputStream en=new FileInputStream(new File(testFolder.listFiles()[i].toString()));
		BufferedReader x=new BufferedReader(new InputStreamReader(en));
		String str=x.readLine();
		while (str!=null) {
			str=str.replaceAll(" ' ","'");
			str=str.replaceAll("[^a-zA-Z']"," ");
			str=str.replaceAll("''"," ");
			str=str.replaceAll("\\s+", " ");
			str=str.trim();
			String arr[]=str.split(" ");
			for (int h = 0; h < arr.length;h++) {
				if(stopText.containsKey(arr[h].toLowerCase()))
					continue;
				testTree.add(arr[h].toLowerCase());
				
			}
			str=x.readLine();
		}
		x.close();
	}
	private static void createExampleMatrix() {
		for (int i = 0; i < noOfTrainingExamples; i++) {
			TreeSet<String> temp=hamText.get(i);
			Iterator<String> itr=temp.iterator();
			while(itr.hasNext()){
				String word=itr.next();
				for (int j = 0; j < features.size(); j++) {
					if(features.get(j).contentEquals(word)){
						matrix[i][j]=1;
						break;
					}
				}		
			}
		}
	}

/*
	private static void createExampleMatrixDummy() {
		for (int i = 0; i <noOfTrainingExamples; i++) {
			TreeSet<String> temp=hamText.get(i);
			Iterator<String> itr=temp.iterator();
			HashMap<String,Integer> hm=new HashMap<String, Integer>();
			while(itr.hasNext()){
				String word=itr.next();
				for (int j = 0; j < features.size(); j++) {
					if(features.get(j).contentEquals(word)){
						hm.put(features.get(j),1);
						break;
					}
				}		
			}
			fileFeatureValueTrain.put(i, hm);
		} 
	}
*/
	private static void createFeatureMatrix() {
		Iterator<String> itr=V.iterator();
		while(itr.hasNext()){
			features.add(itr.next().toLowerCase());
		}

	}
	private static void initializeWeights() {
		
		weights=new Double[V.size()];
		Random r=new Random();
		weight0=-3 + r.nextDouble() * (3 - (-3));
		for (int i = 0; i < weights.length; i++) {
			double x=r.nextDouble();
			weights[i]=(double) x;
			weights[i]=-4 + r.nextDouble() * (3 - (-3));
		}
		
	}

	/*
	private static void learnWeights() {
		
		System.out.println(noOfTrainingExamples);
		System.out.println(weights.length);
		System.out.println(matrix.length);
		System.out.println(matrix[0].length);

		int k=0;
		for (int i = 0; i < weights.length; i++) {
			double temp=0;
			double temp1=0;

			for (int j = 0; j < weights.length; j++) {
				temp1+=weights[j]*matrix[k][j];
			}
			double predValue=sigmoid(temp1+weight0);
			for (int l = 0; l < noOfTrainingExamples; l++) {
			//	String feature=features.get(i);
				double val=classValue.get(l)-predValue;
				temp+=val*(matrix[l][i]);
			}
			k++;
			weights[i]=weights[i]+(learningRate)*temp-(learningRate*lambda*weights[i]);
		}

	}


	private static void learnWeights1() {
		for (int m = 0; m < 1; m++) {

			for (int i = 0; i < weights.length; i++) {
				//calculate expected prob of that class
				System.out.println(i);
				//double temp=0;
				double sum=0;
				for (int j = 0; j < noOfTrainingExamples; j++) {
					double classVal=classValue.get(j);
					double pred=0;
					for (int j2 = 0; j2 < weights.length; j2++) {
						pred+=weights[j2]*matrix[j][j2];
					}
					pred=sigmoid(pred+weight0);
					pred=1-pred;
					sum+=matrix[j][i]*(classVal-pred);
				}
				weights[i]+=(learningRate*sum)-(learningRate*1*weights[i]);
			}
		}
	}


	private static void learnWeightsDummy() {
		for (int m = 0; m < 10; m++) {
			System.out.println(m+"th iteration @ ");
			for (int i = 0; i < weights.length; i++) {
				//double temp=0;
				double sum=0;
				System.out.println(i);
				for (int j = 0; j < noOfTrainingExamples; j++) {
					double classVal=0;
					if(j<hamExamples)
						classVal=1;
					double pred=0;
					//System.out.println(j);
					HashMap<String, Integer> h=fileFeatureValueTrain.get(j);
					for (int j2 = 0; j2 < weights.length; j2++) {
					//	int val=0;
						System.out.println(j2);
						if(h.containsKey(features.get(j2)))
						{
							pred+=weights[j2]*1;
						}
					}
					pred=sigmoid(pred+weight0);
					pred=1-pred;
					int zval=0;
					if(h.get(i)!=null)
					{
						zval=1;
					}
					sum+=zval*(classVal-pred);

				}
				weights[i]+=(learningRate*sum)-(learningRate*1*weights[i]);
			}
		}
	}
*/
	
	private static void learnWeightsDummy1() {
		for (int m = 0; m < iterations; m++) {
	
			for (int i = 0; i < noOfTrainingExamples; i++) {
				double pred=0;
				for (int j = 0; j < weights.length; j++) {
					pred+=weights[j]*matrix[i][j];
				}
				double label=0;
				if(i<hamExamples){
					label=1;
					pred=sigmoid(-1*(pred+weight0));
					pred=1-pred;
				}
				else{
					pred=sigmoid(-1*(pred+weight0));
				}
				for (int j = 0; j < weights.length; j++) {
					weights[j]=weights[j]+(learningRate)*(label-pred)*matrix[i][j]-(learningRate*lambda*weights[j]);
				}
			}
		}
	}

	private static double sigmoid(double z) {
		double d=1/(double)(1+Math.exp(-1*z));
		return d;
	}

	private static void featureExtaction() throws Exception {
		vocab=extractVocab();
		classes=vocab.size();
		String[] ham=vocab.get(0);
		String[] spam=vocab.get(1);
		for (int i = 0; i < ham.length; i++) {
			ham[i]=ham[i].replaceAll(" ' ","'");
			ham[i]=ham[i].replaceAll("[^a-zA-Z']"," ");
			ham[i]=ham[i].replaceAll("''"," ");
			ham[i]=ham[i].replaceAll("\\s+", " ");
			ham[i]=ham[i].trim();
			String temp[]=ham[i].split(" ");
			for (int j = 0; j < temp.length; j++) {
				if(stopText.containsKey(temp[j].toLowerCase()))
					continue;
				V.add(temp[j]);
			}
		}
		for (int i = 0; i < spam.length; i++) {
			spam[i]=spam[i].replaceAll(" ' ","'");
			spam[i]=spam[i].replaceAll("[^a-zA-Z']"," ");
			spam[i]=spam[i].replaceAll("''"," ");
			spam[i]=spam[i].replaceAll("\\s+", " ");
			spam[i]=spam[i].trim();

			String temp[]=spam[i].split(" ");
			for (int j = 0; j < temp.length; j++) {
				if(stopText.containsKey(temp[j].toLowerCase()))
					continue;
				V.add(temp[j]);
			}
		}
	}


	private static ArrayList<String[]> extractVocab() throws Exception{

		StringBuilder builder=new StringBuilder();
		File stopfile = new File(stopWordsFile);
		builder=new StringBuilder();
		BufferedReader x=new BufferedReader(new InputStreamReader(new FileInputStream(stopfile)));
		String str=x.readLine();
		while (str!=null) {
			builder.append(str);
			stopText.put(str, str);
			str=x.readLine();
		}

		File hamfolder = new File(hamTrainFile);
		File[] hamlistOfFiles = hamfolder.listFiles();
		builder=new StringBuilder(); 
		String[] returningObj=new String[hamlistOfFiles.length];
		hamExamples=hamlistOfFiles.length;
		hamText =new ArrayList<TreeSet<String>>();
		TreeSet<String> tree=new TreeSet<String>();
		for (int i = 0; i < hamlistOfFiles.length; i++) {
			tree=new TreeSet<String>();
			builder=new StringBuilder();
			FileInputStream en=new FileInputStream(new File(hamlistOfFiles[i].toString()));
			x=new BufferedReader(new InputStreamReader(en));
			str=x.readLine();
			while (str!=null) {
				builder.append(str);
				str=str.replaceAll(" ' ","'");
				str=str.replaceAll("[^a-zA-Z']"," ");
				str=str.replaceAll("''"," ");
				str=str.replaceAll("\\s+", " ");
				str=str.trim();
				String arr[]=str.split(" ");
				for (int j = 0; j < arr.length; j++) {
					tree.add(arr[j].toLowerCase());
				}
				str=x.readLine();
			}
			x.close();
			hamText.add(tree);
			classValue.add(1);
			returningObj[i]=builder.toString();
		}
		File spamfolder = new File(spamTrainFile);
		File[] spamlistOfFiles = spamfolder.listFiles();
		String[] returningObj1=new String[spamlistOfFiles.length];
		spamExamples=spamlistOfFiles.length;
		for (int i = 0; i < spamlistOfFiles.length; i++) {
			tree=new TreeSet<String>();
			builder=new StringBuilder();
			FileInputStream en=new FileInputStream(new File(spamlistOfFiles[i].toString()));
			x=new BufferedReader(new InputStreamReader(en));
			str=x.readLine();
			while (str!=null) {
				builder.append(str);
				str=str.replaceAll(" ' ","'");
				str=str.replaceAll("[^a-zA-Z']"," ");
				str=str.replaceAll("''"," ");
				str=str.replaceAll("\\s+", " ");
				str=str.trim();
				String arr[]=str.split(" ");
				for (int j = 0; j < arr.length; j++) {
					tree.add(arr[j].toLowerCase());
				}
				str=x.readLine();
			}
			hamText.add(tree);
			classValue.add(0);
			returningObj1[i]=builder.toString();
		}


		ArrayList<String[]> ret=new ArrayList<String[]>();
		ret.add(returningObj);
		ret.add(returningObj1);
		return ret;
	}

}

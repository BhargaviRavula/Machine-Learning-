
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class NaiveBayesTrain {

	static Integer SpamFC=0,HamFC=0,FC=0,classes=0;
	static double prior[];
	static ArrayList<String> vocabList=new ArrayList<String>();
	static String[] hamTest,spamTest;
	static String hamTestLoc,spamTestLoc,hamTrainLoc,spamTrainLoc;

	public static void main(String[] args) throws Exception {
		hamTrainLoc=args[0];
		spamTrainLoc=args[1];
		hamTestLoc=args[2];
		spamTestLoc=args[3];
		Double[][] condprob = naivebayesTraining();
		System.out.println("Ham Accuracy is:"+hamTest(condprob));
		System.out.println("Spam Accuracy is:"+spamTest(condprob));
	}

	private static double hamTest(Double[][] condprob)throws FileNotFoundException, IOException {
		File hamTestfolder = new File(hamTestLoc);
		File[] No_of_files_in_hamTest = hamTestfolder.listFiles();

		hamTest=new String[No_of_files_in_hamTest.length];
		StringBuilder builder=new StringBuilder(); 
		int posCount=0;

		for (int i = 0; i < No_of_files_in_hamTest.length; i++) {

			builder=new StringBuilder();

			FileInputStream en=new FileInputStream(new File(No_of_files_in_hamTest[i].toString()));
			BufferedReader x=new BufferedReader(new InputStreamReader(en));
			String str=x.readLine();
			while (str!=null) { 
				builder.append(str);
				str=x.readLine();
			}
			x.close();

			String strTemp=builder.toString().replaceAll("[^a-zA-Z]"," ");
			strTemp=builder.toString().replaceAll("\\s+", " ");

			double Probability[]=new double[classes];

			for (int j = 0; j < classes; j++) {
				Probability[j]=(double)(-1*Math.log10(prior[j])/Math.log(2));

				String[] tempArray=strTemp.split(" ");

				for (int k = 0; k < tempArray.length; k++) {
					String t=tempArray[k];
					int index=vocabList.indexOf(t);
					if(index!=-1){
						Probability[j]+=(double)(-1*Math.log10(condprob[index][j])/Math.log(2));
					}
				}
			}
			if(Probability[0]<Probability[1]){
				hamTest[i]="HAM";
				posCount++;
			}
			else{
				hamTest[i]="SPAM";
			}
		}
		double hamAccuracy=(double)posCount/(double)No_of_files_in_hamTest.length;
		return hamAccuracy;
	}

	private static double spamTest(Double[][] condprob)throws FileNotFoundException, IOException {

		File spamTestfolder = new File(spamTestLoc);
		File[] No_of_Files_in_spamTest = spamTestfolder.listFiles();

		spamTest=new String[No_of_Files_in_spamTest.length];
		int posCount=0;

		StringBuilder builder=new StringBuilder(); 
		for (int i = 0; i < No_of_Files_in_spamTest.length; i++) {
			builder=new StringBuilder();

			FileInputStream en=new FileInputStream(new File(No_of_Files_in_spamTest[i].toString()));
			BufferedReader x=new BufferedReader(new InputStreamReader(en));
			String str=x.readLine();
			while (str!=null) {
				builder.append(str);
				str=x.readLine();
			}
			x.close();

			String strTemp=builder.toString().replaceAll("[^a-zA-Z]"," ");
			strTemp=builder.toString().replaceAll("\\s+", " ");

			double Probability[]=new double[classes];
			for (int j = 0; j < classes; j++) {
				Probability[j]=(double)(-1*Math.log(prior[j])/Math.log(2));
				String[] tempArray=strTemp.split(" ");
				for (int k = 0; k < tempArray.length; k++) {

					String t=tempArray[k];
					int index=vocabList.indexOf(t);
					if(index!=-1){
						Probability[j]+=(double)(-1*Math.log(condprob[index][j])/Math.log(2));
					}	
				}
			}
			if(Probability[0]<Probability[1]){
				spamTest[i]="HAM";
			}
			else{
				spamTest[i]="SPAM";
				posCount++;
			}
		}
		double spamAccuracy=(double)posCount/(double)No_of_Files_in_spamTest.length;
		return spamAccuracy;
	}

	private static Double[][] naivebayesTraining() throws Exception{
		ArrayList<String[]> all_words=extractWords();
		classes=all_words.size();

		String[] ham=all_words.get(0);
		String[] spam=all_words.get(1);

		TreeSet<String> Vocabulary=new TreeSet<String>();

		for (int i = 0; i < ham.length; i++) {
			ham[i]=ham[i].replaceAll("[^a-zA-Z]"," ");
			ham[i]=ham[i].replaceAll("\\s+", " ");
			String temp[]=ham[i].split(" ");
			for (int j = 0; j < temp.length; j++) {
				Vocabulary.add(temp[j]);
			}
		}
		for (int i = 0; i < spam.length; i++) {
			spam[i]=spam[i].replaceAll("[^a-zA-Z]"," ");
			spam[i]=spam[i].replaceAll("\\s+", " ");

			String temp[]=spam[i].split(" ");
			for (int j = 0; j < temp.length; j++) {
				Vocabulary.add(temp[j]);
			}
		}

		System.out.println("Vocabulary Size is:"+Vocabulary.size());

		SpamFC=spam.length;
		HamFC=ham.length;
		FC=SpamFC+HamFC;

		String[] str=new String[all_words.size()];
		prior=new double[all_words.size()];

		StringBuilder textc=new StringBuilder();

		Double[][] condprob=new Double[Vocabulary.size()][classes]; 
		for (int i = 0; i < classes; i++) {//for each class
			Integer words_in_class = all_words.get(i).length;
			textc=new StringBuilder();

			prior[i]=(double)words_in_class/(double)FC;//calculate prior value for this class

			if(i==0){//for ham,calculate textc

				File folder = new File(hamTrainLoc);
				TreeSet<String> hamSet=new TreeSet<String>();
				File[] hamlistOfFiles = folder.listFiles();
				StringBuilder builder=new StringBuilder(); 

				for (int j = 0; j < hamlistOfFiles.length; j++) {
					FileInputStream en=new FileInputStream(new File(hamlistOfFiles[j].toString()));
					BufferedReader x=new BufferedReader(new InputStreamReader(en));
					String str1=x.readLine();
					while (str1!=null) {
						builder.append(str1);
						str1=x.readLine();
					}
					x.close();
				}
				str[i]=builder.toString().replaceAll("[^a-zA-Z]"," ");
				str[i]=str[i].replaceAll("\\s+", " ");
				String[] temp=str[i].split(" ");
				for (int j = 0; j < temp.length; j++) {
					boolean b=hamSet.add(temp[j]);
					if(b==true||b==false){
						textc.append(temp[j]+" ");
					}
				}
				System.out.println("Hamset count:"+hamSet.size());
			}
			else if(i==1){
				File folder = new File(spamTrainLoc);
				File[] spamlistOfFiles = folder.listFiles();
				StringBuilder builder=new StringBuilder(); 

				for (int j = 0; j < spamlistOfFiles.length; j++) {
					FileInputStream en=new FileInputStream(new File(spamlistOfFiles[j].toString()));
					BufferedReader x=new BufferedReader(new InputStreamReader(en));
					String str1=x.readLine();
					while (str1!=null) {
						builder.append(str1);
						str1=x.readLine();
					}
					x.close();
				}

				str[i]=builder.toString().replaceAll("[^a-zA-Z]"," ");
				str[i]=str[i].replaceAll("\\s+", " ");
				TreeSet<String> spamSet=new TreeSet<String>();
				String[] temp=str[i].split(" ");
				for (int j = 0; j < temp.length; j++) {
					boolean b=spamSet.add(temp[j]);
					if(b==true||b==false){
						textc.append(temp[j]+" ");
					}
				}
				System.out.println("Spamset count:"+spamSet.size());
			}

			Iterator<String> iterator=Vocabulary.iterator();
			Integer[] count=new Integer[Vocabulary.size()];
			int j=-1;
			while (iterator.hasNext()) {
				j++;
				String itrText=iterator.next();
				vocabList.add(itrText);				
				count[j]=textc.toString().split(itrText).length-1;
			}
			iterator=Vocabulary.iterator();
			j=0;
			int sum=0;
			for (int j2 = 0; j2 < count.length; j2++) {
				sum+=count[j2];
			}
			sum=sum+1;
			while (iterator.hasNext()) {
				condprob[j][i]= ((double)(count[j]+1)/(double)(sum+count.length));
				iterator.next();
				j++;
			}

		}
		return condprob;
	}

	private static ArrayList<String[]> extractWords() throws Exception{
		File hamfolder = new File(hamTrainLoc);
		File[] hamlistOfFiles = hamfolder.listFiles();
		StringBuilder builder=new StringBuilder(); 
		String[] returningObj=new String[hamlistOfFiles.length];

		for (int i = 0; i < hamlistOfFiles.length; i++) {
			builder=new StringBuilder();
			FileInputStream en=new FileInputStream(new File(hamlistOfFiles[i].toString()));
			BufferedReader x=new BufferedReader(new InputStreamReader(en));
			String str=x.readLine();
			while (str!=null) {
				builder.append(str);
				str=x.readLine();
			}
			x.close();
			returningObj[i]=builder.toString();
		}

		File spamfolder = new File(spamTrainLoc);
		File[] spamlistOfFiles = spamfolder.listFiles();
		String[] returningObj1=new String[spamlistOfFiles.length];
		for (int i = 0; i < spamlistOfFiles.length; i++) {
			builder=new StringBuilder();
			FileInputStream en=new FileInputStream(new File(spamlistOfFiles[i].toString()));
			BufferedReader x=new BufferedReader(new InputStreamReader(en));
			String str=x.readLine();
			while (str!=null) {
				builder.append(str);
				str=x.readLine();
			}
			x.close();
			returningObj1[i]=builder.toString();
		}


		ArrayList<String[]> ret=new ArrayList<String[]>();
		ret.add(returningObj);
		ret.add(returningObj1);
		return ret;
	}

}

//@author: Nikhil Rao //
//@net id : ngr140030 //
//*********************//

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Scanner;
import java.util.Set;


public class NBMain {

	public static Set<String> distinct_vocab = new HashSet<String>();
	public static TreeMap <String, Integer> spam_wordmap = new TreeMap<String, Integer>();
	public static TreeMap<String, Integer> my_ham_wordmap = new TreeMap<String, Integer>();
	public static Set<String> Stopword_list = new HashSet<String>();

	public static void main(String[] args) throws Exception{


		String dir_location = args[0];
		String to_filter = args[1];
		File dir_spam_train = new File(dir_location+"/train/spam");
		File dir_ham_train = new File(dir_location+"/train/ham");
		File dir_spam_test = new File(dir_location+"/test/spam");
		File dir_ham_test = new File(dir_location+"/test/ham");
		File stopwords = new File(dir_location+"/stopwords.txt");
		
		//do no forget to commnet above section if you want to specify relative or absolute path as input//
		//Uncomment below if you have all your test and train directories and stop words in current folder//
		
		/*File dir_spam_train = new File("train/spam");
		File dir_ham_train = new File("train/ham"); 
		File dir_spam_test = new File("test/spam");
		File dir_ham_test = new File("test/ham");  
		File stopwords = new File("stopwords.txt");
		*/
		String[] splsym = {"!","#","%","^","&","*","(",")","!", ":",".","{","}", "[","]",">","<","?","/", "*","~", "@"};
	
		addDistinct(dir_spam_train);
		addDistinct(dir_ham_train);
	
		for(String s1: splsym){
			distinct_vocab.remove(s1);
		}
		
		
		Scanner s=null;
		try {
			s = new Scanner(stopwords);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		while(s.hasNext()){
			String sw = s.next();
			Stopword_list.add(sw);
		}
		s.close();


		if(to_filter.equals("yes")){
			System.out.println("Removing stop words....");
			for(String str : Stopword_list){
				if(distinct_vocab.contains(str)){
					distinct_vocab.remove(str);
				}
			}
		}
		getHashmap_spam(dir_spam_train);
		getHashmap_ham(dir_ham_train);

		for(String s1: splsym){
			if(spam_wordmap.containsKey(s1) ){
				spam_wordmap.remove(s1);
				
			}
			if(my_ham_wordmap.containsKey(s1) ){
				my_ham_wordmap.remove(s1);
				
			}
		}
		if(to_filter.equals("yes")){

			for(String stopword : Stopword_list){
				if(spam_wordmap.containsKey(stopword) ){
					spam_wordmap.remove(stopword);
				}
				if(my_ham_wordmap.containsKey(stopword) ){
					my_ham_wordmap.remove(stopword);
				}
			}
		}
		
		NaiveBayes nb = new NaiveBayes(my_ham_wordmap,spam_wordmap, distinct_vocab);
		nb.train(1);
		// Priors//
		double priorSpam_probability = 
				1.0*(dir_spam_train.listFiles().length)/(  dir_spam_train.listFiles().length + dir_ham_train.listFiles().length ) ;

		double priorHam_probability = 1.0 - priorSpam_probability;

		double l_priorSpam_probability = Math.log(priorSpam_probability);
		double l_priorHam_probability = Math.log(priorHam_probability);
		//-----
		
		
		
	
		
		double num_correct_spam =0;
		int ns = 0;
		for(File file: dir_spam_test.listFiles()){
			ns = ns +1;
			if(nb.test_doc(file, l_priorHam_probability, l_priorSpam_probability, Stopword_list,to_filter) == 1){
				num_correct_spam = num_correct_spam + 1.0;
			}
		}
		
		if(to_filter.equals("yes")){
			System.out.println("Accuracy of Naive Bayes after removal of Stop Words:");
		}
		else{
			System.out.println("Accuracy of Naive Bayes with out removing Stop Words: ");
		}
		System.out.println();
		//System.out.println("Spam Accuracy "+ num_correct_spam);
		double spam_accuracy = num_correct_spam/ns; 
		System.out.println("Spam % Accuracy "+ spam_accuracy*100);
		
		double num_correct_ham =0;
		int nh=0;
		for(File file: dir_ham_test.listFiles()){
			nh=nh+1;
			if(nb.test_doc(file, priorHam_probability, priorSpam_probability,Stopword_list,to_filter) == 0){
				num_correct_ham = num_correct_ham + 1.0;
			}
		}
		System.out.println();
		//System.out.println("Ham Accuracy "+ num_correct_ham);
		double ham_accuracy = num_correct_ham/nh; 
		System.out.println("Ham % Accuracy : "+ ham_accuracy*100);
		System.out.println();
		//System.out.println("Overall Accuracy  : "+( (num_correct_ham+num_correct_spam)/(ns+nh))*100);
		 

	}


	private static void getHashmap_spam(File dir_spam_train) throws Exception {
		for(File file: dir_spam_train.listFiles()){
			Scanner sc = new Scanner(file);
			while(sc.hasNext()){
				String line = sc.nextLine();
				for(String s: line.toLowerCase().trim().split(" ")){
					if(!s.isEmpty()){
						
						if(spam_wordmap.containsKey(s)){
							spam_wordmap.put(s, spam_wordmap.get(s)+1);
						}else{
							spam_wordmap.put(s, 1);
						}
					}
				}
			}
			sc.close();
		}

	}


	private static void getHashmap_ham(File dir_ham_train) throws Exception {

		for(File file: dir_ham_train.listFiles()){
			Scanner sc = new Scanner(file);
			while(sc.hasNext()){
				String line = sc.nextLine();
				for(String s: line.toLowerCase().trim().split(" ")){
					if(!s.isEmpty()){
						
						if(my_ham_wordmap.containsKey(s)){
							my_ham_wordmap.put(s, my_ham_wordmap.get(s)+1);
						}else{
							my_ham_wordmap.put(s, 1);
						}
					}	
				}
			}
			sc.close();
		}
	}

	private static void addDistinct(File dir_spam_train) throws Exception {

		for(File file: dir_spam_train.listFiles()){

			Scanner scanner = new Scanner(file);
			while(scanner.hasNext()){
				String line = scanner.nextLine();
				for(String s : line.toLowerCase().trim().split(" ")){
					if(!s.isEmpty()){
						distinct_vocab.add(s);
					}
				}
			}
			scanner.close();

		}
	}
}

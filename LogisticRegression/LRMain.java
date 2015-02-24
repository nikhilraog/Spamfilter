import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;


public class LRMain {

	public static Set<String> distinct_vocab = new HashSet<String>();
	public static HashMap<String, Integer> my_spam_wordmap = new HashMap<String, Integer>();
	public static HashMap<String, Integer> my_ham_wordmap = new HashMap<String, Integer>();

	public static HashMap<String,HashMap<String, Integer> >spam_filemap = new HashMap<String, HashMap<String,Integer>>();	
	public static HashMap<String,HashMap<String, Integer> >ham_filemap = new HashMap<String, HashMap<String,Integer>>();

	static Set<String> ham_file_set = new HashSet<String>();
	static Set<String> spam_file_set = new HashSet<String>();
	static Set<String> all_file_set = new HashSet<String>();
	public static Set<String> Stopword_list = new HashSet<String>();

	public static void main(String[] args) throws Exception {


		String dir_location = args[0];
		String to_filter = args[1];
		double learning_rate_eta = Double.parseDouble(args[2]);
		double lambda = Double.parseDouble(args[3]);
		int num_iterations = Integer.parseInt(args[4]);

		File dir_spam_train = new File(dir_location+"/train/spam");
		File dir_ham_train = new File(dir_location+"/train/ham"); 

		File dir_spam_test = new File(dir_location+"/test/spam");
		File dir_ham_test = new File(dir_location+"/test/ham");  

		File stopwords = new File(dir_location+"/stopwords.txt");

		addDistinct(dir_spam_train);
		addDistinct(dir_ham_train);

		Scanner s3=null;
		try {
			s3 = new Scanner(stopwords);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		while(s3.hasNext()){
			String sw = s3.next();
			Stopword_list.add(sw);
		}
		s3.close();

		if(to_filter.equals("yes")){

			System.out.println("Removing stop words...");
			for(String str : Stopword_list){
				if(distinct_vocab.contains(str)){
					distinct_vocab.remove(str);
				}
			}

		}


		getHashmap_spam(dir_spam_train);
		getHashmap_ham(dir_ham_train);

		LogisticReg lr = new LogisticReg(dir_location, distinct_vocab,ham_filemap,spam_filemap,my_ham_wordmap,my_spam_wordmap, learning_rate_eta, lambda, num_iterations,all_file_set,spam_file_set,ham_file_set);


		lr.train();
		int s_count = 0 ;
		int num_spam_testfiles = 0;

		System.out.println("Testing Spam test files.....");
		for(File testfile : dir_spam_test.listFiles()){
			num_spam_testfiles = num_spam_testfiles+1;
			HashMap<String, Integer> my_testmap = new HashMap<String, Integer>();
			Scanner sc = new Scanner(testfile);
			while(sc.hasNext()){
				String line = sc.nextLine();
				for(String s: line.toLowerCase().trim().split(" ")){
					s = s.replaceAll("[^a-zA-Z]+", "");
					if(my_testmap.containsKey(s)){
						my_testmap.put(s, my_testmap.get(s)+1);
					}else{
						my_testmap.put(s, 1);
					}
				}	
			}
			sc.close();
			
			if(to_filter.equals("yes")){
				//System.out.println("Testing begins now");
				for(String stopword: Stopword_list){
					stopword = stopword.replaceAll("[^a-zA-Z]+", "");
					if(my_testmap.containsKey(stopword)){
						my_testmap.remove(stopword);
					}
				}
			}
			
			int spam = lr.test(my_testmap);
			if(spam == 1){
				s_count++;

			}

		}

		double spam_acc = ( (double)s_count / (double)num_spam_testfiles)*100;

		System.out.println("Accuracy  on spam "+ (spam_acc));

		System.out.println();
		System.out.println("Testing ham test files.....");


		int h_count = 0 ;
		int num_ham_testfiles = dir_ham_test.listFiles().length;

		for(File testfile : dir_ham_test.listFiles()){
			//num_ham_testfiles = num_ham_testfiles+1;
			HashMap<String, Integer> my_testmap = new HashMap<String, Integer>();
			Scanner sc = new Scanner(testfile);
			while(sc.hasNext()){
				String line = sc.nextLine();
				for(String s: line.toLowerCase().trim().split(" ")){
					s = s.replaceAll("[^a-zA-Z]+", "");

					if(my_testmap.containsKey(s)){
						my_testmap.put(s, my_testmap.get(s)+1);
					}else{
						my_testmap.put(s, 1);
					}
				}	
			}
			sc.close();
			int ham = lr.test(my_testmap);
			if(ham == 0){
				h_count++;

			}
		}


		double ham_acc = ( (double)h_count / (double)num_ham_testfiles)*100;
		System.out.println("Accuracy  on Ham "+ ham_acc);
		System.out.println("Done...");

	}

	private static void getHashmap_spam(File dir_spam_train) throws Exception {
		for(File file: dir_spam_train.listFiles()){
			HashMap<String, Integer> spam_filevocab = new HashMap<String, Integer>();

			spam_file_set.add(file.getName());
			all_file_set.add(file.getName());
			Scanner sc = new Scanner(file);
			while(sc.hasNext()){
				String line = sc.nextLine();

				for(String s: line.toLowerCase().trim().split(" ")){
					s = s.replaceAll("[^a-zA-Z]+", "");
					if(distinct_vocab.contains(s)){

						if(my_spam_wordmap.containsKey(s)){
							my_spam_wordmap.put(s, my_spam_wordmap.get(s)+1);
						}else{
							my_spam_wordmap.put(s, 1);
						}

						if(spam_filevocab.containsKey(s)){
							spam_filevocab.put(s, spam_filevocab.get(s)+1);
						}
						else{
							spam_filevocab.put(s, 1);
						}
					}

					spam_filemap.put(file.getName(), spam_filevocab);
				}
			}
			sc.close();
		}

	}

	private static void getHashmap_ham(File dir_ham_train) throws Exception {


		for(File file: dir_ham_train.listFiles()){
			HashMap<String, Integer> ham_filevocab = new HashMap<String, Integer>();
			ham_file_set.add(file.getName());
			all_file_set.add(file.getName());

			Scanner sc = new Scanner(file);
			while(sc.hasNext()){
				String line = sc.nextLine();
				for(String s: line.toLowerCase().trim().split(" ")){
					s = s.replaceAll("[^a-zA-Z]+", "");
					if(!s.isEmpty()){

						if(distinct_vocab.contains(s)){
							if(my_ham_wordmap.containsKey(s)){
								my_ham_wordmap.put(s, my_ham_wordmap.get(s)+1);
							}else{
								my_ham_wordmap.put(s, 1);
							}

						}	
					}	

					if(!s.isEmpty()){

						if(distinct_vocab.contains(s)){
							if(ham_filevocab.containsKey(s)){
								ham_filevocab.put(s, ham_filevocab.get(s)+1);
							}else{
								ham_filevocab.put(s, 1);
							}
						}
					}
					ham_filemap.put(file.getName(), ham_filevocab);
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
					s = s.replaceAll("[^a-zA-Z]+", "");
					if(!s.isEmpty()){
						distinct_vocab.add(s);
					}
				}
			}
			scanner.close();

		}
	}
}

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;


public class LogisticReg {

	static HashMap<String, HashMap<String, Integer>> ham_filemap_LR = new HashMap<String, HashMap<String,Integer>>();
	static HashMap<String,HashMap<String,Integer>> spam_filemap_LR = new HashMap<String, HashMap<String,Integer>>();
	static HashMap<String, Integer> my_ham_wordmap_LR = new HashMap<String, Integer>();
	static HashMap<String, Integer> my_spam_wordmap_LR = new HashMap<String, Integer>();
	HashMap<String, Double>file_expected_sum_weights = new HashMap<String, Double>();

	double learning_rate_eta_LR = 0.0 ;
	double lambda_LR = 0; 
	int num_iterations_LR =0;
	String dir_location_LR = new String();
	static Set<String> distinct_vovab_LR = new HashSet<String>();
	static double w0 = 0.1;
	static Set<String> ham_file_set = new HashSet<String>();
	static Set<String> spam_file_set = new HashSet<String>();
	static Set<String> all_file_set = new HashSet<String>();

	static HashMap<String, Double> weighted_wordmap = new HashMap<String, Double>();
	static HashMap<String, Double> new_weighted_wordmap = new HashMap<String, Double>();

	public LogisticReg(String dir_location, Set<String> distinct_vocab,
			HashMap<String, HashMap<String, Integer>> ham_filemap,
			HashMap<String, HashMap<String, Integer>> spam_filemap,
			HashMap<String, Integer> my_ham_wordmap,
			HashMap<String, Integer> my_spam_wordmap, double learning_rate_eta, double lambda, int num_iterations, Set<String> all_file_set2, Set<String> spam_file_set2, Set<String> ham_file_set2){
		
		dir_location_LR = dir_location;
		
		distinct_vovab_LR = distinct_vocab;
		ham_filemap_LR = ham_filemap;
		spam_filemap_LR = spam_filemap;
		
		my_ham_wordmap_LR = my_ham_wordmap;
		my_spam_wordmap_LR = my_spam_wordmap;
		
		learning_rate_eta_LR = learning_rate_eta;
		lambda_LR = lambda; 
		num_iterations_LR = num_iterations;
	
		all_file_set = all_file_set2;
		spam_file_set = spam_file_set2;
		ham_file_set = ham_file_set2;


	}

	public void train() {
		//Assign weights to all the words in the distinct vocabulary set

		int counter = 0;
		for(String s:distinct_vovab_LR){
		
			double r =  (Math.random() * (1 -(-1))) + (-1);
			weighted_wordmap.put(s, r); // assign random weights
		}

		for(int i =0 ; i<1; i++){

			System.out.println("check point "+ counter++);

			for(String currentword : distinct_vovab_LR){
				double delta_wterror = 0;

				for(String filename : all_file_set){
					double classofthisfile;
					int count_currentword = getCountcurrentword(filename, currentword);
					if(spam_file_set.contains(filename)){
						classofthisfile = 1; //spam
					}
					else{
						classofthisfile = 0;//ham
					}
					double Sigmoidofthisfile = calculateWeightofthisfile(filename);
					double error = (classofthisfile - Sigmoidofthisfile);
					delta_wterror = delta_wterror + count_currentword*error;
				}
				
				double new_weight_forWord = weighted_wordmap.get(currentword) + learning_rate_eta_LR*delta_wterror -(learning_rate_eta_LR*lambda_LR*weighted_wordmap.get(currentword));
				weighted_wordmap.put(currentword, new_weight_forWord);
			}
		}
	
	}

	
	
	private int getCountcurrentword(String filename, String currentword) {
		int count = 0;
		if(ham_file_set.contains(filename)){
			try {
				for(Entry<String, Integer> wordcount1: ham_filemap_LR.get(filename).entrySet()){
					if(wordcount1.getKey().equals(currentword)){
						count = wordcount1.getValue();
						return count;
					}
				}
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		else if(spam_file_set.contains(filename)){

			try {
				for(Entry<String, Integer> wordcount: spam_filemap_LR.get(filename).entrySet()){
					if(wordcount.getKey().equals(currentword)){
						count = wordcount.getValue();
						return count;
					}
				}
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		return 0;
	}

	
	
	private double calculateWeightofthisfile(String filename) {

		if(ham_file_set.contains(filename)){
			double weightedsum = w0;
			try{
				for(Entry<String, Integer> values_map: ham_filemap_LR.get(filename).entrySet()){
					weightedsum = weightedsum + weighted_wordmap.get( values_map.getKey() )  * values_map.getValue();
				}	
			}
			catch(Exception E){
				System.out.println( E);
				
			}
			return (Sigmod(weightedsum) );
		}

		else{
			double weightedsum1 = w0;
			try{
				for(Entry<String, Integer> values_map1: spam_filemap_LR.get(filename).entrySet()){
					weightedsum1 = weightedsum1 + weighted_wordmap.get( values_map1.getKey() )  * values_map1.getValue();
					//System.out.println("key "+ values_map1.getKey() + "  value "+ values_map1.getValue());
				}	
			}
			catch(Exception e){
				System.out.println("..");
			}

			return (Sigmod(weightedsum1) );
		}


	}

	private double Sigmod(double weightedsum) {
		if(weightedsum>100){
			return 1.0;
		}
		else if(weightedsum<-100){

			return 0.0;
		}
		else{
			//return (Math.exp(-weightedsum)/(1.0+ Math.exp(-weightedsum)));
			return (1.0 /(1.0+ Math.exp(-weightedsum)));
		}
	}

	public HashMap<String, Integer> getmyWordsinthisfile(String filename) {

		if(ham_file_set.contains(filename)){
			return ham_filemap_LR.get(filename);
		}
		else{
			return spam_filemap_LR.get(filename);
		}

	}

	public int test(HashMap<String, Integer> my_testmap) {
		double test_sum = 0;
		for(Entry<String, Integer> ent :my_testmap.entrySet()){
			if(weighted_wordmap.containsKey(ent.getKey())){
				test_sum = test_sum + (weighted_wordmap.get(ent.getKey())* ent.getValue());
			}
		}
		test_sum = test_sum+w0;
		if(test_sum>=0){
			return 1;
		}
		else{
			return 0;
		}
	}

}

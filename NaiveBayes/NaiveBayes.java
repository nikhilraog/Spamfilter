//@author: Nikhil Rao //
//@net id : ngr140030 //
//*********************//

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;


public class NaiveBayes {


	static HashMap<String, Double> spam_map_likelyhood = new HashMap<String, Double>();
	static HashMap<String, Double> ham_map_likelyhood = new HashMap<String, Double>();

	static TreeMap<String, Integer> spam_map = new TreeMap<String, Integer>();
	static TreeMap<String, Integer> ham_map = new TreeMap<String, Integer>();
	static Set<String> Vocabset = new HashSet<String>();
	static int stotal=0;
	static int htotal=0;

	public NaiveBayes(TreeMap<String,Integer> my_ham_wordmap,
			TreeMap<String, Integer> spam_wordmap, Set<String> distinct_vocab) {

		spam_map = spam_wordmap;
		ham_map = my_ham_wordmap;
		Vocabset = distinct_vocab;

	}
	public int train(int i){

		int spam_totalterms =0;
		for(Entry<String, Integer> entry: spam_map.entrySet()){
			spam_totalterms = spam_totalterms + entry.getValue();
		}
		

		int ham_totalterms =0;
		for(Entry<String, Integer> entry: ham_map.entrySet()){
			ham_totalterms = ham_totalterms + entry.getValue();
		}
		
		for(String s : Vocabset){

			if(spam_map.containsKey(s)){

				double spam_likely = (spam_map.get(s)+1.0)/(spam_totalterms+Vocabset.size()+1.0);
				double spam_loglikely = Math.log(spam_likely);
				spam_map_likelyhood.put(s, spam_loglikely);
			}			
		}
		for(String s : Vocabset){

			if(ham_map.containsKey(s)){

				double ham_likely = (ham_map.get(s)+1.0)/(ham_totalterms+Vocabset.size()+1.0);
				double ham_loglikely = Math.log(ham_likely);
				ham_map_likelyhood.put(s, ham_loglikely);
			}
		
		}
		stotal = spam_totalterms;
		htotal = ham_totalterms;
		
		return 1;
	}

	public int test_doc(File file, double priorHam_probability, double priorSpam_probability) throws Exception {

		double current_spamprob = 0.0;
		double current_hamprob = 0.0;
		Scanner scanner = new Scanner(file);
		while(scanner.hasNext()){
			String line = scanner.nextLine();
		

			for(String s : line.toLowerCase().split(" ")){
					
					if(spam_map_likelyhood.containsKey(s)){
						current_spamprob = current_spamprob + spam_map_likelyhood.get(s);
					}else{

						current_spamprob = current_spamprob + Math.log(1.0 / (stotal + Vocabset.size()+1.0)) ;

					}
					if(ham_map_likelyhood.containsKey(s)){
						current_hamprob = current_hamprob + ham_map_likelyhood.get(s);
					}else{
						current_hamprob = current_hamprob +  Math.log( 1.0 / (htotal + Vocabset.size()+1.0));
					}

				
			}
		}
		scanner.close();
		current_spamprob = current_spamprob + priorSpam_probability;
		current_hamprob = current_hamprob + priorSpam_probability;

		if(current_spamprob > current_hamprob){
			return 1; // spam
		}

		else{
			return 0;
		}
	
	}

	public int test_doc(File file, double priorHam_probability, double priorSpam_probability, Set<String> stopword_list, String tofilter) throws Exception {

		double current_spamprob = 0.0;
		double current_hamprob = 0.0;
		Scanner scanner = new Scanner(file);
		while(scanner.hasNext()){
			String line = scanner.nextLine();
            if(tofilter.equals("yes") ){
            	for(String s : line.toLowerCase().split(" ")){
        
            		if(!stopword_list.contains(s)){
    					if(spam_map_likelyhood.containsKey(s)){
    						current_spamprob = current_spamprob + spam_map_likelyhood.get(s);
    					}else{

    						current_spamprob = current_spamprob + Math.log(1.0 / (stotal + Vocabset.size()+1.0)) ;

    					}
    					if(ham_map_likelyhood.containsKey(s)){
    						current_hamprob = current_hamprob + ham_map_likelyhood.get(s);
    					}else{
    						current_hamprob = current_hamprob +  Math.log( 1.0 / (htotal + Vocabset.size()+1.0));
    					}
            		}
    			}
            }
            else{
            	for(String s : line.toLowerCase().split(" ")){
            			
    					if(spam_map_likelyhood.containsKey(s)){
    						current_spamprob = current_spamprob + spam_map_likelyhood.get(s);
    					}else{

    						current_spamprob = current_spamprob + Math.log(1.0 / (stotal + Vocabset.size()+1.0)) ;

    					}
    					if(ham_map_likelyhood.containsKey(s)){
    						current_hamprob = current_hamprob + ham_map_likelyhood.get(s);
    					}else{
    						current_hamprob = current_hamprob +  Math.log( 1.0 / (htotal + Vocabset.size()+1.0));
    					}

    				}	
    			}
            
		}
		scanner.close();
		current_spamprob = current_spamprob + priorSpam_probability;
		current_hamprob = current_hamprob + priorSpam_probability;

		if(current_spamprob > current_hamprob){
			return 1; // spam
		}

		else{
			return 0;
		}

	}


}

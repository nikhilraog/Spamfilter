# Spamfilter
Spam filter : Classification of e-mail as spam or ham based on Naive Bayes and Logistic Regression


How to run this?

Steps to Compile and run the program:

Naive Bayes:
Download and unzip the folder containg the files of Naive Bayes program : NBMain.java and NaiveBayes.Java

Input Folder Structure : Example
------------------------------------------------
	         F:\temp
		   	\test
	                      \spam
		               \ham
	                 \train
	                       \spam
		                \ham
	                 stopwords.txt
-------------------------------------------------
**Main Driver program - NaiveBayes.java

Argument 0 - folder containing test + train folders + SpamWords.txt
Argument 1 - yes or no to indicate whether to consider ( Yes - Remove Stop word ; No - Do not remove Stop words)

**To run the program with considering Stop word criteria**

Step 1: javac NBMain.java

** To run the program - considering Stop word criteria** (remove stop word and run)

Step 2: 

java NBMain path_to_folder conataining_trainingfolder_and_testfolder_and_stopword.txt_fileno yes/no

In the above case it would be;

java NBMain F:\temp no     
java NBMain F:\temp yes

*********-------------------------------------------------*************
    
Similarly with Logistic Regression for the same folder structure above:

Argument 0 : path to folder containing above folders- test and train + stopword.txt file
Argument  1: to_filter_stopwords: yes or no
Argument 2: learning_rate_eta
Argument 3: lambda
Argument 4: num_iterations

Step1 : javac LRMain.java
Step 2 : java LRMain yes_or_no_to_filter_stop_words 0.01 0.1 5
Example: java LRMain no 0.01 .01 10

**********------------------------------------------------****************

Please note in case og Logistic regression, the program take lot of time to complete execution.
I did test run on Amazon ec2 to get its executaion speed up.


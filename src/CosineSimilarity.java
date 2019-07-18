import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Object;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

@SuppressWarnings("rawtypes")
public class CosineSimilarity {
	
	public static void main(String[] args) throws IOException {
		try {
			String folderTopic = "topic";
			File[] folderTopics = new File(folderTopic).listFiles();
			String[][] matrix = new String[0][0];
			//for each topic
			for(int fold = 0; fold<folderTopics.length; fold++) {
				Path idxpathTopic = Paths.get(folderTopics[fold].toString() + "/index");
				Path idxpathTweet = Paths.get("index");
				Directory dr = FSDirectory.open(idxpathTweet);
				IndexReader reader = DirectoryReader.open(dr);
				Vector vTopic = getVectorTerms(idxpathTopic, 0);
				int interests = folderTopics.length;
				int numTweets = reader.numDocs();
				//only one time for every topic
				if(fold==0)
					matrix = new String[numTweets][interests+1];
				//for each document in topic index and tweet index calculate relative tfidf and relevance score
				//store every score for every tweet to every topic in matrix
				for (int i = 0; i < reader.numDocs(); i++) {
					//calculere tfidf for topic
					Vector tfidfTopic = calculateTFIDFTopic(idxpathTopic, 0);
					//calculare tfidf for tweet
					Vector tfidfTweet = calculateTFIDFTweet(vTopic, idxpathTweet, i);
					//calculate relevance score of tweet
					double score = calculateCosineSimilarity(tfidfTopic, tfidfTweet);
					//store id doc of tweet
					matrix[i][0] = i+"";
					//store score
					matrix[i][fold+1] = Double.toString(score);
					System.out.println("tweet " + i + "fold " + fold + ": " + score + " " + idxpathTopic.toString());
				}
			}
			//intialize csv matrix
			FileWriter csvWriter = new FileWriter("matrixTweetScoreForTopic.csv");  
			csvWriter.append("DocId");  
			csvWriter.append(",");  
			csvWriter.append("BusinessScore"); 
			csvWriter.append(",");  
			csvWriter.append("FashionScore"); 
			csvWriter.append(",");  
			csvWriter.append("MusicScore"); 
			csvWriter.append(",");  
			csvWriter.append("NewsScore"); 
			csvWriter.append(",");  
			csvWriter.append("PoliticScore");  
			csvWriter.append(",");  
			csvWriter.append("ScienceScore");  
			csvWriter.append(",");  
			csvWriter.append("SportsScore");  
			csvWriter.append(",");  
			csvWriter.append("TechScore");  
			csvWriter.append("\n");
			
			//store score from matrix to csv file
			for (int i = 0; i < matrix.length; i++) {
				csvWriter.append(String.join(",", matrix[i]));
				csvWriter.append("\n");
			}
			//close csv
			csvWriter.flush();  
			csvWriter.close();
			System.out.println("DONE");
		}	
		catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	//function to get term vector from lucene index
	public static  Vector getVectorTerms(Path idxpath, int docID) throws IOException {
		Directory dr = FSDirectory.open(idxpath);
		IndexReader reader = DirectoryReader.open(dr);
		Terms terms = reader.getTermVector(docID, "text");
		Vector vector = new Vector();
		if(terms != null) {
			TermsEnum termsEnum = terms.iterator();
			BytesRef bytesRef = termsEnum.next();
			while(bytesRef  != null){
				String term = bytesRef.utf8ToString();
				vector.addElement(term);
				bytesRef = termsEnum.next();
			}
		}
		return vector;
	}
	
	//function to calculate tfidf for topic
	public static Vector calculateTFIDFTopic(Path idxpath, int docID) throws IOException {
		Vector v = new Vector();
		Directory dr = FSDirectory.open(idxpath);
		IndexReader reader = DirectoryReader.open(dr);
		Terms terms = reader.getTermVector(docID, "text");
		if(terms != null) {
			TermsEnum termsEnum = terms.iterator();
			BytesRef bytesRef = termsEnum.next();
			long totalCountTerm = reader.getSumTotalTermFreq("text");
			while(bytesRef  != null){
				Term termInstance = new Term("text", bytesRef);
				double docFreq = reader.docFreq(termInstance);			//n doc with termInstance
				double termFreq = termsEnum.totalTermFreq();			//tf
				double termFreqNorm = termFreq/totalCountTerm;			//tf normalized
				double idf = 1 + Math.log(reader.numDocs()/docFreq);	//idf
				v.addElement(termFreqNorm*idf);
				bytesRef = termsEnum.next();
			}
		}
		return v;
	}
	//function to calculare tfidf for tweet
	public static Vector calculateTFIDFTweet(Vector vTopic, Path idxpath, int docID) throws IOException {
		Vector v = new Vector();
		for ( int i = 0; i < vTopic.size(); i++) {
			v.add(i, 0.0);
		}
		//open tweet index
		Directory dr = FSDirectory.open(idxpath);
		IndexReader reader = DirectoryReader.open(dr);
		Terms terms = reader.getTermVector(docID, "text");
	
		if (terms != null) {
			TermsEnum termsEnum = terms.iterator();
			BytesRef bytesRef = termsEnum.next();
			int j = 0;
			while (bytesRef != null){
				if (vTopic.contains(bytesRef.utf8ToString())) {
					for (int i = j; i < vTopic.size(); i++) {
						Object termTopic = vTopic.get(i);
						if (termTopic.equals(bytesRef.utf8ToString())) {
							long totalCountTerm = reader.getSumTotalTermFreq("text");
							Term termInstance = new Term("text", bytesRef);
							double docFreq = reader.docFreq(termInstance);
							double termFreq = termsEnum.totalTermFreq();			//tf
							double termFreqNorm = termFreq/totalCountTerm;			//tf normalized
							double idf = 1 + Math.log(reader.numDocs()/docFreq);	//idf
							v.set(i, termFreqNorm*idf);
							j = i;
							break;
						}
					}
				}
				bytesRef = termsEnum.next();
			}
		}
		return v;
	}
	
	//calculate cosinSimilarity from two vector
	public static double calculateCosineSimilarity(Vector query, Vector doc) {
		double score = 0.0;
		double dotProduct = 0.0;
		double queryNorm = 0.0;
		double docNorm = 0.0;
		double denom = 0.0;

		//calculate score through cosine similarity
		for (int i = 0; i < query.size(); i++) {
			dotProduct = dotProduct + (double)query.get(i)*(double)doc.get(i);
			queryNorm = queryNorm + Math.pow((double)query.get(i), 2);
			docNorm = docNorm + Math.pow((double)doc.get(i), 2);
		}

		queryNorm = Math.sqrt(queryNorm);
		docNorm = Math.sqrt(docNorm);
		denom = queryNorm*docNorm;
		
		score = dotProduct / (denom);

		//check to prevent division for 0
		if (denom == 0) {
			score = 0.0;
		}else {
			score = dotProduct / (denom);
		}
		
		return score;
	}
}

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.xml.sax.SAXException;

public class TweetIndexing {
	public static void main(String[] args) {
		int cont = 1;
		String xmlpath = "data/Author"; 
		File[] authors = new File(xmlpath).listFiles(); 
		Path idxpath = Paths.get("index");
		//clear folder index
		clearIndex("index");
		Directory dr;
		try {
			dr = FSDirectory.open(idxpath);
			IndexWriterConfig writer = new IndexWriterConfig(TextAnalyzer.textAnalyzer());
			IndexWriter iw = new IndexWriter(dr, writer);
			//indexing every tweet for each author in lucene index with id, author, text, data, retweet number, favorite number
			for(File author : authors) {
				File[] files = new File(author.getAbsolutePath()).listFiles(); 
				for (File file : files) {
					//read every xml file
					File fXmlFile = new File(file.getAbsolutePath());
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
					doc.getDocumentElement().normalize();
					//for each id in xml file
					for(int i=0; i<doc.getElementsByTagName("id").getLength(); i++) {
						Document doc_idx = new Document();
						//set lucene index
						FieldType myFieldType = new FieldType(TextField.TYPE_STORED);
						myFieldType.setStoreTermVectors(true);
						myFieldType.setStoreTermVectorOffsets(true);
						myFieldType.setStoreTermVectorPositions(true);
						//add field for each tweet
						Field idTweetField = new Field("idTweetField", doc.getElementsByTagName("id").item(i).getTextContent() , myFieldType);
						Field authorField = new Field("author", doc.getElementsByTagName("author").item(i).getTextContent() , myFieldType);
						Field textField = new Field("text", doc.getElementsByTagName("text").item(i).getTextContent() , myFieldType);
						Field createdAtField = new Field("createdAt", doc.getElementsByTagName("createdAt").item(i).getTextContent(), myFieldType);
						Field favoriteCountField = new Field("favoriteCount", doc.getElementsByTagName("favoriteCount").item(i).getTextContent(), myFieldType);
						Field retweetCountField = new Field("retweetCount", doc.getElementsByTagName("retweetCount").item(i).getTextContent(), myFieldType);
						//add field value in lucene doc
						doc_idx.add(idTweetField);
						doc_idx.add(authorField);
						doc_idx.add(textField);
						doc_idx.add(createdAtField);
						doc_idx.add(favoriteCountField);
						doc_idx.add(retweetCountField);
						//write doc in lucene index
						iw.addDocument(doc_idx);
						System.out.println(doc.getElementsByTagName("author").item(i).getTextContent() + " " + "idTweet: " + doc.getElementsByTagName("id").item(i).getTextContent());
						cont++;
					}
				}
			}
			//close lucene index
			iw.close();
			System.out.println("FINISHED INDEXING " + cont + " TWEET");
			
		}
		catch (IOException e1) {
				e1.printStackTrace();
			}
		catch (ParserConfigurationException e) {
		      e.printStackTrace();
		    } 
		catch (SAXException e) {
		      e.printStackTrace();
		    }
	}
	//funtcion to clear folder index
	private static void clearIndex(String dir) {
		File directory = new File(dir);
		File[] files = directory.listFiles();
		for (File file : files){
		   if (!file.delete()){
		       System.out.println("Failed to delete "+file);
		   }
		} 	
	}
}

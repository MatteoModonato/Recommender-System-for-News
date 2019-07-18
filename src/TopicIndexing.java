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

public class TopicIndexing {
	public static void main(String[] args) {
		String xmlpath = "topic/"; 
		File[] folders = new File(xmlpath).listFiles();
		try {
			//for each folder in topic folder
			for(File folder : folders) {
				int cont = 1;
				Path xmlFilePath = Paths.get(folder.toString() + "/data");
				Path idxpath = Paths.get(folder.toString() + "/index");
				//clear every topic index
				clearIndex(folder.toString() + "/index");
				Directory dr;
				dr = FSDirectory.open(idxpath);
				IndexWriterConfig writer = new IndexWriterConfig(TextAnalyzer.textAnalyzer());
				IndexWriter iw = new IndexWriter(dr, writer);
				File[] xmlFile = new File(xmlFilePath.toString()).listFiles();
				//read topic document
				File fXmlFile = new File(xmlFile[0].getAbsolutePath());
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
				for(int i=0; i<doc.getElementsByTagName("documents").getLength(); i++) {
					Document doc_idx = new Document();
					FieldType myFieldType = new FieldType(TextField.TYPE_STORED);
					//set lucene index
					myFieldType.setStoreTermVectors(true);
					myFieldType.setStoreTermVectorOffsets(true);
					myFieldType.setStoreTermVectorPositions(true);
					//get text of topic document
					Field textField = new Field("text", doc.getElementsByTagName("documents").item(i).getTextContent() , myFieldType);
					//add text in documet text field of lucene index
					doc_idx.add(textField);
					//add document in lucene index
					iw.addDocument(doc_idx);
				}
				//close index
				iw.close();
				System.out.println(folder + "DONE");
			}
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
	//funtcion to clear index
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

import twitter4j.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Crawler {
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		try {
			//open lista_account.txt
			BufferedReader bufReader = new BufferedReader(new FileReader("lista_account.txt"));
			//set date
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			//read lines of lista_account.txt
			String line = bufReader.readLine();
			//set twitter API
			Twitter twitter = new TwitterFactory().getInstance();
			Paging p = new Paging();
			p.setCount(200);
			
			while((line = bufReader.readLine()) != null) {
				int count = 0;
				//get first id in last modified document for each folder
				String last = lastFileModified("data/Author/" + line.replace("@", ""));
				File xmlFile = new File(last);
		        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		        Document doc = dBuilder.parse(xmlFile);
		        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		        factory.setNamespaceAware(true);
		        XPathFactory xfactory = XPathFactory.newInstance();
		        XPath xpath = xfactory.newXPath();
		        XPathExpression allProductsExpression = xpath.compile("//singleTweet/id/text()");
		        NodeList productNodes = (NodeList) allProductsExpression.evaluate(doc, XPathConstants.NODESET);
		        Node productName = productNodes.item(0);
		        String id = productName.getTextContent();
		  		        
				File file = new File("data/Author/" + line.replace("@", "") + "/" + line + "_" + dateFormat.format(date) + ".xml") ;
				PrintWriter pw = new PrintWriter(file);
				StringBuilder sb = new StringBuilder();
				sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				sb.append("<TWEETS>\n");
				List<Status> statuses = twitter.getUserTimeline(line,p);
				for (Status status : statuses) {
					//stop crawling when last tweet id appear
					if(!id.equals(status.getId()+"")) {
						//append attribute and values in xml format
						sb.append("\t<singleTweet>\n");
						sb.append("\t\t<id>" + status.getId() + "</id>\n");
						sb.append("\t\t<author>" + "@" + status.getUser().getScreenName() + "</author>\n");
						sb.append("\t\t<text>" + status.getText().toString().replaceAll("&", "") + "</text>\n");
						sb.append("\t\t<createdAt>" + status.getCreatedAt() + "</createdAt>\n");
						sb.append("\t\t<favoriteCount>" + status.getFavoriteCount() + "</favoriteCount>\n");             
						sb.append("\t\t<retweetCount>" + status.getRetweetCount() + "</retweetCount>\n");
						sb.append("\t</singleTweet>\n");
						count++;
					}
					else
						break;
				}
				sb.append("</TWEETS>");
				pw.write(sb.toString());
				pw.close();
				System.out.println("downloading " + count +" tweets from: " + line);
			}
			bufReader.close();
		}
		catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get timeline: " + te.getMessage());
			System.exit(-1);
		}
	}
	//funciotn to get last modified file
	public static String lastFileModified(String dir) {
	    File fl = new File(dir);
	    File[] files = fl.listFiles(new FileFilter() {          
	        public boolean accept(File file) {
	            return file.isFile();
	        }
	    });
	    long lastMod = Long.MIN_VALUE;
	    File choice = null;
	    for (File file : files) {
	        if (file.lastModified() > lastMod) {
	            choice = file;
	            lastMod = file.lastModified();
	        }
	    }
	    return choice.toString();
	}
}
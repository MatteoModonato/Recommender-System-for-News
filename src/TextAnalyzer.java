import org.apache.lucene.analysis.custom.CustomAnalyzer;

import java.io.IOException;

public class TextAnalyzer {
	//set a custom analizer for text in lucene field
    public static CustomAnalyzer textAnalyzer() throws IOException {
    	//set regex for link
        String regex = "(http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";
        //set regex for number
        String regexNum = "([0-9]+)";
        return CustomAnalyzer.builder()
        		//tokenize text
                .withTokenizer("uax29urlemail")
                //set text in lowercase
                .addTokenFilter("lowercase")
                //text lemminization
                .addTokenFilter("kstem")
        		//regex link in text
                .addTokenFilter("patternreplace", "pattern", regex, "replacement", "!")
        		//regex num in text
                .addTokenFilter("patternreplace", "pattern", regexNum, "replacement", "!")
        		//remove stop words
                .addTokenFilter("stop", "words", "stopWords_EN.txt", "format", "wordset")
                //remove possessive case
                .addTokenFilter("apostrophe")
                .build();
    }
} 		
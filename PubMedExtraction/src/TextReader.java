import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

//import opennlp.tools.sentdetect.SentenceDetectorME;
//import opennlp.tools.sentdetect.SentenceModel;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.*;
import java.util.*;
import java.lang.System;
import java.io.*;
import java.util.*;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
//import opennlp.tools.sentdetect.*;

/*
 * This java Code does three things:
 * a. Reads the XML files.
 * b. Sends them to the OpenNLP sentence boundary detector to convert the text into sentences.
 * c. Sentences are POS tagged using the Stanford POS tagger.
 *
 */

public class TextReader {

	
    private MaxentTagger tagger;
    
    
		
	public TextReader() throws IOException,ClassNotFoundException {
        
		File file=new File("");
	    this.tagger = new MaxentTagger(file.getAbsolutePath()+"/lib/"+"stanford-postagger-2012-07-09/models/wsj-0-18-bidirectional-distsim.tagger");
       
	}

	public String  Tags(String text)
	{
			String taggedSentence=tagger.tagString(text);
			
			return taggedSentence;
	}
		

}
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;


public class FileReader extends JFrame implements ActionListener{
	
	 private  String mainArticle;
	 private  String referencedArticle;
	 private  String referencedContext;
	 private String filePath;
	 private String fileName;
	 private static String destFolder;
	 private static String folderPath;
	 private JPanel panelFolder;
	 
	 private TextReader textReader;
	 public static boolean startFlag=false;
	 
	 private JLabel labelFolder;
	 private JTextField textFolder;
	 private JButton buttonFolder;
	 
	 
	 public FileReader()
	 {
		 try {
			 
			 this.setTextReader(new TextReader());
		
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		panelFolder=new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelFolder=new JLabel("Folder Path");
		textFolder=new JTextField(10);
		buttonFolder=new JButton("Ok");
		buttonFolder.addActionListener(this);
		panelFolder.add(labelFolder);
		panelFolder.add(textFolder);
		panelFolder.add(buttonFolder);
		this.add(panelFolder);
		this.setTitle("Folder Path");
		this.setLocation(500, 500);
		this.setSize(400, 100);
		this.setFocusable(true);
		this.setVisible(false);
		 
	 }
	 
	
	 
	 
	 public String getFilePath() {
		return filePath;
	}




	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	
	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public TextReader getTextReader() {
		return textReader;
	}


	public void setTextReader(TextReader textReader) {
		this.textReader = textReader;
	}


	public String getMainArticle() {
		return mainArticle;
	}

	public void setMainArticle(String mainArticle) {
		this.mainArticle = mainArticle;
	}

	public String getReferencedArticle() {
		return referencedArticle;
	}

	public void setReferencedArticle(String referencedArticle) {
		this.referencedArticle = referencedArticle;
	}

	public String getReferencedContext() {
		return referencedContext;
	}

	public void setReferencedContext(String referencedContext) {
		this.referencedContext = referencedContext;
	}
 
	
	
	
	public static void main(String argv[]) {
	 
		    
		    folderPath=argv[0].toString();
		    File file = new java.io.File("");  
		    boolean success = (new File(file.getAbsoluteFile()+"/src/"+argv[1].toString())).mkdir();
		    destFolder=file.getAbsoluteFile()+"/src/"+argv[1].toString();
		    xmlParsing();
		

		
			
	}
	
	public static void xmlParsing()
	{
		
		try {

			
			final FileReader fileReader=new FileReader();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			DefaultHandler handler = new DefaultHandler() {

				boolean flagMainArticle = false;
				boolean flagReferencedArticle = false;
				boolean flagRefererencedContext = false;


				public void startElement(String uri, String localName,String qName, 
						Attributes attributes) throws SAXException {



					if (qName.equals("NAME")) {
						flagMainArticle = true;
					}

					if (qName.equals("article-title")) {
						flagReferencedArticle = true;
					}

					if (qName.equals("REFCONTEXT")) {
						flagRefererencedContext = true;
					}



				}

				public void endElement(String uri, String localName,
						String qName) throws SAXException {



				}

				public void characters(char ch[], int start, int length) throws SAXException {

					if (flagMainArticle) {

						String mainArticle=new String(ch,start,length);
						fileReader.setMainArticle(mainArticle);
						flagMainArticle = false;
					}

					if (flagReferencedArticle) {
						String referenceArticle=new  String(ch,start,length);
						fileReader.setReferencedArticle(referenceArticle);

						flagReferencedArticle = false;
					}

					if (flagRefererencedContext) {
						String referenceContext=new  String(ch,start,length);
						fileReader.setReferencedContext(referenceContext);
						
						
						fileReader.preProcessingBlock();

						flagRefererencedContext = false;
					}




				}

			};

			
			File folder = new File(folderPath);
			File[] listOfFiles = folder.listFiles();
			for (File listOfFile : listOfFiles)
			{
				
				fileReader.setFilePath(listOfFile.getAbsolutePath().toString());
				fileReader.setFileName(listOfFile.getName().toString());
				saxParser.parse(fileReader.getFilePath(), handler);
				
				
			}
							

		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
		
		
		
	}
	

	
	

	
	public void preProcessingBlock()
	 {
		 String refContext=this.getReferencedContext();
		 String newRefContext=refContext.replaceAll("\\[[^)]*\\]", "");
		 this.sentenceBoundaryDetector(newRefContext);

		 
		 
	 }
	 
	 public void sentenceBoundaryDetector(String refContext)
	 {
		
		 Reader reader = new StringReader(refContext);
		 DocumentPreprocessor dp = new DocumentPreprocessor(reader);

		 List<String> sentenceList = new LinkedList<String>();
		 Iterator<List<HasWord>> it = dp.iterator();
		 while (it.hasNext()) {
		    StringBuilder sentenceSb = new StringBuilder();
		    List<HasWord> sentence = it.next();
		    for (HasWord token : sentence) {
		       if(sentenceSb.length()>1) {
		          sentenceSb.append(" ");
		       }
		       sentenceSb.append(token);
		    }
		    sentenceList.add(sentenceSb.toString());
		 }

		 for(String sentence:sentenceList) {
		   String taggedSentence=this.getTextReader().Tags(sentence);
		   //System.out.println(taggedSentence);
			 WriteFile(taggedSentence);
		 }
	 }
	 
	 
	 public void WriteFile(String taggedSentence)
		{
			try{
	    		
	            //String path=getClass().getClassLoader().getResource("TaggedFiles").getPath()+"/";
	            
	            String xmlfile=destFolder+"/"+getFileName();
	            File file =new File(xmlfile);
	            file.setReadable(true);
	            file.setWritable(true);
	    		
	    		if(!file.exists()){
	    			file.createNewFile();
	    		}
	 
	    		//true = append file
	    		FileWriter fileWritter = new FileWriter(file.getAbsolutePath(),true);
	    	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	    	         bufferWritter.write("<Document>"+"<mainArticle>"+getMainArticle()+"</mainArticle>"+"<refArticle>"+getReferencedArticle()+"</refArticle>"+"<tagged>"+taggedSentence+"</tagged>"+"</Document>"+"\n");
	    	        bufferWritter.close();
	                
			
		        
	 
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
		
		}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		/*if(arg0.getSource()==buttonFolder)
		{
			if(textFolder.getText().toString().isEmpty())
			{
			
			JOptionPane.showMessageDialog(null, "Please Enter the Folder Path");
			
			}
			else
			{
				setFolderPath(textFolder.getText().toString());
				startFlag=true;
			}
				
				
			
		}*/
	}
		
	 
	}
	



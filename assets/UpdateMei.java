import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;
//INSERT element and value in first category
public class UpdateMei {
	static String[][] alldata = new String[200][50];
	static int allelements =0;
	static boolean FILENOTFOUND;
	public static void main(String[] args) throws IOException { //FIX,MAKE ALL FIELDS WORK
		  parseData();
		  takeInputs();
	}
	public static void parseData() throws FileNotFoundException {
		File file = new File("data.tsv");
		Scanner input = new Scanner(file);
		int counter=0;
		while(input.hasNext()) {
		    //or to process line by line
		    String nextLine = input.nextLine();
		     String[] temp = nextLine.split("\t");
		     for (int i=0; i<temp.length; i++) {
		    	 alldata[counter][i]=temp[i];
		     }
		     
		    counter++;
		    
		}
		allelements=counter;
		input.close();
		
	}
	
	public static void takeInputs() throws IOException {
	     //System.out.print("Folder?:"); 
	    // Scanner four = new Scanner(System.in);
	     //String folder = four.next();
		mainLoop("temp/Fauv/"); 
		mainLoop("temp/IvTrem/");
	     mainLoop("temp/Montpellier/");
	}
	
	public static void mainLoop(String folder) throws IOException {
		  File dir = new File(folder);
		  File[] directoryListing = dir.listFiles();
		  if (directoryListing != null) {
		    for (File child : directoryListing) {
		    	
		    	insertElement(child);
		    }
		  } else {
		    // Handle the case where dir is not really a directory.
		    // Checking dir.isDirectory() above would not be sufficient
		    // to avoid race conditions with another process that deletes
		    // directories.
		  }
	}
	
	public static void insertElement(File file) throws IOException {
		// Open a temporary file to write to.
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("temp.mei")));
		BufferedReader br = null;
		FileReader reader = null;
		boolean alreadyFound=false; //only inserts it in first one
		int  j= findFile(file);
		if (j!=0) {
		try {
		    reader = new FileReader(file.getPath());
		    br = new BufferedReader(reader);
		    String line;
		    while ((line = br.readLine()) != null) {
				    	if (line.contains("<meiHead")){ //insert the data in meihead
				    		writer.println(line);
				    		String temp="";
				    		for (int i=0; i<line.length(); i++) {
				    			if (line.charAt(i)=='\t') {
				    				temp=temp+"\t";
				    			}	
				    		}
				    		
				    			String x = createLines(j);
				    			String y = insertTabs(x,temp);
				    			writer.println(y);
				    			alreadyFound=true;
				    		
							//insert before it
						} else if (alreadyFound && line.contains("</meiHead>")) {
							alreadyFound=false;
						} else if (!alreadyFound) {
							if (line.contains("<instrDef xml")) {
								line =  line.substring(0, 49) + "midi.bpm=\"800\" " + line.substring(49, line.length());
							}
							if (line.contains("<staffDef xml")) {
								line =  line.substring(0, 30) + " ident= \"mensural.black\"" + line.substring(30, line.length());
							}
				            writer.println(line);
						}
		    		}
		
		    writer.close();

		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		}finally{
		    reader.close();

		}

		// ... and finally ...

		File realName = new File(file.getPath());
		realName.delete(); // remove the old file
		new File("temp.mei").renameTo(realName); // Rename temp file*/
		}
	}
	public static String insertTabs(String line, String temp) {
		String output="";
		int counter=2;
		for (int i=0; i<line.length(); i++) {
			if (line.charAt(i)=='/') {
				counter=counter-1;
				String[] split= line.split(" ");

			} if (line.charAt(i)=='\n') {
				counter++;
				if (closedString(findNewLine(line,i))) {
					counter=counter-1;
				}
				output=output+line.charAt(i)+returnTabs(counter);
			} else {
				output=output+line.charAt(i);
			}
		}
		return output;
		
	}
	
	public static String findNewLine(String line,int j) {
		for (int i=j+1; i<line.length(); i++ ) {
			if (line.charAt(i)=='\n') {
				String temp =line.substring(j, i);
				return temp;
			}
		}
		return "";
		
	}
	public static String returnTabs(int counter) {
		String tabs="";
		for (int i=0; i<counter; i++) {
			tabs=tabs + "\t";
		}
		return tabs;
	}
	
	public static boolean closedString(String line) {
		boolean start=false;
		boolean first=false;
		boolean second=false;
		boolean third=false;
		for (int i=0; i<line.length(); i++) {
			if (i<4) {
			if (line.charAt(i)=='\n') {
				start=true;
			}
			if (line.charAt(i)=='<' && start) {
				first=true;
			}
			if (line.charAt(i)=='/' && first) {
				second=true;
			}
			}if (line.charAt(i)=='>' && second) {
				third=true;
				return start && first && second && third;
			}
		}
		return start && first && second && third;
		
	}
	public static String findTitle(String line) {
		String temp =""; //fix me
		int lowbound =0;
		int highbound=0;
		boolean firstone=true;
		for (int i=0; i<line.length(); i++) {
			if (line.charAt(i)=='>' && firstone) {
				lowbound=i+1;
				firstone=false;
			}
			if (line.charAt(i)=='<') {
				highbound = i;
			}
		}
		if (lowbound<highbound) {
			temp = line.substring(lowbound, highbound);
		}
		return temp;
	}
	
	public static int findFile(File file) throws IOException {

		// Open a temporary file to write to.
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("temp.mei")));

		BufferedReader br = null;
		FileReader reader = null;
		boolean alreadyFound=false; //only inserts it in first one
		try {
		    reader = new FileReader(file.getPath());
		    br = new BufferedReader(reader);
		    String line;
		    String title = "";
		    while ((line = br.readLine()) != null && !alreadyFound) {
		    	if (line.contains("<title x")) {
		    		title = findTitle(line);
		    		alreadyFound=true;
		    	}
		    
    
		        
		    }
		    
		    
	    	for (int j=0; j<allelements; j++) {
	  
	    		if (alldata[j][0].equals(title)) { //CREATE FULL
	    			return j;
	    	}   
		   
		    }
		    writer.close();
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		}finally{
		    reader.close();

		}
		return 0;
	}
	
	public static String createLines(int j) {
		return "\t\t<fileDesc xml:id=\"m-3\">\n<titleStmt xml:id=\"m-4\">\n<title xml:id=\"m-10\">" + alldata[j][0] + "</title>"
				+ "\n<composer xml:id=\"m-11\">" + alldata[j][1] + "</composer> \n " +
				"<funder>\n<corpName>Social Sciences and Humanities Research Council, Canada (SSHRC) </corpName>\n</funder>\n"
				+ "<funder>\n<corpName> Schulich School of Music, McGill University </corpName> \n</funder> \n "
				+ "<funder>\n<corpName>Brandeis University</corpName>\n</funder>\n "
				+ "<respStmt> \n"
				+ "<persName role=\"project director\">Karen Desmond</persName>\n"
				+ initialsToNames(alldata[j][24],alldata[j][23],"encoder") //convert initials to names
				+ initialsToNames(alldata[j][26],alldata[j][25],"proofreader") //for loop
				+ "</respStmt>\n" + "</titleStmt>"
				+ "\n<pubStmt xml:id=\"m-15\">\n<publisher>\n "
				+ "<persName>Karen Desmond</persName>\n<corpName>Brandeis University</corpName>\n " + "</publisher>"
				+ "\n<date>2018</date> \n<availability>\n<useRestrict>Available for purposes of academic research and teaching only.</useRestrict>\n</availability>"
				+ "\n</pubStmt> \n<seriesStmt> \n<title>Measuring Polyphony</title> \n<editor> \n<persName>Karen Desmond</persName> \n"
				+ "</editor>\n</seriesStmt>\n</fileDesc>\n<sourceDesc>\n<source>\n<notesStmt>\n<annot>Scanned from "+alldata[j][11]+"</annot>\n"
				+ "</notesStmt>\n</source>\n<source>\n<titleStmt>\n<title>[Primary source for this encoding]\n<identifier>"+alldata[j][4]+"</identifier>\n"
				+ "</title>\n<pubStmt>\n<unpub/>\n</pubStmt>\n<notesStmt>\n"
				+ "<annot>Scan checked and corrected against this manuscript</annot>\n</notesStmt>\n</titleStmt>\n</source>\n<source>\n"
				+ "<titleStmt>\n<title>[Other concordant sources]</title>\n<pubStmt>\n<unpub/>\n</pubStmt>\n<notesStmt>\n"
				+ "<annot>" + alldata[j][8] + "</annot>\n</notesStmt>\n</titleStmt>\n</source>\n</sourceDesc>\n<encodingDesc xml:id=\"m-18\"> \n"
				+ "<appInfo xml:id=\"m-19\">\n<application xml:id=\" + sibelius + \" isodate=\" + 2016-4-29T09:24:36Z + \" version=\"7510\">"
				+ "<name xml:id=\"m-21\" type=\"operating-system\">Mac OS X Mountain Lion</name>"
				+ "</application>\n<application xml:id=\"sibmei\" type=\"plugin\" version=\"2.0.0b3\">\n<name xml:id=\"m-23\">Sibelius to MEI Exporter (2.0.0b3)</name>\n"
				+ "</application> \n<application xml:id=\"meiMENS\" isodate=\" + 2016-4-29 + \">\n<name xml:id=\"m-23\">CMN-MEI to MensuralMEI Translator</name>\n"
				+ "</application>\n</appInfo>\n<editorialDecl>\n<p>\n" + alldata[j][29] 
				+ "</p>\n<p></p>\n</editorialDecl>\n<projectDesc>\n<p>Short Project Description</p>\n</projectDesc>\n</encodingDesc>\n<workDesc xml:id=\"m-5\">\n<work xml:id=\"m-6\">\n<identifier>"
				+ "\n<identifier>"+alldata[j][12]+"</identifier>\n</identifier>\n<titleStmt xml:id=\"m-7\">\n<title xml:id=\"m-8\">" + alldata[j][0]+"</title>\n<respStmt xml:id=\"m-9\">\n"
				+ "<persName xml:id=\"m-12\" role=\"composer\">" + alldata[j][1] + "</persName>\n</respStmt>\n</titleStmt>\n"
				+ titleToParts(alldata[j][0])
				+ "<otherChar>Original clefs " + clefFormat(alldata[j][7]) + "</otherChar>\n<classification>\n<term>" + alldata[j][3] + "</term>\n</classification>\n</work>\n</workDesc>\n<extMeta>\n"
				+ alldata[j][31] + "\n" + "<iimsLink xmlns=\"" + alldata[j][19] + ">IIMS Link</iimsLink>\n</extMeta>\n</meiHead>";
	}
	
	
	public static String initialsToNames(String date, String input, String role) {
		String[] splitInput = input.split(", ");
		String output="";
		for (int i=0; i<splitInput.length; i++) {
			if (splitInput[i].equals("KD")) {
				output = output + "<persName role=\""+role+"\"date=\"" + dateFormat(date) + ">Karen Desmond</persName> \n";
			} else if (splitInput[i].equals("EH")) {
				output = output + "<persName role=\""+role+"\"date=\"" + dateFormat(date) + ">Emily Hopkins</persName> \n";
			} else if (splitInput[i].equals("SH")) {
				output = output + "<persName role=\""+role+"\"date=\"" + dateFormat(date) + ">Sam Howes</persName> \n";
			} else if (splitInput[i].equals("SM")) {
				output = output + "<persName role=\""+role+"\"date=\"" + dateFormat(date) + ">Sadie Menicanin</persName> \n";
			} else if (splitInput[i].equals("DS")) {
				output = output + "<persName role=\""+role+"\"date=\"" + dateFormat(date) + ">Daniel Shapiro</persName> \n";
			} else {
				output = output + "<persName role=\""+role+"\"date=\"" + dateFormat(date) + ">" + splitInput[i] + "</persName> \n";
			}
			}
		
		return output;
	}
	
	public static String titleToParts(String input) {

		String output="";
		String[] splitInput = input.split("/");
		for (int i=0; i<splitInput.length; i++) {
			output=output+ "<incip>\n<incipText>"+splitInput[i]+"</incipText>\n</incip>\n";
			
		}
		return output;
	}
	public static String dateFormat(String input) {
		String[] splitInput = input.split("/");
		String s = String.join("-", splitInput);
		return s;
	}
	
	public static String clefFormat(String input) {
		String[] splitInput = input.split(",");
		String s = String.join("", splitInput);
		return s;
	}
	
	
	public void loadFile() throws FileNotFoundException {
		File file = new File("mei/Fauv/adesto_MENSURAL.mei");
		Scanner input = new Scanner(file);
		while(input.hasNext()) {
		    //or to process line by line
		    String nextLine = input.nextLine();
		}
		input.close();
	}
	
	public static String createLine(String el, String val) {
		return "<" + el + ">" + val + "</" + el + ">";
	}
}
		
	

/*
 *
 * This is a small program to parse the UI xml file of android to extract all the View and
 * generate its declaration and reference statements.
 * It will assign id of the view as its name.
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Rajiv
 */
public class AndroidUIParser {

	// declaration related to document parsing 
    private static ArrayList<String> ViewDeclarationList = new ArrayList<String>();
    private static ArrayList<String> ViewReferenceList = new ArrayList<String>();
    private static File XmlFile;
    private static FileInputStream fis;
    private static FileOutputStream fos;
    private static String currentFilePath;
    private static InputStreamReader isr;
    private static Scanner scanner;
    private static String currentLine;
    private static DocumentBuilderFactory factory;
    private static DocumentBuilder builder;
    private static Document doc;
    // string constants 
    private static final String SPACE = " ";
    private static final String OPENBRACE = "(";
    private static final String ClOSEBRACE = ")";
    private static final String FINDMETHODSTRING = "findViewById";
    private static final String DELIMN = ";";
    private static final String ASIGNMENT = "=";
    private static final String RID = "R.id.";
	
	// access specifier 
	private static final String PUBLIC ="public";
	private static final String PROTECTED="protected";
	private static final String PRIVATE="private";
	private static String AccessSpecifier=null;
	// option holder
		
	private static int Option=0;
	
    public static void main(String[] args) {
        if (args.length > 1 && (args[1].contains(".xml") || args[1].contains(".XML"))) {
			setAccessSpecifier(args[0]);
            factory = DocumentBuilderFactory.newInstance();
            try {
                builder = factory.newDocumentBuilder();
                currentFilePath = args[1];
                XmlFile = new File(currentFilePath);
                doc = builder.parse(XmlFile);
                Element rootElement = doc.getDocumentElement();

                Node rootNode = doc.getFirstChild(); // creating first node 
                extractNodeName(rootNode);

                int numDec = ViewDeclarationList.size();
				// printing view declaration
				System.out.println("*********** view declaration ***********\n");
                for (int i = 0; i < numDec; i++) {
                    System.out.println(ViewDeclarationList.get(i));
                }
                System.out.println();

				System.out.println("*********** view reference ***********\n");
                
                for (int i = 0; i < numDec; i++) {
                    System.out.println(ViewReferenceList.get(i));
                }
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(AndroidUIParser.class.getName()).log(Level.SEVERE,
                        null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(AndroidUIParser.class.getName()).log(Level.SEVERE,
                        null, ex);
            } catch (IOException ex) {
                Logger.getLogger(AndroidUIParser.class.getName()).log(Level.SEVERE,
                        null, ex);
            }

        } else {
			printUsageMethod();
        }

    }

	// recursive method to traverse XML structure and extract view element 
	private static void extractNodeName(Node node) {
        if (!(node instanceof Element)) {
            return;
        } else if (!node.hasChildNodes()) {
            Element childElement = (Element) node;
			
            if (childElement.getAttribute("android:id") != null
                    && childElement.getAttribute("android:id").length() > 0) {
                String tagName = processTagName(childElement.getTagName());
                String id = extractId(childElement.getAttribute("android:id"));
                addToDeclarationList(tagName, id);
                addToReferenceList(tagName, id);
            }
        } else {
            Element childElement = (Element) node;
            if (childElement.getAttribute("android:id") != null
                    && childElement.getAttribute("android:id").length() > 0) {
                String tagName = processTagName(childElement.getTagName());
                String id = extractId(childElement.getAttribute("android:id"));
                addToDeclarationList(tagName, id);
                addToReferenceList(tagName, id);
            }
            NodeList nodeList = childElement.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {

                extractNodeName(nodeList.item(i));
            }
        }
    }

	
	// method to extract option
	private static void setAccessSpecifier(String optionString){
			if(optionString.contains("-") && optionString.length()==2){
				int pos = optionString.lastIndexOf("-");
				int as = Integer.parseInt(optionString.substring(pos+1,optionString.length()));
				switch(as){
					
					case 1: AccessSpecifier = PRIVATE +SPACE; break;
					case 2: AccessSpecifier = PROTECTED+SPACE; break;
					case 3: AccessSpecifier = ""; break;
					case 4: AccessSpecifier = PUBLIC +SPACE; break;
				
				
				}
				
			}else{
				printUsageMethod();
				System.exit(0);
			}
	
	}
	
	private static void printUsageMethod(){
	 System.out.println("Usage:- java AndroidUIParser [-option] <Xml File Name> \n"+ 
											"options:-\n 1 : for declaring Views as private"+
											"\n 2 : for declaring Views as protected"+
											"\n 3 : for declaring Views as default"+
											"\n 4 : for declaring Views as public");
	
	}
	
	// method to extract id from provided attribute value
    private static String extractId(String idAttr) {

        int pos = idAttr.lastIndexOf("/");
        String id = idAttr.substring(pos + 1, idAttr.length());
        return id;
    }
	
	// method to process tagname as some view can contain package name also
	private static String processTagName(String tagName) {
        String processedId = null;
        if (tagName.contains(".")) {
            int pos = tagName.lastIndexOf(".");
            processedId = tagName.substring(pos + 1, tagName.length());
            return processedId;
        } else {
            return tagName;
        }
    }

	
	// method to generate and save view declaration statement
    private static void addToDeclarationList(String tagname, String id) {
        String declaration = AccessSpecifier+tagname + SPACE + id + DELIMN;
        // System.out.println(declaration);
        ViewDeclarationList.add(declaration);
    }

	// method to generate and save reference statement
    private static void addToReferenceList(String tagname, String id) {
        String reference = id + SPACE + ASIGNMENT +SPACE+ OPENBRACE + tagname + ClOSEBRACE + FINDMETHODSTRING + OPENBRACE + RID
                + id + ClOSEBRACE + DELIMN;

        //   System.out.println(reference);
        ViewReferenceList.add(reference);
    }
	
	
	
}
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
    private static String SPACE = " ";
    private static String OPENBRACE = "(";
    private static String ClOSEBRACE = ")";
    private static String FINDMETHODSTRING = "findViewById";
    private static String DELIMN = ";";
    private static String ASIGNMENT = "=";
    private static String RID = "R.id.";

    public static void main(String[] args) {
        if (args.length > 0 && (args[0].contains(".xml") || args[0].contains(".XML"))) {
            factory = DocumentBuilderFactory.newInstance();
            try {
                builder = factory.newDocumentBuilder();
                currentFilePath = args[0];
                XmlFile = new File(currentFilePath);
                doc = builder.parse(XmlFile);
                Element rootElement = doc.getDocumentElement();

                Node rootNode = doc.getFirstChild(); // creating first node 
                extractNodeName(rootNode);

                int numDec = ViewDeclarationList.size();
                for (int i = 0; i < numDec; i++) {
                    System.out.println(ViewDeclarationList.get(i));
                }
                System.out.println();

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
            System.out.println("Please Provide path to xml file ");
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
                String tagName = childElement.getTagName();
                String id = extractId(childElement.getAttribute("android:id"));
                addToDeclarationList(tagName, id);
                addToReferenceList(tagName, id);
            }
        } else {
            Element childElement = (Element) node;
            if (childElement.getAttribute("android:id") != null
                    && childElement.getAttribute("android:id").length() > 0) {
                String tagName = childElement.getTagName();
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

	// method to extract id from provided attribute value
    private static String extractId(String idAttr) {

        int pos = idAttr.lastIndexOf("/");
        String id = idAttr.substring(pos + 1, idAttr.length());
        return id;
    }
	
	
	// method to generate and save view declaration statement
    private static void addToDeclarationList(String tagname, String id) {
        String declaration = tagname + SPACE + id + DELIMN;
        // System.out.println(declaration);
        ViewDeclarationList.add(declaration);
    }

	// method to generate and save reference statement
    private static void addToReferenceList(String tagname, String id) {
        String reference = id + SPACE + ASIGNMENT + OPENBRACE + tagname + ClOSEBRACE + FINDMETHODSTRING + OPENBRACE + RID
                + id + ClOSEBRACE + DELIMN;

        //   System.out.println(reference);
        ViewReferenceList.add(reference);
    }
}
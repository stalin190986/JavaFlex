package TEST;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Handler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXParserExample extends DefaultHandler{

	List myEmpls;
	
	private String tempVal;
	

	
	
	public SAXParserExample(){
		myEmpls = new ArrayList();
	}
	
	public void runExample()
        {
		parseDocument();
                printData();
	}

	private void parseDocument()
        {
                ///String ruta = "file:///C:/Archivos de programa/Apache Software Foundation/Apache Tomcat 6.0.26/webapps/";
                String ruta = "http://132.142.160.187/pentaho/server/biserver-ce/tomcat/webapps/CrossDomain/archivos_xml/";
                ///String nombre = ruta + "xml_reporte_jpivot_2110833.xml";
                String nombre = ruta + "xml_reporte_jpivot_7654664.xml";

		//get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
		
			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();
                        
			//parse the file and also register this class for call backs
			sp.parse(nombre, this);


		}
                catch(SAXException se)
                {
			se.printStackTrace();
		}
                catch(ParserConfigurationException pce)
                {
			pce.printStackTrace();
		}
                catch (IOException ie)
                {
			ie.printStackTrace();
		}
	}

	

	/**
	 * Iterate through the list and print
	 * the contents
	 */
	private void printData(){

		System.out.println("No of Employees '" + myEmpls.size() + "'.");

		Iterator it = myEmpls.iterator();
		while(it.hasNext()) {
			System.out.println(it.next().toString());
		}
	}


        //Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//reset
		tempVal = "";
		if(qName.equalsIgnoreCase("N_0"))
                {
                    for(int i = 0; i < attributes.getLength(); i++)
                    {
                            System.out.println("atributo: " + attributes.getQName(i));
                    }
		}
	}


	public void characters(char[] ch, int start, int length) throws SAXException
        {
		tempVal = new String(ch,start,length);
	}

	public void endElement(String uri, String localName, String qName) throws SAXException
        {

		if(qName.equalsIgnoreCase("N_0"))
                {
                        System.out.println(uri + " -- " + localName + " -- " + qName );

		}

	}

	public static void main(String[] args){
		SAXParserExample spe = new SAXParserExample();
		spe.runExample();
	}
	
}





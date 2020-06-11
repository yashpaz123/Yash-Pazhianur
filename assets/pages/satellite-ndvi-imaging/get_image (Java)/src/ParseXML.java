import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class ParseXML {
	
	// StAX XML Parser
	// Useful if the XML document is too large, and only a section is needed without loading the whole thing in RAM.
	public static ArrayList<String> parseGetURLs(String xml) {
		ArrayList<String> URLs = new ArrayList<String>();
		
		try {
			String text = xml;
			Reader reader = new StringReader(text);
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader xmlReader = factory.createXMLStreamReader(reader);
			XMLEventReader eventReader = factory.createXMLEventReader(xmlReader);

			while(eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				switch(event.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					StartElement startElement = event.asStartElement();
					String sName = startElement.getName().getLocalPart();
					if(sName.equals("link")) {
						Iterator<Attribute> attributes = startElement.getAttributes();
						String linkDat = attributes.next().getValue();
						if(linkDat.indexOf("https://") > -1 && linkDat.indexOf("/$value") > -1) {
							URLs.add(linkDat);
						}
					}
					break;
				} 
			}
		}
		catch(XMLStreamException e) {
			e.printStackTrace();
		}
		
		return URLs;
	}
}

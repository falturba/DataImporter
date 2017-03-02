package tools;
import java.io.IOException;
import java.io.File;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.SAXException;
import java.util.*;

class  XMLParser
{

	public static DataEntry parseDataXML(File file) throws ParserConfigurationException,SAXException,IOException
	{
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(file);
		document.getDocumentElement().normalize();
		Element connectionString = (Element)document.getElementsByTagName("connection-settings").item(0);
		String url = connectionString.getElementsByTagName("url").item(0).getTextContent();
		String driverClass = connectionString.getElementsByTagName("driverClassName").item(0).getTextContent();
		String username = connectionString.getElementsByTagName("username").item(0).getTextContent();
		String password = connectionString.getElementsByTagName("password").item(0).getTextContent();

		DataEntry dataEntry = new DataEntry(url,driverClass,username,password);

		Node xmlData = document.getElementsByTagName("data").item(0);
		if (xmlData.getNodeType() == Node.ELEMENT_NODE) 
		{
			Element eElement = (Element) xmlData;

			for(String table: Global.TABLES)
			{
				NodeList entries =  eElement.getElementsByTagName(table);
				for(int i =0;i<entries.getLength();i++)
				{
					Node entry = entries.item(i);
					NamedNodeMap attributes = entry.getAttributes();
					Map<String,String> record = new HashMap<String,String>();
					for(int a=0; a < attributes.getLength(); a++)
					{
						String[] keyAndValue = attributes.item(a).toString().split("=");
						record.put(keyAndValue[0],keyAndValue[1].replaceAll("^\"|\"$", ""));
					}
					dataEntry.addRecordEntry(record,table);
				}
			}
		}
		return dataEntry;
	}




	public static ImportSettings[] parseConfigXML(File config) throws ParserConfigurationException,SAXException,IOException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(config);
		document.getDocumentElement().normalize();
		NodeList nList = document.getElementsByTagName("import-setting");
		ImportSettings[] settings = new ImportSettings[nList.getLength()];
		for (int i = 0; i < nList.getLength(); i++)
		{
			Node nNode = nList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) 
			{
				Element eElement = (Element) nNode;
				settings[i] = new ImportSettings();
				settings[i].sourcePath =  eElement.getElementsByTagName("source-path").item(0).getTextContent();
				settings[i].successPath = eElement.getElementsByTagName("success-path").item(0).getTextContent();
				settings[i].errorPath = eElement.getElementsByTagName("error-path").item(0).getTextContent();
			}
		}
		return settings;
	}
}

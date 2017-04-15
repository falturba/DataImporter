package tools;
import org.xml.sax.SAXException;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import javax.xml.parsers.ParserConfigurationException;

public class DataImporter
{

	public static void main(String[] args) throws Exception
	{

		if(args.length!=1)
		{
			System.out.println("Wrong arguments, please enter a file path to the xml config file");
			System.exit(0);
		}
		
		ImportSettings[] settings = new ImportSettings[0];
		try
		{
			File file = new File(args[0]);
			settings = XMLParser.parseConfigXML(file);
		}catch(ParserConfigurationException|SAXException|IOException|NullPointerException ex)
		{
			System.out.println("\nError in the config file format\n");
			ex.printStackTrace();
			System.exit(0);
		}
		Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
		for(int s=0;s<settings.length;s++)
		{
			WatchThread watchThread = new WatchThread(settings[s]);
			watchThread.start();
		}

	}

}
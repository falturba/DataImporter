package tools;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Files;
import javax.xml.parsers.ParserConfigurationException;
import java.nio.file.StandardCopyOption;

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
		for(int s=0;s<settings.length;s++)
		{
			handlePreExistingFiles(settings[s]);
			WatchThread watchThread = new WatchThread(settings[s]);
			watchThread.start();
		}





	}
	static void handlePreExistingFiles(ImportSettings setting)
	{
		File sourcePath = new File(setting.sourcePath);
		File[] files = sourcePath.listFiles();
		for(File file : files)
		{
			DataFileHandlerResult res = DataFileHandler.handleDataFile(file);
			if(res.supportedFile)
			{
				String fileName = file.getName();
				if(res.insertionResult == InsertionResult.SUCCESS)
				{
					try
					{
						Files.move(Paths.get(setting.sourcePath+"/"+fileName), Paths.get(setting.successPath+"/"+fileName),StandardCopyOption.REPLACE_EXISTING);
					}catch(IOException e)
					{
						System.out.println("Error moving the file "+fileName+" from "+setting.sourcePath+" \n\n to \n\n"+setting.successPath);
						e.printStackTrace();
					}
					XMLReportGenerator.generateReport(res,setting.successPath);
				}
				else if(res.insertionResult == InsertionResult.PARTIALLY||res.insertionResult == InsertionResult.FAILED)
				{
					try
					{
						Files.move(Paths.get(setting.sourcePath+"/"+fileName), Paths.get(setting.errorPath+"/"+fileName),StandardCopyOption.REPLACE_EXISTING);
					}catch(IOException e)
					{
						System.out.println("Error moving the file "+fileName+" from \n\n"+setting.sourcePath+" \n\n to \n\n"+setting.errorPath+"\n\n");
						e.printStackTrace();
					}
					XMLReportGenerator.generateReport(res,setting.errorPath);
				}
			}
		}
	}

}
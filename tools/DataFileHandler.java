package tools;
import java.util.*;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.text.ParseException;
import javax.xml.parsers.ParserConfigurationException;
public class DataFileHandler
{
	public static void handleDataFile(File file,ImportSettings setting)
	{
		DataFileHandlerResult result = new DataFileHandlerResult();
		result.startTime = new Date();
		result.fileName  = file.getName();
		result.insertionResult = DataFileHandlerResult.InsertionResult.SUCCESS;
		String extension = result.fileName.substring(result.fileName.lastIndexOf(".") + 1, result.fileName.length());
		result.errors = new ArrayList<ErrorMessage>();
		DataEntry dataEntry = null;
		if(extension.equals("xml"))
		{
			System.out.println("XML File "+result.fileName+" is being handled");
			result.supportedFile = true;
			try
			{
				dataEntry = XMLParser.parseDataXML(file);
			}catch(ParserConfigurationException|SAXException|IOException|NullPointerException e)
			{
				result.insertionResult = DataFileHandlerResult.InsertionResult.FAILED;
				result.errors.add(new ErrorMessage("Error while parsing the xml file",ErrorMessage.ErrorType.PARSING_ERROR));
				result.endTime = new Date();
				
			}
		}else if(extension.equals("csv"))
		{
			System.out.println("CSV File "+result.fileName+" is being handled");
			result.supportedFile = true;
			try
			{
				dataEntry = CSVParser.parseDataCSV(file);
			}catch(ParseException|IOException e)
			{
				result.insertionResult = DataFileHandlerResult.InsertionResult.FAILED;
				result.errors.add(new ErrorMessage(e.getMessage(),ErrorMessage.ErrorType.PARSING_ERROR));
				result.endTime = new Date();
				e.printStackTrace();
			}
		}
		else
		{
			result.supportedFile = false;
		}
		if(dataEntry != null)
		{
			int numRecords = dataEntry.getLength();
			if(numRecords == 0) //There are no records to store in the database
			{
				result.insertionResult = DataFileHandlerResult.InsertionResult.SUCCESS;
				result.endTime = new Date();
			}
			else
			{
				int inserted = 0;
				if(dataEntry.getDriverClass().equals("com.mysql.jdbc.Driver"))
				{
					inserted = MySqlDriver.execute(dataEntry,result);
				}
				else if(dataEntry.getDriverClass().equals("mongodb.jdbc.MongoDriver"))
				{
					inserted = MongoDriver.execute(dataEntry,result);
				}
				else
				{
					result.insertionResult = DataFileHandlerResult.InsertionResult.FAILED;
					result.errors.add(new ErrorMessage("Driver is not supported",ErrorMessage.ErrorType.CONNECTION_ERROR));
					result.endTime = new Date();
				}
				if(inserted == 0)
					result.insertionResult = DataFileHandlerResult.InsertionResult.FAILED;
				else if(inserted < numRecords && inserted > 0) 
					result.insertionResult = DataFileHandlerResult.InsertionResult.PARTIALLY;
				else if(inserted == numRecords)
					result.insertionResult = DataFileHandlerResult.InsertionResult.SUCCESS;
				else throw new RuntimeException("Inserted records more than requested to insert!! or  inserted records is below zero!!");
			}
			
		}

		result.endTime = new Date();
		processFile(result,file.getName(),setting);
	}
	private static void processFile(DataFileHandlerResult res,String fileName,ImportSettings setting)
	{
		if(res.supportedFile)
		{
			if(res.insertionResult == DataFileHandlerResult.InsertionResult.SUCCESS)
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
			else if(res.insertionResult == DataFileHandlerResult.InsertionResult.PARTIALLY||res.insertionResult == DataFileHandlerResult.InsertionResult.FAILED)
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

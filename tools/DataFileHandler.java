package tools;
import java.util.*;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.File;
import javax.xml.parsers.ParserConfigurationException;

class DataFileHandler
{
	public static DataFileHandlerResult handleDataFile(File file)
	{
		DataFileHandlerResult result = new DataFileHandlerResult();
		result.startTime = new Date();
		result.fileName  = file.getName();
		result.insertionResult = InsertionResult.SUCCESS;
		String extension = result.fileName.substring(result.fileName.lastIndexOf(".") + 1, result.fileName.length());
		result.errors = new ArrayList<ErrorMessage>();
		DataEntry dataEntry = null;
		if(extension.equals("xml"))
		{
			result.supportedFile = true;
			try
			{
				dataEntry = XMLParser.parseDataXML(file);
			}catch(ParserConfigurationException|SAXException|IOException|NullPointerException e)
			{
				result.insertionResult = InsertionResult.FAILED;
				result.errors.add(new ErrorMessage("Error while parsing the xml file",ErrorType.PARSING_ERROR));
				result.endTime = new Date();
				return result;
			}
		}else if(extension.equals("csv"))
		{
			result.supportedFile = true;
		}
		else
		{
			result.supportedFile = false;
		}
		if(dataEntry != null)
		{
			if(dataEntry.getDriverClass().equals("com.mysql.jdbc.Driver"))
			{
				int numRecords = dataEntry.getLength();
				if(numRecords == 0)
				{
					result.insertionResult = InsertionResult.SUCCESS;
					result.endTime = new Date();
					return result;
				}
				int inserted = MySqlDriver.execute(dataEntry,result);
				if(inserted == 0)
					result.insertionResult = InsertionResult.FAILED;
				else if(inserted < numRecords && inserted > 0) 
					result.insertionResult = InsertionResult.PARTIALLY;
				else if(inserted == numRecords)
					result.insertionResult = InsertionResult.SUCCESS;
				else throw new RuntimeException("Inserted records more than requested to insert!! or  inserted records is below zero!!");
			}
		}

		result.endTime = new Date();
		return result;
	}


}

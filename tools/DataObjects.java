package tools;
import java.util.*;
 class DataFileHandlerResult
{
	String fileName;
	InsertionResult insertionResult;
	Date startTime = new Date();
	Date endTime = new Date();
	List<ErrorMessage> errors;
	boolean supportedFile;
}
enum InsertionResult
{
	SUCCESS,
	PARTIALLY,
	FAILED
}

 class ImportSettings
{
	public String sourcePath;
	public String successPath;
	public String errorPath;
}

class ErrorMessage
{
	RecordEntry recordEntry; //if there's any record associated with the error
	String message;
	ErrorType type;
	public ErrorMessage(RecordEntry recordEntry,String message,ErrorType type)
	{
		this.recordEntry = recordEntry;
		this.message = message;
		this.type = type;
	}
	public ErrorMessage(String message,ErrorType type)
	{
		this.recordEntry = null;
		this.message = message;
		this.type = type;
	}
}

enum ErrorType
{
	DATA_ERROR,
	CONNECTION_ERROR,
	PARSING_ERROR
}

package tools;
public class ErrorMessage
{
	enum ErrorType
	{
		DATA_ERROR,
		CONNECTION_ERROR,
		PARSING_ERROR
	}
	DataEntry.RecordEntry recordEntry; //if there's any record associated with the error
	String message;
	ErrorType type;
	public ErrorMessage(DataEntry.RecordEntry recordEntry,String message,ErrorType type)
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

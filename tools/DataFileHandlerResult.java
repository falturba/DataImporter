package tools;
import java.util.*;
public class DataFileHandlerResult
{
	 public enum InsertionResult
	{
		SUCCESS,
		PARTIALLY,
		FAILED
	}
	String fileName;
	InsertionResult insertionResult;
	Date startTime = new Date();
	Date endTime = new Date();
	List<ErrorMessage> errors;
	boolean supportedFile;
}

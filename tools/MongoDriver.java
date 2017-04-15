package tools;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.MongoInternalException;
import com.mongodb.MongoServerException;
import com.mongodb.MongoSocketException;
import com.mongodb.MongoSocketOpenException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.*;
import java.util.*;
import org.bson.Document;
public class MongoDriver
{


	public static int execute(DataEntry dataEntry,DataFileHandlerResult result)
	{
		int inserted = 0;
		int lastSlash = dataEntry.getUrl().lastIndexOf("/");
		String url = dataEntry.getUrl().substring(0,lastSlash);
		String db = dataEntry.getUrl().substring(lastSlash+1);
		MongoClient mongoClient = null;
		try{
		MongoClientURI mongoUri = new MongoClientURI(url,MongoClientOptions.builder().socketTimeout(5).connectTimeout(5));
	    mongoClient = new MongoClient(mongoUri);
 		MongoDatabase database = mongoClient.getDatabase(db);
		
 		for(DataEntry.RecordEntry recordEntry: dataEntry.getRecordEntries())
			{
				MongoCollection<Document> collection = database.getCollection(recordEntry.getTable());
				Map<String,String> record = recordEntry.getRecord();
				Document doc = new Document();
				for(String col:record.keySet())
				{
					doc.append(col,record.get(col));
				}
				try{
				collection.insertOne(doc);
				inserted++;
				}catch(MongoWriteException|MongoWriteConcernException e)
				{
					result.errors.add(new ErrorMessage(recordEntry,e.getMessage(),ErrorMessage.ErrorType.DATA_ERROR));
				}catch(MongoSocketOpenException e)
				{
					result.endTime = new Date();
					result.errors.add(new ErrorMessage(e.getMessage(),ErrorMessage.ErrorType.CONNECTION_ERROR));
					return inserted;
				}
			}
		} catch (MongoSocketException e) {
			result.endTime = new Date();
			result.errors.add(new ErrorMessage(e.getMessage(),ErrorMessage.ErrorType.CONNECTION_ERROR));
			return 0;
		}catch(MongoInternalException|MongoServerException e)
		{
			result.endTime = new Date();
			result.errors.add(new ErrorMessage(e.getMessage(),ErrorMessage.ErrorType.CONNECTION_ERROR));
			return 0;
		}catch(MongoException e)
		{
			result.endTime = new Date();
			result.errors.add(new ErrorMessage(e.getMessage(),ErrorMessage.ErrorType.CONNECTION_ERROR));
			return 0;
		}catch(NullPointerException e)
		{
			result.endTime = new Date();
			result.errors.add(new ErrorMessage(e.getMessage(),ErrorMessage.ErrorType.CONNECTION_ERROR));
			return 0;
		}catch(Exception e)
		{
			result.endTime = new Date();
			result.errors.add(new ErrorMessage(e.getMessage(),ErrorMessage.ErrorType.CONNECTION_ERROR));
			return 0;
		}
		finally{
			if(mongoClient != null)
			mongoClient.close();
			
		}
		
		return inserted;

			
		
	}
}

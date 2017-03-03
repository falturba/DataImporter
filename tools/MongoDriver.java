package tools;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.*;
import com.mongodb.DBCollection;
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
 		MongoClientURI mongoUri = new MongoClientURI(url);
 		MongoClient mongoClient = new MongoClient(mongoUri);
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
				collection.insertOne(doc);
				inserted++;
			}
		return inserted;
	}
}

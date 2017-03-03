package tools;
import java.util.*;
public class DataEntry
{
	public class RecordEntry
	{
		private Map<String,String> record;
		private String table;
		public RecordEntry(Map<String,String> record,String table)
		{
			this.record = record;
			this.table = table;
		}
		public Map<String,String> getRecord()
		{
			return record;
		}
		public String getTable()
		{
			return table;
		}
	}
	private String url;
	private String username;
	private String password;
	private String driverClass;
	private int length;
	private List<RecordEntry> recordEntries;
	public DataEntry(String url,String driverclass,String username,String password)
	{
		this.url = url;
		this.driverClass = driverclass;
		this.username = username;
		this.password = password;
		this.length = 0;
		recordEntries = new ArrayList<RecordEntry>();
	}
	public String getUrl()
	{
		return this.url;
	}
	public String getDriverClass()
	{
		return this.driverClass;
	}
	public String getUsername()
	{
		return this.username;
	}
	public String getPassword()
	{
		return this.password;
	}
	public void addRecordEntry(Map<String,String> record,String table)
	{
		recordEntries.add(new RecordEntry(record,table));
		length++;
	}
	public RecordEntry getRecordEntry(int index)
	{
		return recordEntries.get(index);
	}
	public List<RecordEntry> getRecordEntries()
	{
		return recordEntries;
	}
	public int getLength()
	{
		return length;
	}
}


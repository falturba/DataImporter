package tools;
import java.io.*;
import java.util.*;
import java.text.ParseException;

public class CSVParser
{
	public static DataEntry parseDataCSV(File file) throws ParseException,IOException
	{
		try
		{
		int lineNumber = 0; //Used to track parsing error location
		BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
		
			String [] connection = br.readLine().split(","); lineNumber++;

			if(connection.length < 4)
			{
				throw new ParseException("The connection values are missing",lineNumber);
			}
			DataEntry dataEntry = new DataEntry(connection[2],connection[3],connection[0],connection[1]);
			String table="";
			Map<String,String> record = new HashMap<String,String>();
			List<String> columns = new ArrayList<String>();
			String line = "";
			while ((line = br.readLine()) != null) 
			{
				lineNumber++;
				String[] values = line.split(",");
				if(values[0].contains("table#"))
				{
					if(!table.equals(""))
					{
						dataEntry.addRecordEntry(record,table);
						record.clear();
						columns.clear();
					}
					table = values[0].split("#")[1];
					for (int i=1;i<values.length;i++)
					{
						columns.add(values[i]);
					}
				}
				else
				{
					if(values.length != columns.size())
					{
						System.out.println("Line "+lineNumber);
						System.out.println(values.length);
						System.out.println(columns.size());
						throw new ParseException("The number of values don't equal the number of columns",lineNumber);
					}
					for(int i=0;i<values.length;i++)
					{
						record.put(columns.get(i),values[i]);
					}
				}
			}
			return dataEntry;
			
		}finally{
			br.close();
			}
		
			
	}
}



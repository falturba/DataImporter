package tools;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
public class MySqlDriver
{
	public static int execute(DataEntry dataEntry,DataFileHandlerResult result)
	{
		try
		{
			Class.forName(dataEntry.getDriverClass());
		}catch(ClassNotFoundException e)
		{
			System.out.println("Driver Class NotFound");
			result.errors.add(new ErrorMessage(e.getMessage(),ErrorMessage.ErrorType.CONNECTION_ERROR));
			result.endTime = new Date();
			return 0;
		}
		Connection con = null;
        Statement stmt = null;
        int inserted = 0;
        String url = dataEntry.getUrl()+"?autoReconnect=true&useSSL=false";
        String user = dataEntry.getUsername();;
        String password = dataEntry.getPassword();
		try {
			try
			{
				con = DriverManager.getConnection(url, user, password);
				stmt = con.createStatement();
			}catch(SQLNonTransientConnectionException e)
			{
				System.out.println(e.getMessage());
				result.errors.add(new ErrorMessage("The database doesn't reponse",ErrorMessage.ErrorType.CONNECTION_ERROR));
				result.endTime = new Date();
				return 0;
			
			}catch(SQLException e)
			{
				System.out.println(e.getMessage());
				result.errors.add(new ErrorMessage("Failed to connect to the database",ErrorMessage.ErrorType.CONNECTION_ERROR));
				result.endTime = new Date();
				return 0;
			}
			for(DataEntry.RecordEntry recordEntry: dataEntry.getRecordEntries())
			{
				Map<String,String> record = recordEntry.getRecord();
				StringBuilder coulmnsNames = new StringBuilder();
				StringBuilder values = new StringBuilder();
				String prefix = "";
				for(String col:record.keySet())
				{
					coulmnsNames.append(prefix);
					values.append(prefix);
					prefix = ",";
					coulmnsNames.append(col);
					values.append("'"+ record.get(col)+"\'");
				}
				 try
				 {
					inserted += stmt.executeUpdate("INSERT INTO "+recordEntry.getTable()+" ("+coulmnsNames.toString()+") VALUES ("+values.toString()+")");
			 	}catch(SQLException e) 
			 	{
			 		result.errors.add(new ErrorMessage(recordEntry,e.getMessage(),ErrorMessage.ErrorType.DATA_ERROR));
			 	}
			}

		} finally {

			if (stmt != null)
			{
				try 
				{
					stmt.close();
				} catch (SQLException sqlex) {}
				stmt = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException sqlex) {}
				con = null;
			}
		}
		return inserted;
	}
}

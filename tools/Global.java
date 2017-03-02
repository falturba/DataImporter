package tools;
final class Global
{
	private static String CURRENT_DIR = System.getProperty("user.dir");
	public static String[] TABLES = {"departments","employees"};
	public static String getCurrentDir()
	{
		return CURRENT_DIR;
	}

}
package tools;
import java.nio.file.*;
import java.io.File;
import java.io.*;
import tools.MySqlDriver;
import static java.nio.file.StandardWatchEventKinds.*;
public class WatchThread extends Thread
{
	ImportSettings setting;
	WatchService watcher;
	public WatchThread(ImportSettings setting)
	{
		this.setting = setting;
		Path path = Paths.get(setting.sourcePath);
		if(path != null)
		{
			try
			{
				watcher =  path.getFileSystem().newWatchService();
				path.register(watcher,ENTRY_CREATE,ENTRY_MODIFY);
			}catch (IOException e)
			{
				System.out.println("Error while watching the directory:\n");
				e.getMessage();
			}
		}
		else
		{
			throw new UnsupportedOperationException("Watch directory "+setting.sourcePath+" not found");
		}
	}
	@Override
	public void run()
	{
		WatchKey watchKey = null;
		System.out.println("Directory is being watched: "+setting.sourcePath);
		while (true)
		{
			File sourcePath = new File(setting.sourcePath);
			File[] files = sourcePath.listFiles();
			for(File file : files)
			{
				processFile(file.getName());
			}
			try
			{
				System.out.println("Watcher is waiting for an event ...");
				watchKey = watcher.take();
			} catch(InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
			
			if(watchKey != null) 
			{
				for (WatchEvent<?> event: watchKey.pollEvents())
				{

					WatchEvent.Kind<?> kind = event.kind();
					if (kind == OVERFLOW)
					{
						continue;
					}
					@SuppressWarnings("unchecked")
					WatchEvent<Path> ev = (WatchEvent<Path>)event;
					processFile(ev.context().toString());

				}
			}
			watchKey.reset();
		}

	}
	void processFile(String fileName)
	{
		
		File dataFile = new File(this.setting.sourcePath+"/"+fileName);
		DataFileHandlerResult res = DataFileHandler.handleDataFile(dataFile);
		if(res.supportedFile)
		{
			if(res.insertionResult == DataFileHandlerResult.InsertionResult.SUCCESS)
			{
				try
				{
					Files.move(Paths.get(setting.sourcePath+"/"+fileName), Paths.get(setting.successPath+"/"+fileName),StandardCopyOption.REPLACE_EXISTING);
				}catch(IOException e)
				{
					System.out.println("Error moving the file "+fileName+" from "+setting.sourcePath+" \n\n to \n\n"+setting.successPath);
					e.printStackTrace();
				}
				XMLReportGenerator.generateReport(res,setting.successPath);
			}
			else if(res.insertionResult == DataFileHandlerResult.InsertionResult.PARTIALLY||res.insertionResult == DataFileHandlerResult.InsertionResult.FAILED)
			{
				try
				{
					Files.move(Paths.get(setting.sourcePath+"/"+fileName), Paths.get(setting.errorPath+"/"+fileName),StandardCopyOption.REPLACE_EXISTING);
				}catch(IOException e)
				{
					System.out.println("Error moving the file "+fileName+" from \n\n"+setting.sourcePath+" \n\n to \n\n"+setting.errorPath+"\n\n");
					e.printStackTrace();
				}
				XMLReportGenerator.generateReport(res,setting.errorPath);
			}
		}
		
}

}
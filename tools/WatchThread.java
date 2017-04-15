package tools;
import java.nio.file.*;
import java.io.File;
import java.io.*;
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
				System.out.print(e.getMessage());
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
				DataFileHandler.handleDataFile(file,setting);  //handle preexisting files
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
					File dataFile = new File(this.setting.sourcePath+"/"+ev.context());
				    DataFileHandler.handleDataFile(dataFile,setting);

				}
			}
			watchKey.reset();
		}

	}

}
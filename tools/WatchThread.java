package tools;
import java.nio.file.*;
import java.io.File;
import java.io.*;
import tools.MySqlDriver;
import static java.nio.file.StandardWatchEventKinds.*;
class WatchThread extends Thread
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
				path.register(watcher, ENTRY_CREATE);
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
	private void handleEvent(String fileName)
	{
		File dataFile = new File(this.setting.sourcePath+"/"+fileName);
		DataFileHandlerResult res = DataFileHandler.handleDataFile(dataFile);
		if(res.supportedFile)
			{
				if(res.insertionResult == InsertionResult.SUCCESS)
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
				else if(res.insertionResult == InsertionResult.PARTIALLY||res.insertionResult == InsertionResult.FAILED)
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
	@Override
	public void run()
	{
		WatchKey watchKey = null;
		System.out.println("Directory is being watched: "+setting.sourcePath);
		while (true)
		{
			try
			{
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
					handleEvent(ev.context().toString());

				}
			}
			watchKey.reset();
		}

	}

}
# DataImporter

This is a tool to observce multiaple directories. Fetch any new file (CML or CSV) and insert all the data into either (MySQL or MongoDB).
It's scalable to support different kind of files and databases. I will generate a report with all the results:


Functions:

* Read the config file and watch source directories, every watcher is a new thread.
* Parse XML and CSV files.
* Insert the data in either MySQL or MongoDB database.
* Move the files to either success or error folder based on the final result (success, partially, or failed).
* Generate an XML report with a postfix (-report), and include the error message and each record has been failed to be inserted.
* I divided the functionality into separate classes so it would be easier to scale.


To run the tool:

1) make sure to export the jar dependcies in the CLASSPATH, or add them in your IDE.

    To add them in your CLASSPATH:
      export CLASSPATH=.:/"FULL PATH"/mongo-java-driver-3.4.2.jar:/"FULL PATH"/mysql-connector-java-5.1.41-bin.jar
      
      To read more about how to set a CLASSPATH -> http://docs.oracle.com/javase/7/docs/technotes/tools/windows/classpath.html
      
2) Add your import settings in the file importer-settings.xml:
      
      <import-setting>
		<source-path>"The directory you want to watch"</source-path>
		<success-path>"Inserted files successfully will be moved to this directory along with a generated report"</success-path>
		<error-path>"Partially inserted or failed to insert along with a report with each row or document failed to be inserted"</error-path>
</import-setting>

3) Then simply run these commands
      javac tools/*.java
      java tools.DataImporter importer-settings.xml
      

Copyright:
    This tool was made for a learning purpose. Please feel free to use it, modify it, or take snippets of code

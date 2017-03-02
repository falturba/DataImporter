package tools;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import java.text.SimpleDateFormat;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.Map;

class XMLReportGenerator
{
	public static void generateReport(DataFileHandlerResult res,String path)
	{
		SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("import-report");
			doc.appendChild(rootElement);
			Attr xmlns = doc.createAttribute("xmlns");
			xmlns.setValue("http://www.phi01tech.com/tools/data-import");
			rootElement.setAttributeNode(xmlns);
			Attr xsi = doc.createAttribute("xmlns:xsi");
			xsi.setValue("http://www.w3.org/2001/XMLSchema-instance");
			rootElement.setAttributeNode(xsi);
			Attr schemaLocation = doc.createAttribute("xsi:schemaLocation");
			schemaLocation.setValue("http://www.phi01tech.com/tools/data-import ./data-import.xsd");
			rootElement.setAttributeNode(schemaLocation);

		// staff elements
			Element resultElement = doc.createElement("result");
			resultElement.setTextContent(res.insertionResult.toString());
			rootElement.appendChild(resultElement);

			Element startTime = doc.createElement("start-time");
			startTime.setTextContent(dtf.format(res.startTime).replace(" ","T"));
			rootElement.appendChild(startTime);

			Element endTime = doc.createElement("end-time");
			endTime.setTextContent(dtf.format(res.endTime).replace(" ","T"));
			rootElement.appendChild(endTime);

			if(res.errors.size()>0)
			{
				Element errorsElement = doc.createElement("errors");
				rootElement.appendChild(errorsElement);
				for(ErrorMessage error:res.errors)
				{
					Element errorElement = doc.createElement(error.type.toString());
					errorsElement.appendChild(errorElement);
					Element messageElement = doc.createElement("error-message");
					messageElement.setTextContent(error.message);
					errorElement.appendChild(messageElement);
					if(error.type == ErrorType.DATA_ERROR)
					{
						Element dataRecordElement = doc.createElement("data-record");
						errorElement.appendChild(dataRecordElement);
						String table = error.recordEntry.getTable();
						Element recElement = doc.createElement(table);
						dataRecordElement.appendChild(recElement);
						Map<String,String> record = error.recordEntry.getRecord();
						for(String col:record.keySet())
						{
							Attr colAttr = doc.createAttribute(col);
							colAttr.setValue(record.get(col));
							recElement.setAttributeNode(colAttr);
						}
					}
				}
			}
		// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			String reportName = res.fileName.replace(".","-report.");
			StreamResult streamRes = new StreamResult(new File(path+"/"+reportName));
			transformer.transform(source, streamRes);
			System.out.println("Report "+reportName+" has been saved in "+path);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
}
package edu.jhu.cvrg.analysis.wrapper.chesnokov;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.XSLTransformException;
import org.jdom.transform.XSLTransformer;

import edu.jhu.cvrg.analysis.util.AnalysisExecutionException;
import edu.jhu.cvrg.analysis.util.AnalysisParameterException;
import edu.jhu.cvrg.analysis.util.AnalysisUtils;
import edu.jhu.cvrg.analysis.vo.AnalysisResultType;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.analysis.wrapper.AnalysisWrapper;
import edu.jhu.cvrg.analysis.wrapper.ApplicationWrapper;
import edu.jhu.icm.ecgFormatConverter.ECGformatConverter;

public class ChesnokovAnalysis extends ApplicationWrapper {
	
	private static final String WINE_COMMAND = "wine";
	private static final String CHESNOKOV_COMMAND = "opt/autoqrs/Release/ecg.exe";
	private static final String CHESNOKOV_FILTERS = "opt/autoqrs/Release/filters/";
	
	private String inputFile; 
	private String path; 
	private String outputFile;
	
	private boolean acceptableFormat=false;
	private List<String> signalNameList;
	
	public ChesnokovAnalysis(AnalysisVO vo) {
		super(vo);
	}
	
	@Override
	protected void _defineInputParameters() throws AnalysisParameterException {
		log.info("Test point A"); 
		String sDatPathName = AnalysisUtils.findPathNameExt(this.getAnalysisVO().getFileNames(), "dat");
		log.info("Test point B");
		path = AnalysisUtils.extractPath(sDatPathName);
		log.info("Test point C");
		inputFile = AnalysisUtils.extractName(sDatPathName);
		log.info("Test point D");
		
		log.info("physionetAnalysisService.PhysionetExecute - sDatName: " + inputFile);
		log.info("physionetAnalysisService.PhysionetExecute - sDatPath: " + path);
		log.info("physionetAnalysisService.PhysionetExecute - sDatPathName: " + sDatPathName);

		int iIndexPeriod = inputFile.lastIndexOf(".");
		outputFile = inputFile.substring(0, iIndexPeriod) + '_' + this.getAnalysisVO().getJobIdNumber();  // This will become the name of the CSV file
	}

	@Override
	protected void _execute() throws AnalysisExecutionException {
		boolean bRet = true;
		log.info("---------------------------");
		log.info("chesnokov()");
		log.info("- inputFile:" + inputFile);
		log.info("- path:" + path);
		log.info("- outputName:" + outputFile);
		// no environment variables are needed, 
		// this is a place keeper so that the three parameter version of
		// exec can be used to specify the working directory.
		String[] envVar = new String[0];  

		try{
			
			populateSignalNameList( inputFile,  envVar, path);
			testFormat( inputFile,  envVar, path);
			
			if(!acceptableFormat){
				log.error("- bAcceptableFormat: false");
				boolean reformat = reformatRecordToF16( inputFile,  path);
				if (reformat) acceptableFormat=true;
			}
			
			int folderLevel = path.split("/").length - 1 /* /opt/ */;
			String folderUp = "";
			for (int i = 0; i < folderLevel; i++) {
				folderUp+="../";
			}
			
			// execute Chesnokov analysis.
			String chesnokovOutputFilenameXml = inputFile.substring(0, inputFile.lastIndexOf(".") + 1) + "xml";

			String command = WINE_COMMAND + " " + folderUp + CHESNOKOV_COMMAND + " " + folderUp + CHESNOKOV_FILTERS + " " + inputFile + " " + chesnokovOutputFilenameXml; // add parameters for "input file" and "output file"

			bRet = executeCommand(command, envVar, path);
			
			String stdReturn = stdReturnHandler();
			log.info("stdReturn returned: " + stdReturn);
			if(!stdReturn.contains("lead:")){
				bRet=false;
				log.error("<ERROR>-- chesnokovV1() - sCommand:" + command);
				this.getAnalysisVO().setErrorMessage(this.getAnalysisVO().getErrorMessage() + "; " + stdReturn);
			}
			
			boolean stdError = stdErrorHandler();
			log.error("stdError returned: " + stdError);

			String chesnokovCSVFilepath="";
			if(bRet){
				
				switch (this.getAnalysisVO().getResultType()) {
				case CSV_FILE:
				case ORIGINAL_FILE:
					log.info("calling chesnokovToCSV(chesnokovOutputFilename)");
					chesnokovCSVFilepath = chesnokovToCSV(path + File.separator + chesnokovOutputFilenameXml, path + File.separator + inputFile, outputFile, path);
					
					File csvFile = new File(chesnokovCSVFilepath);
					bRet = csvFile.exists();
					if(bRet){
						AnalysisUtils.deleteFile(path, chesnokovOutputFilenameXml);
					
						List<String> outputFilenames = new ArrayList<String>();
						log.info("- CSV Output Name: " + chesnokovCSVFilepath);
						outputFilenames.add(chesnokovCSVFilepath);
						this.getAnalysisVO().setOutputFileNames(outputFilenames);
					}else{
						log.error("CSV file not found");
						throw new AnalysisExecutionException("CSV file not found");
					}
					break;
					
				case JSON_DATA:
					String jsonData = null;
					log.info("calling chesnokovToJSON(chesnokovOutputFilename)");
					jsonData = chesnokovToJSON(path + File.separator + chesnokovOutputFilenameXml, path + File.separator + inputFile, outputFile, path);
					
					AnalysisUtils.deleteFile(path, chesnokovOutputFilenameXml);
					
					this.getAnalysisVO().setOutputData(jsonData);
					this.getAnalysisVO().setOutputFileNames(null);
					
					break;
				}
			}else{
				log.error("Command execution error. ["+ command+"]");
				throw new AnalysisExecutionException("Command execution error. ["+ command+"]");
			}
		} catch (IOException e) {
			log.error("Error on "+this.getAnalysisVO().getType()+" command output handling");
			throw new AnalysisExecutionException("Error on "+this.getAnalysisVO().getType()+" command output handling", e);
		}finally{
			AnalysisUtils.deleteFile(path, "annotations.txt");
		}
		
		this.getAnalysisVO().setSucess(bRet);

	}

	/** populates the variable signalNameList using the Physionet library program "signame"
	 * 
	 * @param inputFile
	 * @param envVar
	 * @param path
	 * @throws AnalysisExecutionException 
	 */
	private void populateSignalNameList(String inputFile, String[] envVar, String path) throws AnalysisExecutionException{
	    try{ 
			String recordName = inputFile.substring(0, inputFile.indexOf("."));
			String command = "signame -r " + path + AnalysisUtils.sep + recordName;
			boolean bNoException = executeCommand(command, envVar, AnalysisWrapper.WORKING_DIR);
			log.info("- bNoException:"+ bNoException);
			
			String tempLine = "";
			int lineNumber=0;
			signalNameList = new ArrayList<String>();
		    while ((tempLine = stdInputBuffer.readLine()) != null) {
		    	if (lineNumber<12){
		    		debugPrintln("signame(); " + lineNumber + ")" + tempLine);
		    	}
		    	signalNameList.add(tempLine);
		    	lineNumber++;
		    }
			this.stdErrorHandler();
		
		} catch (IOException ioe) {
			throw new AnalysisExecutionException("Signame read data error", ioe);
		} catch (Exception e) {
			throw new AnalysisExecutionException("Signame error", e);
		}
	}
	
	
	/** Test whether the input Waveform Database format is acceptable to the Chesnokov C++ code, specifically Format 16 and Format 212.
	 *  
	 * @param inputFile  - file name of the WFDB (.hea) header file of the record to analyze.
	 * @param path - FULL path of the header file.
	 * 
	 * @return - Sets class variable "bAcceptableFormat" to true if WFDB Format 16 or Format 212 false for all others.
	 */
	private void testFormat(String inputFile, String[] envVar, String path){
		
	    try{ 
			String recordName = inputFile.substring(0, inputFile.indexOf("."));
			String command = "wfdbdesc " + path + AnalysisUtils.sep + recordName;
			boolean bNoException = executeCommand(command, envVar, AnalysisWrapper.WORKING_DIR);
			log.info("- bNoException:"+ bNoException);
			
			stdReturnMethodHandler();
			this.stdErrorHandler();
		
		} catch (IOException ioe) {
			log.error("IOException Message: wfdbdesc " + ioe.getMessage());
			ioe.printStackTrace();
		} catch (Exception e) {
			System.err.println("Exception Message: wfdbdesc " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	protected void processReturnLine(String line) {
		if(line.contains("Storage format: 16")){
			log.info("- found Format 16");
			acceptableFormat=true;
		}
		if(line.contains("Storage format: 212")){
			log.info("- found Format 212");
			acceptableFormat=true;
		}
	}
	
	private boolean reformatRecordToF16(String inputFile,  String path){
		log.info("converting to WFDB format 16");
		boolean success = true;
		try{
			ECGformatConverter con= new ECGformatConverter();
			int signalsRequested = 0; // zero requests all signals found.
			
			int rowsWritten = con.convert(ECGformatConverter.fileFormat.WFDB, ECGformatConverter.fileFormat.WFDB_16, inputFile, signalsRequested, path, path);
			log.info("Rows written in WFDB format 16:" + rowsWritten);
			if (rowsWritten <= 0) success = false;
		}catch(Exception e){
			log.error("reformat failed: " + e.getMessage());
			success = false;
		}
		return success;
	}
	
	/** Converts the Chesnokov output file (XML) into a CSV format.
	 * 
	 * @param fileName - Chesnokov output file (XML) 
	 * @param fileAnalyzedTempName
	 * @param outputFileName
	 * @param outputPath 
	 * @return
	 */
	private String chesnokovToCSV(String chesnokovFilename, String fileAnalyzedTempName, String outputFileName, String outputPath) {
		
		String xhtml = null;
        String csvOutputFilename = "";
        debugPrintln(" ** converting to CSV " + chesnokovFilename);
   		
		xhtml = extractXmlData(chesnokovFilename, fileAnalyzedTempName, outputFileName);
		
		csvOutputFilename = outputPath + outputFileName + ".csv";
		
		debugPrintln(" ** writing " + csvOutputFilename);
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(csvOutputFilename));
			out.write(xhtml);
			out.close();
		} catch (IOException e) {
			new AnalysisExecutionException("XML file Writing error", e);
		}
		   
   		return csvOutputFilename;
	}

	private String extractXmlData(String chesnokovFilename,	String fileAnalyzedTempName, String outputFileName) {
		String xhtml = null;
		String[] chesSigalNameArray = {"I","II","III","aVR","aVL","aVF","v1","v2","v3","v4","v5","v6"};
		
		if(signalNameList.size() > 12){
			chesSigalNameArray = new String[]{"I","II","III","aVR","aVL","aVF","v1","v2","v3","v4","v5","v6", "vx", "vy", "vz"};
		}
		
		Document xmlDoc = null;
		Document transformed = null;
		InputStream xsltIS = null;
		XSLTransformer xslTransformer = null;
		String row = null;
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader in = new BufferedReader(new FileReader(chesnokovFilename));
			
			while((row = in.readLine()) != null) {
				if(row.indexOf("<autoQRSResults") != -1) {
			    	sb.append("<autoQRSResults>");
				} else {
			    	sb.append(row);
				}
			}
			in.close();
			String str = sb.toString();
			for(int s=signalNameList.size()-1;s>=0;s--){ //replace the signal names Chesnokov uses with the ones found by the Physionet signame program.
				str = str.replace("<Lead>" + chesSigalNameArray[s] + "</Lead>", "<Lead>" + signalNameList.get(s) + "</Lead>");
			}
			xmlDoc = build(str);
			if(AnalysisResultType.JSON_DATA.equals(this.getAnalysisVO().getResultType())){
				xsltIS = this.getClass().getResourceAsStream("/chesnokov_json_datatable.xsl");	
			}else{
				xsltIS = this.getClass().getResourceAsStream("/chesnokov_datatable.xsl");
			}
			xslTransformer = new XSLTransformer(xsltIS);
			transformed = xslTransformer.transform(xmlDoc);
			xhtml = getString(transformed);
			debugPrintln(" ** xslTransformation completed using: " + xsltIS.toString());
			int startTruncPosition = xhtml.indexOf("<html>") + 6;
			int endTruncPosition = xhtml.indexOf("</html>");
			
			xhtml = xhtml.substring(startTruncPosition, endTruncPosition);
			debugPrintln(" ** replacing : " + fileAnalyzedTempName + " with: " + outputFileName);
			xhtml = xhtml.replaceAll(fileAnalyzedTempName, outputFileName);

		} catch (FileNotFoundException e) {
			new AnalysisExecutionException("XML file not found", e);
		} catch (XSLTransformException e) {
			new AnalysisExecutionException("XSLT tranformer error", e);
		} catch (IOException e) {
			new AnalysisExecutionException("XML reading error", e);
		} catch (JDOMException e) {
			new AnalysisExecutionException("XML object creaton error", e);
		}

		
		return xhtml;
	}	
	
	private String chesnokovToJSON(String chesnokovFilename, String fileAnalyzedTempName, String outputFileName, String outputPath) {
		String xhtml = null;
        debugPrintln(" ** converting to JSON " + chesnokovFilename);
   		
		xhtml = extractXmlData(chesnokovFilename, fileAnalyzedTempName, outputFileName);
		xhtml = xhtml.replaceFirst("},]}", "}]}");
     
		return xhtml;
	}	
	
	/**
     * Helper method to build a <code>jdom.org.Document</code> from an 
     * XML document represented as a String
     * @param  xmlDocAsString  <code>String</code> representation of an XML
     *         document with a document declaration.
     *         e.g., <?xml version="1.0" encoding="UTF-8"?>
     *                  <root><stuff>Some stuff</stuff></root>
     * @return Document from an XML document represented as a String
     */
    private Document build(String xmlDocAsString) throws JDOMException {
    	Document doc = null;
        SAXBuilder builder = new SAXBuilder();
        Reader stringreader = new StringReader(xmlDocAsString);
        try {
        	doc = builder.build(stringreader);
        } catch(IOException ioex) {
        	ioex.printStackTrace();
        }
        return doc;
    }
    
    /**
     * Helper method to generate a String output of a
     * <code>org.jdom.Document</code>
     * @param  xmlDoc  Document XML document to be converted to String
     * @return <code>String</code> representation of an XML
     *         document with a document declaration.
     *         e.g., <?xml version="1.0" encoding="UTF-8"?>
     *                  <root><stuff>Some stuff</stuff></root>
     */
    private String getString(Document xmlDoc) throws JDOMException {
        try {
             XMLOutputter xmlOut = new XMLOutputter();
             StringWriter stringwriter = new StringWriter();
             xmlOut.output(xmlDoc, stringwriter);
    
             return stringwriter.toString();
        } catch (Exception ex) {
            throw new JDOMException("Error converting Document to String"+ ex);
        }
    }

	
}

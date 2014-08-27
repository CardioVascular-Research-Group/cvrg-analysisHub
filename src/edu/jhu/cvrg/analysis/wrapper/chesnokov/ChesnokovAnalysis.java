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

import edu.jhu.cvrg.analysis.util.AnalysisUtils;
import edu.jhu.cvrg.analysis.util.ServiceProperties;
import edu.jhu.cvrg.analysis.vo.AnalysisResultType;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.analysis.wrapper.AnalysisWrapper;
import edu.jhu.cvrg.analysis.wrapper.ApplicationWrapper;
import edu.jhu.cvrg.converter.ECGformatConverter;
import edu.jhu.cvrg.converter.vo.ECGFormat;

public class ChesnokovAnalysis extends ApplicationWrapper {
	
	
	private String inputFile; 
	private String path; 
	private String outputFile;
	
	private boolean acceptableFormat=false;
	private List<String> signalNameList;
	
	public ChesnokovAnalysis(AnalysisVO vo) {
		super(vo);
	}
	
	@Override
	public void defineInputParameters() {
		
		//*** The analysis algorithm should return a String array containing the full path/names of the result files.
		String sDatPathName = AnalysisUtils.findPathNameExt(this.getAnalysisVO().getFileNames(), "dat");
		path = AnalysisUtils.extractPath(sDatPathName);
		inputFile = AnalysisUtils.extractName(sDatPathName);
		
		log.info("physionetAnalysisService.PhysionetExecute - sDatName: " + inputFile);
		log.info("physionetAnalysisService.PhysionetExecute - sDatPath: " + path);
		log.info("physionetAnalysisService.PhysionetExecute - sDatPathName: " + sDatPathName);

		int iIndexPeriod = inputFile.lastIndexOf(".");
		outputFile = inputFile.substring(0, iIndexPeriod) + '_' + this.getAnalysisVO().getJobIdNumber();  // This will become the name of the CSV file

	}

	@Override
	public void execute() {
		boolean bRet = true;
		System.out.println("---------------------------");
		System.out.println("chesnokovV1()");
		System.out.println("- sInputFile:" + inputFile);
		System.out.println("- sPath:" + path);
		System.out.println("- sOutputName:" + outputFile);
		// no environment variables are needed, 
		// this is a place keeper so that the three parameter version of
		// exec can be used to specify the working directory.
		String[] asEnvVar = new String[0];  

		try{
			
			populateSignalNameList( inputFile,  asEnvVar, path);
			testFormat( inputFile,  asEnvVar, path);
			
			if(!acceptableFormat){
				System.out.println("- bAcceptableFormat: false");
				reformatRecordToF16( inputFile,  path);
			}
			
			// execute Chesnokov analysis.
			String chesnokovOutputFilenameXml = inputFile.substring(0, inputFile.lastIndexOf(".") + 1) + "xml";

			ServiceProperties prop = ServiceProperties.getInstance();

			String wineCommand = prop.getProperty(ServiceProperties.WINE_COMMAND);	
			
			String chesnokovComand = prop.getProperty(ServiceProperties.CHESNOKOV_COMMAND);
			String chesnokovFilters = prop.getProperty(ServiceProperties.CHESNOKOV_FILTERS);
			
			String sCommand = wineCommand + " " + chesnokovComand + " " + chesnokovFilters + " " + inputFile + " " + chesnokovOutputFilenameXml; // add parameters for "input file" and "output file"


			bRet = executeCommand(sCommand, asEnvVar, path);
			
			String stdReturn = stdReturnHandler();
			debugPrintln(stdReturn);
			if(!stdReturn.contains("lead:")){
				bRet=false;
				System.out.println("<ERROR>-- chesnokovV1() - sCommand:" + sCommand);
				this.getAnalysisVO().setErrorMessage(this.getAnalysisVO().getErrorMessage() + "; " + stdReturn);
			}
			
			boolean stdError = stdErrorHandler();
			debugPrintln("stdError returned: " + stdError);

			String chesnokovCSVFilepath="";
			if(bRet){
				
				switch (this.getAnalysisVO().getResultType()) {
				case CSV_FILE:
				case ORIGINAL_FILE:
					try {
						debugPrintln("calling chesnokovToCSV(chesnokovOutputFilename)");
						chesnokovCSVFilepath = chesnokovToCSV(path + File.separator + chesnokovOutputFilenameXml, path + File.separator + inputFile, outputFile, path);
						debugPrintln("----------------------------");
						File csvFile = new File(chesnokovCSVFilepath);
						bRet = csvFile.exists();
						if(bRet){
							AnalysisUtils.deleteFile(path, chesnokovOutputFilenameXml);
						}
					}catch(Exception e) {
						this.getAnalysisVO().setErrorMessage(this.getAnalysisVO().getErrorMessage() + " chesnokovV1() failed; " + e.getMessage());
						e.printStackTrace();
						bRet = false;
					}
					
					if(bRet) {
						List<String> outputFilenames = new ArrayList<String>();
						debugPrintln("- CSV Output Name: " + chesnokovCSVFilepath);
						outputFilenames.add(chesnokovCSVFilepath);
						this.getAnalysisVO().setOutputFileNames(outputFilenames);
					}
					break;
					
				case JSON_DATA:
					String jsonData = null;
					try {
						debugPrintln("calling chesnokovToCSV(chesnokovOutputFilename)");
						jsonData = chesnokovToJSON(path + File.separator + chesnokovOutputFilenameXml, path + File.separator + inputFile, outputFile, path);
						
						AnalysisUtils.deleteFile(path, chesnokovOutputFilenameXml);
						
						if(bRet) {
							this.getAnalysisVO().setOutputData(jsonData);
							this.getAnalysisVO().setOutputFileNames(null);
						}
					}catch(Exception e) {
						this.getAnalysisVO().setErrorMessage(this.getAnalysisVO().getErrorMessage() + " chesnokovV1() failed; " + e.getMessage());
						e.printStackTrace();
						bRet = false;
					}
					break;
				}
			}
		} catch (Exception e) {
			bRet = false;
			e.printStackTrace();
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
	 */
	private void populateSignalNameList(String inputFile, String[] envVar, String path){
	    try{ 
			String recordName = inputFile.substring(0, inputFile.indexOf("."));
			String command = "signame -r " + path + AnalysisUtils.sep + recordName;
			boolean bNoException = executeCommand(command, envVar, AnalysisWrapper.WORKING_DIR);
			System.out.println("- bNoException:"+ bNoException);
			
//			String stdReturn = stdReturnHandler();
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
			log.error("IOException Message: rdsamp " + ioe.getMessage());
			ioe.printStackTrace();
		} catch (Exception e) {
			System.err.println("Exception Message: rdsamp " + e.getMessage());
			e.printStackTrace();
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
			System.out.println("- bNoException:"+ bNoException);
			
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
			System.out.println("- found Format 16");
			acceptableFormat=true;
		}
		if(line.contains("Storage format: 212")){
			System.out.println("- found Format 212");
			acceptableFormat=true;
		}
	}
	
	private void reformatRecordToF16(String inputFile,  String path){
		
		ECGformatConverter con= new ECGformatConverter();
		int signalsRequested = 0; // zero requests all signals found.
		
		con.convert(ECGFormat.WFDB, ECGFormat.WFDB_16, inputFile, signalsRequested, path, path);
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
        debugPrintln(" ** converting " + chesnokovFilename);
   		try {
   			xhtml = extractXmlData(chesnokovFilename, fileAnalyzedTempName, outputFileName);
			
			csvOutputFilename = outputPath + outputFileName + ".csv";
			
			debugPrintln(" ** writing " + csvOutputFilename);
			BufferedWriter out = new BufferedWriter(new FileWriter(csvOutputFilename));
			out.write(xhtml);
			out.close();
		   
        } catch (Exception ex) {
			this.getAnalysisVO().setErrorMessage(this.getAnalysisVO().getErrorMessage() + " chesnokovToCSV() failed; " + ex.getMessage());

        	ex.printStackTrace();
        }
		return csvOutputFilename;
	}

	private String extractXmlData(String chesnokovFilename,	String fileAnalyzedTempName, String outputFileName)	throws FileNotFoundException, IOException, JDOMException, XSLTransformException {
		String xhtml;
		String[] chesSigalNameArray = {"I","II","III","aVR","aVL","aVF","v1","v2","v3","v4","v5","v6"};
		Document xmlDoc = null;
		Document transformed = null;
		InputStream xsltIS = null;
		XSLTransformer xslTransformer = null;
		String row = null;
		StringBuffer sb = new StringBuffer();
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
			xsltIS = this.getClass().getResourceAsStream("chesnokov_json_datatable.xsl");	
		}else{
			xsltIS = this.getClass().getResourceAsStream("chesnokov_datatable.xsl");
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
		return xhtml;
	}	
	
	private String chesnokovToJSON(String chesnokovFilename, String fileAnalyzedTempName, String outputFileName, String outputPath) {
		String xhtml = null;
        debugPrintln(" ** converting " + chesnokovFilename);
   		try {
   			xhtml = extractXmlData(chesnokovFilename, fileAnalyzedTempName, outputFileName);
			
			xhtml = xhtml.replaceFirst("},]}", "}]}");
		   
        } catch (Exception ex) {
			this.getAnalysisVO().setErrorMessage(this.getAnalysisVO().getErrorMessage() + " chesnokovToJSON() failed; " + ex.getMessage());
        	ex.printStackTrace();
        }
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

package edu.jhu.cvrg.analysis.wrapper.wfdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.jhu.cvrg.analysis.util.AnalysisUtils;
import edu.jhu.cvrg.analysis.vo.AnalysisResultType;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.analysis.wrapper.ApplicationWrapper;

public class RdsampAnalysis extends ApplicationWrapper{

	private boolean summary = false;	// -h Print a usage summary. (to console, meaningless here).
	private String 	firstSignal; 		// -S
	private String 	signalList; 		// -s
	private String 	formatOutput; 		// value is one of the following: pd, pe, ph, pm, ps, pS, Pd, Pe, Ph, Pm, Ps, PS
	private int 	startTime = 0;		// -f
	private int 	endTime = 0;		// -t (-1) defaults to end of record.
	private double 	interval = 0.0;		// -l  If both -l and -t are used, rdsamp stops at the earlier of the two limits
	
	private boolean csv; 				// -c CSV Format
	private boolean highrez; 			// -H
	private boolean columnheads;		// -v
	private boolean xml;				// -X Produce output in WFDB-XML format 
	
	private String path;
	private String headerFilename;
	private String outputName;
	
	public RdsampAnalysis(AnalysisVO vo) {
		super(vo);
	}

	@Override
	public void defineInputParameters() {
		firstSignal	= (String) this.getAnalysisVO().getCommandParamMap().get("S");         // -S
		signalList		= (String) this.getAnalysisVO().getCommandParamMap().get("s");         // -s
		formatOutput 	= (String) this.getAnalysisVO().getCommandParamMap().get("format");    // value is one of the following: pd, pe, ph, pm, ps, pS, Pd, Pe, Ph, Pm, Ps, PS
		
		
		if(this.getAnalysisVO().getCommandParamMap().get("f") != null){
			startTime	= Integer.parseInt(    (String) this.getAnalysisVO().getCommandParamMap().get("f")); // -f
		}
		
		if(this.getAnalysisVO().getCommandParamMap().get("t") != null){
			endTime	= Integer.parseInt(    (String) this.getAnalysisVO().getCommandParamMap().get("t")); // -t (-1) defaults to end of record.
		}
		
		if(this.getAnalysisVO().getCommandParamMap().get("t") != null){
			interval	= Double.parseDouble(  (String) this.getAnalysisVO().getCommandParamMap().get("t")); // -l  If both -l and -t are used, rdsamp stops at the earlier of the two limits
		}
		
		csv 		= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("c")) || AnalysisResultType.CSV_FILE.equals(this.getAnalysisVO().getResultType()); // -c CSV Format
		highrez 	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("H")); // -H
		columnheads= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("v")) || AnalysisResultType.CSV_FILE.equals(this.getAnalysisVO().getResultType()); // -v
		xml		= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("X")); // -X // Produce output in WFDB-XML format 
		
		String sHeaderPathName = AnalysisUtils.findHeaderPathName(this.getAnalysisVO().getFileNames());
		path = AnalysisUtils.extractPath(sHeaderPathName);
		headerFilename = AnalysisUtils.extractName(sHeaderPathName);
		
		debugPrintln("- sHeaderPathName: " + sHeaderPathName);
		debugPrintln("- sHeaderPath: " + path);
		debugPrintln("- sHeaderName: " + headerFilename);
		
		int index = headerFilename.lastIndexOf(".");
		outputName = headerFilename.substring(0, index) + '_' + this.getAnalysisVO().getJobIdNumber() ;
		
	}

	@Override
	public void execute() {
		boolean bRet = true;
		debugPrintln("rdsamp()");
		debugPrintln("- sHeaderFile: " + headerFilename);
		debugPrintln("- sPath: " + path);
		debugPrintln("- bCsv: " + csv);
		debugPrintln("- iStarttime: " + startTime);  
		debugPrintln("- bSummary: " + summary);
		debugPrintln("- bHighrez: " + highrez);
		debugPrintln("- dInterval: " + interval);
		debugPrintln("- sFormatOutput: " + formatOutput); 
		debugPrintln("- sSignallist: " + signalList); 
		debugPrintln("- sFirstSignal: " + firstSignal); 
		debugPrintln("- iEndtime: " + endTime);
		//--  output adjustments
		debugPrintln("- bColumnheads: " + columnheads);  
		debugPrintln("- bXML: " + xml);  

		try {
		
			String[] asEnvVar = new String[0];  
			
			// build command string
			int iIndexPeriod = headerFilename.lastIndexOf(".");
			String sRecord = headerFilename.substring(0, iIndexPeriod);
			
			String command = "rdsamp -r " + path + sRecord; // record name
			
			if(csv) command += " -c"; // CSV Format
			
			if(startTime > 0) command += " -f " + startTime;
			if(summary) command += " -h";	
			if(highrez) command += " -H";
			if(interval > 0) command += " -c " + interval;
			
			// one of the following: pd, pe, ph, pm, ps, pS, Pd, Pe, Ph, Pm, Ps, PS
			if(formatOutput != null && !formatOutput.equals("")) command += " -" + formatOutput; 
			
			if(signalList !=null && !signalList.equals("")) command += " -s " + signalList;
			if(firstSignal != null && !firstSignal.equals("")) command += " -S " + firstSignal;
			if(endTime > 0) command += " -t " + endTime;
			if(columnheads ) command += " -v";
			if(xml) command += " -X"; //Produce output in WFDB-XML format 

			if (csv){
				outputName += ".csv";
			}else if (xml){
				outputName += ".xml";
			}else{
				outputName += ".txt";
			}
			
			debugPrintln("- sOutputFile: " + outputName);  

			// essentially, we will be trying to stream the data and write it line by line to the output file as we receive it
			bRet = this.executeCommand(command, asEnvVar, WORKING_DIR);			

			if(bRet){
				switch (this.getAnalysisVO().getResultType()) {
					case ORIGINAL_FILE:
					case CSV_FILE:
						this.stdReturnHandler(path + outputName);
						bRet &= this.stdErrorHandler();
						
						if(bRet){
							//set first output file to output generated by the sigamp command
							List<String> outputFilenames = new ArrayList<String>();
							
							outputFilenames.add(path + outputName);
							this.getAnalysisVO().setOutputFileNames(outputFilenames);
							
						}else{
							debugPrintln("- Encountered errors.");
						}	
						
						break;
					case JSON_DATA:
						//TODO To be defined
						
						debugPrintln("- To be defined");
						
//						String jsonData = this.stdJSONReturnHandler(null);
//						bRet &= this.stdErrorHandler();
//						
//						if(bRet){
//							this.getAnalysisVO().setOutputData(jsonData);
//							this.getAnalysisVO().setOutputFileNames(null);
//						}else{
//							debugPrintln("- Encountered errors.");
//						}
						break;
				}
			}
		} catch (IOException e) {
			bRet = false;
			log.error(e.getMessage());
		}
		
		this.getAnalysisVO().setSucess(bRet);
	}

	@Override
	protected void processReturnLine(String line) {
		// TODO Auto-generated method stub
	}
}

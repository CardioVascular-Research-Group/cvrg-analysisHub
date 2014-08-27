package edu.jhu.cvrg.analysis.wrapper.wfdb;

import java.io.IOException;

import edu.jhu.cvrg.analysis.util.AnalysisFailureException;
import edu.jhu.cvrg.analysis.util.AnalysisUtils;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.analysis.wrapper.AnnotationOutputAnalysisWrapper;

public class WqrsAnalysis extends AnnotationOutputAnalysisWrapper {

	private String 	signal; 
	private int begin = 0;
	private int time = -1;
	private int threshold = 500;
	private int powerFreq = 60;
	private boolean dumpRaw;
	private boolean printHelp;
	private boolean highrez;
	private boolean findJPoints;
	private boolean resample;
	
	@SuppressWarnings("unused")
	private boolean verbose;
	
	private String path;
	private String headerFilename;
	
	
	public WqrsAnalysis(AnalysisVO vo) {
		super(vo);
	}

	@Override
	protected void processReturnLine(String line) {
		// TODO Auto-generated method stub

	}

	@Override
	public void defineInputParameters() {
		//*** The analysis algorithm should return a String array containing the full path/names of the result files.
		Object[] keys = this.getAnalysisVO().getCommandParamMap().keySet().toArray();
		
		for(int i=0;i<keys.length;i++){
			debugPrintln("Key: \"" + (String)keys[i] + "\" Value: \"" + this.getAnalysisVO().getCommandParamMap().get((String)keys[i]) + "\"");
		}

		signal = (String) this.getAnalysisVO().getCommandParamMap().get("s");         				// -s
		
		if(this.getAnalysisVO().getCommandParamMap().get("f") != null){
			begin = Integer.parseInt( (String) this.getAnalysisVO().getCommandParamMap().get("f"));    // -f	
		}
		
		if(this.getAnalysisVO().getCommandParamMap().get("t") != null) {
			time = Integer.parseInt( (String) this.getAnalysisVO().getCommandParamMap().get("t"));    // -t (-1) defaults to end of record.	
		}
		
		if(this.getAnalysisVO().getCommandParamMap().get("m") != null){
			threshold = Integer.parseInt( (String) this.getAnalysisVO().getCommandParamMap().get("m"));    // -m	
		}
		
		if(this.getAnalysisVO().getCommandParamMap().get("p") != null){
			powerFreq = Integer.parseInt( (String) this.getAnalysisVO().getCommandParamMap().get("p"));    // -p	
		}
		
		dumpRaw 	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("b")); // -d
		printHelp 	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("h")); // -h
		highrez 	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("H")); // -H
		findJPoints = Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("j")); // -j
		resample 	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("R")); // -R
		verbose 	= Boolean.parseBoolean((String) this.getAnalysisVO().getCommandParamMap().get("v")); // -v
		//**********************************************************************
		
		String sHeaderPathName = AnalysisUtils.findHeaderPathName(this.getAnalysisVO().getFileNames());
		path = AnalysisUtils.extractPath(sHeaderPathName);
		headerFilename = AnalysisUtils.extractName(sHeaderPathName);
		
		debugPrintln("- sHeaderPathName: " + sHeaderPathName);
		debugPrintln("- sHeaderPath: " + path);
		debugPrintln("- sHeaderName: " + headerFilename);
	}

	@Override
	public void execute() {
		
		boolean bRet = true;
		debugPrintln("wqrs()");
		debugPrintln("- sHeaderFile:" + headerFilename);
		debugPrintln("- sPath:" + path);
		debugPrintln("- iBegin:" + begin);
		debugPrintln("- bHighrez:" + highrez);
		debugPrintln("- iThreshold:" + threshold);
		debugPrintln("- sSignal:" + signal);
		debugPrintln("- iTime:" + time);
		try {
			// no environment variables are needed, 
			// this is a place keeper so that the three parameter version of
			// exec can be used to specify the working directory.
			String[] asEnvVar = new String[0];  
			
			// build command string
			int iIndexPeriod = headerFilename.lastIndexOf(".");
			String sRecord = headerFilename.substring(0, iIndexPeriod);
			
			String sCommand = "wqrs -r " + path + sRecord;
			if(dumpRaw) sCommand   += " -d ";
			if(begin !=0) sCommand += " -f " + begin;
			if(printHelp) sCommand += " -h ";
			if(highrez) sCommand   += " -H ";
			if(findJPoints) sCommand += " -j ";
			if(threshold != 500) sCommand += " -m " + threshold;
			if(powerFreq != 60) sCommand += " -p " + powerFreq;
			if(resample) sCommand += " -R ";
			if(signal != null && signal.equals("0")) sCommand += " -s " + signal;
			if(time != -1) sCommand += " -t " + time;
			
			sCommand += " -v ";
	
			bRet = executeCommand(sCommand, asEnvVar, WORKING_DIR);
			
			bRet = processCommandReturn(bRet,path, headerFilename, sRecord);
			
		} catch (IOException e) {
			bRet = false;
			log.error(e.getMessage());
		} catch (AnalysisFailureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getAnalysisVO().setSucess(bRet);
	}
	
	@Override
	public String getAnnotationExt() {
		return "wqrs";
	}
}

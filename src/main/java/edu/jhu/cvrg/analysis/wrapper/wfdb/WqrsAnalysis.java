package edu.jhu.cvrg.analysis.wrapper.wfdb;

import java.io.IOException;

import edu.jhu.cvrg.analysis.util.AnalysisParameterException;
import edu.jhu.cvrg.analysis.util.AnalysisExecutionException;
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
	protected void _defineInputParameters() throws AnalysisParameterException {
		
		if(this.getAnalysisVO().getCommandParamMap() != null  && !this.getAnalysisVO().getCommandParamMap().isEmpty()){
			if(log.isDebugEnabled()){
				Object[] keys = this.getAnalysisVO().getCommandParamMap().keySet().toArray();
				
				for(int i=0;i<keys.length;i++){
					debugPrintln("Key: \"" + (String)keys[i] + "\" Value: \"" + this.getAnalysisVO().getCommandParamMap().get((String)keys[i]) + "\"");
				}
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
		}
		
		String headerPathName = AnalysisUtils.findHeaderPathName(this.getAnalysisVO().getFileNames());
		path = AnalysisUtils.extractPath(headerPathName);
		headerFilename = AnalysisUtils.extractName(headerPathName);
		
		debugPrintln("- headerPathName: " + headerPathName);
		debugPrintln("- path: " + path);
		debugPrintln("- headerFilename: " + headerFilename);
	}

	@Override
	protected void _execute() throws AnalysisExecutionException {
		
		boolean bRet = true;
		debugPrintln("wqrs()");
		debugPrintln("- headerFilename:" + headerFilename);
		debugPrintln("- sPath:" + path);
		debugPrintln("- begin:" + begin);
		debugPrintln("- highrez:" + highrez);
		debugPrintln("- threshold:" + threshold);
		debugPrintln("- signal:" + signal);
		debugPrintln("- time:" + time);
		try {
			// no environment variables are needed, 
			// this is a place keeper so that the three parameter version of
			// exec can be used to specify the working directory.
			String[] asEnvVar = new String[0];  
			
			// build command string
			int iIndexPeriod = headerFilename.lastIndexOf(".");
			String sRecord = headerFilename.substring(0, iIndexPeriod);
			
			String command = "wqrs -r " + path + sRecord;
			if(dumpRaw) command   += " -d ";
			if(begin !=0) command += " -f " + begin;
			if(printHelp) command += " -h ";
			if(highrez) command   += " -H ";
			if(findJPoints) command += " -j ";
			if(threshold != 500) command += " -m " + threshold;
			if(powerFreq != 60) command += " -p " + powerFreq;
			if(resample) command += " -R ";
			if(signal != null && signal.equals("0")) command += " -s " + signal;
			if(time != -1) command += " -t " + time;
			
			command += " -v ";
	
			bRet = executeCommand(command, asEnvVar, WORKING_DIR);
			
			bRet = processCommandReturn(bRet,path, headerFilename, sRecord);
			
		} catch (IOException e) {
			throw new AnalysisExecutionException("Error on "+this.getAnalysisVO().getType()+" command output handling", e);
		}
		this.getAnalysisVO().setSucess(bRet);
	}
	
	@Override
	public String getAnnotationExt() {
		return "wqrs";
	}
}

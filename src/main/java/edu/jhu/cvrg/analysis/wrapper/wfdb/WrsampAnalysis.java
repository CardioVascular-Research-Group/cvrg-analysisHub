package edu.jhu.cvrg.analysis.wrapper.wfdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.jhu.cvrg.analysis.util.AnalysisParameterException;
import edu.jhu.cvrg.analysis.util.AnalysisUtils;
import edu.jhu.cvrg.analysis.util.AnalysisExecutionException;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.analysis.wrapper.ApplicationWrapper;

public class WrsampAnalysis extends ApplicationWrapper {

	private int copyStart = 0;
	private int lineStop = 0;
	private int sampleFrequency = 0;
	private int characters = 0;
	private boolean	dither;
	private boolean checkInput;
	private boolean noZero;
	
	private double 	multiply = 0.0;
	
	private String path;
	private String inputFilename;
	
	private String outputFile;
	
	private char lineSeparator = '\u0000';
	private char fieldSeparator = '\u0000';

	
	public WrsampAnalysis(AnalysisVO vo) {
		super(vo);
	}

	@Override
	protected void processReturnLine(String line) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void _defineInputParameters() throws AnalysisParameterException {
		
		if(this.getAnalysisVO().getCommandParamMap() != null && !this.getAnalysisVO().getCommandParamMap().isEmpty()){

			if(this.getAnalysisVO().getCommandParamMap().get("f") != null){
				copyStart = Integer.parseInt( 	(String) this.getAnalysisVO().getCommandParamMap().get("f")); // -f	
			}
			
			if(this.getAnalysisVO().getCommandParamMap().get("t") != null){
				lineStop = Integer.parseInt(   	(String) this.getAnalysisVO().getCommandParamMap().get("t")); // -t (-1) defaults to end of record.	
			}
			
			if(this.getAnalysisVO().getCommandParamMap().get("F") != null){
				sampleFrequency = Integer.parseInt( (String) this.getAnalysisVO().getCommandParamMap().get("F")); // -F Specify the sampling frequency (in samples per second per signal)	
			}
			
			if(this.getAnalysisVO().getCommandParamMap().get("l") != null){
				characters	= Integer.parseInt(  	(String) this.getAnalysisVO().getCommandParamMap().get("l")); // -l Read up to n characters in each line	
			}
			 
			dither		= Boolean.parseBoolean( (String) this.getAnalysisVO().getCommandParamMap().get("d")); // -d Dither the input before converting it to integer output
			checkInput	= Boolean.parseBoolean(	(String) this.getAnalysisVO().getCommandParamMap().get("c")); // -c Check that each input line contains the same number of fields.
			noZero		= Boolean.parseBoolean(	(String) this.getAnalysisVO().getCommandParamMap().get("z")); // -z Don't copy column 0 unless explicitly specified.
			
			
			if(this.getAnalysisVO().getCommandParamMap().get("x") != null){
				multiply = Double.parseDouble(	(String) this.getAnalysisVO().getCommandParamMap().get("x")); // -x Multiply all input samples by n (default: 1)	
			}
		}
					
		String inputPath = AnalysisUtils.extractPath(this.getAnalysisVO().getFileNames().get(0));
		String inputName = AnalysisUtils.extractName(this.getAnalysisVO().getFileNames().get(0));
		
		debugPrintln("- inputPath: " + inputPath);
		debugPrintln("- inputName: " + inputName);
		
		int indexPeriod = inputName.lastIndexOf(".");
		outputFile = inputName.substring(0, indexPeriod) + "_" + this.getAnalysisVO().getJobIdNumber(); // this should be the same name as the input file for this particular function

	}

	@Override
	protected void _execute() throws AnalysisExecutionException {
		boolean bRet = true;
		debugPrintln("wrsamp()");
		debugPrintln("- inputFilename:" + inputFilename);
		debugPrintln("- path:" + path);
		debugPrintln("- checkInput:" + checkInput);
		debugPrintln("- dither:" + dither);  
		debugPrintln("- copyStart:" + copyStart);
		debugPrintln("- sampleFrequency:" + sampleFrequency);
		debugPrintln("- characters:" + characters); 
		debugPrintln("- lineSeparator:" + lineSeparator);
		debugPrintln("- fieldSeparator:" + fieldSeparator);  
		debugPrintln("- lineStop:" + lineStop);
		debugPrintln("- multiply:" + multiply);
		debugPrintln("- noZero:" + noZero);		
		debugPrintln("- outputFile:" + outputFile);

		try {
		
			String[] envVar = new String[0];  
			
			// build command string
			String command = "wrsamp -i " + path + inputFilename; // record name
			if(checkInput) command += " -c";
			if(dither) command += " -d ";
			if(copyStart > 0 ) command += " -f " + copyStart;	
			if(sampleFrequency > 0) command += " -F " + sampleFrequency;
			if(characters > 0) command += " -l " + characters;
			if(multiply > 0) command += " -x " + multiply;
			
			// the -o is a required parameter, the default is the input filename.
			if(outputFile.equals(""))
			{
				int iIndexPeriod = inputFilename.lastIndexOf(".");
				outputFile = inputFilename.substring(0, iIndexPeriod);
			}
			
			command += " -o " + path + outputFile;
						
			if(lineSeparator != '\u0000') command += " -r " + lineSeparator; // \u0000 is the empty char.  It is used since an empty char of '' cannot be declared in Java
			if(fieldSeparator != '\u0000') command += " -s " + fieldSeparator;
			if(lineStop > 0) command += " -t " + lineStop;
			if(noZero) command += " -z";

			// essentially, we will be trying to stream the data and write it line by line to the output file as we receive it
			bRet = executeCommand(command, envVar, WORKING_DIR);			

			if(bRet){
				switch (this.getAnalysisVO().getResultType()) {
				case ORIGINAL_FILE:
					bRet &= stdErrorHandler();
					
					if(bRet){
						List<String> outputFilenames = new ArrayList<String>();
						//set first output file to output generated by the sigamp command
						
						outputFilenames.add(path + outputFile + ".dat");
						outputFilenames.add(path + outputFile + ".hea");
					}else{
						throw new AnalysisExecutionException("Command execution with error. ["+ command+"]");
					}	
					break;
	
				default:
					throw new AnalysisExecutionException("Unexpected output format ["+this.getAnalysisVO().getResultType()+"] for this analysis ["+this.getAnalysisVO().getType()+"]");
				}
			}else{
				throw new AnalysisExecutionException("Command execution error. ["+ command+"]");
			}
			
		} catch (IOException e) {
			throw new AnalysisExecutionException("Error on "+this.getAnalysisVO().getType()+" command output handling", e);
		}

		this.getAnalysisVO().setSucess(bRet);
	}
}

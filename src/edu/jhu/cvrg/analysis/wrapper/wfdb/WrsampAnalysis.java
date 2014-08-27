package edu.jhu.cvrg.analysis.wrapper.wfdb;

import java.util.ArrayList;
import java.util.List;

import edu.jhu.cvrg.analysis.util.AnalysisUtils;
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
	public void defineInputParameters() {
		
		//*** The analysis algorithm should return a String array containing the full path/names of the result files.

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
		 
		//************************************************************************************************************			
		String sInputPath = AnalysisUtils.extractPath(this.getAnalysisVO().getFileNames().get(0));
		String sInputName = AnalysisUtils.extractName(this.getAnalysisVO().getFileNames().get(0));
		
		debugPrintln("- sInputPath: " + sInputPath);
		debugPrintln("- sInputName: " + sInputName);
		
		int iIndexPeriod = sInputName.lastIndexOf(".");
		outputFile = sInputName.substring(0, iIndexPeriod) + "_" + this.getAnalysisVO().getJobIdNumber(); // this should be the same name as the input file for this particular function

	}

	@Override
	public void execute() {
		boolean bRet = true;
		debugPrintln("wrsamp()");
		debugPrintln("- sInputFile:" + inputFilename);
		debugPrintln("- sPath:" + path);
		debugPrintln("- bCheckInput:" + checkInput);
		debugPrintln("- bDither:" + dither);  
		debugPrintln("- iCopyStart:" + copyStart);
		debugPrintln("- iSampleFrequency:" + sampleFrequency);
		debugPrintln("- iCharacters:" + characters); 
		debugPrintln("- cLineSeparator:" + lineSeparator);
		debugPrintln("- cFieldSeparator:" + fieldSeparator);  
		debugPrintln("- iLineStop:" + lineStop);
		debugPrintln("- dMultiply:" + multiply);
		debugPrintln("- bNoZero:" + noZero);		
		debugPrintln("- sOutputName:" + outputFile);

		try {
		
			String[] asEnvVar = new String[0];  
			
			// build command string
			
			String sCommand = "wrsamp -i " + path + inputFilename; // record name
			if(checkInput) sCommand += " -c";
			if(dither) sCommand += " -d ";
			if(copyStart > 0 ) sCommand += " -f " + copyStart;	
			if(sampleFrequency > 0) sCommand += " -F " + sampleFrequency;
			if(characters > 0) sCommand += " -l " + characters;
			if(multiply > 0) sCommand += " -x " + multiply;
			
			// the -o is a required parameter, the default is the input filename.
			if(outputFile.equals(""))
			{
				int iIndexPeriod = inputFilename.lastIndexOf(".");
				outputFile = inputFilename.substring(0, iIndexPeriod);
			}
			
			sCommand += " -o " + path + outputFile;
						
			if(lineSeparator != '\u0000') sCommand += " -r " + lineSeparator; // \u0000 is the empty char.  It is used since an empty char of '' cannot be declared in Java
			if(fieldSeparator != '\u0000') sCommand += " -s " + fieldSeparator;
			if(lineStop > 0) sCommand += " -t " + lineStop;
			if(noZero) sCommand += " -z";

			// essentially, we will be trying to stream the data and write it line by line to the output file as we receive it
			bRet = executeCommand(sCommand, asEnvVar, WORKING_DIR);			

			switch (this.getAnalysisVO().getResultType()) {
			case ORIGINAL_FILE:
				bRet &= stdErrorHandler();
				
				if(bRet){
					List<String> outputFilenames = new ArrayList<String>();
					//set first output file to output generated by the sigamp command
					
					outputFilenames.add(path + outputFile + ".dat");
					outputFilenames.add(path + outputFile + ".hea");
				}else{
					debugPrintln("- Encountered errors.");
				}	
				break;

			default:
				debugPrintln("ERROR - Selected resultType is invalid to this algorithm.");
				break;
			}
			
						
			
		} catch (Exception e) {
			bRet = false;
			log.error(e.getMessage());
		}

		this.getAnalysisVO().setSucess(bRet);

	}

}

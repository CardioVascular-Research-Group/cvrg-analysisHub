package edu.jhu.cvrg.analysis.wrapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import edu.jhu.cvrg.analysis.vo.AnalysisVO;

/**
 * Base class to analysis witch depends to shell command executions
 * @author avilard4
 *
 */
public abstract class ApplicationWrapper extends AnalysisWrapper {

	protected BufferedReader stdInputBuffer = null;
	protected BufferedReader stdError = null;
	protected int lineNum = 0;
	
	public ApplicationWrapper(AnalysisVO vo) {
		super(vo);
	}
	
	/** Executes the command and pipes the response and errors to stdInputBuffer and stdError respectively.
	 * 
	 * @param command - a specified system command.
	 * @param envVar - array of strings, each element of which has environment variable settings in format name=value.
	 * @param workingDir - the working directory of the subprocess, or null if the subprocess should inherit the working directory of the current process. 
	 * @return 
	 */
	protected boolean executeCommand(String command, String[] envVar, String workingDir){
		
		if(envVar == null){
			envVar = new String[0];
		}
		
		log.info("++ executeCommand(" + command + ")" );
		log.info(", asEnvVar[" + envVar.length + "]");
		log.info(", " + workingDir + ")");
		boolean bRet = true;	
		
		try {
			File fWorkingDir = new File(workingDir); //converts the dir name to File for exec command.
			Runtime rt = Runtime.getRuntime();
			String[] commandArray = command.split("\\|");
			if(commandArray.length == 1 ){
				Process process = rt.exec(command, envVar, fWorkingDir);
				InputStream is = process.getInputStream();  // The input stream for this method comes from the output from rt.exec()
				InputStreamReader isr = new InputStreamReader(is);
				stdInputBuffer = new BufferedReader(isr);
				InputStream errs = process.getErrorStream();
				InputStreamReader esr = new InputStreamReader(errs);
				stdError = new BufferedReader(esr);	
			}else{
				
				Process[] processArray = new Process[commandArray.length];
				// Start processes: ps ax | grep rbe | grep JavaVM
				for (int i = 0; i < commandArray.length; i++) {
					processArray[i] =  rt.exec(commandArray[i].trim(), envVar, fWorkingDir);
				}
		        // Start piping
		        java.io.InputStream in = Piper.pipe(processArray);
		        
		        // Show output of last process
		        InputStreamReader isr = new InputStreamReader(in);
				stdInputBuffer = new BufferedReader(isr);
				InputStream errs = processArray[processArray.length-1].getErrorStream();
				InputStreamReader esr = new InputStreamReader(errs);
				stdError = new BufferedReader(esr);	
			}
			
		} catch (IOException ioe) {
			log.error("IOException Message: executeCommand(" + command + ")" + ioe.getMessage());
			bRet = false;
		} catch (Exception e) {
			log.error("Exception Message: executeCommand(" + command + ")" + e.getMessage());
			bRet = false;
		}
		log.info("++ returning: " + bRet);
		return bRet;
	}
	
	/** This writes the output to the standard output
	 * 
	 * @throws IOException
	 */	
	
	protected String stdReturnHandler() throws IOException{
	    
		StringBuilder sb = new StringBuilder();
		lineNum = 0;
		String tempLine;
		
	    log.info("Here is the returned text of the command (if any):");
	    while ((tempLine = stdInputBuffer.readLine()) != null) {
	    	log.info(lineNum + ")" + tempLine);
	    	sb.append(tempLine);
	    	lineNum++;
	    }
	    
	    return sb.toString();
	}
	
	protected void stdReturnMethodHandler() throws IOException{
	    
		lineNum = 0;
		String tempLine;
		
	    log.info("Here is the returned text of the command (if any):");
	    while ((tempLine = stdInputBuffer.readLine()) != null) {
	    	log.info(lineNum + ")" + tempLine);
	    	this.processReturnLine(tempLine);
	    	lineNum++;
	    }
	}
	
	protected abstract void processReturnLine(String line);
	
	/** This writes the output of the execution to a file instead of standard output
	 * 
	 * @param outputFilename
	 * @throws IOException
	 */
	protected void stdReturnHandler(String outputFilename) throws IOException{
	    String line;
		try{
			// Create file 
			log.info("stdReturnHandler(FName) Creating output file: " + outputFilename);
			FileWriter fstream = new FileWriter(outputFilename);
			BufferedWriter bwOut = new BufferedWriter(fstream);

			lineNum = 0;
		    log.info("Here is the returned text of the command (if any): \"");
		    while ((line = stdInputBuffer.readLine()) != null) {
		    	
		    	bwOut.write(line);
		    	bwOut.newLine();
		    	if (lineNum<10){
		    		log.info(lineNum + ")" + line);
		    	}
		    	
		    	lineNum++;
		    }
		    log.info(". . . ");
		    log.info(lineNum + ")" + line);
	        log.info("\"");
			bwOut.flush();
			//Close the output stream
			bwOut.close();
		}catch (Exception e){//Catch exception if any
		   log.error("Error: " + e.getMessage());
		}
	}
	
	
	/** This writes the output of the execution to a file instead of standard output
	 * 
	 * @param outputFilename
	 * @throws IOException
	 */
	protected String stdCSVReturnHandler(String path, String outputFilename, String[] headers) throws IOException{
	    String line;
	    String file = path + outputFilename + ".csv";
		
	    // Create file 
		log.info("stdCSVReturnHandler(FName) Creating output file: " + file);
		FileWriter fstream = new FileWriter(file);
		BufferedWriter bwOut = new BufferedWriter(fstream);

		lineNum = 0;
	    log.info("Here is the returned text of the command (if any): \"");
	    
	    if(headers != null ){
	    	String headerLine = "";
    		for (String string : headers) {
				headerLine += (string+','); 
			}
    		headerLine = headerLine.substring(0, headerLine.length()-1);
    		bwOut.write(headerLine);
	    	bwOut.newLine();
	    }
	    
	    while ((line = stdInputBuffer.readLine()) != null) {
	    	
	    	line = line.replaceAll("\\s+",",").replaceAll("\\t",", "); 
	    	if(line.charAt(0) == ','){
	    		line = line.substring(1, line.length());
	    	}
	    	
	    	bwOut.write(line);
	    	bwOut.newLine();
	    	if (lineNum<10){
	    		log.info(lineNum + ")" + line);
	    	}
	    	
	    	lineNum++;
	    }
	    log.info(lineNum + ")" + line);
        log.info("\"");
		bwOut.flush();
		//Close the output stream
		bwOut.close();
	
		return file;
	}
	
	protected String stdJSONReturnHandler(String[] headers) throws IOException{
	    StringBuilder sb = new StringBuilder("{\"results\":[");
	   	
		lineNum = 0;
	    
	    String line;
	    while ((line = stdInputBuffer.readLine()) != null) {
	    	
	    	line = line.replaceAll("\\s+",",").replaceAll("\\t",", ");
	    	if(line.charAt(0) == ','){
	    		line = line.substring(1, line.length());
	    	}
	    	
	    	String[] values = line.split(",");
	    	
	    	int columnLenght = values.length;
	    	if(headers != null){
	    		sb.append('{');
	    		columnLenght = headers.length;
	    	}
	    	
	    	for (int j = 0; j < columnLenght; j++) {
	    		if(headers != null){
	    			sb.append('\"').append(headers[j]).append("\":");
	    		}
	    		if(j < values.length){
	    			try{
	    				Double.parseDouble(values[j]);
	    				sb.append(values[j]);
	    			}catch (NumberFormatException e){
	    				sb.append('\"').append(values[j]).append('\"');	
	    			}
	    		}else{
	    			sb.append("null");
	    		}
	    		
	    		sb.append(',');
			}
	    	
	    	sb.deleteCharAt(sb.lastIndexOf(","));
	    	
	    	if(headers != null){
	    		sb.append('}');
	    	}
	    	sb.append(",\n");
	    }
		
	    sb.deleteCharAt(sb.lastIndexOf(","));
	    sb.append("]}");
		    
		return sb.toString();
	}
	
	/** This function prints messages resulting from runtime problems to the system standard error
	 * @return Boolean variable:  True if there are no errors, false if there are errors.
	 * 
	 * @throws IOException
	 */	
	protected boolean stdErrorHandler() throws IOException{
		boolean bRet = true;
		String error;
	    lineNum = 0;

	    // read any errors from the attempted command
	    log.info("");
	    log.info("Here is the standard error of the command (if any): \"");
        while ((error = stdError.readLine()) != null) {
        	if(error.length() > 0){
        		
        		switch (this.getAnalysisVO().getType()) {
				case RDSAMP:
				case SQRS:
					
					if(!error.contains("checksum error in signal")){
						bRet = false;	
					}
					
					break;

				default:
					bRet = false;
					break;
				}
        		
        		log.error(lineNum + ">" + error);
	            lineNum++;
				
        	}
        }
        log.info("\"");
		return bRet;

	}
	
	protected void debugPrintln(String text){
		log.info("- ApplicationWrapper - " + text);
	}

	
	protected void setJSONOutput() throws IOException{
		this.getAnalysisVO().setOutputData(stdJSONReturnHandler(this.getDataHeaders()));
		this.getAnalysisVO().setOutputFileNames(null);
	}
}

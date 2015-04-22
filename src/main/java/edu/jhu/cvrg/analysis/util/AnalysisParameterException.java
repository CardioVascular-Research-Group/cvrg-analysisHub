package edu.jhu.cvrg.analysis.util;

import org.apache.log4j.Logger;

public class AnalysisParameterException extends Exception{

	private static final long serialVersionUID = 6794589448142833475L;
	private Logger log = Logger.getLogger(AnalysisParameterException.class);
	
	public AnalysisParameterException() {
		log.error("no message");
		// TODO Auto-generated constructor stub
	}

	public AnalysisParameterException(String message) {
		super(message);
		log.error(message);
		// TODO Auto-generated constructor stub
	}

	public AnalysisParameterException(Throwable cause) {
		super(cause);
		log.error("Cause: " + cause.getMessage());
		// TODO Auto-generated constructor stub
	}

	public AnalysisParameterException(String message, Throwable cause) {
		super(message, cause);
		log.error(message + " - Cause: " + cause.getMessage());
	}

}

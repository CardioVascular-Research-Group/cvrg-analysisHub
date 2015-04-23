package edu.jhu.cvrg.analysis.util;

import org.apache.log4j.Logger;

public class AnalysisExecutionException extends Exception{

	private static final long serialVersionUID = 6794589448142833475L;
	private Logger log = Logger.getLogger(AnalysisExecutionException.class);
	
	public AnalysisExecutionException() {
		log.error("AnalysisExecutionException, no message or cause.");
		// TODO Auto-generated constructor stub
	}

	public AnalysisExecutionException(String message) {
		super(message);
		log.error(message + " no cause provided ");
		// TODO Auto-generated constructor stub
	}

	public AnalysisExecutionException(Throwable cause) {
		super(cause);
		log.error("no message - cause:" + cause.getMessage());
		// TODO Auto-generated constructor stub
	}

	public AnalysisExecutionException(String message, Throwable cause) {
		super(message, cause);
		log.error("message: " + message + " - cause: " + cause.getMessage());
	}

}

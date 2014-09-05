package edu.jhu.cvrg.analysis.util;

import org.apache.log4j.Logger;

public class AnalysisExecutionException extends Exception{

	private static final long serialVersionUID = 6794589448142833475L;
	private Logger log = Logger.getLogger(AnalysisExecutionException.class);
	
	public AnalysisExecutionException() {
		// TODO Auto-generated constructor stub
	}

	public AnalysisExecutionException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public AnalysisExecutionException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public AnalysisExecutionException(String message, Throwable cause) {
		super(message, cause);
		log.error(message + " - " + cause.getMessage());
	}

}

package edu.jhu.cvrg.analysis.util;

import org.apache.log4j.Logger;

public class AnalysisFailureException extends Exception{

	private static final long serialVersionUID = 6794589448142833475L;
	private Logger log = Logger.getLogger(AnalysisFailureException.class);
	
	public AnalysisFailureException() {
		// TODO Auto-generated constructor stub
	}

	public AnalysisFailureException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public AnalysisFailureException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public AnalysisFailureException(String message, Throwable cause) {
		super(message, cause);
		log.error(message + " - " + cause.getMessage());
	}

}

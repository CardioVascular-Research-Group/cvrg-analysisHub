package edu.jhu.cvrg.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.jhu.cvrg.analysis.vo.AnalysisVO;


public class TestBase {

	private static int jobId = 1;
	
	protected static String TEST_FILE_PATH = "/opt/liferay/mavenTestResources/analysisHub/";
	
	public int getJobId(){
		return jobId++;
	}
	
	protected List<String> getInputFiles(){
		List<String> inputFileNames = new ArrayList<String>();
		inputFileNames.add(TEST_FILE_PATH + "twa01.hea");
		inputFileNames.add(TEST_FILE_PATH + "twa01.dat");
		return inputFileNames;
	}
	
	public void clean(AnalysisVO vo){
		if(vo.getFileNames() != null && !vo.getFileNames().isEmpty()){
			File inputFile = new File(vo.getFileNames().get(0));
			File parent = inputFile.getParentFile();
			
			for (File f : parent.listFiles()) {
				if(f.getAbsolutePath().matches(".*\\.(w?qrs|atr|csv)$") ){
					f.delete();
				}
			}
		}
	}
}

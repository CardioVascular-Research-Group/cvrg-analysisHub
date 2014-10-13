package edu.jhu.cvrg.analysis;

import java.io.File;

import edu.jhu.cvrg.analysis.vo.AnalysisVO;


public class TestBase {

	private static int jobId = 1;
	
	public int getJobId(){
		return jobId++;
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

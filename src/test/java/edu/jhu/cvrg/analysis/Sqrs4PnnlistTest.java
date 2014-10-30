package edu.jhu.cvrg.analysis;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import edu.jhu.cvrg.analysis.vo.AnalysisResultType;
import edu.jhu.cvrg.analysis.vo.AnalysisType;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.analysis.wrapper.AnalysisWrapper;

public class Sqrs4PnnlistTest extends TestBase{
	
	AnalysisType type = AnalysisType.SQRS4PNNLIST;
	
	@Test
	public void testJSON() {
 		
		AnalysisResultType resulType = AnalysisResultType.JSON_DATA;
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		AnalysisVO analysis = new AnalysisVO(String.valueOf(this.getJobId()), type, resulType, this.getInputFiles(), params);
		
		try {
			
			AnalysisWrapper algorithm = analysis.getType().getWrapper().getConstructor(AnalysisVO.class).newInstance(analysis);
			
			algorithm.defineInputParameters();
			algorithm.execute();
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		Assert.assertTrue(analysis.isSucess());
		
		clean(analysis);
	}
	
	@Test
	public void testCSV() {
 
		AnalysisResultType resulType = AnalysisResultType.CSV_FILE;
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		AnalysisVO analysis = new AnalysisVO(String.valueOf(this.getJobId()), type, resulType, this.getInputFiles(), params);
		
		try {
			
			AnalysisWrapper algorithm = analysis.getType().getWrapper().getConstructor(AnalysisVO.class).newInstance(analysis);
			
			algorithm.defineInputParameters();
			algorithm.execute();
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		Assert.assertTrue(analysis.isSucess());
		
		clean(analysis);
	}
	
	@Test
	public void testOriginal() {
 
		AnalysisResultType resulType = AnalysisResultType.ORIGINAL_FILE;
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		AnalysisVO analysis = new AnalysisVO(String.valueOf(this.getJobId()), type, resulType, this.getInputFiles(), params);
		
		try {
			
			AnalysisWrapper algorithm = analysis.getType().getWrapper().getConstructor(AnalysisVO.class).newInstance(analysis);
			
			algorithm.defineInputParameters();
			algorithm.execute();
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		Assert.assertFalse(analysis.isSucess());
		clean(analysis);
	}
}

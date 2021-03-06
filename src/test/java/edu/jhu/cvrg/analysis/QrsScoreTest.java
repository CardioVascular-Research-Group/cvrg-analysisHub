package edu.jhu.cvrg.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import edu.jhu.cvrg.analysis.vo.AnalysisResultType;
import edu.jhu.cvrg.analysis.vo.AnalysisType;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.analysis.wrapper.AnalysisWrapper;

public class QrsScoreTest extends TestBase{
	
	AnalysisType type = AnalysisType.QRS_SCORE;
	
//	@Test
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
		
		System.out.println(analysis.getOutputData());
		
		Assert.assertTrue(analysis.isSucess());
		
		clean(analysis);
	}
	
//	@Test
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
	
//	@Test
	public void testOriginal() {
 
		AnalysisResultType resulType = AnalysisResultType.ORIGINAL_FILE;
		
		List<String> inputFileNames = new ArrayList<String>();
		inputFileNames.add("/home/avilard4/testAnalysis/twa01.hea");
		inputFileNames.add("/home/avilard4/testAnalysis/twa01.dat");
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		AnalysisVO analysis = new AnalysisVO(String.valueOf(this.getJobId()), type, resulType, inputFileNames, params);
		
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
}

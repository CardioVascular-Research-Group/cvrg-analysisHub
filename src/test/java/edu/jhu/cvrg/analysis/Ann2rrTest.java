package edu.jhu.cvrg.analysis;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import edu.jhu.cvrg.analysis.vo.AnalysisResultType;
import edu.jhu.cvrg.analysis.vo.AnalysisType;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.analysis.wrapper.AnalysisWrapper;

public class Ann2rrTest extends TestBase{
	
	AnalysisType type = AnalysisType.NGUESS;
	
	@Test
	public void testOriginal() {
 
		AnalysisResultType resulType = AnalysisResultType.ORIGINAL_FILE;
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		AnalysisVO analysis = new AnalysisVO(String.valueOf(this.getJobId()), AnalysisType.SQRS, AnalysisResultType.ORIGINAL_FILE, this.getInputFiles(), params);
		
		analysis.setRename(false);
		
		try {
			
			AnalysisWrapper algorithm = analysis.getType().getWrapper().getConstructor(AnalysisVO.class).newInstance(analysis);
			
			algorithm.defineInputParameters();
			algorithm.execute();
			
			analysis.getFileNames().addAll(analysis.getOutputFileNames());
			
			analysis = new AnalysisVO(String.valueOf(this.getJobId()), type, resulType, analysis.getFileNames(), params);
			
			algorithm = analysis.getType().getWrapper().getConstructor(AnalysisVO.class).newInstance(analysis);
			
			algorithm.defineInputParameters();
			algorithm.execute();
			
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		Assert.assertTrue(analysis.isSucess());
		
		clean(analysis);
	}
}

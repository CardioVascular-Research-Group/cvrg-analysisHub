package edu.jhu.cvrg.analysis;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.jhu.cvrg.analysis.util.AnalysisExecutionException;
import edu.jhu.cvrg.analysis.util.AnalysisParameterException;
import edu.jhu.cvrg.analysis.vo.AnalysisResultType;
import edu.jhu.cvrg.analysis.vo.AnalysisType;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.analysis.wrapper.AnalysisWrapper;

public class MainAnalysisTest {
	
	private AnalysisVO analysis = null;
	
	@Before
	public void setup(){
		String jobId = "1";
		AnalysisType type = AnalysisType.SQRS4IHR;
		AnalysisResultType resulType = AnalysisResultType.JSON_DATA;
		
		List<String> inputFileNames = new ArrayList<String>();
		inputFileNames.add("/home/avilard4/testAnalysis/twa01.hea");
		inputFileNames.add("/home/avilard4/testAnalysis/twa01.dat");
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		analysis = new AnalysisVO(jobId, type, resulType, inputFileNames, params);
	}
	
	@Test
	public void testAnalysis() {
 
		try {
			AnalysisWrapper algorithm = analysis.getType().getWrapper().getConstructor(AnalysisVO.class).newInstance(analysis);
			
			algorithm.defineInputParameters();
			algorithm.execute();
			
			System.out.println("INPUT FILES");
			for (String string : analysis.getFileNames()) {
				System.out.println(string);
			}
			
			switch (analysis.getResultType()) {
			case JSON_DATA:
				System.out.println("OUTPUT JSON DATA");
				System.out.println(analysis.getOutputData());
				break;

			default:
				System.out.println("OUTPUT FILES");
				for (String string : analysis.getOutputFileNames()) {
					System.out.println(string);
				}
				break;
			}
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (AnalysisExecutionException e) {
			e.printStackTrace();
		} catch (AnalysisParameterException e) {
			e.printStackTrace();
		}
		
		Assert.assertTrue(analysis.isSucess());
	}
}

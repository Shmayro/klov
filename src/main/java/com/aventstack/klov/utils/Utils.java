package com.aventstack.klov.utils;

import java.util.List;

import com.aventstack.klov.domain.Test;
import com.aventstack.klov.repository.custom.TestRepositoryCustom;

public class Utils {
	
	private static TestRepositoryCustom testRepository;
	
	/**
	 * @param testRepository the testRepository to set
	 */
	public static void setTestRepository(TestRepositoryCustom testRepository) {
		Utils.testRepository = testRepository;
	}

	public static long countPassedSubTests(Test test) {
		if(test!=null && test.getChildNodesLength()!=null && test.getChildNodesLength()>0) {
			return testRepository.findNodes(test.getId()).parallelStream().filter(subTest->subTest.getStatus().equals("pass")).count();
		}
		return 0;
	}
	
	public static long countFailedSubTests(Test test) {
		if(test!=null && test.getChildNodesLength()!=null && test.getChildNodesLength()>0) {
			return testRepository.findNodes(test.getId()).parallelStream().filter(subTest->List.of("fail","fatal","error").contains(subTest.getStatus())).count();
		}
		return 0;
	}
	
	public static long countOthersSubTests(Test test) {
		if(test!=null && test.getChildNodesLength()!=null && test.getChildNodesLength()>0) {
			testRepository.findNodes(test.getId()).parallelStream().filter(subTest->List.of("skip","warning").contains(subTest.getStatus())).count();
		}
		return 0;
	}
	
}

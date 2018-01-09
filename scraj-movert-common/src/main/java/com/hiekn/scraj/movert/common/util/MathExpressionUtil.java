package com.hiekn.scraj.movert.common.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

public class MathExpressionUtil {
	
	private static final Logger LOGGER = Logger.getLogger(MathExpressionUtil.class);
	private static final ScriptEngine JAVA_SCRIPT_ENGINE = new ScriptEngineManager().getEngineByName("JavaScript");
	
	public static final String eval(String expression) throws ScriptException {
		LOGGER.info("eval start... " + expression);
		String result = JAVA_SCRIPT_ENGINE.eval(expression).toString().replaceAll("\\..*", "");
		LOGGER.info("eval done... " + result);
		return result;
	}
}

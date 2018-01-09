package com.hiekn.sfe4j.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hiekn.sfe4j.util.ConstResource;
import com.hiekn.sfe4j.util.StringUtils;

/**
 * 
 * 
 * 
 * 
 * @author pzn
 * @since 1.7
 * @version 2016/07/19
 *
 */
public class FliverContext {
	//
	private static final Logger LOGGER = Logger.getLogger(FliverContext.class);
	
	private static final String CUSTOMS_FORMATTER_PATH = ConstResource.FILVER_CUSTOM_PATH;
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final Charset UTF_8_CHAR_SET = Charset.forName("utf-8");
	
	
	private static final String SFE4J_FILTER_TYPE = "sfe4j.filter";
	private static final String SFE4J_FINDER_TYPE = "sfe4j.finder";
	private static final String SFE4J_REPLACER_TYPE = "sfe4j.replacer";
	private static final String SFE4J_SUBSTRING_TYPE = "sfe4j.substring";
	private static final String SFE4J_TIME_TYPE = "sfe4j.time";
	//
	
	private Map<String, String> registeredFormatterMappings;
	
	private int filterNo;
	private int finderNo;
	private int replacerNo;
	private int substringNo;
	private int timeStdNo;
	
	public Map<String, String> registeredFormatterMappings() {
		return registeredFormatterMappings;
	}
	
	public String findFormatter(String fno) {
		return registeredFormatterMappings.get(fno);
	}
	
	public void registerFormatter(String fJson) throws IOException {
		//
		if (StringUtils.isNullOrEmpty(CUSTOMS_FORMATTER_PATH)) 
			throw new NullPointerException("自定义格式器存储文件路径未指定，请在sfe4j.properties中配置filver_custom_path这个属性.");
		
		//
		Files.write(new File(CUSTOMS_FORMATTER_PATH).toPath(), 
				(LINE_SEPARATOR + fJson).getBytes(UTF_8_CHAR_SET), 
				StandardOpenOption.CREATE, 
				StandardOpenOption.APPEND);
		
		if (fJson.indexOf(SFE4J_FILTER_TYPE) > -1) registeredFormatterMappings.put("" + filterNo++, fJson);
		else if (fJson.indexOf(SFE4J_FINDER_TYPE) > -1) registeredFormatterMappings.put("" + finderNo++, fJson);
		else if (fJson.indexOf(SFE4J_REPLACER_TYPE) > -1) registeredFormatterMappings.put("" + replacerNo++, fJson);
		else if (fJson.indexOf(SFE4J_SUBSTRING_TYPE) > -1) registeredFormatterMappings.put("" + substringNo++, fJson);
		else if (fJson.indexOf(SFE4J_TIME_TYPE) > -1) registeredFormatterMappings.put("" + timeStdNo++, fJson);
		
	}
	
	public FliverContext() throws IOException, URISyntaxException {
		initial();
	}
	
	
	//
	// 初始化mapping
	//
	private void initial() throws IOException, URISyntaxException {
		LOGGER.info("init registered formatter start ... ");
		
		//
		filterNo = 1000;
		finderNo = 2000;
		replacerNo = 3000;
		substringNo = 4000;
		timeStdNo = 5000;
		
		registeredFormatterMappings = new HashMap<String, String>();
		
		try (BufferedReader drd = new BufferedReader(new InputStreamReader(getClass()
				.getClassLoader().getResourceAsStream("defaults.json"), UTF_8_CHAR_SET))) {
			String line;
			while (null != (line = drd.readLine())) {
				// 空行和注释行去掉
				if (line.length() == 0 || line.indexOf("#") > -1 || line.length() < 5) continue;
				
				// 
				if (line.indexOf(SFE4J_FILTER_TYPE) > -1) registeredFormatterMappings.put("" + filterNo++, line);
				else if (line.indexOf(SFE4J_FINDER_TYPE) > -1) registeredFormatterMappings.put("" + finderNo++, line);
				else if (line.indexOf(SFE4J_REPLACER_TYPE) > -1) registeredFormatterMappings.put("" + replacerNo++, line);
				else if (line.indexOf(SFE4J_SUBSTRING_TYPE) > -1) registeredFormatterMappings.put("" + substringNo++, line);
				else if (line.indexOf(SFE4J_TIME_TYPE) > -1) registeredFormatterMappings.put("" + timeStdNo++, line);
			}
		}
		
		
		if (!StringUtils.isNullOrEmpty(CUSTOMS_FORMATTER_PATH)) {
			List<String> lines = Files.readAllLines(new File(CUSTOMS_FORMATTER_PATH).toPath(), UTF_8_CHAR_SET);
			for (String line : lines) {
				// 空行和注释行去掉
				if (line.length() == 0 || line.indexOf("#") > -1 || line.length() < 5) continue;
				
				// 
				if (line.indexOf(SFE4J_FILTER_TYPE) > -1) registeredFormatterMappings.put("" + filterNo++, line);
				else if (line.indexOf(SFE4J_FINDER_TYPE) > -1) registeredFormatterMappings.put("" + finderNo++, line);
				else if (line.indexOf(SFE4J_REPLACER_TYPE) > -1) registeredFormatterMappings.put("" + replacerNo++, line);
				else if (line.indexOf(SFE4J_SUBSTRING_TYPE) > -1) registeredFormatterMappings.put("" + substringNo++, line);
				else if (line.indexOf(SFE4J_TIME_TYPE) > -1) registeredFormatterMappings.put("" + timeStdNo++, line);
			}
		}
		
		//
		LOGGER.info("init registered formatter done ... ");
	}
	
}

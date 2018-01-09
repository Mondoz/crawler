package com.hiekn.scraj.rest.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.hiekn.scraj.rest.service.MonitorService;

@Path("/monitor")
public class MonitorApi {
	
	@GET
	@Path("/{pageNo}/{pageSize}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
	public Response getMonitorInfo(@PathParam("pageNo") int pageNo, 
								   @PathParam("pageSize") int pageSize, 
								   @QueryParam("queryDate") String queryDate) {
		
		if (queryDate == null || queryDate.equals("")) {
			queryDate = sd.format(new Date());
		}
		Map<String, Object> monitorInfo = null;
		try {
			monitorInfo = MonitorService.getMonitorInfo(pageNo, pageSize,queryDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.ok(monitorInfo).build();
	}
	
	@GET
	@Path("/{taskId}/data")
	@Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
	public Response getMonitorData(@PathParam("taskId") String taskId, 
									@QueryParam("pageNo") int pageNo, 
									@QueryParam("pageSize") int pageSize) {
		Map<String, Object> rsMap = new HashMap<>();
		try {
			
			Map<String, Object> data = MonitorService.getMonitorData(taskId, pageNo, pageSize);
			rsMap.put("code", 200);
			rsMap.put("status", "ok");
			rsMap.put("data", data);
			
//			int rsCount = 1;
//			List<Map<String, Object>> rsData = new ArrayList<>();
//			Map<String, Object> testData = new HashMap<>();
//			testData.put("id", "abcd123456");
//			testData.put("title", "测试标题");
//			testData.put("date", "2015-10-14");
//			rsData.add(testData);
//			
//			Map<String, Object> data = new HashMap<>();
//			data.put("rsCount", rsCount);
//			data.put("rsData", rsData);
//			
//			rsMap.put("code", 200);
//			rsMap.put("status", "ok");
//			rsMap.put("data", data);
			
		} catch (Exception e) {
			e.printStackTrace();
			
			rsMap.put("code", 500);
			rsMap.put("status", "error");
			rsMap.put("data", e.getMessage());
		}
		
		return Response.ok().entity(rsMap).build();
	}
	
	
	@GET
	@Path("/{taskId}/data/{id}")
	@Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
	public Response getMonitorDataInfo(@PathParam("taskId") String taskId, 
										@PathParam("id") String id) {
		Map<String, Object> rsMap = new HashMap<>();
		try {
//			Map<String, Object> data = MonitorService.getMonitorDataInfo(id);
//			rsMap.put("code", 200);
//			rsMap.put("status", "ok");
//			rsMap.put("data", data);
			
			Map<String, Object> testData = new HashMap<>();
			testData.put("content", "测试内容");
			testData.put("author", "测试作者");
			
			rsMap.put("code", 200);
			rsMap.put("status", "ok");
			rsMap.put("data", testData);
		} catch (Exception e) {
			e.printStackTrace();
			
			rsMap.put("code", 500);
			rsMap.put("status", "error");
			rsMap.put("data", e.getMessage());
		}
		
		return Response.ok().entity(rsMap).build();
	}
	
	
	private final SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
}
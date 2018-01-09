package com.hiekn.scraj.rest.api;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.hiekn.scraj.rest.service.DynamicPreviewService;
import com.hiekn.scraj.rest.service.StaticPreviewService;

@Path("/preview")
public class PreviewApi {
	
	/**
	 * 
	 * 静态采集配置预览
	 * 
	 * @param id
	 * @param conf
	 * @return
	 */
	@POST
	@Path("/static")
	@Produces("application/json; charset=UTF-8")
	public Response staticPreview(
			@FormParam("id") String id,
			@FormParam("conf") String conf) {
		Map<String, Object> rsMap = new HashMap<>();
		try {
			Object data = StaticPreviewService.preview(conf);
			rsMap.put("code", 200);
			rsMap.put("status", "ok");
			rsMap.put("data", data);
		} catch(Exception e) {
			e.printStackTrace();
			
			rsMap.put("code", 500);
			rsMap.put("status", "error");
			rsMap.put("data", e.getMessage());
		}
		return Response.ok().entity(rsMap).build();
	}
	
	/**
	 * 
	 * 动态采集配置预览
	 * 
	 * @param id
	 * @param conf
	 * @return
	 */
	@POST
	@Path("/dynamic")
	@Produces("application/json; charset=UTF-8")
	public Response dynamicPreview(
			@FormParam("id") String id,
			@FormParam("conf") String conf) {
		Map<String, Object> rsMap = new HashMap<>();
		try {
			Object data = DynamicPreviewService.preview(conf);
			rsMap.put("code", 200);
			rsMap.put("status", "ok");
			rsMap.put("data", data);
		} catch(Exception e) {
			e.printStackTrace();
			
			rsMap.put("code", 500);
			rsMap.put("status", "error");
			rsMap.put("data", e.getMessage());
		}
		return Response.ok().entity(rsMap).build();
	}
	
}

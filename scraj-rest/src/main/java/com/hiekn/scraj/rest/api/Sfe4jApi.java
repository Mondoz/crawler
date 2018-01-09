package com.hiekn.scraj.rest.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.hiekn.scraj.rest.service.Sfe4jService;

@Path("/sfe")
public class Sfe4jApi {
	
	@GET
	@Path("/formatters")
	@Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
	public Response getDefaultFormatters(@QueryParam("tt") long tt) {
		Map<String, Object> resp = new HashMap<>();
		try {
			List<Map<String, String>> list = Sfe4jService.getDefaultFormatters();
			resp.put("code", 200);
			resp.put("status", "ok");
			resp.put("data", list);
		} catch (Exception e) {
			e.printStackTrace();
			resp.put("code", 500);
			resp.put("status", "error");
			resp.put("data", "服务器未知错误... ");
		}
		return Response.ok().entity(resp).build();
	}
	
}

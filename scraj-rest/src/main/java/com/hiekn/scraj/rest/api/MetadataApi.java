package com.hiekn.scraj.rest.api;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.hiekn.scraj.rest.service.MetadataService;

@Path("/")
public class MetadataApi {
	
	@POST
	@Path("/save")
	public String saveService(@FormParam("name") String name, 
							@FormParam("intervalTime") long intervalTime,
							@FormParam("intervalExp") String intervalExp, 
							@FormParam("intervalType") int intervalType,
							@FormParam("cookie") String cookie, 
							@FormParam("conf") String conf, 
							@FormParam("confType") int confType,
							@FormParam("confPriority") int confPriority, 
							@FormParam("confParallel") int confParallel, 
							@FormParam("confGraph") String confGraph,
							@FormParam("completed") int completed, 
							@FormParam("groupId") int groupId) {
		
		String info = "";
		try {
			info = MetadataService.save(name,intervalTime,intervalExp,intervalType,cookie,conf,confType,confGraph,confPriority,confParallel,completed,groupId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
	
	@POST
	@Path("/modifyInfo")
	public String modifyInfo(@FormParam("id") int id, 
							@FormParam("name") String name, 
							@FormParam("intervalTime") long intervalTime,
							@FormParam("intervalExp") String intervalExp, 
							@FormParam("intervalType") int intervalType,
							@FormParam("cookie") String cookie, 
							@FormParam("confType") int confType,	
							@FormParam("confPriority") int confPriority,
							@FormParam("confParallel") int confParallel, 
							@FormParam("completed") int completed, 
							@FormParam("group_id") int groupId)  {
		
		String info = "";
		try {
			info = MetadataService.modifyInfo(id,name,intervalTime,intervalExp,intervalType,cookie,confType,confPriority,confParallel,completed,groupId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
	
	@POST
	@Path("/modifyConf")
	public String modifyConf(@FormParam("id") int id, 
							@FormParam("conf") String conf, 
							@FormParam("confGraph") String confGraph, 
							@FormParam("completed") int completed) {
		String info = "";
		try {
			info = MetadataService.modifyConf(id,conf,confGraph,completed);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
	
	
	@POST
	@Path("/control")
	public String switchSpider(@FormParam("id") int id, 
								@FormParam("switchFlag") int switchFlag)  {
		
		String info =  "";
		try {
			info = MetadataService.modifySwitchFlag(id,switchFlag);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return info;
	}
	
	@GET
	@Path("/getConf/{id}")
	@Produces("application/json;charset=UTF-8")
	public static String getConf(@PathParam("id") int id){
		String info = "";
		try {
			info = MetadataService.getConf(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
	
	@GET
	@Path("/infoList/{pageNo}/{pageSize}")
	@Produces("application/json;charset=UTF-8")
	public static String getInfoList(@PathParam("pageNo") int pageNo, 
									@PathParam("pageSize") int pageSize) {
		String info = "";
		try {
			info = MetadataService.getInfoList(pageNo,pageSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
	
	@POST
	@Path("/delete/{id}")
	@Produces("application/json;charset=UTF-8")
	public static String deleteMatadata(@PathParam("id") int id) {
		String info = "";
		try {
			info = MetadataService.deleteMetadata(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
}

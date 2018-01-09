package com.hiekn.scraj.rest.api;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.hiekn.scraj.rest.service.MetadataService;

@Path("/template")
public class ConfTemplateApi {
	
	@GET
	@Path("/GetConfigTemplate")
	public static String getConfigTemplate(@QueryParam("pageSize") int pageSize, 
											@QueryParam("pageNo") int pageNo, 
											@QueryParam("confType") int confType) {
		
		String configTemplate = "";
		try {
			configTemplate = MetadataService.getConfigTemplate(pageNo, pageSize,confType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return configTemplate;
	}
	
	@GET
	@Path("/GetFlowTemplate")
	public static String getFlowTemplate( @QueryParam("pageSize") int pageSize, 
										  @QueryParam("pageNo") int pageNo, 
										  @QueryParam("confType") int confType) {
		
		String flowTemplate = "";
		try {
			flowTemplate = MetadataService.getFlowTemplate(pageNo, pageSize,confType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flowTemplate;
	}
	
	@GET
	@Path("/GetIdConfigTemplate")
	public static String getIdConfigTemplate(@QueryParam("id") int id) {
		String configIdTemplate = "";
		try {
			configIdTemplate = MetadataService.getIdConfigTemplate(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return configIdTemplate;
	}
	
	@GET
	@Path("/GetIdFlowTemplate")
	public static String getIdFlowTemplate(@QueryParam("id") int id) {
		
		String flowIdTemplate = "";
		try {
			flowIdTemplate = MetadataService.getIdFlowTemplate(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flowIdTemplate;
	}
	
	@POST
	@Path("/SaveConfigTemplate")
	public static String saveConfigTemplate(@FormParam("name") String name, 
											@FormParam("conf") String conf, 
											@FormParam("confGraph") String confGraph, 
											@FormParam("confType") int confType) {
		
		String configTemplate = "";
		try {
			configTemplate = MetadataService.saveConfigTemplate(name, conf, confGraph,confType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return configTemplate;
	}
	
	@POST
	@Path("/SaveFlowTemplate")
	public static String saveFlowTemplate(@FormParam("name") String name,@FormParam("conf") String conf,@FormParam("confGraph") String confGraph,@FormParam("confType") int confType) {
		String flowTemplate = "";
		try {
			flowTemplate = MetadataService.saveFlowTemplate(name, conf, confGraph,confType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flowTemplate;
	}
	
	@POST
	@Path("/DeleteConfigTemplate")
	public static String deleteConfigTemplate(@FormParam("id") int id) {
		String info = "";
		try {
			info = MetadataService.deleteConfigTemplate(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
	
	@POST
	@Path("/DeleteFlowTemplate")
	public static String deleteFlowTemplate(@FormParam("id") int id) {
		String info = "";
		try {
			info = MetadataService.deleteFlowTemplate(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return info;
	}
	
	@POST
	@Path("/ModifyConfigTemplate")
	public static String modifyConfigTemplate(@FormParam("id") int id,@FormParam("name") String name,@FormParam("conf") String conf,@FormParam("confGraph") String confGraph,@FormParam("confType") int confType) {
		String info = "";
		try {
			info = MetadataService.modifyConfigTemplate(id,name,conf,confGraph,confType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
	
	@POST
	@Path("/ModifyFlowTemplate")
	public static String modifyFlowTemplate(@FormParam("id") int id,@FormParam("name") String name,@FormParam("conf") String conf,@FormParam("confGraph") String confGraph,@FormParam("confType") int confType) {
		String info = "";
		try {
			info = MetadataService.modifyFlowTemplate(id,name,conf,confGraph,confType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
}

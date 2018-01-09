package com.hiekn.scraj.rest.api;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.hiekn.scraj.rest.service.VisualService;
import com.hiekn.scraj.uyint.common.http.HttpReader;
import com.hiekn.scraj.uyint.common.http.impl.ApacheHttpReader;

@Path("/visual")
public class VisuallyApi {
	
	@GET
	@Path("/assert/html/{id}")
	@Produces(MediaType.TEXT_HTML + "; charset=utf-8")
	public Response getHtmlAssert(@PathParam("id") String id, 
								  @QueryParam("url") String url,
								  @QueryParam("tt") long tt) {
		String html = "";
		try {
			// 读mongo 不存在则 请求源码 并保存
			html = VisualService.getHtml(id);
			if (html.length() < 1) {
				HttpReader rd = new ApacheHttpReader();
				html = rd.readSource(url);
				rd.close();
				
				
				StringBuilder sb = new StringBuilder(50).append("<head>")
						.append("\n")
						.append("	<base href=\""+url+"\" />")
						;
				html = html.replaceFirst("<head>", sb.toString());
				
				// 源码里面链接地址转换
//				String abstractPath = "";// 不包括最后一个/
//				int idx = url.indexOf("/", url.indexOf("//") + 2);
//				if (idx > -1) 
//					abstractPath = url.substring(0, idx);
//				else
//					abstractPath = url;
//				//
//				String relativePath = "";// 不包括最后一个/
//				idx = url.lastIndexOf("/");
//				if (idx > 6)
//					relativePath = url.substring(0, idx);
//				else
//					relativePath = url;
//				// 替换绝对地址 :即以一个/开头
//				html = html.replaceAll("href=(['\"]?+)(/{1}(?!/).+)\\1", "href=$1" + abstractPath + "$2$1");
//				html = html.replaceAll("src=(['\"]?+)(/{1}(?!/).+)\\1", "src=$1" + abstractPath + "$2$1");
//				// 替换相对地址
//				html = html.replaceAll("href=(['\"]?+)((?!/|http).+)\\1", "href=$1" + relativePath + "/$2$1");
//				html = html.replaceAll("src=(['\"]?+)((?!/|http).+)\\1", "src=$1" + relativePath + "/$2$1");
				
				//
				VisualService.saveHtml(id, url, html);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Response.ok().entity(html).build();
	}
	
	// href="/r/s/e" or src="/r/s/e"
	static Pattern ABS_URL_PATTERN = Pattern.compile("href=(['\"]?+)(/{1}(?!/).+)\\1|src=(['\"]?+)(/{1}(?!/).+)\\3");
	// href="r/s/e" or src="r/s/e"
	static Pattern REL_URL_PATTERN = Pattern.compile("href=(['\"]?+)((?!/|http).+)\\1|src=(['\"]?+)((?!/|http).+)\\3");
}

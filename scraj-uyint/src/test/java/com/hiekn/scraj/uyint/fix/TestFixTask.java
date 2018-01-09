package com.hiekn.scraj.uyint.fix;

import java.util.List;

import org.junit.Test;

import com.google.gson.reflect.TypeToken;
import com.hiekn.scraj.uyint.act.StaticDedupActImpl;
import com.hiekn.scraj.uyint.act.StaticStoreDataActImpl;
import com.hiekn.scraj.uyint.common.act.StaticAct;
import com.hiekn.scraj.uyint.common.act.StaticDedupAct;
import com.hiekn.scraj.uyint.common.act.StaticStoreDataAct;
import com.hiekn.scraj.uyint.common.core.ApacheActor;

public class TestFixTask {
	
	@Test
	public void test1() {
		String cookie = "[\"ASP.NET_SessionId=qf2p41vweljisxyuak2mlp04; UM_distinctid=15e9904497e200-05611c59bf161b-5c1b3517-1fa400-15e9904497f740; CNZZDATA4400375=cnzz_eid%3D683963076-1505802726-%26ntime%3D1505802726\"]";
		List<String> cookieList = StaticAct.GSON.fromJson(cookie, new TypeToken<List<String>>() {}.getType());;
		System.out.println("ssf");
	}
	
	@Test
	public void test163() throws Exception {
//		String commandJson = "[{\"staticOpen\":{\"urls\":[\"http://money.163.com/special/00252C1E/gjcj.html\"],\"requestType\":\"get\",\"charset\":\"gbk\",\"params\":\"\",\"formatter\":{\"defaults\":[],\"customs\":[]}}},{\"staticPagination\":{\"maxPage\":5,\"batchPage\":5,\"splitPage\":20,\"sleepMillis\":1000,\"requestType\":\"get\",\"params\":\"\",\"paginationMeta\":{\"strategy\":\"rule\",\"paginationUrls\":[\"http://money.163.com/special/00252C1E/gjcj_(pn).html\"],\"paginationRule\":\"currentPage\",\"numFromPage\":\"\",\"stdLength\":2,\"leftAlign\":\"0\",\"rightAlign\":\"\"},\"innerCommands\":[{\"staticFetchList\":{\"field\":\"\",\"byType\":\"byCss\",\"typeValue\":\"div.col_l > div.list_item.clearfix\",\"innerCommands\":[{\"staticFetchKVs\":{\"includeSource\":false,\"fetchValueWithKeys\":[{\"field\":\"title\",\"byType\":\"byCss\",\"typeValue\":\"h2 > a\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"url\",\"byType\":\"byCss\",\"typeValue\":\"h2 > a\",\"byAttr\":\"href\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"publishTime\",\"byType\":\"byCss\",\"typeValue\":\"p > span.time\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}}]}}]}}]}},{\"store\":{\"fields\":[],\"mappingFields\":[],\"storeMeta\":{\"engine\":\"jdbc\",\"driver\":\"com.mysql.jdbc.Driver\",\"url\":\"jdbc:mysql://192.168.1.156:3306/data\",\"user\":\"root\",\"password\":\"root@ecust4poa\",\"table\":\"test_data\"}}}]";
		String commandJson = "[ { \"staticOpen\": { \"urls\": [ \"https://www.infoq.com/articles/\" ], \"requestType\": \"get\", \"charset\": \"\", \"params\": \"\", \"formatter\": { \"defaults\": [ ], \"customs\": [ ] } } }, { \"staticPagination\": { \"maxPage\": 1, \"batchPage\": 5, \"splitPage\": 20, \"sleepMillis\": 1000, \"requestType\": \"get\", \"params\": \"\", \"paginationMeta\": { \"strategy\": \"rule\", \"paginationUrls\": [ \"https: //www.infoq.com/articles/(pn)\" ], \"paginationRule\": \"currentPage*12\", \"numFromPage\": \"\", \"stdLength\": 0, \"leftAlign\": \"\", \"rightAlign\": \"\" }, \"innerCommands\": [ { \"staticFetchList\": { \"field\": \"\", \"byType\": \"byCss\", \"typeValue\": \"div.news_type2.full_screen\", \"innerCommands\": [ { \"staticFetchKVs\": { \"includeSource\": false, \"fetchValueWithKeys\": [ { \"field\": \"title\", \"byType\": \"byCss\", \"typeValue\": \"h2>a\", \"byAttr\": \"innerText\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [ ], \"customs\": [ ] } }, { \"field\": \"author\", \"byType\": \"byCss\", \"typeValue\": \"span.authors-list>span.followable>a\", \"byAttr\": \"innerText\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [ ], \"customs\": [ ] } }, { \"field\": \"date\", \"byType\": \"byCss\", \"typeValue\": \"span.author\", \"byAttr\": \"ownText\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [ \"5000\" ], \"customs\": [ ] } }, { \"field\": \"abs\", \"byType\": \"byCss\", \"typeValue\": \"p\", \"byAttr\": \"innerText\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [ ], \"customs\": [ ] } }, { \"field\": \"url\", \"byType\": \"byCss\", \"typeValue\": \"h2>a\", \"byAttr\": \"href\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [ ], \"customs\": [ { \"type\": \"sfe4j.complete\", \"prefix\": \"https: //www.infoq.com\", \"prefixStd\": \"\", \"suffix\": \"\", \"suffixStd\": \"\" } ] } }, { \"field\": \"source\", \"byType\": \"byConstant\", \"typeValue\": \"infoq\", \"byAttr\": \"\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [ ], \"customs\": [ ] } } ] } } ] } } ] } }, { \"dedup\": { \"fields\": [ \"url\" ], \"mappingFields\": [ ], \"dedupMeta\": { \"engine\": \"mongodb\", \"host\": \"localhost\", \"port\": 27017, \"user\": \"\", \"password\": \"\", \"database\": \"dedup\", \"collection\": \"article_dedup\" } } }, { \"staticHandleList\": { \"sleepMillis\": 1000, \"innerCommands\": [ { \"staticOpen\": { \"urls\": [ \"$url\" ], \"requestType\": \"get\", \"charset\": \"\", \"params\": \"\", \"formatter\": { \"defaults\": [ ], \"customs\": [ ] } } }, { \"staticFetchKVs\": { \"includeSource\": false, \"fetchValueWithKeys\": [ { \"field\": \"content\", \"byType\": \"byCss\", \"typeValue\": \"div.text_info.text_info_article\", \"byAttr\": \"innerHTML\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [ ], \"customs\": [ ] } } ] } } ] } }, { \"store\": { \"fields\": [ ], \"mappingFields\": [ ], \"storeMeta\": { \"engine\": \"mongodb\", \"host\": \"localhost\", \"port\": 27017, \"user\": \"\", \"password\": \"\", \"database\": \"data\", \"collection\": \"article_data\" } } } ]";
		
		int taskId = 163;
		String taskName = "163";
		int parallel = 0;
		
		StaticDedupAct dedupAct = new StaticDedupActImpl();
		StaticStoreDataAct storeDataAct = new StaticStoreDataActImpl();
		ApacheActor actor = new ApacheActor(dedupAct, storeDataAct);
		String cookie = "[\"ASP.NET_SessionId=qf2p41vweljisxyuak2mlp04; UM_distinctid=15e9904497e200-05611c59bf161b-5c1b3517-1fa400-15e9904497f740; CNZZDATA4400375=cnzz_eid%3D683963076-1505802726-%26ntime%3D1505802726\"]";
		List<String> cookieList = StaticAct.GSON.fromJson(cookie, new TypeToken<List<String>>() {}.getType());;
		actor.actContext().writeCookies(cookieList);
//		actor.actContext().resetCookie();
		actor.perform(taskId, taskName, parallel, commandJson);
		
	}
	
}

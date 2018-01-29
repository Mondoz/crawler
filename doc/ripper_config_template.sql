/*
Navicat MySQL Data Transfer

Source Server         : 192.168.1.156
Source Server Version : 50717
Source Host           : 192.168.1.156:3306
Source Database       : yqcp

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2017-10-10 16:47:23
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ripper_config_template
-- ----------------------------
DROP TABLE IF EXISTS `ripper_config_template`;
CREATE TABLE `ripper_config_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `conf` longtext,
  `conf_graph` longtext,
  `conf_type` int(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ripper_config_template
-- ----------------------------
INSERT INTO `ripper_config_template` VALUES ('1', '模板1', '[{\"staticOpen\":{\"urls\":[\"http://xueshu.baidu.com/s?wd=%E5%93%8D%E5%BA%94%E5%BC%8F%E7%B3%BB%E7%BB%9F&pn=0&tn=SE_baiduxueshu_c1gjeupa&ie=utf-8&sc_f_para=sc_tasktype%3D%7BfirstSimpleSearch%7D&sc_hit=1\"],\"requestType\":\"get\",\"charset\":\"gb2312\",\"params\":\"\",\"formatter\":{\"defaults\":[],\"customs\":[]}}},{\"staticPagination\":{\"maxPage\":5,\"batchPage\":5,\"splitPage\":20,\"sleepMillis\":1000,\"requestType\":\"get\",\"params\":\"\",\"paginationMeta\":{\"strategy\":\"rule\",\"paginationUrls\":[\"http://xueshu.baidu.com/s?wd=%E5%93%8D%E5%BA%94%E5%BC%8F%E7%B3%BB%E7%BB%9F&pn=(pn)&tn=SE_baiduxueshu_c1gjeupa&ie=utf-8&sc_f_para=sc_tasktype%3D%7BfirstSimpleSearch%7D&sc_hit=1\"],\"paginationRule\":\"currentPage * 10\",\"numFromPage\":\"\",\"stdLength\":0,\"leftAlign\":\"\",\"rightAlign\":\"\"},\"innerCommands\":[{\"staticFetchList\":{\"field\":\"\",\"byType\":\"byCss\",\"typeValue\":\"div#bdxs_result_lists > div.sc_default_result.result\",\"innerCommands\":[{\"staticFetchKVs\":{\"includeSource\":false,\"fetchValueWithKeys\":[{\"field\":\"论文标题\",\"byType\":\"byCss\",\"typeValue\":\"h3.t.c_font\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"url\",\"byType\":\"byCss\",\"typeValue\":\"h3.t.c_font > a\",\"byAttr\":\"href\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[{\"type\":\"sfe4j.complete\",\"prefix\":\"http://xueshu.baidu.com\",\"prefixStd\":\"\",\"suffix\":\"\",\"suffixStd\":\"\"}]}},{\"field\":\"论文作者\",\"byType\":\"byCss\",\"typeValue\":\"div.sc_info > span:nth-child(1)\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"type\",\"byType\":\"byConstant\",\"typeValue\":\"论文资源\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}}]}},{\"staticFetchList\":{\"field\":\"资源链接\",\"byType\":\"byCss\",\"typeValue\":\"span.v_item_span\",\"innerCommands\":[{\"staticFetchKVs\":{\"includeSource\":false,\"fetchValueWithKeys\":[{\"field\":\"sourceUrl\",\"byType\":\"byCss\",\"typeValue\":\"a\",\"byAttr\":\"href\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"sourceName\",\"byType\":\"byCss\",\"typeValue\":\"a\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}}]}}]}}]}}]}},{\"dedup\":{\"fields\":[\"论文标题\"],\"mappingFields\":[\"_id\"],\"dedupMeta\":{\"engine\":\"mongodb\",\"host\":\"192.168.1.156\",\"port\":19130,\"user\":\"\",\"password\":\"\",\"database\":\"dedup\",\"collection\":\"huawei\"}}},{\"staticHandleList\":{\"sleepMillis\":1000,\"innerCommands\":[{\"staticOpen\":{\"urls\":[\"$url\"],\"requestType\":\"get\",\"charset\":\"gb2312\",\"params\":\"\",\"formatter\":{\"defaults\":[],\"customs\":[]}}},{\"staticFetchKVs\":{\"includeSource\":false,\"fetchValueWithKeys\":[{\"field\":\"论文摘要\",\"byType\":\"byCss\",\"typeValue\":\"p.abstract\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"出版源\",\"byType\":\"byCss\",\"typeValue\":\"p.publish_text\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"content\",\"byType\":\"byCss\",\"typeValue\":\"p.abstract\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}}]}}]}},{\"store\":{\"fields\":[],\"mappingFields\":[],\"storeMeta\":{\"engine\":\"mongodb\",\"host\":\"192.168.1.156\",\"port\":19130,\"user\":\"\",\"password\":\"\",\"database\":\"data\",\"collection\":\"huawei\"}}}]', '{\"type\":\"0\",\"graph\":[{\"id\":\"1487127970487\",\"process_name\":\"打开网址\",\"resizable\":false,\"process_data\":{\"classid\":\"staticOpen\",\"prop\":{\"urls\":[\"http://xueshu.baidu.com/s?wd=%E5%93%8D%E5%BA%94%E5%BC%8F%E7%B3%BB%E7%BB%9F&pn=0&tn=SE_baiduxueshu_c1gjeupa&ie=utf-8&sc_f_para=sc_tasktype%3D%7BfirstSimpleSearch%7D&sc_hit=1\"],\"requestType\":\"get\",\"charset\":\"gb2312\",\"params\":\"\",\"formatter\":{\"defaults\":[],\"customs\":[]}}},\"process_to\":\"1487128058592\",\"style\":\"left: 504px; top: 71px; width: 120px; height: 35px; right: auto; bottom: auto;\"},{\"id\":\"1487128058592\",\"process_name\":\"循环翻页\",\"resizable\":true,\"process_data\":{\"classid\":\"staticPagination\",\"prop\":{\"maxPage\":5,\"batchPage\":5,\"splitPage\":20,\"sleepMillis\":1000,\"requestType\":\"get\",\"params\":\"\",\"paginationMeta\":{\"strategy\":\"rule\",\"paginationUrls\":[\"http://xueshu.baidu.com/s?wd=%E5%93%8D%E5%BA%94%E5%BC%8F%E7%B3%BB%E7%BB%9F&pn=(pn)&tn=SE_baiduxueshu_c1gjeupa&ie=utf-8&sc_f_para=sc_tasktype%3D%7BfirstSimpleSearch%7D&sc_hit=1\"],\"paginationRule\":\"currentPage * 10\",\"numFromPage\":\"\",\"stdLength\":0,\"leftAlign\":\"\",\"rightAlign\":\"\"},\"innerCommands\":[{\"staticFetchList\":{\"field\":\"\",\"byType\":\"byCss\",\"typeValue\":\"div#bdxs_result_lists > div.sc_default_result.result\",\"innerCommands\":[{\"staticFetchKVs\":{\"includeSource\":false,\"fetchValueWithKeys\":[{\"field\":\"论文标题\",\"byType\":\"byCss\",\"typeValue\":\"h3.t.c_font\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"url\",\"byType\":\"byCss\",\"typeValue\":\"h3.t.c_font > a\",\"byAttr\":\"href\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[{\"type\":\"sfe4j.complete\",\"prefix\":\"http://xueshu.baidu.com\",\"prefixStd\":\"\",\"suffix\":\"\",\"suffixStd\":\"\"}]}},{\"field\":\"论文作者\",\"byType\":\"byCss\",\"typeValue\":\"div.sc_info > span:nth-child(1)\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"type\",\"byType\":\"byConstant\",\"typeValue\":\"论文资源\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}}]}},{\"staticFetchList\":{\"field\":\"资源链接\",\"byType\":\"byCss\",\"typeValue\":\"span.v_item_span\",\"innerCommands\":[{\"staticFetchKVs\":{\"includeSource\":false,\"fetchValueWithKeys\":[{\"field\":\"sourceUrl\",\"byType\":\"byCss\",\"typeValue\":\"a\",\"byAttr\":\"href\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"sourceName\",\"byType\":\"byCss\",\"typeValue\":\"a\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}}]}}]}}]}}]}},\"process_to\":\"1487142137675\",\"style\":\"left: 317px; top: 159px; width: 494px; height: 287px; right: auto; bottom: auto;\"},{\"id\":\"1487142137675\",\"process_name\":\"列表排重\",\"resizable\":false,\"process_data\":{\"classid\":\"dedup\",\"prop\":{\"fields\":[\"论文标题\"],\"mappingFields\":[\"_id\"],\"dedupMeta\":{\"engine\":\"mongodb\",\"host\":\"192.168.1.156\",\"port\":19130,\"user\":\"\",\"password\":\"\",\"database\":\"dedup\",\"collection\":\"huawei\"}}},\"process_to\":\"1487151370928\",\"style\":\"left: 504px; top: 490px; width: 120px; height: 35px; right: auto; bottom: auto;\"},{\"id\":\"1487151370928\",\"process_name\":\"处理列表\",\"resizable\":true,\"process_data\":{\"classid\":\"staticHandleList\",\"prop\":{\"sleepMillis\":1000,\"innerCommands\":[{\"staticOpen\":{\"urls\":[\"$url\"],\"requestType\":\"get\",\"charset\":\"gb2312\",\"params\":\"\",\"formatter\":{\"defaults\":[],\"customs\":[]}}},{\"staticFetchKVs\":{\"includeSource\":false,\"fetchValueWithKeys\":[{\"field\":\"论文摘要\",\"byType\":\"byCss\",\"typeValue\":\"p.abstract\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"出版源\",\"byType\":\"byCss\",\"typeValue\":\"p.publish_text\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"content\",\"byType\":\"byCss\",\"typeValue\":\"p.abstract\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}}]}}]}},\"process_to\":\"1487142136289\",\"style\":\"left: 415px; top: 552px; width: 300px; height: 200px; right: auto; bottom: auto;\"},{\"id\":\"1487142136289\",\"process_name\":\"数据存储\",\"resizable\":false,\"process_data\":{\"classid\":\"store\",\"prop\":{\"fields\":[],\"mappingFields\":[],\"storeMeta\":{\"engine\":\"mongodb\",\"host\":\"192.168.1.156\",\"port\":19130,\"user\":\"\",\"password\":\"\",\"database\":\"data\",\"collection\":\"huawei\"}}},\"process_to\":\"\",\"style\":\"left: 806px; top: 635px; width: 120px; height: 35px; right: auto; bottom: auto;\"},{\"id\":\"1487151380592\",\"process_name\":\"打开网址\",\"resizable\":false,\"process_data\":{\"classid\":\"staticOpen\",\"prop\":{\"urls\":[\"$url\"],\"requestType\":\"get\",\"charset\":\"gb2312\",\"params\":\"\",\"formatter\":{\"defaults\":[],\"customs\":[]}}},\"process_to\":\"1487151399154\",\"style\":\"left: 501px; top: 603px; width: 120px; height: 35px; right: auto; bottom: auto;\"},{\"id\":\"1487151399154\",\"process_name\":\"提取多值\",\"resizable\":false,\"process_data\":{\"classid\":\"staticFetchKVs\",\"prop\":{\"includeSource\":false,\"fetchValueWithKeys\":[{\"field\":\"论文摘要\",\"byType\":\"byCss\",\"typeValue\":\"p.abstract\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"出版源\",\"byType\":\"byCss\",\"typeValue\":\"p.publish_text\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"content\",\"byType\":\"byCss\",\"typeValue\":\"p.abstract\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}}]}},\"process_to\":\"\",\"style\":\"left: 501px; top: 668px; width: 120px; height: 35px; right: auto; bottom: auto;\"},{\"id\":\"1487128175379\",\"process_name\":\"提取列表\",\"resizable\":true,\"process_data\":{\"classid\":\"staticFetchList\",\"prop\":{\"field\":\"\",\"byType\":\"byCss\",\"typeValue\":\"div#bdxs_result_lists > div.sc_default_result.result\",\"innerCommands\":[{\"staticFetchKVs\":{\"includeSource\":false,\"fetchValueWithKeys\":[{\"field\":\"论文标题\",\"byType\":\"byCss\",\"typeValue\":\"h3.t.c_font\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"url\",\"byType\":\"byCss\",\"typeValue\":\"h3.t.c_font > a\",\"byAttr\":\"href\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[{\"type\":\"sfe4j.complete\",\"prefix\":\"http://xueshu.baidu.com\",\"prefixStd\":\"\",\"suffix\":\"\",\"suffixStd\":\"\"}]}},{\"field\":\"论文作者\",\"byType\":\"byCss\",\"typeValue\":\"div.sc_info > span:nth-child(1)\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"type\",\"byType\":\"byConstant\",\"typeValue\":\"论文资源\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}}]}},{\"staticFetchList\":{\"field\":\"资源链接\",\"byType\":\"byCss\",\"typeValue\":\"span.v_item_span\",\"innerCommands\":[{\"staticFetchKVs\":{\"includeSource\":false,\"fetchValueWithKeys\":[{\"field\":\"sourceUrl\",\"byType\":\"byCss\",\"typeValue\":\"a\",\"byAttr\":\"href\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"sourceName\",\"byType\":\"byCss\",\"typeValue\":\"a\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}}]}}]}}]}},\"process_to\":\"\",\"style\":\"left: 405px; top: 212px; width: 347px; height: 212px; right: auto; bottom: auto;\"},{\"id\":\"1487128286557\",\"process_name\":\"提取多值\",\"resizable\":false,\"process_data\":{\"classid\":\"staticFetchKVs\",\"prop\":{\"includeSource\":false,\"fetchValueWithKeys\":[{\"field\":\"论文标题\",\"byType\":\"byCss\",\"typeValue\":\"h3.t.c_font\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"url\",\"byType\":\"byCss\",\"typeValue\":\"h3.t.c_font > a\",\"byAttr\":\"href\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[{\"type\":\"sfe4j.complete\",\"prefix\":\"http://xueshu.baidu.com\",\"prefixStd\":\"\",\"suffix\":\"\",\"suffixStd\":\"\"}]}},{\"field\":\"论文作者\",\"byType\":\"byCss\",\"typeValue\":\"div.sc_info > span:nth-child(1)\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"type\",\"byType\":\"byConstant\",\"typeValue\":\"论文资源\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}}]}},\"process_to\":\"1487141798129\",\"style\":\"left: 542px; top: 246px; width: 120px; height: 35px; right: auto; bottom: auto;\"},{\"id\":\"1487141798129\",\"process_name\":\"提取列表\",\"resizable\":true,\"process_data\":{\"classid\":\"staticFetchList\",\"prop\":{\"field\":\"资源链接\",\"byType\":\"byCss\",\"typeValue\":\"span.v_item_span\",\"innerCommands\":[{\"staticFetchKVs\":{\"includeSource\":false,\"fetchValueWithKeys\":[{\"field\":\"sourceUrl\",\"byType\":\"byCss\",\"typeValue\":\"a\",\"byAttr\":\"href\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"sourceName\",\"byType\":\"byCss\",\"typeValue\":\"a\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}}]}}]}},\"process_to\":\"\",\"style\":\"left: 494px; top: 314px; width: 216px; height: 97px; right: auto; bottom: auto;\"},{\"id\":\"1487141840972\",\"process_name\":\"提取多值\",\"resizable\":false,\"process_data\":{\"classid\":\"staticFetchKVs\",\"prop\":{\"includeSource\":false,\"fetchValueWithKeys\":[{\"field\":\"sourceUrl\",\"byType\":\"byCss\",\"typeValue\":\"a\",\"byAttr\":\"href\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}},{\"field\":\"sourceName\",\"byType\":\"byCss\",\"typeValue\":\"a\",\"byAttr\":\"innerText\",\"group\":0,\"valueType\":\"string\",\"formatter\":{\"defaults\":[],\"customs\":[]}}]}},\"process_to\":\"\",\"style\":\"left: 527px; top: 349px; width: 120px; height: 35px; right: auto; bottom: auto;\"}]}', '0');
INSERT INTO `ripper_config_template` VALUES ('2', '模板2', '[ { \"staticOpen\": { \"urls\": [ \"http://www.leiphone.com/\" ], \"requestType\": \"get\", \"charset\": \"utf-8\", \"params\": \"\", \"formatter\": { \"defaults\": [], \"customs\": [] } } }, { \"staticFetchList\": { \"field\": \"\", \"byType\": \"byCss\", \"typeValue\": \"div.lph-pageList.index-pageList > div.list > ul.clr > li\", \"innerCommands\": [ { \"staticFetchKVs\": { \"includeSource\": false, \"fetchValueWithKeys\": [ { \"field\": \"title\", \"byType\": \"byCss\", \"typeValue\": \"h3\", \"byAttr\": \"innerText\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [], \"customs\": [] } }, { \"field\": \"url\", \"byType\": \"byCss\", \"typeValue\": \"h3 > a\", \"byAttr\": \"href\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [], \"customs\": [] } }, { \"field\": \"article_img\", \"byType\": \"byCss\", \"typeValue\": \"div.img > a > img\", \"byAttr\": \"data-original\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [], \"customs\": [] } }, { \"field\": \"source\", \"byType\": \"byConstant\", \"typeValue\": \"雷锋网\", \"byAttr\": \"\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [], \"customs\": [] } } ] } } ] } }, { \"dedup\": { \"fields\": [ \"url\" ], \"mappingFields\": [], \"dedupMeta\": { \"engine\": \"mongodb\", \"host\": \"localhost\", \"port\": 27017, \"user\": \"\", \"password\": \"\", \"database\": \"dedup\", \"collection\": \"\" } } }, { \"staticHandleList\": { \"sleepMillis\": 1000, \"innerCommands\": [ { \"staticOpen\": { \"urls\": [ \"$url\" ], \"requestType\": \"get\", \"charset\": \"utf-8\", \"params\": \"\", \"formatter\": { \"defaults\": [], \"customs\": [] } } }, { \"staticFetchKVs\": { \"includeSource\": false, \"fetchValueWithKeys\": [ { \"field\": \"publish_time\", \"byType\": \"byCss\", \"typeValue\": \"td.time\", \"byAttr\": \"innerText\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [ \"5000\" ], \"customs\": [] } }, { \"field\": \"content\", \"byType\": \"byCss\", \"typeValue\": \"div.lph-article-comView\", \"byAttr\": \"innerHTML\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [], \"customs\": [] } } ] } } ] } }, { \"store\": { \"fields\": [], \"mappingFields\": [], \"storeMeta\": { \"engine\": \"mongodb\", \"host\": \"localhost\", \"port\": 27017, \"user\": \"\", \"password\": \"\", \"database\": \"data\", \"collection\": \"\" } } } ]', '{ \"type\": \"0\", \"graph\": [ { \"id\": \"1486953526685\", \"process_name\": \"打开网址\", \"resizable\": false, \"process_data\": { \"classid\": \"staticOpen\", \"prop\": { \"urls\": [ \"http://www.leiphone.com/\" ], \"requestType\": \"get\", \"charset\": \"utf-8\", \"params\": \"\", \"formatter\": { \"defaults\": [], \"customs\": [] } } }, \"process_to\": \"1486953687216\", \"style\": \"left: 408px; top: 114px; width: 120px; height: 35px; right: auto; bottom: auto;\" }, { \"id\": \"1486953687216\", \"process_name\": \"提取列表\", \"resizable\": true, \"process_data\": { \"classid\": \"staticFetchList\", \"prop\": { \"field\": \"\", \"byType\": \"byCss\", \"typeValue\": \"div.lph-pageList.index-pageList > div.list > ul.clr > li\", \"innerCommands\": [ { \"staticFetchKVs\": { \"includeSource\": false, \"fetchValueWithKeys\": [ { \"field\": \"title\", \"byType\": \"byCss\", \"typeValue\": \"h3\", \"byAttr\": \"innerText\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [], \"customs\": [] } }, { \"field\": \"url\", \"byType\": \"byCss\", \"typeValue\": \"h3 > a\", \"byAttr\": \"href\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [], \"customs\": [] } }, { \"field\": \"article_img\", \"byType\": \"byCss\", \"typeValue\": \"div.img > a > img\", \"byAttr\": \"data-original\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [], \"customs\": [] } }, { \"field\": \"source\", \"byType\": \"byConstant\", \"typeValue\": \"雷锋网\", \"byAttr\": \"\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [], \"customs\": [] } } ] } } ] } }, \"process_to\": \"1486953786779\", \"style\": \"left: 318px; top: 188px; width: 300px; height: 128px; right: auto; bottom: auto;\" }, { \"id\": \"1486953786779\", \"process_name\": \"列表排重\", \"resizable\": false, \"process_data\": { \"classid\": \"dedup\", \"prop\": { \"fields\": [ \"url\" ], \"mappingFields\": [], \"dedupMeta\": { \"engine\": \"mongodb\", \"host\": \"localhost\", \"port\": 27017, \"user\": \"\", \"password\": \"\", \"database\": \"dedup\", \"collection\": \"\" } } }, \"process_to\": \"1486954119923\", \"style\": \"left: 407px; top: 360px; width: 120px; height: 35px; right: auto; bottom: auto;\" }, { \"id\": \"1486954119923\", \"process_name\": \"处理列表\", \"resizable\": true, \"process_data\": { \"classid\": \"staticHandleList\", \"prop\": { \"sleepMillis\": 1000, \"innerCommands\": [ { \"staticOpen\": { \"urls\": [ \"$url\" ], \"requestType\": \"get\", \"charset\": \"utf-8\", \"params\": \"\", \"formatter\": { \"defaults\": [], \"customs\": [] } } }, { \"staticFetchKVs\": { \"includeSource\": false, \"fetchValueWithKeys\": [ { \"field\": \"publish_time\", \"byType\": \"byCss\", \"typeValue\": \"td.time\", \"byAttr\": \"innerText\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [ \"5000\" ], \"customs\": [] } }, { \"field\": \"content\", \"byType\": \"byCss\", \"typeValue\": \"div.lph-article-comView\", \"byAttr\": \"innerHTML\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [], \"customs\": [] } } ] } } ] } }, \"process_to\": \"1486953962879\", \"style\": \"left: 317px; top: 419px; width: 300px; height: 200px; right: auto; bottom: auto;\" }, { \"id\": \"1486953962879\", \"process_name\": \"数据存储\", \"resizable\": false, \"process_data\": { \"classid\": \"store\", \"prop\": { \"fields\": [], \"mappingFields\": [], \"storeMeta\": { \"engine\": \"mongodb\", \"host\": \"localhost\", \"port\": 27017, \"user\": \"\", \"password\": \"\", \"database\": \"data\", \"collection\": \"\" } } }, \"process_to\": \"\", \"style\": \"left: 406px; top: 685px; width: 120px; height: 35px; right: auto; bottom: auto;\" }, { \"id\": \"1486953736620\", \"process_name\": \"提取多值\", \"resizable\": false, \"process_data\": { \"classid\": \"staticFetchKVs\", \"prop\": { \"includeSource\": false, \"fetchValueWithKeys\": [ { \"field\": \"title\", \"byType\": \"byCss\", \"typeValue\": \"h3\", \"byAttr\": \"innerText\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [], \"customs\": [] } }, { \"field\": \"url\", \"byType\": \"byCss\", \"typeValue\": \"h3 > a\", \"byAttr\": \"href\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [], \"customs\": [] } }, { \"field\": \"article_img\", \"byType\": \"byCss\", \"typeValue\": \"div.img > a > img\", \"byAttr\": \"data-original\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [], \"customs\": [] } }, { \"field\": \"source\", \"byType\": \"byConstant\", \"typeValue\": \"雷锋网\", \"byAttr\": \"\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [], \"customs\": [] } } ] } }, \"process_to\": \"\", \"style\": \"left: 406px; top: 238px; width: 120px; height: 35px; right: auto; bottom: auto;\" }, { \"id\": \"1486954129604\", \"process_name\": \"打开网址\", \"resizable\": false, \"process_data\": { \"classid\": \"staticOpen\", \"prop\": { \"urls\": [ \"$url\" ], \"requestType\": \"get\", \"charset\": \"utf-8\", \"params\": \"\", \"formatter\": { \"defaults\": [], \"customs\": [] } } }, \"process_to\": \"1486954145213\", \"style\": \"left: 404px; top: 458px; width: 120px; height: 35px; right: auto; bottom: auto;\" }, { \"id\": \"1486954145213\", \"process_name\": \"提取多值\", \"resizable\": false, \"process_data\": { \"classid\": \"staticFetchKVs\", \"prop\": { \"includeSource\": false, \"fetchValueWithKeys\": [ { \"field\": \"publish_time\", \"byType\": \"byCss\", \"typeValue\": \"td.time\", \"byAttr\": \"innerText\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [ \"5000\" ], \"customs\": [] } }, { \"field\": \"content\", \"byType\": \"byCss\", \"typeValue\": \"div.lph-article-comView\", \"byAttr\": \"innerHTML\", \"group\": 0, \"valueType\": \"string\", \"formatter\": { \"defaults\": [], \"customs\": [] } } ] } }, \"process_to\": \"\", \"style\": \"left: 405px; top: 527px; width: 120px; height: 35px; right: auto; bottom: auto;\" } ] }', '0');
INSERT INTO `ripper_config_template` VALUES ('3', 'fddd', '[]', '{\"type\":\"1\",\"graph\":[]}', '1');

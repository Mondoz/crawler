/**
 * 
 * 
 */
DROP TABLE IF EXISTS `ripper_config`;


/**
 * 
 * 
 * 
 */
CREATE TABLE `ripper_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `last_time` varchar(25) DEFAULT NULL,
  `last_time_millis` bigint(11) DEFAULT '0',
  `next_time_millis` bigint(11) DEFAULT '0',
  `interval_millis` bigint(11) DEFAULT '1800000',
  `interval_exp` varchar(255) DEFAULT NULL COMMENT '周期表达式',
  `interval_type` int(1) DEFAULT '0' COMMENT '周期类型  0-固定周期  1-固定时间',
  `conf` text,
  `conf_graph` text,
  `conf_priority` int(1) DEFAULT '5' COMMENT '任务优先级 1-9 默认5 数字越大优先级越高',
  `conf_type` int(1) DEFAULT '0' COMMENT '爬虫配置类型  0-使用静态爬虫 1-使用动态爬虫',
  `conf_parallel` int(1) DEFAULT '0' COMMENT '配置任务是否并行 相当于对这个任务进行拆解 0不并行 1基于url并行 2基于最大页数并行',
  `switch_flag` int(1) DEFAULT '0' COMMENT '配置开关 1对爬虫调度程序开启 其他表示关闭',
  `completed` int(1) DEFAULT '0' COMMENT '配置文件是否完成  1-完成 其他未完成',
  `cookie` text,
  `group_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `next_time_millis_index` (`next_time_millis`),
  KEY `switch_flag_index` (`switch_flag`),
  KEY `conf_type_index` (`conf_type`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8;
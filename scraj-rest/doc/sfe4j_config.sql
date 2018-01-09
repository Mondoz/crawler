/**
 * 
 */
DROP TABLE IF EXISTS `sfe4j_config`;


/**
 * 
 */
CREATE TABLE `sfe4j_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_name` varchar(255) DEFAULT NULL,
  `schedule_interval_millis` bigint(11) DEFAULT NULL,
  `last_schedule_millis` bigint(11) DEFAULT NULL,
  `last_schedule_time` varchar(20) DEFAULT NULL,
  `next_schedule_millis` bigint(11) DEFAULT NULL,
  `next_schedule_time` varchar(20) DEFAULT NULL,
  `conf` text,
  `switch_status` int(1) DEFAULT NULL COMMENT '任务状态 1 启动  0关闭',
  `completed` int(1) DEFAULT NULL COMMENT '配置是否完成 1配置完成 0配置未完成',
  PRIMARY KEY (`id`),
  KEY `next_schedule_millis_index` (`next_schedule_millis`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


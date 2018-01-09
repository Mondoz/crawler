/**
 * 
 */
DROP TABLE IF EXISTS `ripper_config`;


/**
 * 
 */
CREATE TABLE `ripper_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `last_time` varchar(25) DEFAULT NULL,
  `last_time_millis` bigint(11) DEFAULT '0',
  `next_time_millis` bigint(11) DEFAULT '0',
  `interval_millis` int(255) unsigned DEFAULT NULL,
  `interval_exp` varchar(255) DEFAULT NULL,
  `interval_type` int(1) DEFAULT NULL,
  `conf` longtext,
  `conf_graph` longtext,
  `conf_priority` int(1) DEFAULT NULL,
  `conf_type` int(1) DEFAULT NULL,
  `conf_parallel` int(1) DEFAULT '0',
  `switch_flag` int(1) DEFAULT NULL,
  `completed` int(1) DEFAULT NULL,
  `cookie` longtext,
  `group_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


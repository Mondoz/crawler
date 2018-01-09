/**
 * 
 */
DROP TABLE IF EXISTS `ripper_config_template`;


/**
 * 
 */
CREATE TABLE `ripper_config_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `conf` longtext,
  `conf_graph` longtext,
  `conf_type` int(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*
Navicat MySQL Data Transfer

Source Server         : 192.168.1.156
Source Server Version : 50717
Source Host           : 192.168.1.156:3306
Source Database       : yqcp

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2017-10-10 16:47:29
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ripper_flow_template
-- ----------------------------
DROP TABLE IF EXISTS `ripper_flow_template`;
CREATE TABLE `ripper_flow_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `conf` longtext,
  `conf_graph` longtext,
  `conf_type` int(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of ripper_flow_template
-- ----------------------------
INSERT INTO `ripper_flow_template` VALUES ('1', 'test', null, null, '0');
INSERT INTO `ripper_flow_template` VALUES ('2', 'test', null, null, '1');

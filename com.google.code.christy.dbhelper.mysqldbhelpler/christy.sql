/*
Navicat MySQL Data Transfer

Source Server         : christy
Source Server Version : 50083
Source Host           : localhost:3306
Source Database       : christy

Target Server Type    : MYSQL
Target Server Version : 50083
File Encoding         : 65001

Date: 2010-04-15 23:33:54
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `enterpriseuser`
-- ----------------------------
DROP TABLE IF EXISTS `enterpriseuser`;
CREATE TABLE `enterpriseuser` (
  `username` char(50) NOT NULL,
  `password` char(50) NOT NULL,
  `creationDate` datetime NOT NULL,
  `modificationDate` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of enterpriseuser
-- ----------------------------

-- ----------------------------
-- Table structure for `shop`
-- ----------------------------
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop` (
  `shopId` int(20) NOT NULL,
  `enterpriseUser` char(50) NOT NULL,
  `type` text NOT NULL,
  `title` tinytext NOT NULL,
  `content` text,
  `shopImg` text,
  `creationDate` datetime NOT NULL,
  `modificationDate` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`shopId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of shop
-- ----------------------------

-- ----------------------------
-- Table structure for `shopevaluation`
-- ----------------------------
DROP TABLE IF EXISTS `shopevaluation`;
CREATE TABLE `shopevaluation` (
  `shopId` int(20) NOT NULL default '0',
  `itemId` int(20) NOT NULL,
  `itemName` varchar(50) NOT NULL,
  PRIMARY KEY  (`shopId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of shopevaluation
-- ----------------------------

-- ----------------------------
-- Table structure for `shoploc`
-- ----------------------------
DROP TABLE IF EXISTS `shoploc`;
CREATE TABLE `shoploc` (
  `shopId` int(11) NOT NULL,
  `longitude` double NOT NULL,
  `latitude` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of shoploc
-- ----------------------------

-- ----------------------------
-- Table structure for `shopvoter`
-- ----------------------------
DROP TABLE IF EXISTS `shopvoter`;
CREATE TABLE `shopvoter` (
  `username` char(50) NOT NULL,
  `shopId` int(20) NOT NULL,
  `itemId` int(20) NOT NULL,
  `value` int(11) NOT NULL,
  PRIMARY KEY  (`username`,`shopId`,`itemId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of shopvoter
-- ----------------------------

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `username` char(50) NOT NULL,
  `password` char(50) NOT NULL,
  `creationDate` datetime NOT NULL,
  `modificationDate` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('Noah', '123', '2010-04-10 14:29:55', '2010-04-10 14:30:27');
INSERT INTO `user` VALUES ('Noah2', '123', '2010-04-12 20:30:42', '2010-04-12 20:30:51');

-- ----------------------------
-- Table structure for `userroster`
-- ----------------------------
DROP TABLE IF EXISTS `userroster`;
CREATE TABLE `userroster` (
  `rosterId` int(20) NOT NULL auto_increment,
  `username` char(50) NOT NULL,
  `jid` char(50) NOT NULL,
  `name` char(50) default NULL,
  `ask` enum('unsubscribe','subscribe') default NULL,
  `subscription` enum('remove','both','from','to','none') NOT NULL,
  PRIMARY KEY  (`rosterId`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of userroster
-- ----------------------------
INSERT INTO `userroster` VALUES ('2', 'Noah', 'Noah2@example.com', 'NoahNickName', null, 'both');
INSERT INTO `userroster` VALUES ('3', 'Noah', 'Noah3@example.com', 'NoahNickName34444', null, 'none');
INSERT INTO `userroster` VALUES ('4', 'Noah2', 'Noah@example.com', 'Noah', null, 'both');
INSERT INTO `userroster` VALUES ('5', 'Noah2', 'Noah3@example.com', 'Noah3', null, 'both');

-- ----------------------------
-- Table structure for `userrostergroup`
-- ----------------------------
DROP TABLE IF EXISTS `userrostergroup`;
CREATE TABLE `userrostergroup` (
  `rosterId` int(20) NOT NULL,
  `groupName` varchar(255) NOT NULL,
  PRIMARY KEY  (`rosterId`,`groupName`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of userrostergroup
-- ----------------------------
INSERT INTO `userrostergroup` VALUES ('2', 'group1');
INSERT INTO `userrostergroup` VALUES ('2', 'group2');
INSERT INTO `userrostergroup` VALUES ('3', 'group111');
INSERT INTO `userrostergroup` VALUES ('3', 'group555');

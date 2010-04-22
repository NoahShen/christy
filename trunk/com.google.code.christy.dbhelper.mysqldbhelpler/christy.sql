/*
Navicat MySQL Data Transfer

Source Server         : connection
Source Server Version : 50022
Source Host           : localhost:3306
Source Database       : christy

Target Server Type    : MYSQL
Target Server Version : 50022
File Encoding         : 65001

Date: 2010-04-22 12:15:30
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
INSERT INTO `enterpriseuser` VALUES ('ENoah', '123456', '2010-04-18 10:17:14', '2010-04-18 10:17:22');

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
  `district` varchar(20) NOT NULL,
  `street` varchar(50) NOT NULL,
  `tel` varchar(20) NOT NULL,
  `longitude` double NOT NULL,
  `latitude` double NOT NULL,
  `creationDate` datetime NOT NULL,
  `modificationDate` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`shopId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of shop
-- ----------------------------
INSERT INTO `shop` VALUES ('0', 'ENoah', 'food::localcuisine ', '上海1号私藏菜', '私藏菜比私房菜更多一点点“藏”的意思，有“酒香不怕巷子深”的傲气，正合了中国人爱追根究底的惯常。所以对于私藏变为公众皆知的秘密也就理所当然，无数的欲说还休。\r\n   老上海的韵味一边敛一边放。老式台灯、桌案、杨州漆器、铁质鸟笼、欧式沙发、回纹走廊等等，尽数着婉约复古的气息，美食暖胃，缓如流水。\r\n   上海1号私藏菜是以本帮菜、海派菜为主打，每一道菜都是玩过花样儿的。即使冠着简单寻常的名字，厨师们却下了无数的心思在里面，让时尚上海人的健康饮食观念贯彻得更透，浓油赤酱皆改作了清爽耐品，许多烹饪秘方私家独创，精致耐品，故名之“私藏菜”。\r\n   细碟精巧的手撕豇豆藏着淡淡芥末味，毫无疑问地手工制作；老弄堂红烧肉的选材更是讲究，只用野猪与家猪杂交的第五代猪肉；火山石器烧裙翅用功深，滋补功效好，中看中吃', '/resource/hongshaorou.jpg', '静安区', '南京西路1856号', '021-51501177', '121.443297', '31.221891', '2010-04-18 10:29:22', '2010-04-22 11:04:11');

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
-- Table structure for `shopoverall`
-- ----------------------------
DROP TABLE IF EXISTS `shopoverall`;
CREATE TABLE `shopoverall` (
  `shopId` int(20) NOT NULL,
  `itemId` int(20) NOT NULL,
  `itemName` varchar(50) NOT NULL,
  `itemValue` varchar(50) NOT NULL,
  PRIMARY KEY  (`shopId`,`itemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of shopoverall
-- ----------------------------
INSERT INTO `shopoverall` VALUES ('0', '0', 'score', '90');
INSERT INTO `shopoverall` VALUES ('0', '1', 'perCapita', '95');

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
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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

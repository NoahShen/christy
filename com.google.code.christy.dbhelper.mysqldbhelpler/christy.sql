/*
Navicat MySQL Data Transfer

Source Server         : christy
Source Server Version : 50083
Source Host           : localhost:3306
Source Database       : christy

Target Server Type    : MYSQL
Target Server Version : 50083
File Encoding         : 65001

Date: 2010-06-22 22:07:48
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `activity`
-- ----------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity` (
  `activityId` int(20) NOT NULL auto_increment,
  `shopId` int(20) NOT NULL,
  `title` tinytext NOT NULL,
  `content` text,
  `activityImg` text,
  `longitude` double default NULL,
  `latitude` double default NULL,
  `longZone` char(10) default NULL,
  `latZone` char(10) default NULL,
  `easting` int(20) default NULL,
  `northing` int(20) default NULL,
  `creationDate` datetime NOT NULL,
  `modificationDate` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`activityId`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of activity
-- ----------------------------
INSERT INTO `activity` VALUES ('1', '0', '折扣', '折扣', null, null, null, null, null, null, null, '2010-06-22 21:17:20', '2010-06-22 21:25:03');

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
-- Table structure for `lastpublishtime`
-- ----------------------------
DROP TABLE IF EXISTS `lastpublishtime`;
CREATE TABLE `lastpublishtime` (
  `time` datetime NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of lastpublishtime
-- ----------------------------
INSERT INTO `lastpublishtime` VALUES ('2010-06-21 11:33:51');

-- ----------------------------
-- Table structure for `privatedata`
-- ----------------------------
DROP TABLE IF EXISTS `privatedata`;
CREATE TABLE `privatedata` (
  `username` char(50) NOT NULL,
  `stanzaKey` char(50) NOT NULL,
  `stanzaXml` longtext NOT NULL,
  PRIMARY KEY  (`username`,`stanzaKey`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of privatedata
-- ----------------------------
INSERT INTO `privatedata` VALUES ('noah', '[storage][storage:bookmarks]', '<storage xmlns=\"storage:bookmarks\">\n<conference autojoin=\"false\" name=\"name\" jid=\"conference.example.com\">\n<nick>Noah</nick>\n<password>password</password>\n</conference>\n<url url=\"http://www.google.com.hk\" name=\"google HK\"/>\n</storage>');
INSERT INTO `privatedata` VALUES ('noah', '[preference][christy:user:preference]', '<preference xmlns=\"christy:user:preference\"><item name=\"showContactPos\">true</item><item name=\"shareLoc\">false</item></preference>');

-- ----------------------------
-- Table structure for `pubsubaffiliation`
-- ----------------------------
DROP TABLE IF EXISTS `pubsubaffiliation`;
CREATE TABLE `pubsubaffiliation` (
  `serviceId` varchar(100) NOT NULL,
  `nodeId` varchar(100) NOT NULL,
  `jid` varchar(255) NOT NULL,
  `affiliation` enum('outcast','publisher','owner') NOT NULL,
  PRIMARY KEY  (`serviceId`,`nodeId`,`jid`(70))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of pubsubaffiliation
-- ----------------------------
INSERT INTO `pubsubaffiliation` VALUES ('123', 'node2', 'noah@example.com', 'owner');

-- ----------------------------
-- Table structure for `pubsubitem`
-- ----------------------------
DROP TABLE IF EXISTS `pubsubitem`;
CREATE TABLE `pubsubitem` (
  `serviceId` varchar(100) NOT NULL,
  `nodeId` varchar(100) NOT NULL,
  `itemId` varchar(100) NOT NULL,
  `jid` varchar(255) NOT NULL,
  `payload` mediumtext,
  `creationDate` datetime NOT NULL,
  PRIMARY KEY  (`serviceId`,`nodeId`,`itemId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of pubsubitem
-- ----------------------------
INSERT INTO `pubsubitem` VALUES ('123', 'node2', '0', 'noah@example.com', null, '2010-06-21 10:27:56');
INSERT INTO `pubsubitem` VALUES ('123', 'node2', '1', 'noah@example.com', null, '2010-06-21 10:27:59');
INSERT INTO `pubsubitem` VALUES ('123', 'node2', '2', 'noah@example.com', null, '2010-06-21 10:28:01');
INSERT INTO `pubsubitem` VALUES ('123', 'node2', 'bnd81g37d61f49fgn581', 'noah@example.com', '<entry xmlns=\"http://www.w3.org/2005/Atom\">\n          <title>Soliloquy</title>\n          <summary>\nTo be, or not to be: that is the question:\nWhether \'tis nobler in the mind to suffer\nThe slings and arrows of outrageous fortune,\nOr to take arms against a sea of troubles,\nAnd by opposing end them?\n          </summary>\n          <link rel=\"alternate\" type=\"text/html\" href=\"http://denmark.lit/2003/12/13/atom03\"/>\n          <id>tag:denmark.lit,2003:entry-32397</id>\n          <published>2003-12-13T18:30:02Z</published>\n          <updated>2003-12-13T18:30:02Z</updated>\n        </entry>', '2010-06-21 11:33:01');
INSERT INTO `pubsubitem` VALUES ('123', 'node3', 'bnd81g37d61f49fgn581', 'noah@example.com', '<entry xmlns=\"http://www.w3.org/2005/Atom\">\n          <title>Soliloquy</title>\n          <summary>\nTo be, or not to be: that is the question:\nWhether \'tis nobler in the mind to suffer\nThe slings and arrows of outrageous fortune,\nOr to take arms against a sea of troubles,\nAnd by opposing end them?\n          </summary>\n          <link rel=\"alternate\" type=\"text/html\" href=\"http://denmark.lit/2003/12/13/atom03\"/>\n          <id>tag:denmark.lit,2003:entry-32397</id>\n          <published>2003-12-13T18:30:02Z</published>\n          <updated>2003-12-13T18:30:02Z</updated>\n        </entry>', '2010-06-21 11:33:51');

-- ----------------------------
-- Table structure for `pubsubnode`
-- ----------------------------
DROP TABLE IF EXISTS `pubsubnode`;
CREATE TABLE `pubsubnode` (
  `serviceId` varchar(100) NOT NULL,
  `nodeId` varchar(100) NOT NULL,
  `leaf` tinyint(4) NOT NULL,
  `creationDate` datetime NOT NULL,
  `modificationDate` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP,
  `parent` varchar(100) default NULL,
  `creator` varchar(255) NOT NULL,
  `description` varchar(255) default NULL,
  `name` varchar(50) default NULL,
  PRIMARY KEY  (`serviceId`,`nodeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Records of pubsubnode
-- ----------------------------
INSERT INTO `pubsubnode` VALUES ('123', 'node1', '0', '2010-06-12 16:36:15', '2010-06-12 17:13:30', null, 'noah@example.com', 'desc', 'node1name');
INSERT INTO `pubsubnode` VALUES ('123', 'node2', '1', '2010-06-12 17:12:51', '2010-06-12 17:13:34', 'node1', 'noah@example.com', 'des2', 'node2name');
INSERT INTO `pubsubnode` VALUES ('123', 'node3', '1', '2010-06-17 16:41:36', '2010-06-17 16:41:39', 'node1', 'noah@example.com', 'descnode3', 'node3name');

-- ----------------------------
-- Table structure for `pubsubnodeconfig`
-- ----------------------------
DROP TABLE IF EXISTS `pubsubnodeconfig`;
CREATE TABLE `pubsubnodeconfig` (
  `serviceId` varchar(100) NOT NULL,
  `nodeId` varchar(100) NOT NULL,
  `creationDate` datetime NOT NULL,
  `modificationDate` timestamp NOT NULL default '0000-00-00 00:00:00' on update CURRENT_TIMESTAMP,
  `deliverPayloads` tinyint(4) NOT NULL,
  `maxPayloadSize` int(11) default NULL,
  `persistItems` tinyint(4) default NULL,
  `maxItems` int(11) default NULL,
  `notifyConfigChanges` tinyint(4) NOT NULL,
  `notifyDelete` tinyint(4) NOT NULL,
  `notifyRetract` tinyint(4) NOT NULL,
  `sendItemSubscribe` tinyint(4) NOT NULL,
  `publisherModel` varchar(15) NOT NULL,
  `subscriptionEnabled` tinyint(4) NOT NULL,
  `configSubscription` tinyint(4) NOT NULL,
  `subscribeModel` varchar(10) NOT NULL,
  PRIMARY KEY  (`serviceId`,`nodeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of pubsubnodeconfig
-- ----------------------------
INSERT INTO `pubsubnodeconfig` VALUES ('123', 'node2', '2010-06-17 12:09:42', '2010-06-17 12:09:45', '1', null, null, null, '1', '1', '1', '1', 'open', '1', '0', 'open');
INSERT INTO `pubsubnodeconfig` VALUES ('123', 'node3', '2010-06-17 16:43:26', '2010-06-17 16:43:30', '1', null, null, null, '1', '1', '1', '1', 'open', '1', '0', 'open');

-- ----------------------------
-- Table structure for `pubsubsubscription`
-- ----------------------------
DROP TABLE IF EXISTS `pubsubsubscription`;
CREATE TABLE `pubsubsubscription` (
  `serviceId` varchar(100) NOT NULL,
  `nodeId` varchar(100) NOT NULL,
  `subId` varchar(100) NOT NULL,
  `jid` varchar(255) NOT NULL,
  `subscriber` varchar(255) NOT NULL,
  `subscription` enum('none','unconfigured','subscribed') NOT NULL,
  `deliver` tinyint(4) default NULL,
  `digest` tinyint(4) default NULL,
  `digest_frequency` int(11) default NULL,
  `expire` int(15) default NULL,
  `includeBody` tinyint(4) default NULL,
  `showValues` varchar(30) default NULL,
  `subscriptionDepth` tinyint(4) default NULL,
  PRIMARY KEY  (`serviceId`,`nodeId`,`subId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of pubsubsubscription
-- ----------------------------
INSERT INTO `pubsubsubscription` VALUES ('123', 'node2', '123', 'pubsub.example.com', 'noah@example.com', 'subscribed', '1', '0', null, null, '0', 'chat;online;away;', null);
INSERT INTO `pubsubsubscription` VALUES ('123', 'node3', '2531e52f5087be95ffc2a8b25dfdd297', 'pubsub.example.com', 'noah@example.com', 'subscribed', '1', '0', null, null, '0', 'chat;online;away;', null);

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
  `longZone` char(10) NOT NULL,
  `latZone` char(10) NOT NULL,
  `easting` int(20) NOT NULL,
  `northing` int(20) NOT NULL,
  `creationDate` datetime NOT NULL,
  `modificationDate` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`shopId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of shop
-- ----------------------------
INSERT INTO `shop` VALUES ('0', 'ENoah', 'restaurant', '上海1号私藏菜', '私藏菜比私房菜更多一点点“藏”的意思，有“酒香不怕巷子深”的傲气，正合了中国人爱追根究底的惯常。所以对于私藏变为公众皆知的秘密也就理所当然，无数的欲说还休。\r\n   老上海的韵味一边敛一边放。老式台灯、桌案、杨州漆器、铁质鸟笼、欧式沙发、回纹走廊等等，尽数着婉约复古的气息，美食暖胃，缓如流水。\r\n   上海1号私藏菜是以本帮菜、海派菜为主打，每一道菜都是玩过花样儿的。即使冠着简单寻常的名字，厨师们却下了无数的心思在里面，让时尚上海人的健康饮食观念贯彻得更透，浓油赤酱皆改作了清爽耐品，许多烹饪秘方私家独创，精致耐品，故名之“私藏菜”。\r\n   细碟精巧的手撕豇豆藏着淡淡芥末味，毫无疑问地手工制作；老弄堂红烧肉的选材更是讲究，只用野猪与家猪杂交的第五代猪肉；火山石器烧裙翅用功深，滋补功效好，中看中吃', '/resource/hongshaorou.jpg', '静安区', '南京西路1856号', '021-51501177', '121.443297', '31.221891', '51', 'R', '351724', '3455237', '2010-04-18 10:29:22', '2010-05-18 10:30:43');
INSERT INTO `shop` VALUES ('1', 'ENoah', 'restaurant', '吃饭2', '吃饭的地方', '/resource/hongshaorou.jpg', '黄浦区', '某某路某某号', '021-13245678', '121.4481', '31.22422', '51', 'R', '352185', '3455489', '2010-04-22 17:31:05', '2010-05-18 10:30:45');
INSERT INTO `shop` VALUES ('2', 'ENoah', 'restaurant', '吃饭2', '吃饭的地方', '/resource/hongshaorou.jpg', '黄浦区', '某某路某某号', '021-13245678', '121.4481', '31.22422', '51', 'R', '352185', '3455489', '2010-04-22 17:31:05', '2010-05-18 10:30:46');
INSERT INTO `shop` VALUES ('3', 'ENoah', 'restaurant', '吃饭2', '吃饭的地方', '/resource/hongshaorou.jpg', '黄浦区', '某某路某某号', '021-13245678', '121.4481', '31.22422', '51', 'R', '352185', '3455489', '2010-04-22 17:31:05', '2010-05-18 10:30:48');
INSERT INTO `shop` VALUES ('4', 'ENoah', 'restaurant', '吃饭2', '吃饭的地方', '/resource/hongshaorou.jpg', '黄浦区', '某某路某某号', '021-13245678', '121.4481', '31.22422', '51', 'R', '352185', '3455489', '2010-04-22 17:31:05', '2010-05-18 10:30:49');
INSERT INTO `shop` VALUES ('5', 'ENoah', 'restaurant', '吃饭2', '吃饭的地方', '/resource/hongshaorou.jpg', '黄浦区', '某某路某某号', '021-13245678', '121.4481', '31.22422', '51', 'R', '352185', '3455489', '2010-04-22 17:31:05', '2010-05-18 10:30:50');
INSERT INTO `shop` VALUES ('6', 'ENoah', 'restaurant', '吃饭2', '吃饭的地方', '/resource/hongshaorou.jpg', '黄浦区', '某某路某某号', '021-13245678', '121.4481', '31.22422', '51', 'R', '352185', '3455489', '2010-04-22 17:31:05', '2010-05-18 10:30:51');
INSERT INTO `shop` VALUES ('7', 'ENoah', 'restaurant', '吃饭2', '吃饭的地方', '/resource/hongshaorou.jpg', '黄浦区', '某某路某某号', '021-13245678', '121.4481', '31.22422', '51', 'R', '352185', '3455489', '2010-04-22 17:31:05', '2010-05-18 10:30:52');
INSERT INTO `shop` VALUES ('8', 'ENoah', 'restaurant', '吃饭2', '吃饭的地方', '/resource/hongshaorou.jpg', '黄浦区', '某某路某某号', '021-13245678', '121.4481', '31.22422', '51', 'R', '352185', '3455489', '2010-04-22 17:31:05', '2010-05-18 10:30:52');
INSERT INTO `shop` VALUES ('9', 'ENoah', 'restaurant', '吃饭2', '吃饭的地方', '/resource/hongshaorou.jpg', '黄浦区', '某某路某某号', '021-13245678', '121.4481', '31.22422', '51', 'R', '352185', '3455489', '2010-04-22 17:31:05', '2010-05-18 10:30:54');

-- ----------------------------
-- Table structure for `shopcomment`
-- ----------------------------
DROP TABLE IF EXISTS `shopcomment`;
CREATE TABLE `shopcomment` (
  `commentId` int(20) NOT NULL auto_increment,
  `shopId` int(20) NOT NULL,
  `username` char(50) NOT NULL,
  `score` int(20) NOT NULL,
  `content` text,
  `creationDate` datetime NOT NULL,
  `modificationDate` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`commentId`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of shopcomment
-- ----------------------------
INSERT INTO `shopcomment` VALUES ('1', '0', 'noah', '90', '早就听说这家的菜很好吃了 很多人都喜欢来这家的哦 我是和家人一起来的 那次我们吃的都是很满意呢 这家的环境还是不错哦 价格也是公道的 我们都是可以接受呢 真的是不错哦 热情的服务也是我们非常的满意呢 值得来试试哦', '2010-04-22 14:01:22', '2010-04-25 19:57:01');
INSERT INTO `shopcomment` VALUES ('2', '0', 'noah2', '95', '很奇怪的一家店，11点过去，刚开门的时候，居然就排队，排队的都是5、60的老人。诺大的店堂，居然只有非常小的电梯可以上去，一次也就6个人。中午的午市的火山石器烧裙翅吃口不错，才43，的确是特色了。下次有机会来吃点心', '2010-04-22 14:05:46', '2010-04-25 19:57:27');
INSERT INTO `shopcomment` VALUES ('6', '0', 'noah', '91', '9090', '2010-04-25 21:01:02', '2010-04-25 21:01:02');
INSERT INTO `shopcomment` VALUES ('7', '0', 'noah', '92', '9292', '2010-04-25 21:23:04', '2010-04-25 21:23:04');
INSERT INTO `shopcomment` VALUES ('8', '0', 'noah', '12', 'content', '2010-05-16 21:08:59', '2010-05-16 21:08:59');
INSERT INTO `shopcomment` VALUES ('9', '0', 'noah', '3', null, '2010-05-26 15:21:42', '2010-05-26 15:21:42');
INSERT INTO `shopcomment` VALUES ('10', '0', 'noah', '12', '4测试', '2010-05-26 16:10:57', '2010-05-26 16:10:57');
INSERT INTO `shopcomment` VALUES ('11', '0', 'noah', '12', '阿道夫', '2010-05-26 17:42:44', '2010-05-26 17:42:44');
INSERT INTO `shopcomment` VALUES ('12', '0', 'noah', '12', 'ff', '2010-06-09 11:08:31', '2010-06-09 11:08:31');
INSERT INTO `shopcomment` VALUES ('13', '0', 'noah', '6', 'dsfg', '2010-06-09 11:20:43', '2010-06-09 11:20:43');

-- ----------------------------
-- Table structure for `shopoverall`
-- ----------------------------
DROP TABLE IF EXISTS `shopoverall`;
CREATE TABLE `shopoverall` (
  `shopId` int(20) NOT NULL,
  `itemName` varchar(50) NOT NULL,
  `itemValue` varchar(50) NOT NULL,
  PRIMARY KEY  (`shopId`,`itemName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of shopoverall
-- ----------------------------
INSERT INTO `shopoverall` VALUES ('0', 'perCapita', '95');
INSERT INTO `shopoverall` VALUES ('0', 'score', '90');
INSERT INTO `shopoverall` VALUES ('0', 'service', '90');
INSERT INTO `shopoverall` VALUES ('0', 'taste', '90');

-- ----------------------------
-- Table structure for `shopvoter`
-- ----------------------------
DROP TABLE IF EXISTS `shopvoter`;
CREATE TABLE `shopvoter` (
  `voterId` int(20) NOT NULL auto_increment,
  `username` char(50) NOT NULL,
  `shopId` int(20) NOT NULL,
  `itemName` varchar(50) NOT NULL,
  `value` int(11) NOT NULL,
  PRIMARY KEY  (`voterId`)
) ENGINE=MyISAM AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of shopvoter
-- ----------------------------
INSERT INTO `shopvoter` VALUES ('13', 'noah', '0', 'service', '92');
INSERT INTO `shopvoter` VALUES ('10', 'noah', '0', 'service', '93');
INSERT INTO `shopvoter` VALUES ('11', 'noah', '0', 'perCapita', '92');
INSERT INTO `shopvoter` VALUES ('12', 'noah', '0', 'taste', '95');
INSERT INTO `shopvoter` VALUES ('14', 'noah', '0', 'perCapita', '92');
INSERT INTO `shopvoter` VALUES ('15', 'noah', '0', 'taste', '92');
INSERT INTO `shopvoter` VALUES ('16', 'noah', '0', 'service', '3');
INSERT INTO `shopvoter` VALUES ('17', 'noah', '0', 'perCapita', '43');
INSERT INTO `shopvoter` VALUES ('18', 'noah', '0', 'taste', '3');
INSERT INTO `shopvoter` VALUES ('19', 'noah', '0', 'service', '23');
INSERT INTO `shopvoter` VALUES ('20', 'noah', '0', 'perCapita', '2');
INSERT INTO `shopvoter` VALUES ('21', 'noah', '0', 'taste', '4');
INSERT INTO `shopvoter` VALUES ('22', 'noah', '0', 'service', '12');
INSERT INTO `shopvoter` VALUES ('23', 'noah', '0', 'perCapita', '12');
INSERT INTO `shopvoter` VALUES ('24', 'noah', '0', 'taste', '12');
INSERT INTO `shopvoter` VALUES ('25', 'noah', '0', 'service', '12');
INSERT INTO `shopvoter` VALUES ('26', 'noah', '0', 'perCapita', '54');
INSERT INTO `shopvoter` VALUES ('27', 'noah', '0', 'taste', '12');
INSERT INTO `shopvoter` VALUES ('28', 'noah', '0', 'service', '12');
INSERT INTO `shopvoter` VALUES ('29', 'noah', '0', 'perCapita', '43');
INSERT INTO `shopvoter` VALUES ('30', 'noah', '0', 'taste', '12');
INSERT INTO `shopvoter` VALUES ('31', 'noah', '0', 'service', '66');
INSERT INTO `shopvoter` VALUES ('32', 'noah', '0', 'perCapita', '3');
INSERT INTO `shopvoter` VALUES ('33', 'noah', '0', 'taste', '34');

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
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('Noah', '123', '2010-04-10 14:29:55', '2010-04-10 14:30:27');
INSERT INTO `user` VALUES ('Noah2', '123', '2010-04-12 20:30:42', '2010-04-12 20:30:51');

-- ----------------------------
-- Table structure for `userfavoriteshop`
-- ----------------------------
DROP TABLE IF EXISTS `userfavoriteshop`;
CREATE TABLE `userfavoriteshop` (
  `id` int(11) NOT NULL auto_increment,
  `username` char(50) NOT NULL,
  `shopId` int(20) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of userfavoriteshop
-- ----------------------------
INSERT INTO `userfavoriteshop` VALUES ('1', 'noah', '0');
INSERT INTO `userfavoriteshop` VALUES ('3', 'noah', '2');
INSERT INTO `userfavoriteshop` VALUES ('4', 'noah', '3');
INSERT INTO `userfavoriteshop` VALUES ('6', 'noah', '5');
INSERT INTO `userfavoriteshop` VALUES ('9', 'noah', '3');
INSERT INTO `userfavoriteshop` VALUES ('10', 'noah', '1');

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
) ENGINE=MyISAM AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of userroster
-- ----------------------------
INSERT INTO `userroster` VALUES ('22', 'noah', 'noah2@example.com', null, null, 'both');
INSERT INTO `userroster` VALUES ('3', 'noah', 'Noah3@example.com', null, null, 'both');
INSERT INTO `userroster` VALUES ('25', 'noah2', 'noah@example.com', null, null, 'both');
INSERT INTO `userroster` VALUES ('5', 'noah2', 'Noah3@example.com', 'Noah3', null, 'both');
INSERT INTO `userroster` VALUES ('6', 'noah', 'Noah4@example.com', null, 'subscribe', 'none');
INSERT INTO `userroster` VALUES ('7', 'noah', 'Noah5@example.com', null, 'subscribe', 'none');
INSERT INTO `userroster` VALUES ('8', 'noah', 'Noah6@example.com', null, 'subscribe', 'none');
INSERT INTO `userroster` VALUES ('9', 'noah', 'Noah7@example.com', null, 'subscribe', 'none');
INSERT INTO `userroster` VALUES ('10', 'noah', 'Noah8@example.com', null, 'subscribe', 'none');
INSERT INTO `userroster` VALUES ('20', 'noah', 'Noah10@example.com', null, 'subscribe', 'none');

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

-- ----------------------------
-- Table structure for `uservcard`
-- ----------------------------
DROP TABLE IF EXISTS `uservcard`;
CREATE TABLE `uservcard` (
  `username` char(50) NOT NULL,
  `vCard` longtext,
  PRIMARY KEY  (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of uservcard
-- ----------------------------
INSERT INTO `uservcard` VALUES ('noah', '<vCard xmlns=\"vcard-temp\"><NICKNAME>Noah</NICKNAME><PHOTO><TYPE>image/png</TYPE><BINVAL>\n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\n/y1t8LRQ5EPK0K9JiOe4zuvYjrOkrrPU7tPkjv2E5rX0jrXMtsXcjoVi2GL9wHr3zOHQ+tX03u3\nE2v7Y8nbf/CZ0cr15eKW2b7kcuVyKWCyGzRfBpou7xku6hqpg/dUwZFFDY0FBSk+J/1ix02SF93\nxj+Epn0lJ32mxH6mRL4mhDfH9VZGOme0agYYSzSqyXTl6sc3VucEdj+jC0ZKQhC1EcVxrjUZwdE\nZoW5pYV51Ra4FRR5VnR4F1W61lY7JSTYZEdY5Ydrhnrrh7hbJERZJMVoOhrJOGsrhVooh9mDgkF\nCWbPFQgXPkiQmvzihfwDwK/nl8jel3j4eSZ0vQI2De8bmR7pWxuDbk50Lgy3j/a2dne11bVDSzt\nGcrqXE6GHkTDA6XNg7/eA3u9+fV8DQJTrvwnuuwrpu/qREvzLMf5TvP/2E6D3fyOq9yYFfpzRtZ\nXVtV3Us1s1dNA4dtg4stM4uFLRNdwAH4EOTQzPzi+srW3ube0d756c7h/trZ4crJ8c7hwe7u8cH\nK9uHcys7k0sbY/OrY/MLg1PTPcPD/X2waDQhobaPEAcMs9lKNd6rNRjsTVqA5G5gsiZ6Uwfb0ma\nak8FXLfleGcF6Me6qaUFGpUne9TlhSBas8a6CxfacyerkpqTvWpzgtz8TbTc9K1SQyzTY2yS4yx\njwkxC3PX8rTSDjNRCDAwTHOR8dTVCzUwSbFX9tWVdFVQ9lVXc5SHhPWdAd4AL314Qdn737PsbgC\n/y9zAkIPo0s2OpsLGnrra6p6FwtiN/pyf/aKhwE1k4DS3p72rshPbVwmdzYNvx3adRiIeQnp9Ef\n/frf/YbuAvovwns/x+i/83sz4Of5Ib2P/1E2MAzQFzfRRZsrqZ/unNqvX95Z2Rta2xtbXJ1eWZt\nbnSid3oGubrct7s5cLjVe7SJOFjtOljuOF2EnS0iz5aHztcnzjYXTreXTnbXj/e2Tne3zg82zne\nXj9bGdufgq8MNk515Q1URE+Ueo4W2oyWuCy3RG3156wPFSz0F87Dc2a4MoOiaJPtkT7UwOxnAdW\nmCc0d59Exf2fJA+V5PyVJdQleyS1OWh7OzopqlpHGojUG4k1Gwk76PhZaThqq9jKKTsIKbqFaIj\npS7grKvulawpoK7tIKrpKqbpJg5ByTsdYJfgrkfgHzxRf7qg/wLgD/y14Duu1jYRWb3ZkHTYGll\nTWN5bn9d5kJb+tlgzlFvxgYid7GnYqqvvbdvoBE+WdC5lNF9EA+7jELchSBfkza//gffQZCT3YA\n8IaD/LrDvIXDgMaj/MXjgCeCV1v6H8P67yL6b6P6buIHL+P7zhP7z7N6tztGZ2ZXZ/ZPd04vd3f\n3F1ZWhpYWetYXu3RXo3nLHwXLrwVLD/kLV7kzp9mTBzmjB6UjZYV/pJrRgsSNvobNwBVm5NdKyP\n9V1utBzszHwuD38vN3/uAG/W+m4nK45HizYg8Ut1PuNV/nOtcVs9ufvjVftjdcAzMOyu4oC8kIM\noxzkfYxFgqxlyhLdJrvyd6ZbdsZr9/pLJ6uj6+OsazKcXd3kjJyk9TzVtH119H30dNw11OykVKz\n5ley4lZz55N0kpZwlVX0UVLzlJO2ElN0l1T0kBQ0ZIKHIp1f0PAMASQYjfwUI6fk1DPktpPMivu\nsgF76RU9+TkltUWVEy3de8O1C+CU1bhyYvQ1MmWuNHO7Mnehv6+xCNnYPl3YvZ3bux0NMQkFEN3\nPuOPvqMPfqN3PmP3geOPgWMvvgPvSJw6GvY8Nf48W+xfVfxiIPUnu0s+HIudKZmYGVgdvXiYPVh\nb+Jma+hsCb470bQ+VLE+ULoxWLLSl7c7Xno4Xbk/Wbo9Ubw1XrQ2nLeMSN/qTDnoTDuEph10p25\n1vFYfm93JOz3pIMrtDuReTFc8LDc8r7Y8rTQ/Ljc9LDXezFSdjhZu9GTMdyTMdyev9uas9eUvIL\nKGG+Nbcr1DbGUDLGS8TKU8jKSb8yJ3J9tWBqrmgOR78ntqY1org6vK/XxDtHTthLSdxAx9FM0CN\nSyCtcwC1VTthaRMWBVseRWdxBVdpLT8lDR8FCRsBIRNOAUMWdg1qCEg9wQAMgSB6CfpwG0Bwvq+\nBr7mZ0cpyN3U1tHo4vrE/KKysrzlvrp1RP4aPG21O3a6KWCo2mewJny4JQfWVNPSAq9om87pXEu\nC78X0nYYNnwWPXPj1HwcOXgQP34SM3IH8N2TwMXzoIXb4Phy6H9u9kQlfqRnegM1tj69tr+7unx\n5tXq30Xs80HQ+VbcKyltsSl9sS1ruTthEp+30ZJ6N5pxOFB6P5W0PZa/1Z84iUmbbozZaY/ZaYo\n464w65YgCNY7Flf0vlg6tlQ+tlI9sV4/uVU8eVk6eVM5c1c7f1iw9fNzuf11ov5mt2Rwo3+HADA\n9RIis782uiXXNz/KOspVy15bOMxRZ6Ahf3Wg4XiuY3uidmu6frQnt6MlLr/E29ZDVk6fSc2GV8d\nNTN1RSNaSU8lWQMNZzMBL1jpMU9tLQcVVSt1NRslZQsyUm0+fhVODjlrmCwQ4acDAFTBTABC+gv\ntuQK0FJjuoC/x7z4N7D+L6N1N7phMaO8Lyc8ISwrvr8+e6i5cQuXNdiVPNIb1lbrACN+Bu8Mq0z\nuqKxoaO2rah0u7pfMR8Rt986sBafO9OfN9x/MBJ/MBZXN9ZLPI4ofcoFbmf0r1c2L/aOrU5sr67\neXp8enV8fLp+vD12NFl/OFiw0Z221BK31Bi13By53ha52QHG8K3u6G143BYiYRMevwFPWOqOmW8\nJXaoLWKsP2mwK3WmLPILGn/elX4/m3k0W3k8V302X3UyXA5bPx0tPx8supiovZ6sfV5u/bXV83e\nm8XWk4nirfGsxbhGdMtScONcR1FQfDKqKTfU1t1PgLo92WehsXeqo3R+pWB8r25hpXpqpHBvLrG\niK8gjX07ATlTNhU7PhlzTnEjJjUHEUMfeR13CSV7QQ1XKQVHcSU7cUkzHnZlCjpZIlZFSmYlSgg\nfkPX/kNXAIGD/4L/4K3f0L3P0I3v0GXQ4EnU0F7q8Eoacji6tiY4LSE3M2G4o2JpsGYWmj7ZFjF\nU7QnPs4XlOHbn+LbnRbSVZLTXVba0tNS2d1R2dpRCe4uQi/m9mwA5yM1s+HoWbCUPtlKMWKgbXu\npd3FjcP9g92Tm9WD8/ndtZhc0PFm/15ewi0rdhyXvwpH144n537GZb6Gqj73y1+2Kt53KDz1KT3\n0pLwHJLwFyT/2yd11K9z0q972ZLCHgmYPluvPBlvurrUv3TfN3zYuPLD8e4nW+4mKk5nSwHzILx\nfrnh6w7guuN+rfl0pnKtLxcQDf6c/tpYZGVslIuOj5k8rCxlfaB1fbDxdhV+uth6t404XGmbHiu\ntrQ9381fRtubTtOFXsuQWN2SSs+Iy8lUw8JYTN2RhlSdiliMF/ArqMLMrUxFwfcJme8ckSyKkxw\nrxHb71Hb5+pXvwJ259hx69hx+9hp+ByQaM3oQOncQNb6cNLSbB+hNq6qMSEmBtdYtjnVPwgvFW0\nPTyGyqx60zU7Uw0aIs360x1heWHwkvj28uSWipTmxpK6juQVV2jNdDxGthEPWKquW+2e2Sxd3xh\ndHZ+dWv54HD5cHfkYL37eLV5f658YyhjG5m825N8iEw5G0i7HEw9RcbutAWu1Lmv13us1DgvVTk\nuVDnPVzpNldmNFloP5VmsN/tutQcfwKLA8wHLX+drf1tr/8s27Ntq99c16NcN2ON69+1y6/l8w+\nlszcls5dVi7e1q3cNG49NWM+D6eavtYrF2Z6QYOPVoc3JFgounoWRmgM1sd+UysvF0HvF9d+huv\netpr+dwuXlqtKShMdI7SFPPVsjYTVLRgktEj1HWnAsIWUiXgU2BWECLFp0OgsP2jl6CkEWejIwf\nk4Qfg0ORUtSAHeI3/OQ//BAwBPBqF0GDTyBe+Q199Rn+7jf6K4hgQOnhfQexfRtJyLnM7qGA2NS\n8vDxYe3V/R15/Xeh8W9BKq+dIgVFfkjo8VhUep9uXZoPMcoZmuECzvbpA17GusrW5o7sL0dc7ND\no6Pjs7v7a6vLu1eryzdL43e7rZvz/fDCLb/mT+2XzB7WLJ5VTB5WT+9XjO9WjmzUja5UDiCTz8q\nCtwq8lztcZxscJ2qdJusdJhpsR6PN9sOM9koc51rc3/oCfmfDjjdqL4Yab2+0rn75u9v28P/bI1\n8LjWc73UeTbbfDLbcLbQcLHU8LzTfr/ecLFQeTpbdr1U97Ld/rzTcbfWujNSOtWWnupr6GEg0ZQ\nZutnfvIZsul0ZuFvuvlhovlnt2JionBws7OpIDIk2MnYQVTPnljPmkDXhULYVENFnIuL9SMz7UV\nSfiVYMn1oEl0+FVt5CQMGMX8qYW9aYR9KICxIw8D1o4GvwwEto/0tYHxi/Bvd/D+r/PWDwL36Df\n/gP/B469C1i4Da65zgBuZPTsxybXxsQGpmZmdhWl9lc6N1f4bYL9dtqslsuM5zJ0ZhM05zKNhrP\nMu9PNe/LsOsvDBxpKRyFNi4MQbdmBg+Wxk/WJi82pq+3ph72p282+i8W24HQTqdKzqYK7haKv65\nXvWzUAXxdrX5aKL2byr4aTb0aTLjqjz3o8F+vd1upclqrcVmvdV2udJwrsZsqsR2vsFlq8TrsjT\nsfzbkYL76aBP+x8/t63+8749+3R582Bq+X4WfzrYezjUdzDSeLdVfLdZeLVSczpQcThYDru9XG5\n632583Oq6XmjYHi4iibNB9jZFniZl/T0RjseKLrZr7terH5drVtdbhkBJEN4qF3gIaMBoO4Bp2U\nPpuqjZC+h5y6gxiHIjmjNL6UCbu2o5SypZCOo5Sxp5KmnbiMEbesETcYIa/Fwo+UNqr3X4jsfQr\nv/RrS+4t/z9fA3q8xI78ljjzH950lI/fy+zZqe6bcA4PDI4LqK1KLkx2rEw3m6h0B0eddjrs1Rh\nsl+mslJguFFmOZZmNZ9hNl/lu9JSBLu1iGPWwNgaz2fr3/aglxPtt5tdB1NF53Ml7zsNT8bbPtZ\naXhabnmZb3hcbPpZaf1l932r5v19/OlF+OZZ4NJZ33xtyPJx7CIrRb/nRbfrUbfpUrX6SK7sSKb\nsQq75Q7/05GU29likLrdzDV+XUP8sTvxbWv0+87k972J593Rm3XEyWLb3nT97mTl9kjB8XQJUDS\ng+3q55n6t6Wmz9Wmz7dfDnvP5pu6ikLacwMGa1FVEzf3S0Bqiar41bQ2RdTBWNofImuzNGUDmRM\nSYK+uxyemyyRhyKJjzGnjKO0YZWAVrGnrLWQar2YFsz1vJzFtF006UXYaUlOsTs8QXEU0GSOzQe\nfLYZWzvfmT3elLPLkAMdDem5zh55CGq7xp0v6J7z+J6jxIQG4mwxSzYdFZde2FtdXhUYHiQXV2+\nX1OWTVO8+ny15W6z9XKZ7mKJ/lat3Wat63yZ63JtMOj2Ho4UXsxXA9V83e6+X22/XWi+X2p/We2\n+nWt7XOr6ugYHeFmFvaxDf91B/nE0eL8NezpAfD1EPu++JgbnU8Unw9knQ5kXI5lnAyknyARgxz\nudEWvNQbNVniMlDvMt3rt9MdfTubdzJbez1XcLzc+r8JeNoe8743erg5cLyMtl5OUq4mS562Ch5\nXC+ARC9P1Z0NFkKcDhRcjJdDt7lZbvjcb39Ya3jeKJ+oSt3uiVnsatktbt8rjV3sDRkoDwQUewH\nLQucHyzs6Urz9tUwtZWQ0mBRsxAy9lA081HVdZNRtRMBqbQ6SD/cpNWsBYQ16GWN2OWMufiUKfi\nUqdhBehcOHY+AjvrXd3uVNQZVtfiXN7rm17rkNwTUDXhUIT2re/2bhkJb+oPru/xKan3zC8Nysm\nJzkwPCPIIDbYrT3Ooz7NrSjGeqbTeb7efKDcbzdSYKTaZK7Oeq/Ha6064nKx5Wql626n7Zbf6+0\n/S02nC/VP+83PRttf1lufN5setxqftpBQF4edkaBrjfHDxbR1zuIO/2eh/3eu+2um5Wmq/mqi+m\n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\n+fb0w8X2093pw+3F7c3V7eXZ/dnhyxUg+vjrq7T3H893Ae+/3p9+vz3+5e4I4PsteMLe/dn61eH\nS2e7c9uLoxsLw+vzIysLoxsr0zubS/t7m0eHu0tLC0dHJ5flNc0O7prIuNSkjCw23lJC8hpy6CJ\ncQ4JEUhxiwDKwDjOR4pIBucAAeBP8E6Mb5hCXEIRAeEJ6elNHS1NqH7O3qbO9FwkqK85qaqlpaq\nhCI5tFR+NLSGLDm5eXJycn+/n4oAtHe0FQ+PAwfRMKQnW2bC9P7G3PlZSkGlnLmHipy1vwqXpKe\nxc72WfbaofqKXjpE0rT02lyC9lL8tmIKgUq8zvzAfsWDhVmdGSBa3kIuaVoOKeq6oWIGURLaEaJ\nSPqwSPiyKYXzi/hxyYfwi3uy8LowC7mw8zkxyAYKKfrwGwcImPgKRyXp9iITJnsz+hthZGDjLox\nycHPeD6N3HmzOg6JuL85uzs/vzEyDq56vjb7dn3+/Ov9+dfrs9AX7yCEi/3AMAz7893QJ2cbw9d\n7Axfbq9eLy9cLA+t7U2vbk6s7E6t7G+tLW5en5+enFxdXF2PT+znBSTpiKnwcsqxEDBTPGFkhSf\nlASPhAiHiOAzAR4GHhjBr1+wv6C8QcHHxAfHuOi4rHSsduZ2Oak5kSERleVVcCisrbW5B9HdUF+\nFRHZ2dNQ1NJTk5ycDtLRUgHPl6uqKSkqyKiryMjLi8rKTSvKzOxvrlqdGF6cGSsuTXAOMwGos2I\nJhFKWqE60q5SUl76ckZCdNr80h5iwj5SnLZc0j5S/N78YLXEE4iIfS+gvEMU7FO0vPIUnVMlbWK\nVvdPkvZJEXKJl/FqUzbNFPOMFVaIYxbKYzPOE3RKFXBJk/dMkXGKk7CLlw0Kde4Fx43Bk/pb4qb\n6srdHG8BWzJAffwAfOPyGGj57ury6ebm8fLs4fzo4QyQe/bb4w3A15vz+7P966Ptm+Od2xOQV22\ne760ebsxvLU6szQ3vrUztrk7trgCWp9aWJlcWp5aXZleW5ycmJsbGxtZWNudmFovzSrNT82NDk5\nip2UjxyciJKCiJKMm+kANyQRINyCXAJiTEIsT4hAnYR32PBh4xN7JIiEwI8g1Mik2srqxqbmyqr\nalqaqxtbqrt7e2qqyvp7m6MiPDx93dubCwF0q6tLUxJicjOTvTzc4mOCExNiCrJTYe11I4OdtU1\n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\nJLVF+RQEIARvP5K/0fWQVnRkAr6hG0ojbP/eNpvDPofXIVfIu1zBMUcqrNGIVv0NWJZl4NDFBpB\n0oKXXQKNT/wQ2iSm6MYaWGHglyMdmapeWWPc0+62BvKI3eb8X3FemEIS+k6H0k4FUkIEcI5LP+j\nNvxgvuZ0v3e1Pnm0JHSjw64o3zHcRiNBhCFUi8xNDitCgC5QichLECVGjb4qzSXOS9dTkd1Ji9j\nAUj3VTTQi2zo10yIjziApwjve08LBQd9EVNlNn1ZOkBxcYKDKYKjGZyTHYqvHqC9NbSAkb83JpM\nrBZ8Eg8jy8etw2fVA5eVA1e1I3eNE/fNM3ctc/ftIJlbu21feIKufkVsPIOU44dT33fM3DSOX5e\nM/tq0/Adi7alnbqulW5WLWU6IRlqGhlkY4zMjBA80+LWw1Xy5Ihqt1QO4mHRQlH2pIxq0LBN4Dc\nM4uHRQEhpsXVM0mRRw8TjemASqgk4nvQyuU7IOjcJHsNqiGcQs64ltnc7qkMNtlcYe1qhukyagH\n8mu4kcDkbOhY1VGR2OHEImBlUcUUrnXLQoEIhDTIAHPWLnwRNXsTANYredmf/LZWNZ+b9LuYNp2\nf8oeMhncmm23PWarKQKMgPHrkTxA9GxDMCzDpjZYs8BZKs2IO1qNzkfic4AMrqcYlhXnRwdh3Gh\n9Li8VWi9NOl8DlgALnigX6YwQw+Ikt9KUgNzYgJQQdy9LVUdDcXM1HkMFJmMFJhNFRjN5BhNZOj\nMZZn0ReltZPidFCRNBfmlyyo6EjIeh+fO6oauqoevq0Zu6ibum2fvmhbvmxZuWxevm+fvOlSdg4\nl2LD50LD93zt21TV/Wj3xpnvzXMPLfN/ja4slDeqMBIL8pKISNBzyXyhYgVgscBwReAiNuQx7fb\n2WXImCcJFE86ZfSbmsWxBleqiFjiaHoxp7Y6GQYKoTNBFO1YeNQJpc1pjf1FKMRA6YdmlyDmliV\ngFkfrVybmmsOfBjOxT+WzjAfX6wdXN3Bn0XBnFjTCU3JjcMpSNosTB/vMX9NDJ/qofL3IZLW0FE\n1Eg+d6b/zNTNE2PGF/GFwwMBPck/F8MPMYlrTfHnsETboayD4Dl10eyACKbk02K/ZRzHWSSDXmi\n1SlDVYgtuN+a831wZT1nYsYkYMokaUgnocqrY8Os5s2g5s+W4idZEqgcVaEU2qQS5SHla+1upup\n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\nqKW7C5yAgAAAABJRU5ErkJggg==</BINVAL></PHOTO></vCard>');


-- ---
-- Globals
-- ---

-- SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
-- SET FOREIGN_KEY_CHECKS=0;

-- ---
-- Table 'location'
-- 
-- ---

DROP TABLE IF EXISTS `location`;
		
CREATE TABLE `location` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `parent` INT NULL DEFAULT NULL COMMENT 'Ссылка на родительский адрес',
  `name` VARCHAR(64) NOT NULL,
  `type` INT NOT NULL COMMENT 'flat,floor,house,block,district,city,region,country,world',
  `longitude` FLOAT NULL DEFAULT NULL,
  `lattitude` FLOAT NULL DEFAULT NULL,
  `altitude` FLOAT NULL DEFAULT NULL COMMENT 'высота или номер этажа',
  PRIMARY KEY (`id`),
KEY (`parent`)
);

-- ---
-- Table 'user'
-- 
-- ---

DROP TABLE IF EXISTS `user`;
		
CREATE TABLE `user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `location` INT NULL DEFAULT NULL COMMENT 'ссылка на адрес проживания',
  `login` VARCHAR(32) NOT NULL,
  `password` VARCHAR(32) NOT NULL,
  `firstName` VARCHAR(32) NULL DEFAULT NULL,
  `secondName` VARCHAR(32) NULL DEFAULT NULL,
  `DOB` DATE NULL DEFAULT NULL COMMENT 'дата рождения',
  `sex` INT NULL DEFAULT NULL,
  `intrests` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
KEY (`location`)
);

-- ---
-- Table 'group'
-- 
-- ---

DROP TABLE IF EXISTS `group`;
		
CREATE TABLE `group` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `location` INT NULL DEFAULT NULL,
  `comment` VARCHAR(255) NULL DEFAULT NULL COMMENT 'расширенное описание или коммент',
  `shortName` VARCHAR(16) NOT NULL,
  `name` VARCHAR(200) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
KEY (`location`)
);

-- ---
-- Table 'topic'
-- 
-- ---

DROP TABLE IF EXISTS `topic`;
		
CREATE TABLE `topic` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `message` INT NOT NULL COMMENT 'сообщение',
  `messageNum` INT NOT NULL DEFAULT 0 COMMENT 'число сообщений в теме',
  `viewers` INT NOT NULL DEFAULT 0 COMMENT 'число пользоателей, просматривающих сообщение',
  `usersNum` INT NOT NULL DEFAULT 1 COMMENT 'число пользователей оставивших сообщения в теме',
  `lastUpdate` DATETIME NOT NULL COMMENT 'время создания последнего дочернего сообщения',
  `likes` INT NOT NULL DEFAULT 0,
  `unlikes` INT NOT NULL DEFAULT 0,
  `rubric` INT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
KEY (`rubric`)
);

-- ---
-- Table 'message'
-- сообщение
-- ---

DROP TABLE IF EXISTS `message`;
		
CREATE TABLE `message` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `parent` INT NULL DEFAULT NULL COMMENT 'идентификатор родительского сообщения, NULL для корневого со',
  `type` INT NOT NULL COMMENT 'тип один из (сообщение, чат)',
  `topic` INT NOT NULL,
  `author` INT NOT NULL COMMENT 'автор сообщения или темы',
  `recipient` INT NULL DEFAULT NULL COMMENT 'адресат задан только для личных сообщений, иначе NULL',
  `created` DATETIME NOT NULL COMMENT 'дата создания',
  `edited` DATETIME NULL DEFAULT NULL,
  `approved` INT NULL DEFAULT NULL COMMENT 'идентификатор пользователя промодерировавшего сообщение',
  `content` MEDIUMTEXT NOT NULL COMMENT 'содержание сообщения',
  `likes` INT NOT NULL DEFAULT 0,
  `unlikes` INT NOT NULL DEFAULT 0,
  `group` INT NOT NULL,
  `idForum` INT NULL DEFAULT NULL,
  `idShop` INT NULL DEFAULT NULL,
  `idDialog` INT NULL DEFAULT NULL,
  `idNews` INT NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT 'сообщение';

-- ---
-- Table 'user_message'
-- отношение пользователя к сообщению
-- ---

DROP TABLE IF EXISTS `user_message`;
		
CREATE TABLE `user_message` (
  `user` INT NOT NULL,
  `message` INT NOT NULL,
  `read` bit NOT NULL DEFAULT 0,
  `unintrested` bit NOT NULL DEFAULT 0,
  `like` INT NULL DEFAULT NULL,
  `unlike` INT NULL DEFAULT NULL,
  PRIMARY KEY (`user`, `message`),
KEY (`message`)
) COMMENT 'отношение пользователя к сообщению';

-- ---
-- Table 'user_rubric'
-- Рубрики интересные пользователю
-- ---

DROP TABLE IF EXISTS `user_rubric`;
		
CREATE TABLE `user_rubric` (
  `user` INT NOT NULL,
  `rubric` INT NOT NULL,
  `grp` INT NULL DEFAULT NULL,
  `subscribed` TINYINT NOT NULL DEFAULT 1,
  `topics` INT NOT NULL DEFAULT 0 COMMENT 'число тем в рубрике от пользователя',
  `messages` INT NOT NULL DEFAULT 0 COMMENT 'число сообщений пользователя в рубрике',
  PRIMARY KEY (`user`, `rubric`, `grp`)
) COMMENT 'Рубрики интересные пользователю';

-- ---
-- Table 'rubric'
-- 
-- ---

DROP TABLE IF EXISTS `rubric`;
		
CREATE TABLE `rubric` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(32) NOT NULL DEFAULT 'NULL',
  `description` VARCHAR(255) NOT NULL DEFAULT 'NULL' COMMENT 'Описание рубрики',
  `topics` INT NULL DEFAULT NULL COMMENT 'число тем в рубрике',
  `messages` TINYINT NOT NULL DEFAULT 0 COMMENT 'число сообщений в рубрике',
  PRIMARY KEY (`id`)
);

-- ---
-- Table 'friendship'
-- список друзей
-- ---

DROP TABLE IF EXISTS `friendship`;
		
CREATE TABLE `friendship` (
  `user` INT NOT NULL AUTO_INCREMENT,
  `friend` INT NOT NULL COMMENT 'идентификатор друга',
  `state` INT NOT NULL DEFAULT 0 COMMENT 'состояние - запрос, подтверждено, отклонено ',
  PRIMARY KEY (`user`),
KEY (`friend`)
) COMMENT 'список друзей';

-- ---
-- Table 'session'
-- таблица активных сессий
-- ---

DROP TABLE IF EXISTS `session`;
		
CREATE TABLE `session` (
  `salt` CHAR(16) NOT NULL,
  `user` INT NOT NULL,
  `created` DATETIME NOT NULL,
  `userAgent` VARCHAR(64) NULL DEFAULT NULL,
  `cookie` VARCHAR(32) NULL DEFAULT NULL,
  `lastUpdate` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`salt`),
KEY (`user`)
) COMMENT 'таблица активных сессий';

-- ---
-- Table 'user_topic'
-- активность пользователя в теме
-- ---

DROP TABLE IF EXISTS `user_topic`;
		
CREATE TABLE `user_topic` (
  `user` INT NOT NULL,
  `topic` INT NOT NULL,
  `archived` bit NOT NULL DEFAULT false,
  `messages` INT NOT NULL DEFAULT 0 COMMENT 'число сообщкний пользоваткля в топике',
  `lastActivity` INT NOT NULL,
  `dolike` TINYINT NOT NULL DEFAULT 0,
  `readMessageNum` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`user`, `topic`)
) COMMENT 'активность пользователя в теме';

-- ---
-- Table 'curtom_user_group'
-- Таблица пользовательских групп
-- ---

DROP TABLE IF EXISTS `curtom_user_group`;
		
CREATE TABLE `curtom_user_group` (
  `user` INT NOT NULL AUTO_INCREMENT,
  `grp` INT NOT NULL COMMENT 'идентификатор группы но при автогенерации sql из sqld group ',
  `location` INT NULL DEFAULT NULL,
  `creator` INT NULL DEFAULT NULL,
  PRIMARY KEY (`user`)
) COMMENT 'Таблица пользовательских групп';

-- ---
-- Foreign Keys 
-- ---

ALTER TABLE `location` ADD FOREIGN KEY (parent) REFERENCES `location` (`id`);
ALTER TABLE `user` ADD FOREIGN KEY (location) REFERENCES `location` (`id`);
ALTER TABLE `group` ADD FOREIGN KEY (id) REFERENCES `location` (`id`);
ALTER TABLE `topic` ADD FOREIGN KEY (message) REFERENCES `message` (`id`);
ALTER TABLE `topic` ADD FOREIGN KEY (rubric) REFERENCES `rubric` (`id`);
ALTER TABLE `message` ADD FOREIGN KEY (parent) REFERENCES `message` (`id`);
ALTER TABLE `message` ADD FOREIGN KEY (topic) REFERENCES `topic` (`id`);
ALTER TABLE `message` ADD FOREIGN KEY (author) REFERENCES `user` (`id`);
ALTER TABLE `message` ADD FOREIGN KEY (recipient) REFERENCES `user` (`id`);
ALTER TABLE `message` ADD FOREIGN KEY (approved) REFERENCES `user` (`id`);
ALTER TABLE `message` ADD FOREIGN KEY (idForum) REFERENCES `message` (`id`);
ALTER TABLE `message` ADD FOREIGN KEY (idShop) REFERENCES `message` (`id`);
ALTER TABLE `message` ADD FOREIGN KEY (idDialog) REFERENCES `message` (`id`);
ALTER TABLE `message` ADD FOREIGN KEY (idNews) REFERENCES `message` (`id`);
ALTER TABLE `user_message` ADD FOREIGN KEY (user) REFERENCES `user` (`id`);
ALTER TABLE `user_message` ADD FOREIGN KEY (message) REFERENCES `message` (`id`);
ALTER TABLE `user_rubric` ADD FOREIGN KEY (user) REFERENCES `user` (`id`);
ALTER TABLE `user_rubric` ADD FOREIGN KEY (rubric) REFERENCES `rubric` (`id`);
ALTER TABLE `friendship` ADD FOREIGN KEY (user) REFERENCES `user` (`id`);
ALTER TABLE `friendship` ADD FOREIGN KEY (friend) REFERENCES `user` (`id`);
ALTER TABLE `session` ADD FOREIGN KEY (user) REFERENCES `user` (`id`);
ALTER TABLE `user_topic` ADD FOREIGN KEY (user) REFERENCES `user` (`id`);
ALTER TABLE `user_topic` ADD FOREIGN KEY (topic) REFERENCES `topic` (`id`);
ALTER TABLE `curtom_user_group` ADD FOREIGN KEY (user) REFERENCES `user` (`id`);
ALTER TABLE `curtom_user_group` ADD FOREIGN KEY (grp) REFERENCES `group` (`id`);
ALTER TABLE `curtom_user_group` ADD FOREIGN KEY (location) REFERENCES `location` (`id`);
ALTER TABLE `curtom_user_group` ADD FOREIGN KEY (creator) REFERENCES `user` (`id`);

-- ---
-- Table Properties
-- ---

-- ALTER TABLE `location` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `user` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `group` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `topic` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `message` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `user_message` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `user_rubric` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `rubric` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `friendship` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `session` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `user_topic` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- ALTER TABLE `curtom_user_group` ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ---
-- Test Data
-- ---

-- INSERT INTO `location` (`id`,`parent`,`name`,`type`,`longitude`,`lattitude`,`altitude`) VALUES
-- ('','','','','','','');
-- INSERT INTO `user` (`id`,`location`,`login`,`password`,`firstName`,`secondName`,`DOB`,`sex`,`intrests`) VALUES
-- ('','','','','','','','','');
-- INSERT INTO `group` (`id`,`location`,`comment`,`shortName`,`name`) VALUES
-- ('','','','','');
-- INSERT INTO `topic` (`id`,`message`,`messageNum`,`viewers`,`usersNum`,`lastUpdate`,`likes`,`unlikes`,`rubric`) VALUES
-- ('','','','','','','','','');
-- INSERT INTO `message` (`id`,`parent`,`type`,`topic`,`author`,`recipient`,`created`,`edited`,`approved`,`content`,`likes`,`unlikes`,`group`,`idForum`,`idShop`,`idDialog`,`idNews`) VALUES
-- ('','','','','','','','','','','','','','','','','');
-- INSERT INTO `user_message` (`user`,`message`,`read`,`unintrested`,`like`,`unlike`) VALUES
-- ('','','','','','');
-- INSERT INTO `user_rubric` (`user`,`rubric`,`grp`,`subscribed`,`topics`,`messages`) VALUES
-- ('','','','','','');
-- INSERT INTO `rubric` (`id`,`name`,`description`,`topics`,`messages`) VALUES
-- ('','','','','');
-- INSERT INTO `friendship` (`user`,`friend`,`state`) VALUES
-- ('','','');
-- INSERT INTO `session` (`salt`,`user`,`created`,`userAgent`,`cookie`,`lastUpdate`) VALUES
-- ('','','','','','');
-- INSERT INTO `user_topic` (`user`,`topic`,`archived`,`messages`,`lastActivity`,`dolike`,`readMessageNum`) VALUES
-- ('','','','','','','');
-- INSERT INTO `curtom_user_group` (`user`,`grp`,`location`,`creator`) VALUES
-- ('','','','');


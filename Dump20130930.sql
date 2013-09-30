CREATE DATABASE  IF NOT EXISTS `entropy` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `entropy`;
-- MySQL dump 10.13  Distrib 5.6.13, for Win32 (x86)
--
-- Host: localhost    Database: entropy
-- ------------------------------------------------------
-- Server version	5.6.13

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accounts` (
  `account_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `battle_tokens` int(10) unsigned NOT NULL DEFAULT '0',
  `gold_tokens` int(10) unsigned NOT NULL DEFAULT '0',
  `active_deck_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  KEY `Active deck_idx` (`active_deck_id`),
  CONSTRAINT `Active deck` FOREIGN KEY (`active_deck_id`) REFERENCES `card_collections` (`collection_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (7,'crovea','lol',0,0,8),(8,'crovea2','lol',0,0,10);
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `card_collection_relation`
--

DROP TABLE IF EXISTS `card_collection_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `card_collection_relation` (
  `collection_id` int(11) NOT NULL,
  `card_id` int(11) NOT NULL,
  `count` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`collection_id`,`card_id`),
  KEY `card_idx` (`card_id`),
  CONSTRAINT `collection` FOREIGN KEY (`collection_id`) REFERENCES `card_collections` (`collection_id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `card` FOREIGN KEY (`card_id`) REFERENCES `cards` (`card_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_collection_relation`
--

LOCK TABLES `card_collection_relation` WRITE;
/*!40000 ALTER TABLE `card_collection_relation` DISABLE KEYS */;
INSERT INTO `card_collection_relation` VALUES (7,2,30),(7,3,9),(7,4,1),(8,2,10),(8,3,2),(8,4,1),(9,2,19),(9,3,8),(9,4,3),(10,2,9),(10,3,3),(10,4,2);
/*!40000 ALTER TABLE `card_collection_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `card_collections`
--

DROP TABLE IF EXISTS `card_collections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `card_collections` (
  `collection_id` int(11) NOT NULL AUTO_INCREMENT,
  `collection_type` int(6) NOT NULL,
  `account_id` int(11) NOT NULL,
  PRIMARY KEY (`collection_id`),
  KEY `Owner of collection_idx` (`account_id`),
  CONSTRAINT `Owner of collection` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`account_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_collections`
--

LOCK TABLES `card_collections` WRITE;
/*!40000 ALTER TABLE `card_collections` DISABLE KEYS */;
INSERT INTO `card_collections` VALUES (7,1,7),(8,2,7),(9,1,8),(10,2,8);
/*!40000 ALTER TABLE `card_collections` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cards`
--

DROP TABLE IF EXISTS `cards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cards` (
  `card_id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(45) NOT NULL,
  `race` int(11) NOT NULL,
  `type` int(6) NOT NULL,
  `rarity` int(11) NOT NULL,
  `race_cost` int(6) DEFAULT NULL,
  `any_cost` int(6) DEFAULT NULL,
  `strength` int(6) DEFAULT NULL,
  `intelligence` int(6) DEFAULT NULL,
  `vitality` int(6) DEFAULT NULL,
  PRIMARY KEY (`card_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cards`
--

LOCK TABLES `cards` WRITE;
/*!40000 ALTER TABLE `cards` DISABLE KEYS */;
INSERT INTO `cards` VALUES (2,'Crawnid Larvae Swarm',2,0,0,1,1,1,1,1),(3,'Anid Sting Swarm',2,0,1,1,1,2,1,2),(4,'Anid Swarm Spawner',2,0,2,2,1,2,3,3),(5,'Darwinistic Innihilation',2,6,3,3,3,0,0,0);
/*!40000 ALTER TABLE `cards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `race`
--

DROP TABLE IF EXISTS `race`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `race` (
  `race_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `resource_name` varchar(45) NOT NULL,
  PRIMARY KEY (`race_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `race`
--

LOCK TABLES `race` WRITE;
/*!40000 ALTER TABLE `race` DISABLE KEYS */;
INSERT INTO `race` VALUES (1,'Orc','Meat'),(2,'Crawnid','Biomass'),(3,'Human','Gold');
/*!40000 ALTER TABLE `race` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_items`
--

DROP TABLE IF EXISTS `shop_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `shop_items` (
  `item_id` int(11) NOT NULL AUTO_INCREMENT,
  `item_type` int(11) NOT NULL,
  `item_name` varchar(45) NOT NULL,
  `item_description` varchar(200) DEFAULT NULL,
  `item_cost_bt` int(11) DEFAULT NULL,
  `item_cost_gt` int(11) DEFAULT NULL,
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_items`
--

LOCK TABLES `shop_items` WRITE;
/*!40000 ALTER TABLE `shop_items` DISABLE KEYS */;
INSERT INTO `shop_items` VALUES (1,1,'Starters Pack','A basic pack good for a beginner with not many cards! Less useful for advanced collectors',1000,50),(2,1,'Collectors Pack','A well balanced pack with a decent chance of getting rare cards!',2000,90),(3,1,'Gold Pack','A high end pack with the highest chance of obtaining rare and legendary cards!',5000,200);
/*!40000 ALTER TABLE `shop_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'entropy'
--
/*!50003 DROP PROCEDURE IF EXISTS `create_account` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `create_account`(
	IN _username varchar(45),
	IN _password varchar(45)

)
BEGIN

	insert into accounts values (null, _username, _password, 5000,0, null);

	SET @last_account_id = LAST_INSERT_ID();

	-- Insert an 'All-cards' collection
	insert into card_collections values (null, 1, @last_account_id);
	-- Insert a starting deck
	insert into card_collections values (null, 2, @last_account_id);

	SET @last_collection_id = last_insert_id();

	update accounts set active_deck_id = @last_collection_id where account_id=@last_account_id;


END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-09-30 20:44:44

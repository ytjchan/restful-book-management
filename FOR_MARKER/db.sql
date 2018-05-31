/* These are copied from mySQL workbench directly yet not tested */

CREATE DATABASE `bookmanagement` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE bookmanagement;
CREATE TABLE `book` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) DEFAULT NULL,
  `author` varchar(100) DEFAULT NULL,
  `publisher` varchar(100) DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  `available` tinyint(4) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1307 DEFAULT CHARSET=latin1;
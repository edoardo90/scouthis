-- phpMyAdmin SQL Dump
-- version 3.4.11.1deb2
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Apr 18, 2014 at 05:30 PM
-- Server version: 5.5.35
-- PHP Version: 5.4.4-14+deb7u8

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `scoutish`
--

-- --------------------------------------------------------

--
-- Table structure for table `FRIENDSHIP`
--

CREATE TABLE IF NOT EXISTS `FRIENDSHIP` (
  `ID1` int(11) NOT NULL,
  `ID2` int(11) NOT NULL,
  PRIMARY KEY (`ID1`,`ID2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `FRIENDSHIP`
--

INSERT INTO `FRIENDSHIP` (`ID1`, `ID2`) VALUES
(10, 20),
(10, 22),
(10, 30),
(11, 20),
(12, 32),
(15, 20),
(15, 38),
(20, 10),
(59, 20);

-- --------------------------------------------------------

--
-- Table structure for table `NAMES`
--

CREATE TABLE IF NOT EXISTS `NAMES` (
  `ID` int(11) NOT NULL,
  `NAME` varchar(100) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `NAMES`
--

INSERT INTO `NAMES` (`ID`, `NAME`) VALUES
(519418557, 'Carolina Ssq'),
(523594984, 'Daniele Chiappa');

-- --------------------------------------------------------

--
-- Table structure for table `POSITION`
--

CREATE TABLE IF NOT EXISTS `POSITION` (
  `ID` int(11) NOT NULL,
  `ONLINE` tinyint(1) NOT NULL,
  `LATITUDE` double NOT NULL,
  `LONGITUDE` double NOT NULL,
  `LASTACCESS` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `POSITION`
--

INSERT INTO `POSITION` (`ID`, `ONLINE`, `LATITUDE`, `LONGITUDE`, `LASTACCESS`) VALUES
(10, 1, 799.12, 51.21, '0000-00-00 00:00:00'),
(11, 1, 799.12, 51.21, '0000-00-00 00:00:00'),
(12, 1, 123.12, 51.21, '0000-00-00 00:00:00'),
(15, 1, 799.12, 51.21, '0000-00-00 00:00:00'),
(20, 1, 2020, 2021, '0000-00-00 00:00:00'),
(30, 1, 463, 657, '0000-00-00 00:00:00'),
(59, 1, 59.12, 509.95, '0000-00-00 00:00:00');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

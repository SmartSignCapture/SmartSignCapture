# Building Instructions

## Android App 
### Unity
For the implementation of the Input using an avatar Unity 5.2.2 was used. To include the Unity-Part in the Android App you must export an "Android Project" using Unity's export options.
Due to the fact Unity isn't capable of exporting proper Android Studio Projects you need to copy / link the following files and folders to the Android Studio Project:

```
./android/SmartSignCapture/app/src/main/assets/bin/-> ./unity/AndroidProject/SmartSignCapture/assets/bin
./android/SmartSignCapture/app/src/main/jniLibs/armeabi-v7a -> ./unity/AndroidProject/SmartSignCapture/libs/armeabi-v7a
./android/SmartSignCapture/app/src/main/jniLibs/x86/-> ./unity/AndroidProject/SmartSignCapture/libs/x86/
```

### Android Studio

#### GCM
The App uses Google Cloud Messaging for informing the clients about new Messages to enable GCM in your app follow the instructions on https://developers.google.com/cloud-messaging/

#### API
To connect the App your Server you need to alter the values of the string constants in 
```
./android/SmartSignCapture/app/src/main/java/at/fhs/smartsigncapture/data/API/APIConstants.java
```

## Server

#### API
For setting up the connection with your MySQL-Server you need to add your Server-Credentials in
```
./server/api/config.php
```

To enable downloading of user images you need to the add the link to your image folder in:

````
./server/api/Controller/UserController.php

180: $entry[self::KEY_IMAGE] = LINK_TO_YOUR_IMAGE_FOLDER.$entry[self::KEY_IMAGE];
````

### Dictionary
If you want to use the Dictionary-Page you need to add the link to your API in
```
./server/dictionary/partials/partial_sign.php

87: var API_URL = "YOUR URL HERE";
```

### SQL Create-Scripts
``` SQL
-- phpMyAdmin SQL Dump
-- version 4.0.10.9
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jan 28, 2016 at 09:23 AM
-- Server version: 5.1.73
-- PHP Version: 5.3.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `ssc_dev`
--

-- --------------------------------------------------------

--
-- Table structure for table `Friendship`
--

CREATE TABLE IF NOT EXISTS `Friendship` (
  `fromUser` int(11) NOT NULL,
  `toUser` int(11) NOT NULL,
  `state` tinyint(4) NOT NULL,
  `dateRequest` datetime NOT NULL,
  `dateAccepted` datetime NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `fromUser` (`fromUser`,`toUser`),
  KEY `toUser` (`toUser`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `GCMToken`
--

CREATE TABLE IF NOT EXISTS `GCMToken` (
  `userID` int(11) NOT NULL,
  `token` varchar(255) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`userID`,`token`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Message`
--

CREATE TABLE IF NOT EXISTS `Message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fromUserID` int(11) NOT NULL,
  `toUserID` int(11) NOT NULL,
  `message` text NOT NULL,
  `date` datetime NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `from` (`fromUserID`),
  KEY `to` (`toUserID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=306 ;

-- --------------------------------------------------------

--
-- Table structure for table `oauth_access_tokens`
--

CREATE TABLE IF NOT EXISTS `oauth_access_tokens` (
  `access_token` varchar(40) NOT NULL,
  `client_id` varchar(80) NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `expires` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `scope` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`access_token`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `oauth_authorization_codes`
--

CREATE TABLE IF NOT EXISTS `oauth_authorization_codes` (
  `authorization_code` varchar(40) NOT NULL,
  `client_id` varchar(80) NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `redirect_uri` varchar(2000) NOT NULL,
  `expires` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `scope` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`authorization_code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `oauth_clients`
--

CREATE TABLE IF NOT EXISTS `oauth_clients` (
  `client_id` varchar(80) NOT NULL,
  `client_secret` varchar(80) NOT NULL,
  `redirect_uri` varchar(2000) NOT NULL,
  PRIMARY KEY (`client_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `oauth_refresh_tokens`
--

CREATE TABLE IF NOT EXISTS `oauth_refresh_tokens` (
  `refresh_token` varchar(40) NOT NULL,
  `client_id` varchar(80) NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `expires` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `scope` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`refresh_token`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `oauth_users`
--

CREATE TABLE IF NOT EXISTS `oauth_users` (
  `username` varchar(255) NOT NULL,
  `password` varchar(2000) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Sign`
--

CREATE TABLE IF NOT EXISTS `Sign` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userID` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `date` datetime NOT NULL,
  `sign` text NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `userID` (`userID`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=172 ;

-- --------------------------------------------------------

--
-- Table structure for table `SignHasTag`
--

CREATE TABLE IF NOT EXISTS `SignHasTag` (
  `signID` int(11) NOT NULL,
  `tagID` int(11) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`signID`,`tagID`),
  KEY `tagID` (`tagID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Tag`
--

CREATE TABLE IF NOT EXISTS `Tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tag` varchar(255) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=57 ;

-- --------------------------------------------------------

--
-- Table structure for table `User`
--

CREATE TABLE IF NOT EXISTS `User` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `nickName` varchar(255) NOT NULL,
  `firstName` varchar(255) NOT NULL,
  `lastName` varchar(255) NOT NULL,
  `image` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `registrationDate` datetime NOT NULL,
  `lastLogin` datetime DEFAULT NULL,
  `lastConnection` datetime DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=55 ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `Friendship`
--
ALTER TABLE `Friendship`
  ADD CONSTRAINT `Friendship_ibfk_1` FOREIGN KEY (`fromUser`) REFERENCES `User` (`id`),
  ADD CONSTRAINT `Friendship_ibfk_2` FOREIGN KEY (`toUser`) REFERENCES `User` (`id`);

--
-- Constraints for table `GCMToken`
--
ALTER TABLE `GCMToken`
  ADD CONSTRAINT `GCMToken_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `User` (`id`);

--
-- Constraints for table `Message`
--
ALTER TABLE `Message`
  ADD CONSTRAINT `Message_ibfk_1` FOREIGN KEY (`fromUserID`) REFERENCES `User` (`id`),
  ADD CONSTRAINT `Message_ibfk_2` FOREIGN KEY (`toUserID`) REFERENCES `User` (`id`);

--
-- Constraints for table `Sign`
--
ALTER TABLE `Sign`
  ADD CONSTRAINT `Sign_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `User` (`id`);

--
-- Constraints for table `SignHasTag`
--
ALTER TABLE `SignHasTag`
  ADD CONSTRAINT `SignHasTag_ibfk_1` FOREIGN KEY (`signID`) REFERENCES `Sign` (`id`),
  ADD CONSTRAINT `SignHasTag_ibfk_2` FOREIGN KEY (`tagID`) REFERENCES `Tag` (`id`);
```

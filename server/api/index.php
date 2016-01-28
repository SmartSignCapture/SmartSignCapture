<?php
/**
 * Created by PhpStorm.
 * User: MartinTiefengrabner
 * Date: 13/07/15
 * Time: 15:27
 */

require 'config.php';
require 'vendor/autoload.php';
require 'Controller/UserController.php';
require 'Controller/GCMController.php';
require 'Controller/OAuthController.php';
require 'Controller/FriendshipController.php';
require 'Controller/MessageController.php';
require 'Controller/SignController.php';
require 'Middleware/OAuthMiddleware.php';
require 'Middleware/Compress.php';


\Slim\Slim::registerAutoloader();

$app = new \Slim\Slim(array(
    'debug' => true,
    'mode' => 'development'
));


//$app->add(new API\Middleware\OAuthMiddleware());

$compressor = new \AaronSaray\SlimPHPMiddleware\Compress();

$app->add($compressor);

$app->post('/request-token/', '\API\Controller\OAuthController:login');

$app->post('/users/', 'API\Controller\UserController:post');
$app->get('/users/', 'API\Controller\UserController:get');
$app->get('/users/:id/', 'API\Controller\UserController:get');
$app->post('/users/:id/gcm-token/:token/', 'API\Controller\GCMController:putGCMToken');
$app->post('/users/:id/friend-request/:friendID/', 'API\Controller\FriendshipController:postFriendshipRequest');
$app->post('/users/:id/friendship-accepted/:friendID/', 'API\Controller\FriendshipController:postFriendshipAccept');
$app->get('/users/:id/friend/:friendID/', 'API\Controller\FriendshipController:getFriend');
$app->get('/users/:id/friends/', 'API\Controller\FriendshipController:getFriends');

$app->post('/users/:id/send-message/:receiver/', 'API\Controller\MessageController:post');

$app->get('/users/:id/messages/received/:messageID/', 'API\Controller\MessageController:getReceivedMessages');
$app->get('/users/:id/messages/received/', 'API\Controller\MessageController:getReceivedMessages');

$app->get('/users/:id/messages/sent/:messageID/', 'API\Controller\MessageController:getSentMessages');
$app->get('/users/:id/messages/sent/', 'API\Controller\MessageController:getSentMessages');
$app->get('/users/:id/messages/', 'API\Controller\MessageController:getMessages');
$app->get('/users/:id/messages/:messageID/', 'API\Controller\MessageController:getMessages');

$app->post('/users/:id/signs/', 'API\Controller\SignController:post');

$app->get('/signs/:id/', 'API\Controller\SignController:get');
$app->get('/signs/', 'API\Controller\SignController:get');
$app->get('/signs/tags/:id/', 'API\Controller\SignController:getByTag');
$app->get('/signs/users/:id/', 'API\Controller\SignController:getByUser');

$app->run();



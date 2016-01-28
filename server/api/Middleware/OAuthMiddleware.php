<?php
/**
 * Created by PhpStorm.
 * User: MartinTiefengrabner
 * Date: 16/07/15
 * Time: 08:18
 */

namespace API\Middleware;

// error reporting (this is a demo, after all!)
ini_set('display_errors', 1);
error_reporting(E_ALL);

// Autoloading (composer is preferred, but for this example let's just do this)
require_once('vendor/bshaffer/oauth2-server-php/src/OAuth2/Autoloader.php');
\OAuth2_Autoloader::register();
require_once('SSCOauthStoragePDO.php');
require_once('SSC_OAuth2_GrantType_UserCredentials.php');

class OAuthMiddleware extends \Slim\Middleware
{

    protected $storage;
    protected $server;

    public function __construct()
    {
        $dsn = 'mysql:host=' . MYSQL_SERVER . ';dbname=' . MYSQL_DB . ';charset=utf8';
        $this->storage = new SSCOauthStoragePDO(array('dsn' => $dsn, 'username' => MYSQL_USER, 'password' => MYSQL_PW));
        $this->server = new \OAuth2_Server($this->storage);
    }

    public function call()
    {
        if ($this->app->request->getPathInfo() != "/request-token/") {
            $request = \OAuth2_Request::createFromGlobals();


            if(isset($request->server["HTTP_ACCESS_TOKEN"])){
                //$request->query["access-token"] = $request->server["HTTP_ACCESS_TOKEN"];
            }
    //var_dump($request);

            if (!$this->server->verifyResourceRequest($request, new \OAuth2_Response())) {
                var_dump($this->server->getResponse());
                $this->server->getResponse()->send();
                die;
            }
        }
        $this->next->call();
    }
} 
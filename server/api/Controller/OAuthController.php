<?php
/**
 * Created by PhpStorm.
 * User: MartinTiefengrabner
 * Date: 16/07/15
 * Time: 09:42
 */

namespace API\Controller;

require_once('APIController.php');
require_once('Middleware/SSCOauthStoragePDO.php');
require_once('Middleware/SSC_OAuth2_GrantType_UserCredentials.php');
require_once('Middleware/SSC_OAuth2_GrantType_ClientCredentials.php');

class OAuthController extends APIController
{

    protected $storage;
    protected $server;

    function __construct()
    {
        parent::__construct();

        $dsn = 'mysql:host=' . MYSQL_SERVER . ';dbname=' . MYSQL_DB . ';charset=utf8';
        $this->storage = new \API\Middleware\SSCOauthStoragePDO(array('dsn' => $dsn, 'username' => MYSQL_USER, 'password' => MYSQL_PW));
        $this->server = new \OAuth2_Server($this->storage);
        $this->server->addGrantType(new \API\Middleware\SSC_OAuth2_GrantType_UserCredentials($this->storage));
        $this->server->addGrantType(new \API\Middleware\SSC_OAuth2_GrantType_ClientCredentials($this->storage));
    }

    public function login()
    {
        $request = \OAuth2_Request::createFromGlobals();

        foreach ($request->query as $key => $value) {
            $request->request[$key] = $value;
        }


        $response = $this->server->handleTokenRequest($request, new \OAuth2_Response());
        //$this->app->user_id = 1;
        $response->send();
        die;
    }
} 
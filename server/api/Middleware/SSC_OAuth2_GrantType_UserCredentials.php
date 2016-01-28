<?php
/**
 * Created by PhpStorm.
 * User: MartinTiefengrabner
 * Date: 16/07/15
 * Time: 09:03
 */

namespace API\Middleware;

require_once('vendor/bshaffer/oauth2-server-php/src/OAuth2/Autoloader.php');
\OAuth2_Autoloader::register();

class SSC_OAuth2_GrantType_UserCredentials implements \OAuth2_GrantTypeInterface
{
    private $storage;
    private $userInfo;

    public function __construct(\OAuth2_Storage_UserCredentialsInterface $storage)
    {
        $this->storage = $storage;
    }

    public function getQuerystringIdentifier()
    {
        return 'password';
    }

    public function validateRequest(\OAuth2_RequestInterface $request, \OAuth2_ResponseInterface $response)
    {
        if (!$request->request("password") || !$request->request("email")) {
            $response->setError(400, 'invalid_request', 'Missing parameters: "email" and "password" required');
            return null;
        }

        if (!$this->storage->checkUserCredentials($request->request("email"), $request->request("password"))) {
            $response->setError(400, 'invalid_grant', 'Invalid email and password combination');
            return null;
        }

        $userInfo = $this->storage->getUserDetails($request->request("email"));

        if (empty($userInfo)) {
            $response->setError(400, 'invalid_grant', 'Unable to retrieve user information');
            return null;
        }

        if (!isset($userInfo['id'])) {
            throw new \LogicException("you must set the user_id on the array returned by getUserDetails");
        }

        $this->userInfo = $userInfo;

        return true;
    }

    public function getClientId()
    {
        return null;
    }

    public function getUserId()
    {
        return $this->userInfo['id'];
    }

    public function getScope()
    {
        return isset($this->userInfo['scope']) ? $this->userInfo['scope'] : null;
    }

    public function createAccessToken(\OAuth2_ResponseType_AccessTokenInterface $accessToken, $client_id, $user_id, $scope)
    {

        return $accessToken->createAccessToken($client_id, $user_id, $scope);
    }
}

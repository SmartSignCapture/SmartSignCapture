<?php
/**
 * Created by PhpStorm.
 * User: MartinTiefengrabner
 * Date: 17/07/15
 * Time: 08:28
 */

namespace API\Middleware;


class SSC_OAuth2_GrantType_ClientCredentials extends \OAuth2_ClientAssertionType_HttpBasic implements \OAuth2_GrantTypeInterface
{
    public function getQuerystringIdentifier()
    {
        return 'client_credentials';
    }

    public function getScope()
    {
        return null;
    }

    public function getUserId()
    {
        return null;
    }

    public function createAccessToken(\OAuth2_ResponseType_AccessTokenInterface $accessToken, $client_id, $user_id, $scope)
    {
        /*
         * Client Credentials Grant does NOT include a refresh token
         * @see http://tools.ietf.org/html/rfc6749#section-4.4.3
         */

        $includeRefreshToken = false;
        return $accessToken->createAccessToken($client_id, $user_id, $scope, $includeRefreshToken);
    }
}

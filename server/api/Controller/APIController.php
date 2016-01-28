<?php
/**
 * Created by PhpStorm.
 * User: MartinTiefengrabner
 * Date: 16/07/15
 * Time: 09:43
 */

namespace API\Controller;

require 'vendor/autoload.php';
require_once('Connector/MySQLConnector.php');

abstract class ErrorRespond
{
    const INVALID_DATA = "DATA INVALID";
}

abstract class HTTPResponse
{
    const OK = 200;
    const BAD_REQUEST = 400;
    const NOT_FOUND = 404;
    const SERVER_ERROR = 500;
}

class APIController {
    protected $dbConnector;

    function __construct()
    {
        $this->app = \Slim\Slim::getInstance();

        $this->dbConnector = new \Connector\MySQLConnector();
        $this->prepareStatements();
    }

    protected function prepareStatements()
    {

    }
} 
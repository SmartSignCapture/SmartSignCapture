<?php
/**
 * Created by PhpStorm.
 * User: MartinTiefengrabner
 * Date: 13/07/15
 * Time: 16:42
 */

namespace Connector;

class MySQLConnector
{
    public $dbHandler;

    public function __construct()
    {
        try {
            $dsn = 'mysql:host=' . MYSQL_SERVER . ';dbname=' . MYSQL_DB . ';charset=utf8';
            $this->dbHandler = new \PDO($dsn, MYSQL_USER, MYSQL_PW);
        } catch (PDOException $e) {
            print "Error!: " . $e->getMessage() . "<br/>";
            die();
        }
    }

    public function prepareStatement($preparedStatement){
        return $this->dbHandler->prepare($preparedStatement);
    }

    public function lastInsertID(){
        return $this->dbHandler->lastInsertId();
    }
} 
<?php
/**
 * Created by PhpStorm.
 * User: MartinTiefengrabner
 * Date: 13/07/15
 * Time: 16:15
 */

namespace API\Controller;

require_once("APIDataController.php");

class UserController extends APIDataController
{
    //{"email": "test@test.com", "nickName":"nickName", "firstName":"John", "lastName":"Doe","password":"123pass456"}

    const PREPARED_STATEMENT_INSERT = <<<STMNT
                                    INSERT INTO `User`(`email`, `nickName`, `firstName`, `lastName`, `password`)
                                    VALUES (:email, :nickName, :firstName, :lastName, :password)
STMNT;

    const PREPARED_STATEMENT_SELECT_BY_ID_STUMP = <<<STMNT
                                    SELECT %s
                                    FROM `User`
                                    WHERE `id` = :id
STMNT;

    const PREPARED_STATEMENT_FILTER_STUMP = <<<STMNT
                                    SELECT %s
                                    FROM `User`
                                    WHERE %s
STMNT;

    const PREPARED_STATEMENT_SEARCH_STUMP = <<<STMNT
                                    SELECT %s
                                    FROM `User`
                                    WHERE `firstName` LIKE :searchTerm OR `lastName` LIKE :searchTerm OR `nickName` LIKE :searchTerm
STMNT;


    const KEY_EMAIL = "email";
    const KEY_NICK_NAME = "user_name";
    const KEY_FIRST_NAME = "first_name";
    const KEY_LAST_NAME = "last_name";
    const KEY_PASSWORD = "password";
    const KEY_IMAGE = "image_url";
    const KEY_ID = "id";

    const TABLE_KEY_EMAIL = "email";
    const TABLE_KEY_NICK_NAME = "nickName";
    const TABLE_KEY_FIRST_NAME = "firstName";
    const TABLE_KEY_LAST_NAME = "lastName";
    const TABLE_KEY_PASSWORD = "password";
    const TABLE_KEY_IMAGE = "image";
    const TABLE_KEY_ID = "id";

    private $preparedStatementInsert;

    function __construct()
    {
        parent::__construct();

        $this->objectName = "users";

        $this->postDataMandatoryJSONKeys = array(
            self::KEY_EMAIL,
            self::KEY_NICK_NAME,
            self::KEY_FIRST_NAME,
            self::KEY_LAST_NAME,
            self::KEY_PASSWORD
        );

        $this->filterKeysMappedToTableColumns = array(
            self::KEY_EMAIL => self::TABLE_KEY_EMAIL,
            self::KEY_NICK_NAME => self::TABLE_KEY_NICK_NAME,
            self::KEY_FIRST_NAME => self::TABLE_KEY_FIRST_NAME,
            self::KEY_LAST_NAME => self::TABLE_KEY_LAST_NAME
        );

        $this->fieldKeysMappedToTableColumns = array(
            self::KEY_EMAIL => self::TABLE_KEY_EMAIL,
            self::KEY_NICK_NAME => self::TABLE_KEY_NICK_NAME,
            self::KEY_FIRST_NAME => self::TABLE_KEY_FIRST_NAME,
            self::KEY_LAST_NAME => self::TABLE_KEY_LAST_NAME,
            self::KEY_ID => self::TABLE_KEY_ID,
            self::KEY_IMAGE => self::TABLE_KEY_IMAGE
        );
    }

    public static function hashPassword($password){
        $salt = strtr(base64_encode(mcrypt_create_iv(16, MCRYPT_DEV_URANDOM)), '+', '.');
        return crypt($password, $salt);
    }


    public function post()
    {
        parent::post();

        $error = "";

        if (!$this->isDataValid($this->postDataMandatoryJSONKeys, $error)) {
            $this->setBadRequestResponse(ErrorRespond::INVALID_DATA, $error);
        } else {

            $hashedPassword = self::hashPassword($this->data[self::KEY_PASSWORD]);

            $this->preparedStatementInsert->bindParam(':email', $this->data[self::KEY_EMAIL]);
            $this->preparedStatementInsert->bindParam(':nickName', $this->data[self::KEY_NICK_NAME]);
            $this->preparedStatementInsert->bindParam(':firstName', $this->data[self::KEY_FIRST_NAME]);
            $this->preparedStatementInsert->bindParam(':lastName', $this->data[self::KEY_LAST_NAME]);
            $this->preparedStatementInsert->bindParam(':password', $hashedPassword);

            if ($this->preparedStatementInsert->execute()) {
                $error = false;
                $user = $this->getUser($this->dbConnector->lastInsertID(), $error);

                $this->setSuccessfulPostObjectResponse($user);

            } else {
                $errorArray = $this->preparedStatementInsert->errorInfo();
                $this->setServerErrorResponse($errorArray[2]);
            }
        }
    }

    public function get($id = null)
    {
        parent::get();
        $statement = null;

        if ($id) {
            $error = false;
            $user = $this->getUser($id, $error);
            if(!$error){
                if($user){
                    $this->setObjectGetSuccessfulResponse($user);
                }
                else{
                    $this->setObjectNotFoundResponse();
                }
            }
            else{
                $this->setServerErrorResponse($error);
            }

        }
        else if($this->searchTerm){
            $query = sprintf(self::PREPARED_STATEMENT_SEARCH_STUMP, $this->createFieldListSQLString());

            $statement = $this->dbConnector->prepareStatement($query);

            if ($statement->execute(array(':searchTerm' => '%'.$this->searchTerm.'%'))) {
                $this->setObjectGetSuccessfulResponse($this->fetchObjectsFromStatement($statement, $this->fields));
            } else {
                $errorArray = $this->$statement->errorInfo();
                $this->setServerErrorResponse($errorArray[2]);
            }
        }
        else {
            $query = sprintf(self::PREPARED_STATEMENT_FILTER_STUMP, $this->createFieldListSQLString(), $this->createWhereClauseFromFilters());

            $statement = $this->dbConnector->prepareStatement($query);

            if ($statement->execute()) {
                $this->setObjectGetSuccessfulResponse($this->fetchObjectsFromStatement($statement, $this->fields));
            } else {

                $errorArray = $this->$statement->errorInfo();
                $this->setServerErrorResponse($errorArray[2]);
            }
        }
    }

    protected function fetchObjectsFromStatement($statement, $fieldList)
    {
        $result = parent::fetchObjectsFromStatement($statement,$fieldList);

        foreach($result as &$entry){
            $entry[self::KEY_IMAGE] = LINK_TO_YOUR_IMAGE_FOLDER.$entry[self::KEY_IMAGE];
        }

        return $result;
    }

    public function selectUser($userID, &$error){
        parent::get();
        $result = null;

        $error = false;

        $user = $this->getUser($userID, $error);

        if(!$error){
            if($user){
                $result = $user;
            }
        }
        return $result;
    }

    private function getUser($id,  &$error){

        $result = null;

        $query = sprintf(self::PREPARED_STATEMENT_SELECT_BY_ID_STUMP, $this->createFieldListSQLString());
        $statement = $this->dbConnector->prepareStatement($query);
        $statement->bindParam(":id", $id);

        if ($statement->execute()) {
            $fetchedObjects = $this->fetchObjectsFromStatement($statement, $this->fields);

            if(count($fetchedObjects) == 1){
                $result = $fetchedObjects[0];
            }
        }else {
            $errorArray = $this->$statement->errorInfo();
            $error = $errorArray[2];
        }

        return $result;

    }


    protected function prepareStatements()
    {
        $this->preparedStatementInsert = $this->dbConnector->prepareStatement(self::PREPARED_STATEMENT_INSERT);
    }
}
<?php
/**
 * Created by PhpStorm.
 * User: MartinTiefengrabner
 * Date: 21/07/15
 * Time: 13:32
 */

namespace API\Controller;

require_once("GCMController.php");
require_once("UserController.php");

class FriendshipController extends APIDataController
{

    const PREPARED_STATEMENT_INSERT_FRIEND_REQUEST = <<<STMNT
                                                    INSERT IGNORE INTO `Friendship`(`fromUser`, `toUser`, `state`, `dateRequest`) VALUES (:userID, :friendID, 0, NOW())
STMNT;
    const PREPARED_STATEMENT_SELECT_ANY_FRIENDSHIP = <<<STMNT
                                                    SELECT `fromUser`, `toUser`, `state`, `dateRequest`, `dateAccepted`
                                                    FROM `Friendship`
                                                    WHERE (fromUser = :fromID AND toUser = :toID) OR (toUser = :fromID2 AND fromUser = :toID2)
STMNT;

    const PREPARED_STATEMENT_SELECT_ANY_FRIENDSHIPS = <<<STMNT
                                                    SELECT `fromUser`, `toUser`, `state`, `dateRequest`, `dateAccepted`
                                                    FROM `Friendship`
                                                    WHERE fromUser = :fromID OR toUser = :toID
STMNT;

    const PREPARED_STATEMENT_SELECT_FROM_FRIENDSHIP = <<<STMNT
                                                    SELECT `fromUser`, `toUser`, `state`, `dateRequest`, `dateAccepted`
                                                    FROM `Friendship`
                                                    WHERE fromUser = :friendID AND toUser = :userID
STMNT;

    const PREPARED_STATEMENT_SELECT_FRIENDSHIP = <<<STMNT
                                                    SELECT `fromUser`, `toUser`, `state`, `dateRequest`, `dateAccepted`
                                                    FROM `Friendship`
                                                    WHERE fromUser = :fromID AND toUser = :toID
STMNT;

    const PREPARED_STATEMENT_ACCEPT_FRIENDSHIP = <<<STMNT
                                                    UPDATE `Friendship`
                                                    SET `state`=1,`dateAccepted`=NOW()
                                                    WHERE `fromUser`=:friendID AND `toUser`=:userID
STMNT;


    const KEY_FROM_USER = "fromUser";
    const KEY_TO_USER = "toUser";
    const KEY_STATE = "state";
    const KEY_DATE_REQUEST = "dateRequest";
    const KEY_DATE_ACCEPTED = "dateAccepted";

    const TABLE_COLUMN_FROM_USER = "fromUser";
    const TABLE_COLUMN_TO_USER = "toUser";
    const TABLE_COLUMN_STATE = "state";
    const TABLE_COLUMN_DATE_REQUEST = "dateRequest";
    const TABLE_COLUMN_DATE_ACCEPTED = "dateAccepted";

    private $gcmController;
    private $userController;

    public function __construct()
    {
        parent::__construct();

        $this->objectName = "friendships";

        $this->fieldKeysMappedToTableColumns = array(
            self::KEY_FROM_USER => self::TABLE_COLUMN_FROM_USER,
            self::KEY_TO_USER => self::TABLE_COLUMN_TO_USER,
            self::KEY_STATE => self::TABLE_COLUMN_STATE,
            self::KEY_DATE_REQUEST => self::TABLE_COLUMN_DATE_REQUEST,
            self::KEY_DATE_ACCEPTED => self::TABLE_COLUMN_DATE_ACCEPTED
        );

        $this->gcmController = new GCMController();
        $this->userController = new UserController();
    }

    public function postFriendshipAccept($userID, $friendID)
    {
        parent::post();
        $statement = $this->dbConnector->prepareStatement(self::PREPARED_STATEMENT_ACCEPT_FRIENDSHIP);

        $statement->bindParam(":userID", $userID);
        $statement->bindParam(":friendID", $friendID);

        if ($statement->execute()) {
            $error = false;
            $friendShip = $this->getFriendship($friendID, $userID, $error, false);

            if (!$error) {

                $this->gcmController->sendGCMToUser($friendID, 'friend-request-accepted', $friendShip, $error);
                $this->setSuccessfulPostObjectResponse($friendShip);
            } else {
                $this->setServerErrorResponse($error);
            }
        } else {
            $errorArray = $statement->errorInfo();
            $this->setServerErrorResponse($errorArray[2]);
        }
    }


    public function postFriendshipRequest($userID, $friendID)
    {
        parent::post();
        $statement = $this->dbConnector->prepareStatement(self::PREPARED_STATEMENT_INSERT_FRIEND_REQUEST);

        $statement->bindParam(":userID", $userID);
        $statement->bindParam(":friendID", $friendID);

        if ($statement->execute()) {
            $error = false;
            $friendShip = $this->getFriendship($userID, $friendID, $error);

            if (!$error) {
                $this->gcmController->sendGCMToUser($friendID, 'friend-request', $friendShip, $error);
                $this->setSuccessfulPostObjectResponse($friendShip);
            } else {
                $this->setServerErrorResponse($error);
            }
        } else {
            $errorArray = $statement->errorInfo();
            $this->setServerErrorResponse($errorArray[2]);
        }
    }

    public function getFriendship($fromID, $toID, &$error, $anyDirection = true)
    {
        $result = null;
        if ($anyDirection) {
            $statement = $this->dbConnector->prepareStatement(self::PREPARED_STATEMENT_SELECT_ANY_FRIENDSHIP);
            $statement->bindParam(":fromID2", $fromID);
            $statement->bindParam(":toID2", $toID);
        } else {
            $statement = $this->dbConnector->prepareStatement(self::PREPARED_STATEMENT_SELECT_FRIENDSHIP);
        }

        $statement->bindParam(":fromID", $fromID);
        $statement->bindParam(":toID", $toID);


        if ($statement->execute()) {
            $fetchedObjects = $this->fetchObjectsFromStatement($statement, $this->fields);

            if (count($fetchedObjects) == 1) {
                $result = $fetchedObjects[0];
            }
        } else {
            $errorArray = $this->statement->errorInfo();
            $error = $errorArray[2];
        }

        return $result;
    }

    public function getFriendships($userID, &$error, $anyDirection = true)
    {
        $result = null;
        if ($anyDirection) {
            $statement = $this->dbConnector->prepareStatement(self::PREPARED_STATEMENT_SELECT_ANY_FRIENDSHIPS);
            $statement->bindParam(":fromID", $userID);
            $statement->bindParam(":toID", $userID);
        } else {
            return null;
        }

        if ($statement->execute()) {
            $result = $this->fetchObjectsFromStatement($statement, $this->fields);
        } else {
            $errorArray = $this->statement->errorInfo();
            $error = $errorArray[2];
        }

        return $result;
    }

    public function getFriend($userID, $friendID)
    {
        parent::get();
        $error = false;
        if ($this->getFriendship($userID, $friendID, $error) && !$error) {
            $this->userController->get($friendID);
        } else {
            $this->setObjectNotFoundResponse();
        }
    }

    public function getFriends($userID)
    {
        parent::get();
        $error = false;
        $friends = $this->getFriendships($userID, $error);
        if ( !$error) {
           if(count($friends) > 0){

               $user =  $this->userController->selectUser($userID, $error);

               foreach($friends as &$friend){
                   if($friend[self::KEY_FROM_USER] == $userID){
                       $friend[self::KEY_FROM_USER] = $user;
                       $friend[self::KEY_TO_USER] = $this->userController->selectUser($friend[self::KEY_TO_USER], $error);
                   }
                   else{
                       $friend[self::KEY_TO_USER] = $user;
                       $friend[self::KEY_FROM_USER] = $this->userController->selectUser($friend[self::KEY_FROM_USER], $error);
                   }
               }

                $this->setObjectGetSuccessfulResponse($friends);
           }
            else{
                $this->setObjectNotFoundResponse();
            }
        } else {
            $this->setServerErrorResponse($error);
        }
    }

}
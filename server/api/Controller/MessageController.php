<?php
/**
 * Created by PhpStorm.
 * User: MartinTiefengrabner
 * Date: 24/07/15
 * Time: 13:06
 */

namespace API\Controller;

require_once("APIDataController.php");
require_once("GCMController.php");

class MessageController extends APIDataController
{

    const PREPARED_STATEMENT_INSERT = <<<STMNT
                                    INSERT INTO `Message`(`fromUserID`, `toUserID`, `message`, `date`)
                                    VALUES (:sender, :receiver, :message, :date)
STMNT;

    const PREPARED_STATEMENT_SELECT_BY_ID_STUMP = <<<STMNT
                                    SELECT %s
                                    FROM `Message`
                                    WHERE `id` = :id
STMNT;
    const PREPARED_STATEMENT_SELECT_BY_RECEIVER_AND_ID_STUMP = <<<STMNT
                                    SELECT %s
                                    FROM `Message`
                                    WHERE `id` = :id
                                    AND toUserID = :receiver
STMNT;
    const PREPARED_STATEMENT_SELECT_BY_SENDER_AND_ID_STUMP = <<<STMNT
                                    SELECT %s
                                    FROM `Message`
                                    WHERE `id` = :id
                                    AND fromUserID = :sender
STMNT;
    const PREPARED_STATEMENT_SELECT_BY_RECEIVER_STUMP = <<<STMNT
                                    SELECT %s
                                    FROM `Message`
                                    WHERE toUserID = :receiver
STMNT;
    const PREPARED_STATEMENT_SELECT_BY_SENDER_STUMP = <<<STMNT
                                    SELECT %s
                                    FROM `Message`
                                    WHERE fromUserID = :sender
STMNT;
    const PREPARED_STATEMENT_SELECT_BY_ANY_STUMP = <<<STMNT
                                    SELECT %s
                                    FROM `Message`
                                    WHERE fromUserID = :sender OR toUserID = :sender
STMNT;
    const PREPARED_STATEMENT_SELECT_BY_USER_AND_ID = <<<STMNT
                                    SELECT %s
                                    FROM `Message`
                                    WHERE (fromUserID = :fromUserID OR toUserID = :toUserID) AND (id = :messageID)
STMNT;

    const TABLE_KEY_ID = "id";
    const TABLE_KEY_FROM = "fromUserID";
    const TABLE_KEY_TO = "toUserID";
    const TABLE_KEY_MESSAGE = "message";
    const TABLE_KEY_DATE = "date";

    const KEY_ID = "id";
    const KEY_FROM = "sender";
    const KEY_TO = "receiver";
    const KEY_MESSAGE = "message";
    const KEY_DATE = "date";

    private $gcmController;

    function __construct()
    {
        parent::__construct();

        $this->objectName = "users";

        $this->postDataMandatoryJSONKeys = array(
            self::KEY_MESSAGE,
            self::KEY_DATE
        );

        $this->filterKeysMappedToTableColumns = array(
            self::KEY_ID => self::TABLE_KEY_ID,
            self::KEY_FROM => self::TABLE_KEY_FROM,
            self::KEY_TO => self::TABLE_KEY_TO,
            self::KEY_MESSAGE => self::TABLE_KEY_MESSAGE,
            self::KEY_DATE => self::TABLE_KEY_DATE
        );

        $this->fieldKeysMappedToTableColumns = array(
            self::KEY_ID => self::TABLE_KEY_ID,
            self::KEY_FROM => self::TABLE_KEY_FROM,
            self::KEY_TO => self::TABLE_KEY_TO,
            self::KEY_MESSAGE => self::TABLE_KEY_MESSAGE,
            self::KEY_DATE => self::TABLE_KEY_DATE
        );

        $this->gcmController = new GCMController();
    }

    public function post($senderID, $receiverID)
    {
        parent::post();

        if (!$this->isDataValid($this->postDataMandatoryJSONKeys, $error)) {
            $this->setBadRequestResponse(ErrorRespond::INVALID_DATA, $error);
        } else {
            $statement = $this->dbConnector->prepareStatement(self::PREPARED_STATEMENT_INSERT);

            if ($statement->execute(array(":sender" => $senderID, ":receiver" => $receiverID, ":message" => stripslashes($this->data[self::KEY_MESSAGE]), ":date" => $this->data[self::KEY_DATE]))) {
                $error = false;
                $message = $this->selectMessage($this->dbConnector->lastInsertID(), $error);

                if (!$error) {
                    $msg = $message;
                    unset($msg[self::KEY_MESSAGE]);
                    $this->gcmController->sendGCMToUser($receiverID,"new-message", $msg, $error);

                    $this->setSuccessfulPostObjectResponse($message);
                } else {
                    $this->setServerErrorResponse($error);
                }
            } else {
                $errorArray = $this->$statement->errorInfo();
                $this->setServerErrorResponse($errorArray[2]);
            }
        }
    }

    public function getReceivedMessages($receiverID, $messageID = false)
    {
        parent::get();

        $error = false;
        if ($messageID) {
            $message = $this->selectMessage($messageID, $error, $receiverID);
            if (!$error) {
                if ($message) {
                    $this->setObjectGetSuccessfulResponse($message);
                } else {
                    $this->setObjectNotFoundResponse();
                }
            } else {
                $this->setServerErrorResponse($error);
            }
        } else {
            $query = sprintf(self::PREPARED_STATEMENT_SELECT_BY_RECEIVER_STUMP, $this->createFieldListSQLString());
            $statement = $this->dbConnector->prepareStatement($query);

            if ($statement->execute(array(":receiver" => $receiverID))) {
                $this->setObjectGetSuccessfulResponse($this->fetchObjectsFromStatement($statement, $this->fields));
            } else {
                $errorArray = $this->$statement->errorInfo();
                $this->setServerErrorResponse($errorArray[2]);
            }
        }

    }

    public function getSentMessages($senderID, $messageID = false)
    {
        parent::get();

        $error = false;
        if ($messageID) {
            $message = $this->selectMessage($messageID, $error, false, $senderID);
            if (!$error) {
                if ($message) {
                    $this->setObjectGetSuccessfulResponse($message);
                } else {
                    $this->setObjectNotFoundResponse();
                }
            } else {
                $this->setServerErrorResponse($error);
            }
        } else {


            $query = sprintf(self::PREPARED_STATEMENT_SELECT_BY_SENDER_STUMP, $this->createFieldListSQLString());
            $statement = $this->dbConnector->prepareStatement($query);

            if ($statement->execute(array(":sender" => $senderID))) {
                $this->setObjectGetSuccessfulResponse($this->fetchObjectsFromStatement($statement, $this->fields));
            } else {
                $errorArray = $this->$statement->errorInfo();
                $this->setServerErrorResponse($errorArray[2]);
            }
        }

    }

    public function getMessages($userID, $messageID = false){
        parent::get();

        $error = false;
        if ($messageID) {


            $query = sprintf(self::PREPARED_STATEMENT_SELECT_BY_USER_AND_ID, $this->createFieldListSQLString());
            $statement = $this->dbConnector->prepareStatement($query);

            if ($statement->execute(array(":fromUserID" => $userID,":toUserID" => $userID, ":messageID" =>$messageID))) {
                $result = $this->fetchObjectsFromStatement($statement, $this->fields);

                if(count($result) == 1){
                    $this->setObjectGetSuccessfulResponse($result[0]);
                }
                else{
                    $this->setObjectNotFoundResponse();
                }
            } else {
                $errorArray = $this->$statement->errorInfo();
                $this->setServerErrorResponse($errorArray[2]);
            }

        } else {
            $query = sprintf(self::PREPARED_STATEMENT_SELECT_BY_ANY_STUMP, $this->createFieldListSQLString());
            $statement = $this->dbConnector->prepareStatement($query);

            if ($statement->execute(array(":sender" => $userID))) {
                $this->setObjectGetSuccessfulResponse($this->fetchObjectsFromStatement($statement, $this->fields));
            } else {
                $errorArray = $this->$statement->errorInfo();
                $this->setServerErrorResponse($errorArray[2]);
            }
        }
    }

    private function selectMessage($id, &$error, $receiverID = false, $senderID = false)
    {
        $result = null;

        if (!$receiverID && !$senderID) {
            $query = sprintf(self::PREPARED_STATEMENT_SELECT_BY_ID_STUMP, $this->createFieldListSQLString());
            $statement = $this->dbConnector->prepareStatement($query);
            $statement->bindParam(":id", $id);
        } else if ($receiverID && !$senderID) {
            $query = sprintf(self::PREPARED_STATEMENT_SELECT_BY_RECEIVER_AND_ID_STUMP, $this->createFieldListSQLString());
            $statement = $this->dbConnector->prepareStatement($query);
            $statement->bindParam(":id", $id);
            $statement->bindParam(":receiver", $receiverID);
        }
        else if (!$receiverID && $senderID) {
            $query = sprintf(self::PREPARED_STATEMENT_SELECT_BY_SENDER_AND_ID_STUMP, $this->createFieldListSQLString());
            $statement = $this->dbConnector->prepareStatement($query);
            $statement->bindParam(":id", $id);
            $statement->bindParam(":sender", $senderID);
        }

        if ($statement->execute()) {
            $fetchedObjects = $this->fetchObjectsFromStatement($statement, $this->fields);
            if (count($fetchedObjects) == 1) {
                $result = $fetchedObjects[0];
            }
        } else {
            $errorArray = $this->$statement->errorInfo();
            $error = $errorArray[2];
        }

        return $result;
    }
} 
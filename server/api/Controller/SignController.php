<?php
/**
 * Created by PhpStorm.
 * User: MartinTiefengrabner
 * Date: 19/08/15
 * Time: 11:05
 */

namespace API\Controller;

require_once("APIDataController.php");

class SignController extends APIDataController
{
    const TABLE_KEY_ID = "id";
    const TABLE_KEY_DATE = "date";
    const TABLE_KEY_USER_ID = "userID";
    const TABLE_KEY_NAME = "name";
    const TABLE_KEY_SIGN = "sign";
    const TABLE_KEY_TAG = "tag";
    const TABLE_KEY_TAG_ID = "tagID";
    const TABLE_KEY_SIGN_ID = "signID";

    const KEY_SIGN = "sign";
    const KEY_ID = "id";
    const KEY_NAME = "name";
    const KEY_DATE = "date";
    const KEY_AUTHOR = "author";
    const KEY_TAGS = "tags";
    const KEY_TAG = "tag";

    const PREPARED_STATEMENT_INSERT_SIGN = <<<STMNT
                                    INSERT INTO `Sign`(`userID`, `name`, `date`, `sign`)
                                    VALUES (:userID,:name,:date,:sign)
STMNT;
    const PREPARED_STATEMENT_INSERT_TAG = <<<STMNT
                                    INSERT INTO `Tag`(tag)
                                    VALUES (:tag)
STMNT;

    const PREPARED_STATEMENT_SELECT_TAG_BY_TAG = <<<STMNT
                                    SELECT `id`, `tag`
                                    FROM `Tag`
                                    WHERE LOWER(`tag`) = LOWER(:tag)
STMNT;

    const PREPARED_STATEMENT_INSERT_SIGN_HAS_TAG = <<<STMNT
                                    INSERT INTO `SignHasTag`(`signID`, `tagID`)
                                    VALUES (:signID, :tagID)
STMNT;

    const PREPARED_STATEMENT_SELECT_ALL_SIGNS_STUMP = <<<STMNT
                                    SELECT %s
                                    FROM `Sign`
STMNT;

    const PREPARED_STATEMENT_SELECT_BY_ID_STUMP = <<<STMNT
                                    SELECT %s
                                    FROM `Sign`
                                    WHERE `id` = :id
STMNT;

    const PREPARED_STATEMENT_SELECT_SIGN_WITH_TAG = <<<STMNT
                                    SELECT %s
                                    FROM `Sign` s
                                    INNER JOIN `SignHasTag`st ON st.signID = s.ID
                                    WHERE st.`tagID` = :id
STMNT;

    const PREPARED_STATEMENT_SELECT_SIGNS_OF_USER = <<<STMNT
                                    SELECT %s
                                    FROM `Sign` s
                                    WHERE s.`userID` = :id
STMNT;

    const PREPARED_STATEMENT_SELECT_TAGS_OF_SIGN = <<<STMNT
                                    SELECT t.* FROM `Tag` t
                                    INNER JOIN `SignHasTag`st ON st.tagID = t.ID
                                    WHERE st.signID = :signID
STMNT;

    const PREPARED_STATEMENT_SEARCH_STUMP = <<<STMNT
                                    SELECT %s
                                    FROM `Sign` s
                                    INNER JOIN `SignHasTag`st ON st.signID = s.ID
                                    INNER JOIN Tag t ON t.id = st.tagID
                                    WHERE LOWER(`s`.`name`) LIKE LOWER(:searchTerm) OR LOWER(`t`.`tag`) LIKE LOWER(:searchTerm)
                                    ORDER BY s.name
STMNT;

    const PREPARED_STATEMENT_SEARCH_SELECT_STUMP = <<<STMNT
                                    SELECT DISTINCT %s
                                    FROM `Sign` s
                                    INNER JOIN `SignHasTag`st ON st.signID = s.ID
                                    INNER JOIN Tag t ON t.id = st.tagID
                                    :where
                                    ORDER BY s.name
STMNT;
    const PREPARED_STATEMENT_SEARCH_WHERE_CLAUSE = <<<STMNT
                                    LOWER(`s`.`name`) LIKE CONCAT("%%",LOWER(:searchTerm),"%%") OR LOWER(`t`.`tag`) LIKE CONCAT("%%",LOWER(:searchTerm),"%%")
STMNT;

    function __construct()
    {
        parent::__construct();

        $this->objectName = "users";

        $this->postDataMandatoryJSONKeys = array(
            self::KEY_SIGN,
            self::KEY_NAME,
            self::KEY_DATE,
            self::KEY_AUTHOR,
            self::KEY_TAGS
        );

        $this->filterKeysMappedToTableColumns = array();

        $this->fieldKeysMappedToTableColumns = array(
            self::KEY_ID => self::TABLE_KEY_ID,
            self::KEY_SIGN => self::TABLE_KEY_SIGN,
            self::KEY_NAME => self::TABLE_KEY_NAME,
            self::KEY_DATE => self::TABLE_KEY_DATE,
            self::KEY_AUTHOR => self::TABLE_KEY_USER_ID,
            self::KEY_TAGS => self::KEY_TAGS
        );
    }

    public function post($userID)
    {
        parent::post();

        if (!$this->isDataValid($this->postDataMandatoryJSONKeys, $error)) {
            $this->setBadRequestResponse(ErrorRespond::INVALID_DATA, $error);
        } else {
            $statement = $this->dbConnector->prepareStatement(self::PREPARED_STATEMENT_INSERT_SIGN);

            if ($statement->execute(
                array(
                    ":userID" => $userID,
                    ":name" => $this->data[self::KEY_NAME],
                    ":date" => $this->data[self::KEY_DATE],
                    ":sign" => stripslashes($this->data[self::KEY_SIGN])
                ))
            ) {
                $error = false;
                $signID = $this->dbConnector->lastInsertID();

                foreach ($this->data[self::KEY_TAGS] as $tag) {
                    $tagID = $this->insertOrSelectTag($tag[self::KEY_TAG], $error);

                    if (!$error) {
                        $this->insertSignHasTag($signID, $tagID, $error);

                        if ($error) {
                            $this->setServerErrorResponse($error);
                        }
                    } else {
                        $this->setServerErrorResponse($error);
                    }
                }

                $sign = $this->selectSign($signID, $error);
                if (!$error) {
                    $this->setSuccessfulPostObjectResponse($sign);
                } else {
                    $this->setServerErrorResponse($error);
                }

            } else {
                $errorArray = $this->$statement->errorInfo();
                $this->setServerErrorResponse($errorArray[2]);
            }
        }
    }

    public function get($signID = false)
    {
        parent::get();

        $error = false;
        if ($signID) {

            $sign = $this->selectSign($signID, $error);

            if (!$error) {
                if ($sign) {
                    $this->setObjectGetSuccessfulResponse($sign);
                } else {
                    $this->setObjectNotFoundResponse();
                }
            }
        } else {
            if ($this->searchTerm) {

                $statement = $this->createSearchStatement(self::PREPARED_STATEMENT_SEARCH_SELECT_STUMP, self::PREPARED_STATEMENT_SEARCH_WHERE_CLAUSE);

            } else {
                $query = sprintf(self::PREPARED_STATEMENT_SELECT_ALL_SIGNS_STUMP, $this->createFieldListSQLString());

                $statement = $this->dbConnector->prepareStatement($query);
            }

            if ($statement->execute()) {
                $signs = $this->fetchSigns($statement, $error);
                if (!$error) {
                    if (count($signs) > 0) {
                        $this->setObjectGetSuccessfulResponse($signs);
                    } else {
                        $this->setObjectNotFoundResponse();
                    }
                }

            } else {
                $errorArray = $statement->errorInfo();
                $error = $errorArray[2];
            }
        }

        if ($error) {
            $this->setServerErrorResponse($error);
        }
    }

    protected function createSearchStatement($queryStump, $whereClause, $searchTerms = false, $whereClausePlaceHolder = ":where", $searchTermPlaceholder = ":searchTerm")
    {
        if (!$searchTerms) {
            $searchTerms = str_getcsv($this->searchTerm, ' ');
        }

        $whereClauses = array();


        for ($i = 0; $i < count($searchTerms); $i++) {
            $specificClause = '(' . str_replace($searchTermPlaceholder, $searchTermPlaceholder . $i, $whereClause) . ')';
            $whereClauses[] = $specificClause;
        }

        $whereStump = 'WHERE ' . implode(" OR ", $whereClauses);

        $query = str_replace($whereClausePlaceHolder, $whereStump, $queryStump);
        $query = sprintf($query, $this->createFieldListSQLString("s"));

        $statement = $this->dbConnector->prepareStatement($query);

        for ($i = 0; $i < count($searchTerms); $i++) {
            $statement->bindParam($searchTermPlaceholder . $i, $searchTerms[$i]);
        }

        return $statement;
    }

    public function getByTag($tagID)
    {
        parent::get();

        $error = false;

        $query = sprintf(self::PREPARED_STATEMENT_SELECT_SIGN_WITH_TAG, $this->createFieldListSQLString("s"));

        $statement = $this->dbConnector->prepareStatement($query);
        $statement->bindParam(":id", $tagID);

        if ($statement->execute()) {
            $signs = $this->fetchSigns($statement, $error);
            if (!$error) {
                if (count($signs) > 0) {
                    $this->setObjectGetSuccessfulResponse($signs);
                } else {
                    $this->setObjectNotFoundResponse();
                }
            }
        } else {
            $errorArray = $statement->errorInfo();
            $error = $errorArray[2];
        }

        if ($error) {
            $this->setServerErrorResponse($error);
        }
    }

    public function getByUser($userID)
    {
        parent::get();

        $error = false;

        $query = sprintf(self::PREPARED_STATEMENT_SELECT_SIGNS_OF_USER, $this->createFieldListSQLString("s"));

        $statement = $this->dbConnector->prepareStatement($query);
        $statement->bindParam(":id", $userID);

        if ($statement->execute()) {
            $signs = $this->fetchSigns($statement, $error);
            if (!$error) {
                if (count($signs) > 0) {
                    $this->setObjectGetSuccessfulResponse($signs);
                } else {
                    $this->setObjectNotFoundResponse();
                }
            }
        } else {
            $errorArray = $statement->errorInfo();
            $error = $errorArray[2];
        }

        if ($error) {
            $this->setServerErrorResponse($error);
        }
    }

    protected function createFieldListSQLString($tableAlias = false)
    {
        $tableColumns = array();

        foreach ($this->fields as $field) {
            if ($field != self::KEY_TAGS) {
                if (!$tableAlias) {
                    $tableColumns[] = '`' . $this->fieldKeysMappedToTableColumns[$field] . '`';
                } else {
                    $tableColumns[] = '`' . $tableAlias . '`' . '.' . '`' . $this->fieldKeysMappedToTableColumns[$field] . '`';
                }
            }
        }
        return join(",", $tableColumns);
    }

    protected function fetchObjectsFromStatement($statement, $fieldList)
    {
        $result = array();
        while ($row = $statement->fetch()) {

            $entry = array();

            foreach ($fieldList AS $field) {
                if (isset($this->fieldKeysMappedToTableColumns[$field])) {
                    $entry[$field] = $row[$this->fieldKeysMappedToTableColumns[$field]];
                }
            }

            $result[] = $entry;
        }

        return $result;
    }

    private function selectSign($id, &$error)
    {
        $result = null;

        $tableColumns = $this->createFieldListSQLString();

        $query = sprintf(self::PREPARED_STATEMENT_SELECT_BY_ID_STUMP, $tableColumns);
        $statement = $this->dbConnector->prepareStatement($query);
        $statement->bindParam(":id", $id);


        if ($statement->execute()) {
            $signs = $this->fetchSigns($statement, $error);

            if (count($signs) == 1) {
                $result = $signs[0];
            }

        } else {
            $errorArray = $this->$statement->errorInfo();
            $error = $errorArray[2];
        }

        return $result;
    }

    private function fetchSigns($statement, &$error)
    {

        $result = array();

        $fields = $this->fields;

        $tagsFieldIdx = array_search(self::KEY_TAGS, $fields);
        if ($tagsFieldIdx != -1) {
            unset($fields[$tagsFieldIdx]);
        }

        $result = $this->fetchObjectsFromStatement($statement, $fields);

        foreach ($result as &$entry) {
            if ($tagsFieldIdx != -1) {
                $entry[self::KEY_TAGS] = $this->selectTagsOfSign($entry[self::KEY_ID], $error);
            }

        }

        return $result;
    }

    private function selectTagsOfSign($id, &$error)
    {
        $result = array();


        $statement = $this->dbConnector->prepareStatement(self::PREPARED_STATEMENT_SELECT_TAGS_OF_SIGN);
        $statement->bindParam(":signID", $id);

        if ($statement->execute()) {
            $result = $this->fetchTags($statement);
        } else {
            $errorArray = $this->$statement->errorInfo();
            $error = $errorArray[2];
        }

        return $result;
    }

    private function fetchTags($statement)
    {
        $result = array();

        while ($row = $statement->fetch()) {

            $entry = array();
            $entry[self::KEY_TAG] = $row[self::TABLE_KEY_TAG];
            $entry[self::KEY_ID] = $row[self::TABLE_KEY_ID];

            $result[] = $entry;
        }

        return $result;
    }

    private function insertOrSelectTag($tag, &$error)
    {
        $tagID = -1;

        $statement = $this->dbConnector->prepareStatement(self::PREPARED_STATEMENT_SELECT_TAG_BY_TAG);

        if ($statement->execute(
            array(
                ":tag" => $tag
            ))
        ) {
            if ($statement->rowCount() == 1) {
                $tagID = $statement->fetchColumn(0);
            } else {
                $tagID = $this->insertTag($tag, $error);
            }
        } else {
            $errorArray = $this->$statement->errorInfo();
            $error = $errorArray[2];
        }


        return $tagID;
    }

    private function insertTag($tag, &$error)
    {
        $tagID = -1;


        $statement = $this->dbConnector->prepareStatement(self::PREPARED_STATEMENT_INSERT_TAG);

        if ($statement->execute(
            array(
                ":tag" => $tag
            ))
        ) {
            $tagID = $this->dbConnector->lastInsertID();
        } else {
            $errorArray = $this->$statement->errorInfo();
            $error = $errorArray[2];
        }


        return $tagID;
    }

    private function insertSignHasTag($signID, $tagID, &$error)
    {

        $result = false;

        $statement = $this->dbConnector->prepareStatement(self::PREPARED_STATEMENT_INSERT_SIGN_HAS_TAG);

        if ($statement->execute(
            array(
                ":signID" => $signID,
                ":tagID" => $tagID
            ))
        ) {
            $result = $statement->rowCount() == 1;
        } else {
            $errorArray = $statement->errorInfo();
            $error = $errorArray[2];
        }

        return $result;
    }
} 
<?php
/**
 * Created by PhpStorm.
 * User: MartinTiefengrabner
 * Date: 13/07/15
 * Time: 16:12
 */

namespace API\Controller;


require_once("APIController.php");


class APIDataController extends APIController
{

    protected $objectName;

    protected $app;
    protected $data;
    protected $fields;
    protected $filters;
    protected $searchTerm;


    protected $postDataMandatoryJSONKeys = array();
    protected $filterKeysMappedToTableColumns = array();
    protected $fieldKeysMappedToTableColumns = array();

    function __construct()
    {
        parent::__construct();
    }

    //region Public

    public function get()
    {
        $this->parseFilters();
        $this->parseFieldList();
        $this->parseSearchTerms();
    }

    public function put()
    {


    }

    public function post($id = null)
    {
        $this->parseFieldList();
        $this->parseBody();
    }


    public function delete()
    {

    }

    //endregion

    //region Protected


    protected function isDataValid($mandatoryDataKeys, &$msg)
    {
        $result = true;
        $msg = "";
        foreach ($mandatoryDataKeys as $dataKey) {
            if (!isset($this->data[$dataKey])) {
                $result = false;
                $msg = $msg . $dataKey . " is missing; ";
            }
        }
        return $result;
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

    //region GET

    protected function createFieldListSQLString($tableAlias =false)
    {
        $tableColumns = array();

        foreach ($this->fields as $field) {
            if (!$tableAlias) {
                $tableColumns[] = '`' . $this->fieldKeysMappedToTableColumns[$field] . '`';
            } else {
                $tableColumns[] = '`' .$tableAlias .'`' . '.' .'`' .  $this->fieldKeysMappedToTableColumns[$field] . '`';
            }
        }
        return join(",", $tableColumns);
    }

    protected function createWhereClauseFromFilters()
    {
        $result = "1;";

        $clauses = array();

        foreach ($this->filters as $column => $value) {
            $clauses[] = '(`' . $column . '` = ' . '"' . $value . '")';
        }

        if (count($clauses)) {
            $result = join(" AND ", $clauses);
        }

        return $result;
    }

    //endregion

    //region Responses
    protected function setOKResponse($data = null)
    {
        $this->app->response->setStatus(HTTPResponse::OK);
        $this->app->response->headers->set('Content-Type', 'application/json');
        if ($data) {
            $this->app->response->body(json_encode($data));
        }
    }

    protected function setSuccessfulPostObjectResponse($object)
    {
        $this->setOKResponse($object);
    }

    protected function setObjectNotFoundResponse()
    {
        $this->app->response->setStatus(HTTPResponse::NOT_FOUND);
        $this->app->response->headers->set('Content-Type', 'application/json');
    }

    protected function setObjectGetSuccessfulResponse($data)
    {
        $body = array();
        $body = $data;

        $this->setOKResponse($body);
    }

    protected function setBadRequestResponse($error, $detail)
    {

        $body = array();
        $body["error"] = $error;
        $body["description"] = $detail;


        $this->app->response->setStatus(HTTPResponse::BAD_REQUEST);
        $this->app->response->headers->set('Content-Type', 'application/json');
        $this->app->response->body(json_encode($body));

    }

    protected function setServerErrorResponse($error)
    {

        $body = array();
        $body["error"] = $error;

        $this->app->response->setStatus(HTTPResponse::SERVER_ERROR);
        $this->app->response->headers->set('Content-Type', 'application/json');
        $this->app->response->body(json_encode($body));

    }

    //endregion

    //endregion

    //region Private

    //region GET

    private function parseFieldList()
    {

        $this->fields = array();
        $tmp = $this->app->request->params('fields');

        if ($tmp) {
            $tmp = explode(',', $tmp);
            foreach ($tmp as $field) {
                if (isset($this->fieldKeysMappedToTableColumns[$field])) {
                    $this->fields[] = $field;
                }
            }
        }

        if (count($this->fields) == 0) {
            $this->fields = array_keys($this->fieldKeysMappedToTableColumns);
        }
    }

    private function parseSearchTerms(){
        $tmp = $this->app->request->params('q');

        if($tmp){
            $this->searchTerm = $tmp;
        }
    }

    private function parseBody()
    {
        $this->data = json_decode($this->app->request->getBody(), true);
    }

    private function parseFilters()
    {
        $this->filters = array();

        foreach ($this->filterKeysMappedToTableColumns as $dataKey => $columnKey) {
            $value = $this->app->request()->params($dataKey);
            if ($value) {
                $this->filters[$columnKey] = $value;
            }
        }
    }

    //endregion

    //endregion
} 
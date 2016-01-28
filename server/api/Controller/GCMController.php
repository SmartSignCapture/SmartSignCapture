<?php
/**
 * Created by PhpStorm.
 * User: MartinTiefengrabner
 * Date: 21/07/15
 * Time: 11:07
 */

namespace API\Controller;

require_once('APIDataController.php');

class GCMController extends APIDataController {

    const PREPARED_STATEMENT_INSERT_GCM_TOKEN = <<<STMTN
                                                INSERT IGNORE INTO `GCMToken`(`userID`, `token`) VALUES (:userID, :token)
STMTN;

    const PREPARED_STATEMENT_TOKENS_OF_USER= <<<STMTN
                                                SELECT `token`
                                                FROM `GCMToken`
                                                WHERE `userID`= :userID
STMTN;


    function __construct(){
        parent::__construct();
    }

    public function putGCMToken($userID, $token){
        $statement = $this->dbConnector->prepareStatement(self::PREPARED_STATEMENT_INSERT_GCM_TOKEN);

        $statement->bindParam(':userID', $userID);
        $statement->bindParam(':token', $token);

        if($statement->execute()){
            $result = array();
            $result['userID'] = $userID;
            $result['token'] = $token;

            $this->setSuccessfulPostObjectResponse($result);
        }
        else{
            $errorArray = $statement->errorInfo();
            $this->setServerErrorResponse($errorArray[2]);
        }
    }

    public function sendGCMToUser($userID, $key, $msg, &$error){

        $result = false;

        $statement = $this->dbConnector->prepareStatement(self::PREPARED_STATEMENT_TOKENS_OF_USER);

        $statement->bindParam(":userID", $userID);

        if($statement->execute()){
            $tokens = $statement->fetchAll(\PDO::FETCH_COLUMN, 0);
            if(count($tokens > 0)){

                $data = array();
                $data['key'] = $key;
                $data['body'] = $msg;

                $result = $this->sendGCM($data,$tokens);
            }
            else{
                $error = "NO TOKENS FOR USER";
            }
        }
        else{
            $errorArray = $this->$statement->errorInfo();
            $error = $errorArray[2];
        }

        return $result;
    }

    public function sendGCM( $data, $ids )
    {
        //------------------------------
        // Replace with real GCM API
        // key from Google APIs Console
        //
        // https://code.google.com/apis/console/
        //------------------------------

        $apiKey = 'AIzaSyAqFhpd6fGCaGbMGe21hvSAwW56Tquij_0';

        //------------------------------
        // Define URL to GCM endpoint
        //------------------------------

        $url = 'https://android.googleapis.com/gcm/send';

        //------------------------------
        // Set GCM post variables
        // (Device IDs and push payload)
        //------------------------------

        $post = array(
            'registration_ids'  => $ids,
            'data'              => $data,
        );

        //------------------------------
        // Set CURL request headers
        // (Authentication and type)
        //------------------------------

        $headers = array(
            'Authorization: key=' . $apiKey,
            'Content-Type: application/json'
        );

        //------------------------------
        // Initialize curl handle
        //------------------------------

        $ch = curl_init();

        //------------------------------
        // Set URL to GCM endpoint
        //------------------------------

        curl_setopt( $ch, CURLOPT_URL, $url );

        //------------------------------
        // Set request method to POST
        //------------------------------

        curl_setopt( $ch, CURLOPT_POST, true );

        //------------------------------
        // Set our custom headers
        //------------------------------

        curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers );

        //------------------------------
        // Get the response back as
        // string instead of printing it
        //------------------------------

        curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );

        //------------------------------
        // Set post data as JSON
        //------------------------------

        curl_setopt( $ch, CURLOPT_POSTFIELDS, json_encode( $post ) );

        //------------------------------
        // Actually send the push!
        //------------------------------

        $result = curl_exec( $ch );

        //------------------------------
        // Error? Display it!
        //------------------------------

        if ( curl_errno( $ch ) )
        {
            echo 'GCM error: ' . curl_error( $ch );
        }

        //------------------------------
        // Close curl handle
        //------------------------------

        curl_close( $ch );

        //------------------------------
        // Debug GCM response
        //------------------------------

        return $result;
    }

} 
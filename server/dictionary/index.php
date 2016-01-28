<?php
/**
 * Created by PhpStorm.
 * User: MartinTiefengrabner
 * Date: 13/07/15
 * Time: 17:35
 */

?>

<head>
    <link type="text/css" rel="stylesheet" href="/dictionary/lib/bootstrap/3.3.5/css/bootstrap.min.css">
    <link type="text/css" rel="stylesheet" href="/dictionary/lib/bootstrap/3.3.5/css/bootstrap-theme.css">
    <link type="text/css" rel="stylesheet" href="/dictionary/css/typo.css">
    <link href='https://fonts.googleapis.com/css?family=Hind+Vadodara:400,300,500' rel='stylesheet' type='text/css'>



    <script language="javascript" type="text/javascript" src="/dictionary/lib/jquery-2.1.4.min.js"></script>

    <script language="javascript" type="text/javascript" src="/dictionary/lib/jquery-ui/1.11.4/jquery-ui.min.js"></script>

    <script language="javascript" type="text/javascript" src="/dictionary/lib/bootstrap/3.3.5/js/bootstrap.min.js"></script>
    <script language="javascript" type="text/javascript" src="/dictionary/lib/bootstrap/3.3.5/js/npm.js"></script>

    <link type="text/css" rel="stylesheet" href="/dictionary/css/page.css">
    <link type="text/css" rel="stylesheet" href="/dictionary/css/typo.css">


<?php
    if(isset($_GET['sign'])){
        require_once('partials/partial_sign.php');
    }
else{
    require_once('partials/partial_home.php');
}
?>



</body>
<?php
require("vendor/autoload.php");
use Swagger\Swagger;
$swagger = new Swagger('/mmt_lun/data/SHC/www/html/shc/projects/ssc/dev/api','/mmt_lun/data/SHC/www/html/shc/projects/ssc/dev/api/vendor');
header('Content-Type: application/json');

var_dump($swagger->getResourceList());
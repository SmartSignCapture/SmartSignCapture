<?php
/**
 * Created by PhpStorm.
 * User: MartinTiefengrabner
 * Date: 20/08/15
 * Time: 10:20
 */

require_once '../doc/vendor/restler.php';
use Luracast\Restler\Restler;
use Luracast\Restler\Defaults;
//set the defaults to match your requirements
Defaults::$throttle = 20; //time in milliseconds for bandwidth throttling
//setup restler
$r = new Restler();
$r->addAPIClass('YourApiClassNameHere'); // repeat for more
$r->addAPIClass('Resources'); //from restler framework for API Explorer
$r->addFilterClass('RateLimit'); //Add Filters as needed
$r->handle(); //serve the response
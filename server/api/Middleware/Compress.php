<?php
/**
 * Slim Middleware Compressor
 *
 * @author Aaron Saray
 * @version 0.1
 */
namespace AaronSaray\SlimPHPMiddleware;
/**
 * Class Compress
 * @package SlimMiddleware
 */
class Compress extends \Slim\Middleware
{
    /**
     * The middleware calling function
     *
     * Please note: there are no real functions to this in this first version
     */
    public function call()
    {
        ob_start('ob_gzhandler');
        $this->next->call();
    }
}
<?php
 
/**
 * Clase para la conexi�n a la base de datos
 */
class DB_CONNECT {
 
    // constructor
    function __construct() {
        // connecting to database
        $this->connect();
    }
 
    // destructor
    function __destruct() {
        // closing db connection
        $this->close();
    }
 
    /**
     * Function to connect with database
     */
    function connect() {
        // import database connection variables
        require_once 'db_config.php';
 
        // Connecting to mysql database
        $con = mysql_connect(DB_SERVER, DB_USER, DB_PASSWORD) or die(mysql_error());
	 if (!$con) {
    		die('No pudo conectarse: ' . mysql_error());
	 }
	 echo 'Conectado satisfactoriamente';
 
        // Selecing database
        $db = mysql_select_db(DB_DATABASE) or die(mysql_error()) or die(mysql_error());
 
        // returing connection cursor
        return $con;
    }
 
    /**
     * Function to close db connection
     */
    function close() {
        // closing db connection
        mysql_close();
    }
 
}
 
?>
<?php
 
class funciones_BD {
 
    private $db;
 
    // constructor

    function __construct() {
        require_once 'connectbd.php';
        // connecting to database

        $this->db = new DB_Connect();
        $this->db->connect();

    }
 
    // destructor
    function __destruct() {
 
    }
 
    /**
     * estado gas
     */
    public function estado_gas() {

    $result = mysql_query("SELECT GAS, AGUA FROM LLAVES");
       while ($line = mysql_fetch_array($result, MYSQL_ASSOC)) {
    //echo "\t<tr>\n";
    $output[]=$line;
    foreach ($line as $col_value) {
        echo "\t\t<td>$col_value</td>\n";
    }

        if ($col_value == 1){
	     echo "El resultado es uno";
            return true;

        } else {
	     
            return false;
        }

    }

     /**
     * estado agua
     */
    public function estado_agua() {

    $result = mysql_query("SELECT AGUA FROM LLAVES");
        // check for successful store

        if ($result) {

            return true;

        } else {

            return false;
        }

    }
       
}
 
?>

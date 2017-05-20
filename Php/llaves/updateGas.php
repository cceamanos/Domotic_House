<?php
 
/*
 * Este código hace update del estado de la llave del Gas
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if ($_POST['GAS']) {
 
    $gas = $_POST['GAS'];
    echo $gas;
    // include db connect class
    require_once 'config.php';
 
    // connecting to db
    $db = new DB_CONNECT();
    if (strcmp($gas,"1"){
    	// mysql update row with matched pid
    	$result = mysql_query("UPDATE LLAVES SET GAS = 1");
    } else {
	$result = mysql_query("UPDATE LLAVES SET GAS = 0");
    }
 
    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Product successfully updated.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
 
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>

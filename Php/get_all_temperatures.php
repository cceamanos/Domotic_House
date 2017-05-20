<?php
 
/*
 * Following code will list all the products
 */
 
echo "Comienzo!!" 

// array for JSON response
$response = array();
 
// include db connect class
//require_once __DIR__ . '/db_connect.php';
require_once('/var/www/db_connect.php');
 
// connecting to db
$db = new DB_CONNECT();
echo "He conectado!!" 

// get all products from products table
$result = mysql_query("SELECT *FROM temperaturas") or die(mysql_error());
 
// check for empty result
if (mysql_num_rows($result) > 0) {
    // bucle por todos los resultados
    // de las temperaturas de las habitaciones
    $response["habitaciones"] = array();
 
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $hab_temp = array();
        $hab_temp["habitacion"] = $row["habitacion"];
        $hab_temp["temperatura"] = $row["temperatura"];
        $hab_temp["humedad"] = $row["humedad"];
	 $hab_temp["lim_temperatura"] = $row["lim_temperatura"]
        $hab_temp["created_at"] = $row["created_at"];
        $hab_temp["updated_at"] = $row["updated_at"];
 
        // pongo la respuesta de una sola habitacion en el array de respuesta
        array_push($response["habitacion"], $hab_temp);
    }
    // success
    $response["success"] = 1;
 
    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No se encontraron datos";
 
    // echo no users JSON
    echo json_encode($response);
}
?>
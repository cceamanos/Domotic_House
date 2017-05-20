<?php
 
// array for JSON response
$response = array();
// Conectando, seleccionando la base de datos
$link = mysql_connect(localhost, user, passwd) or die('No se pudo conectar: ' . mysql_error());

mysql_select_db(domotic_house) or die('No se pudo seleccionar la base de datos');

$query2 = 'SELECT ADDR_ZBEE FROM HABITACION WHERE COD_HAB=_DISP=1';
$result2 = mysql_query($query2) or die('Consulta fallida: ' . mysql_error());

// Determinar el codigo ZigBee
while ($line = mysql_fetch_array($result2, MYSQL_ASSOC)) {
    $zigbee=$line;
	 
// Realizar una consulta MySQL
$query = 'UPDATE DISPOSITIVO SET EST_DISP = 0 WHERE TIPO_DISP=1';
$result = mysql_query($query) or die('Consulta fallida: ' . mysql_error());

// check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Successfully updated.";
	 exec("python /home/pi/PFC2/enviapush1.py'".$zigbee"'",$output);
	 echo $output[0];
	
 
        // echoing JSON response
        echo json_encode($response);
    } else {
 
     // required field is missing
    $response["success"] = 0;
    $response["message"] = "Error Updating";
 
    // echoing JSON response
    echo json_encode($response);
}

?>


<?php
 
echo "<p>Esta es mi quinta frase hecha con Php!</p>" ;// Conectando, seleccionando la base de datos
$link = mysql_connect(localhost, user, passwd) or die('No se pudo conectar: ' . mysql_error());

mysql_select_db(domotic_house) or die('No se pudo seleccionar la base de datos');

// array for JSON response
$response = array();

// Realizar una consulta MySQL
$query = 'UPDATE DISPOSITIVO SET EST_DISP = 0 WHERE TIPO_DISP=1';
$result = mysql_query($query) or die('Consulta fallida: ' . mysql_error());

//check if row inserted or not
    if ($result) {
        echo "Pasa por aqui";
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Successfully updated.";
	 
 
        // echoing JSON response
        echo json_encode($response);
    } else {
 
     // required field is missing
    $response["success"] = 0;
    $response["message"] = "Error Updating";
 
    // echoing JSON response
    echo json_encode($response);
}

mysql_select_db(domotic_house) or die('No se pudo seleccionar la base de datos');

$query2 = 'SELECT ADDR_ZBEE FROM HABITACION WHERE COD_HAB=1';
$result2 = mysql_query($query2) or die('Consulta fallida: ' . mysql_error());

// Determinar el codigo ZigBee
$zigbeeaddr= mysql_fetch_array($result2);	 
$addr=$zigbeeaddr[0];
$valor=1;
$params=array();
$params[0]=$addr;
$params[1]='0';
$salida=array();
$command = "sudo python /home/pi/PFC2/Raspberry/xbee_envio_arduino_gas_prueba.py $addr $valor";
echo "<p>El valor del comando es $command.</p>" ;
echo "<p>El valor de mi primera variable es $addr.</p>" ;
exec($command); // Funciona la llamada así al dispositivo
//passthru($command);
echo "$error";


?>


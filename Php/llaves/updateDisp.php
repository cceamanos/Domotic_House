<?php
// Conectando, seleccionando la base de datos
$cod_dispositivo = $_POST['cod_disp'];
$estado = $_POST['estado'];



$link = mysql_connect(localhost, user, passwd)
    or die('No se pudo conectar: ' . mysql_error());
//echo 'Connected successfully';
mysql_select_db(domotic_house) or die('No se pudo seleccionar la base de datos');

// Realizar una consulta MySQL
$query = "UPDATE DISPOSITIVO SET EST_DISP = '$estado', CAMBIADO = '1' WHERE COD_DISP='$cod_dispositivo'";
$result = mysql_query($query) or die('Consulta fallida: ' . mysql_error());

// check if row inserted or not
    if ($result) {
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

mysql_free_result($result);

// Cerrar la conexión
mysql_close($link);
?>


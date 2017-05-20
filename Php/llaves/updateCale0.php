<?php
// Conectando, seleccionando la base de datos
$estadodisp = $_POST['est_disp'];
$hist = $_POST['histeresis'];
$temperatura = $_POST['temp_des'];

$link = mysql_connect(localhost, user, passwd)
    or die('No se pudo conectar: ' . mysql_error());
//echo 'Connected successfully';
mysql_select_db(domotic_house) or die('No se pudo seleccionar la base de datos');

// Realizar una consulta MySQL
$query = "UPDATE DISPOSITIVO SET EST_DISP = '$estadodisp', VAL_AUX1 = '$hist', VAL_AUX2 = '$temperatura'  WHERE TIPO_DISP=5";
$result = mysql_query($query) or die('Consulta fallida: ' . mysql_error());

if ($result) 
{
       $resultado[]=array("logstatus"=>"1");

} else {

       $resultado[]=array("logstatus"=>"0");
}

echo json_encode($resultado);

mysql_free_result($result);

// Cerrar la conexión
mysql_close($link);
?>




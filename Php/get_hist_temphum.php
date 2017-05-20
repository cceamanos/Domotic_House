<?php
// Conectando, seleccionando la base de datos
$cod_hab = $_POST['cod_hab'];
$tip_sen = $_POST['tip_sen'];
$fecha = $_POST['fecha'];

$link = mysql_connect(localhost, user, passwd)
    or die('No se pudo conectar: ' . mysql_error());
//echo 'Connected successfully';
mysql_select_db(domotic_house) or die('No se pudo seleccionar la base de datos');

// Realizar una consulta MySQL
$query="SELECT VAL_SEN FROM HIST_SENSORES WHERE COD_HAB='$cod_hab' AND TIP_SEN='$tip_sen' AND TIME_HIST>'$fecha'";
$result = mysql_query($query) or die('Consulta fallida: ' . mysql_error());

while ($line = mysql_fetch_array($result, MYSQL_ASSOC)) {
    $output[]=$line;
}

print(json_encode($output));

// Liberar resultados
mysql_free_result($result);

// Cerrar la conexión
mysql_close($link);
?>


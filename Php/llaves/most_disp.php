<?php
// Conectando, seleccionando la base de datos
$cod_hab = $_POST['cod_hab'];


$link = mysql_connect(localhost, user, passwd)
    or die('No se pudo conectar: ' . mysql_error());
//echo 'Connected successfully';
mysql_select_db(domotic_house) or die('No se pudo seleccionar la base de datos');

// Realizar una consulta MySQL
$query="SELECT COD_DISP, DESC_DISP, TIPO_DISP, EST_DISP FROM DISPOSITIVO WHERE COD_HAB='$cod_hab' AND TIPO_DISP<>1 AND TIPO_DISP<>3 AND TIPO_DISP<>4";
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


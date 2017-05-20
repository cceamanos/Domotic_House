<?php
// Conectando, seleccionando la base de datos
$cod_aut = $_POST['cod_aut'];

$link = mysql_connect(localhost, user, passwd)
    or die('No se pudo conectar: ' . mysql_error());
//echo 'Connected successfully';
mysql_select_db(domotic_house) or die('No se pudo seleccionar la base de datos');

// Realizar una consulta MySQL
$query="SELECT ESTADO FROM AUTOMATISMOS WHERE COD_AUT='$cod_aut'";
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


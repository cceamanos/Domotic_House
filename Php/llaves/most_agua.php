<?php
// Conectando, seleccionando la base de datos
$link = mysql_connect(localhost, user, passwd)
    or die('No se pudo conectar: ' . mysql_error());
//echo 'Connected successfully';
mysql_select_db(domotic_house) or die('No se pudo seleccionar la base de datos');

// Realizar una consulta MySQL
$query = 'SELECT EST_DISP FROM DISPOSITIVO WHERE TIPO_DISP=3';
$result = mysql_query($query) or die('Consulta fallida: ' . mysql_error());

// Imprimir los resultados en HTML
//echo "<table>\n";
while ($line = mysql_fetch_array($result, MYSQL_ASSOC)) {
    //echo "\t<tr>\n";
    $output[]=$line;
}
       if ($col_value == 1){
		$resultado[]=array("logstatus"=>"0");
	}else{
	$resultado[]=array("logstatus"=>"1");
	}
//echo "</table>\n";
print(json_encode($output));
// Liberar resultados
mysql_free_result($result);

// Cerrar la conexión
mysql_close($link);
?>

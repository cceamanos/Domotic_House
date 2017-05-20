<?php
// Conectando, seleccionando la base de datos
$link = mysql_connect(localhost, user, paswd)
    or die('No se pudo conectar: ' . mysql_error());
//echo 'Connected successfully';
mysql_select_db(domotic_house) or die('No se pudo seleccionar la base de datos');

// Realizar una consulta MySQL
$query = 'SELECT habitacion, temperatura, humedad FROM temperaturas';
$result = mysql_query($query) or die('Consulta fallida: ' . mysql_error());

// Imprimir los resultados en HTML
//echo "<table>\n";
while ($line = mysql_fetch_array($result, MYSQL_ASSOC)) {
    //echo "\t<tr>\n";
    $output[]=$line;
    foreach ($line as $col_value) {
        //echo "\t\t<td>$col_value</td>\n";
    }
    //echo "\t</tr>\n";
}
//echo "</table>\n";
print(json_encode($output));
// Liberar resultados
mysql_free_result($result);

// Cerrar la conexión
mysql_close($link);
?>
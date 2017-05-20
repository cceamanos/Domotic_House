<?php
// Conectando, seleccionando la base de datos
$cod_hab = $_POST['cod_hab'];
$cod_oper = $_POST['cod_oper'];

$link = mysql_connect(localhost, user, passwd)
    or die('No se pudo conectar: ' . mysql_error());
//echo 'Connected successfully';
mysql_select_db(domotic_house) or die('No se pudo seleccionar la base de datos');

// Realizar una consulta MySQL
if (!strcmp($cod_oper, "2"))
{
	$query="SELECT COD_DISP, DESC_DISP, TIPO_DISP, EST_DISP FROM DISPOSITIVO WHERE COD_HAB='$cod_hab' AND TIPO_DISP>10";
}
else
	if (!strcmp($cod_oper, "3"))
	{
		$query="SELECT COD_DISP, DESC_DISP, TIPO_DISP, EST_DISP FROM DISPOSITIVO WHERE COD_HAB='$cod_hab' AND TIPO_DISP=10";
	}
	else
	{
		$query="SELECT COD_SEN, DESC_SEN, TIP_SEN, VAL_SEN, TIME_SEN FROM SENSORES_ACTUALES WHERE COD_HAB='$cod_hab'";
	}

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


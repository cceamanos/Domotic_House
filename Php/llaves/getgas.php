
<?php

/*ESTADO GAS*/

require_once 'funciones_bd.php';
$db = new funciones_BD();

	if($db->estado_gas()){
		$resultado[]=array("logstatus"=>"1");
	}else{
		$resultado[]=array("logstatus"=>"0");
	}

echo json_encode($resultado);




?>

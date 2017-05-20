
<?php

/*ESTADO GAS*/

$usuario = $_POST['usuario'];
$passw = $_POST['password'];


require_once 'funciones_bd.php';
$db = new funciones_BD();

	if($db->estado_agua()){

	$resultado[]=array("Agua"=>"0");
	}else{
	$resultado[]=array("Agua"=>"1");
	}

echo json_encode($resultado);




?>

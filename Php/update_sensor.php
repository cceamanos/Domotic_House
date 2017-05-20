<?php
	$host='localhost';
	$uname='user';
	$pwd='passwd';
	$db="db_name";

	$con = mysql_connect($host,$uname,$pwd) or die("connection failed");
	mysql_select_db($db,$con) or die("db selection failed");
	 
	$cod_sen=$_REQUEST['codigo'];
	$des_sen=$_REQUEST['descripcion'];
	$tipo_sen=$_REQUEST['tiposen'];
	$cod_sen_ini=$_REQUEST['codigo_ini'];




	$flag['code']=0;
	
	// no existe
       if($r=mysql_query("UPDATE SENSORES_ACTUALES SET COD_SEN='$cod_sen', DESC_SEN='$des_sen', TIP_SEN='$tipo_sen' WHERE COD_SEN='$cod_sen_ini'",$con)) 
	{
		$flag['code']=1;
		
	}

	print(json_encode($flag));
	mysql_close($con);
?>
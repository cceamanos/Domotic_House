<?php
	$host='localhost';
	$uname='user';
	$pwd='passwd';
	$db="db_name";

	$con = mysql_connect($host,$uname,$pwd) or die("connection failed");
	mysql_select_db($db,$con) or die("db selection failed");
	 
	$cod_disp=$_REQUEST['codigo'];
	$des_disp=$_REQUEST['descripcion'];
	$tipo_disp=$_REQUEST['tipodisp'];
	$est_ini=$_REQUEST['estadoini'];
	$cod_disp_ini=$_REQUEST['codigo_ini'];




	$flag['code']=0;
	
	// no existe
       if($r=mysql_query("UPDATE DISPOSITIVO SET COD_DISP='$cod_disp', DESC_DISP='$des_disp', TIPO_DISP='$tipo_disp', VAL_INI='$est_ini' WHERE COD_DISP='$cod_disp_ini'",$con)) 
	{
		$flag['code']=1;
		
	}

	print(json_encode($flag));
	mysql_close($con);
?>
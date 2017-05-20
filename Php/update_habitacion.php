<?php
	$host='localhost';
	$uname='user';
	$pwd='passwd';
	$db="db_name";

	$con = mysql_connect($host,$uname,$pwd) or die("connection failed");
	mysql_select_db($db,$con) or die("db selection failed");
	 
	$cod_hab=$_REQUEST['cod_hab'];
	$nom_hab=$_REQUEST['nom_hab'];
	$des_hab=$_REQUEST['des_hab'];
	$addr_zbee=$_REQUEST['addr_zbee'];
	$cod_hab_ant=$_REQUEST['cod_hab_ant'];




	$flag['code']=0;
	
	// no existe
       if($r=mysql_query("UPDATE HABITACION SET COD_HAB='$cod_hab', NOM_HAB='$nom_hab', DES_HAB='$des_hab', ADDR_ZBEE='$addr_zbee' WHERE COD_HAB='$cod_hab_ant'",$con)) 
	{
		$flag['code']=1;
		
	}

	print(json_encode($flag));
	mysql_close($con);
?>
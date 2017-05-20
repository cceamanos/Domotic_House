<?php
	$host='localhost';
	$uname='user';
	$pwd='password';
	$db="db_name";

	$con = mysql_connect($host,$uname,$pwd) or die("connection failed");
	mysql_select_db($db,$con) or die("db selection failed");
	 
	$codigo=$_REQUEST['codigo'];

	// no existe
	$flag['code']=0;
	
	
       if($r=mysql_query("DELETE FROM SENSORES_ACTUALES WHERE COD_SEN='$codigo'",$con)) 
	{
		$flag['code']=1;
		
	}

	print(json_encode($flag));
	mysql_close($con);
?>
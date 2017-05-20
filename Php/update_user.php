<?php
	$host='localhost';
	$uname='user';
	$pwd='passwd';
	$db="db_name";

	$con = mysql_connect($host,$uname,$pwd) or die("connection failed");
	mysql_select_db($db,$con) or die("db selection failed");
	 
	$username=$_REQUEST['username'];
	$login=$_REQUEST['login'];
	$passw=$_REQUEST['passw'];
	$usuarioactual=$_REQUEST['user_act'];



	$flag['code']=0;
	
	// no existe
       if($r=mysql_query("UPDATE USUARIOS SET USERNAME='$login', NOM_USER='$username', PASSW='$passw' WHERE USERNAME='$usuarioactual'",$con)) 
	{
		$flag['code']=1;
		
	}

	print(json_encode($flag));
	mysql_close($con);
?>
<?php
	$host='localhost';
	$uname='user';
	$pwd='password';
	$db="db_name";

	$con = mysql_connect($host,$uname,$pwd) or die("connection failed");
	mysql_select_db($db,$con) or die("db selection failed");
	 
	$username=$_REQUEST['username'];
	$login=$_REQUEST['login'];
	$passw=$_REQUEST['passw'];


	$flag['code']=0;
	
	$result = mysql_query("SELECT USERNAME FROM USUARIOS WHERE USERNAME = '$login'");

       $num_rows = mysql_num_rows($result); //numero de filas retornadas

        if ($num_rows > 0) {

            // el usuario existe 
	     $flag['code']=2; //ya existe y no se inserta


        } else {
            // no existe
            if($r=mysql_query("INSERT INTO USUARIOS (COD_USER, USERNAME, NOM_USER, PASSW, TIP_USER) VALUES (NULL,'$login','$username','$passw',2) ",$con)) 
	     {
		$flag['code']=1;
		
	     }
        }


	print(json_encode($flag));
	mysql_close($con);
?>
<?php
	$host='localhost';
	$uname='user';
	$pwd='password';
	$db="db_name";

	$con = mysql_connect($host,$uname,$pwd) or die("connection failed");
	mysql_select_db($db,$con) or die("db selection failed");
	 
	$codigo1=$_REQUEST['codigo'];
	$codhab=$_REQUEST['cod_hab'];
	$descripciones=$_REQUEST['descripcion'];
	$tipodisp=$_REQUEST['tipodisp'];
	


	$flag['code']=0;
	
	$result = mysql_query("SELECT COD_SEN FROM SENSORES_ACTUALES WHERE COD_SEN = '$codigo1'");

       $num_rows = mysql_num_rows($result); //numero de filas retornadas

       if ($num_rows > 0) {

            // el usuario existe 
	     $flag['code']=2; //ya existe y no se inserta


        }
	 else 
	 {
		
	            // no existe
       	     if($r=mysql_query("INSERT INTO SENSORES_ACTUALES (COD_SEN, COD_HAB, DESC_SEN,VAL_SEN,TIP_SEN) VALUES ('$codigo1','$codhab','$descripciones',0,'$tipodisp') ",$con)) 
	     	     {
			$flag['code']=1;
		
	     	     }
	}

	print(json_encode($flag));
	mysql_close($con);
?>

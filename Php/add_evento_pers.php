<?php
	$host='localhost';
	$uname='user';
	$pwd='password';
	$db="db_name";

	$con = mysql_connect($host,$uname,$pwd) or die("connection failed");
	mysql_select_db($db,$con) or die("db selection failed");
	 
	$fecha=$_REQUEST['fecha'];
	$descripcion=$_REQUEST['descripcion'];
	$hora=$_REQUEST['hora'];
	$cod_disp=$_REQUEST['cod_disp'];
	$cod_hab=$_REQUEST['cod_hab'];
	$valoraux=$_REQUEST['val_aux'];



	$flag['code']=0;
	
	$result = mysql_query("SELECT COD_HAB, FINI_EVENT, HINI_EVENT, COD_DISP, VAL_AUX1 FROM EVENTOS_PROG WHERE COD_HAB = '$cod_hab', FINI_EVENT = '$fecha', HINI_EVENT = '$hora', EST_DISP = '$valoraux', COD_DISP = '$cod_disp' ");

       $num_rows = mysql_num_rows($result); //numero de filas retornadas

        if ($num_rows > 0) {

            // el usuario existe 
	     $flag['code']=2; //ya existe y no se inserta


        } else {
            // no existe
            if($r=mysql_query("INSERT INTO EVENTOS_PROG (COD_EVENT, COD_HAB, DESC_EVENT, FINI_EVENT, HINI_EVENT, COD_DISP, VAL_AUX1) VALUES (NULL,'$cod_hab','$descripcion','$fecha','$hora','$cod_disp','$valoraux') ",$con)) 
	     {
		$flag['code']=1;
		
	     }
        }


	print(json_encode($flag));
	mysql_close($con);
?>

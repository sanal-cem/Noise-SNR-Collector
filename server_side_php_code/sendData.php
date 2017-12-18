<?php
   $username = $_GET['username'];
   $password = $_GET['password'];
   $ROUTENUM = $_GET['ROUTENUM'];
   $SNR = $_GET['SNR'];
   $LOC_X = $_GET['LOC_X'];
   $LOC_Y = $_GET['LOC_Y'];
   $SDATE = $_GET['SDATE'];
   $STIME = $_GET['STIME'];
   $TABLE = $_GET['TABLE'];
   
   $con = mysqli_connect("mysql1.cibo6qjcekup.eu-west-2.rds.amazonaws.com", "$username", "$password", "bmi");

   if (mysqli_connect_errno($con)) {
      echo "Failed to connect to MySQL: " . mysqli_connect_error();
   }
   if (mysqli_query($con, "INSERT INTO $TABLE (ROUTENUM, SNR, LOC_X, LOC_Y, SDATE, STIME) VALUES('$ROUTENUM', '$SNR', '$LOC_X', '$LOC_Y', '$SDATE', '$STIME')")) {
      echo "New record created successfully";
   }
   mysqli_close($con);
?>
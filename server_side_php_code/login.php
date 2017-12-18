<?php
   $username = $_GET['username'];
   $password = $_GET['password'];
   $TABLE = $_GET['TABLE'];
   
   $con = mysqli_connect("mysql1.cibo6qjcekup.eu-west-2.rds.amazonaws.com", "$username", "$password", "bmi");

   if (mysqli_connect_errno($con)) {
      echo "Failed to connect to MySQL: " . mysqli_connect_error();
   }
   $result = mysqli_query($con,"SELECT ID, ROUTENUM, SNR, LOC_X, LOC_Y, SDATE, STIME FROM $TABLE");
   while($row=mysqli_fetch_array($result)){
    $rows[]=$row;
   }
   echo json_encode($rows);
   mysqli_close($con);
?>
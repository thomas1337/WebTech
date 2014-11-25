<?php
/**
 * Created by PhpStorm.
 * User: evgenijavstein
 * Date: 24/11/14
 * Time: 18:18
 */
 
 $DSN = "sqlite:cinam.s3db");
 $db = new PDO($DSN);
 $reservedSeatsStatement= $db->prepare("SELECT seat FROM seats WHERE movie_id = :movieId");

//client requests list of reserved seats as json object
if( isset($_GET["movie"])){

    $data = array(1,2,3);//testdummydata
    $reservedSeatsStatement->bindParam(":movieId", $_GET["movie"]);
    if($reservedSeatsStatement->execute(){
      while($seatNum = $reservedSeatsStatement->fetch()){
        array_push($data$sea, $seatNum);
      }
    }
    header('Content-Type: application/json');
    echo json_encode($data);

    exit();
}else{
    //header("HTTP/1.0 404 Not Found");
    $data = array(1, 2, 3, 4);
    header('Content-Type: application/json');
    echo json_encode($data);
    exit();
}



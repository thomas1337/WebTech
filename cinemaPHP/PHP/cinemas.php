<?php
/**
 * Created by PhpStorm.
 * User: evgenijavstein
 * Date: 24/11/14
 * Time: 18:18
 */
 
 $DSN = "sqlite:../cinema.s3db");
 $db = new PDO($DSN);
 $reservedSeatsStatement= $db->prepare("SELECT seat FROM seats WHERE movie_id = :movieId");
 $doReservationStatement= $db->prepare("INSERT INTO seats VALUES(:res_id,:movie_id,:name,:email,:seatnum");

//client requests list of reserved seats as json object
if( $_SERVER["REQUEST_METHOD"] == "GET" and isset($_GET["movie"])){

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
    
    //client posts the reservation data
} else if($_SERVER["REQUEST_METHOD"] == "POST"){
    //row id will be automatically added
    $reservationId=3;
    $doReservationStatement->bindParam(":res_id", $reservationId);
    $doReservationStatement->bindParam(":movie_id", $_POST["movieId"]);
    $doReservationStatement->bindParam(":name", $_POST["name"]);
    $doReservationStatement->bindParam(":email", $_POST["email"]);
    var seats = json_decode($_POST["seats"]);
    foreach($seats as $seat){
      $doReservationStatement->bindParam(":seatnum", $seat);
      $doReservationStatement->execute();
    }
    echo $reservationId;//give resId back to client for success message
}else{
    //header("HTTP/1.0 404 Not Found");
    $data = array(1, 2, 3, 4);
    header('Content-Type: application/json');
    echo json_encode($data);
    exit();
}



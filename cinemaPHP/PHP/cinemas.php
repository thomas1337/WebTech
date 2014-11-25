<?php
/**
 * Created by PhpStorm.
 * User: evgenijavstein
 * Date: 24/11/14
 * Time: 18:18
 */
 
 $DSN = "sqlite:../cinema.s3db";
 $db = new PDO($DSN);
 $reservedSeatsStatement= $db->prepare("SELECT seat FROM seats WHERE movie_id = :movieId");
 $doReservationStatement= $db->prepare("INSERT INTO seats (res_id,movie_id,name,email,seat) VALUES (:res_id,:movie_id,:name,:email,:seatnum);");

//client requests list of reserved seats as json object
if( $_SERVER["REQUEST_METHOD"] == "GET" and isset($_GET["movie"])){

    $data = array();//testdummydata
    $reservedSeatsStatement->bindParam(":movieId", $_GET["movie"]);
    if($reservedSeatsStatement->execute()){
      while($seatNum = $reservedSeatsStatement->fetch()){
        array_push($data, $seatNum["seat"]);
      }
    }
    header('Content-Type: application/json');
    echo json_encode($data);

    exit();
    
    //client posts the reservation data
} else if($_SERVER["REQUEST_METHOD"] == "POST"){
    
    $input= json_decode(file_get_contents('php://input'));
    $seats = $input->seats;
    foreach($seats as $seat){
        $doReservationStatement->bindParam(":res_id", hash("md5", $input->email+$input->movieId));

        $doReservationStatement->bindParam(":movie_id", $input->movieId);

        $doReservationStatement->bindParam(":name", $input->name);

        $doReservationStatement->bindParam(":email", $input->email);

        $doReservationStatement->bindParam(":seatnum", $seat);

        $doReservationStatement->execute();

    }
     //give resId back to client for success message
    exit("Die Reservierung war erfolgreich. Ihre ReservierungsID lautet: ".hash("md5", $input->email+$input->movieId));
    //TODO send back not only registration ID but again the array with all reserved seats,
    //so the user cannot register twice for the same seats
    //use JSON of course
}else{
    header("HTTP/1.0 404 Not Found");

    exit();
}



<?php
/**
 * Created by PhpStorm.
 * User: evgenijavstein
 * Date: 24/11/14
 * Time: 18:18
 */



if( $_GET["movie"])
{
    $data = array(1, 2, 3, 4,5);
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



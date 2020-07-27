<?php
    include('./library/connect.php');
    $conn = connect();

    
    $post = file_get_contents("php://input");
    $json = json_decode($post, true);

    $result = array();
    if ($json == null){
        $result["result"] = "fail";
        $result["comment"] = "json error";
        echo(json_encode($result));
        disconnect($conn);
        return;
    }

    $id = $json['id'];
    $session = $json['session'];

    if (removeSession($conn, $id, $session)) {
        $result["result"] = "success";
    }else {
        $result["result"] = "fail";
        $result["comment"] = "session is expire";
    }
    echo(json_encode($result));

    disconnect($conn);
?>
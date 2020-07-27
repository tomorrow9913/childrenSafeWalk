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
    $pwd = hash("sha256", $json['pwd']);

    $session = makeSession($conn, $id, $pwd);
    if ($session == ""){
        $result["result"] = "fail";
        $result["comment"] = "login fail";
    }else {
        $result["result"] = "success";
        $result["session"] = $session;
    }
    $output = json_encode($result);
    echo($output);

    disconnect($conn);
?>
<?php
    include('./library/connect.php');
    $conn = connect();

    $post = file_get_contents("php://input");
    $json = json_decode($post, true);

    $result = array();
    if ($json == null){
        $result["result"] = "fail";
        $result["comment"] = "json error";
        $output = json_encode($result);
        echo $output;
        disconnect($conn);
        return;
    }
    $id = $json['id'];
    $pwd = hash("sha256", $json['pwd']);
    $email = $json['email'];
    $name = $json['name'];
    $nickname = $json['nickname'];
    

    if (array_key_exists("callNum", $json)) {
        $callNum = $json['callNum'];
        $sql = "INSERT INTO user(userId, userPwd, email, name, nickname, callNum) values('$id', '$pwd', '$email', '$name', '$nickname', '$callNum')"; 
    }else {
        $callNum = null;
        $sql = "INSERT INTO user(userId, userPwd, email, name, nickname) values('$id', '$pwd', '$email', '$name', '$nickname')"; 
    }
    
    if (!mysqli_query($conn, $sql)) {
        $result["result"] = "fail";
        $result["comment"] = mysqli_error($conn);
    }else {
        $query = mysqli_query($conn, "SELECT id from user WHERE userId='$id'");
        $varId = mysqli_fetch_array($query)[0];
        $result["result"] = "success";
    }
    
    $output = json_encode($result);
    echo $output;

    disconnect($conn);
?>
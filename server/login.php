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

    $isid = checkId($conn, $id);
    $loginSuccess = checkLogin($conn, $id, $pwd);

    if ($isid == false) {
        $result["result"] = "fail";
        $result["comment"] = "not found : id";
    }else if ($loginSuccess == false) {
        $result["result"] = "fail";
        $result["comment"] = "diff : pw";
    }else if ($loginSuccess == true) {
        $session = makeSession($conn, $id, $pwd);
        if ($session == ""){
            $result["result"] = "fail";
            $result["comment"] = "login fail";
        }else {
            $sql = "SELECT * from user where userId='$id' and userPwd='$pwd'";
            $data = mysqli_query($conn, $sql);
            $p = mysqli_fetch_array($data);
            $result["result"] = "success";
            $result["session"] = $session;
            $result['nickname'] = $p['nickname'].'@'.$p['id'];
            $result['name'] = $p['name'];
            $result['email'] = $p['email'];
            $result['callNum'] = $p['callNum'];
        }
    }
    $output = json_encode($result);
    echo($output);

    disconnect($conn);
?>
<?php
    include('./library/connect.php');
    $conn = connect();
    $rawPOST = file_get_contents("php://input");
    $decode = json_decode($rawPOST, true);

    $result = array();
    if ($decode == null){
        $json["result"] = "fail";
        $json["comment"] = "json error";
        echo(json_encode($json));
        disconnect($conn);
        return;
    }

    $session = $decode['session'];
    $name = $decode['name']; 
    $desc = $decode['desc'];
    $type = $decode['type'];
    $poly = makePolygon($decode['position']);

    $json = array();
    if (checkSession($conn, $session)){
        $sql = "INSERT INTO dangerouslocation(name, descript, type, geo) values('$name', '$desc', '$type', PolygonFromText('$poly'))";

        $result = mysqli_query($conn, $sql);
        if (!$result){
            $json['result'] = 'fail';
            $json['comment'] = mysqli_error($conn);
        }else {

            $sql = "SELECT id from dangerouslocation ORDER BY id desc";
            $result = mysqli_query($conn, $sql);
            $id = (int)mysqli_fetch_array($result)[0];
            $json['id'] = $id;
            $json['result'] = 'success';
            $json['color']['r'] = 255;
            $json['color']['g'] = 0;
            $json['color']['b'] = 0;
            $json['color']['a'] = 100;
        }
    }else {
        $json['result'] = "fail";
        $json['comment'] = "session is dead";
    }
    
    $output = json_encode($json);
    echo($output);

    disconnect($conn);
?>


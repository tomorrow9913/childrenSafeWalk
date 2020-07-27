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

    $session = $json['session'];
    $type = $json['type'];

    if (checkSession($conn, $session)) {
        $sql = "SELECT *, AsText(geo) from dangerouslocation";
        $data = mysqli_query($conn, $sql);

        $item = array();
        while ($row = mysqli_fetch_array($data)) {
            $elem = array();
            $elem['id'] = $row['id'];
            $elem['color']['r'] = 255;
            $elem['color']['g'] = 0;
            $elem['color']['b'] = 0;
            $elem['color']['a'] = 100;
            $elem['name'] = $row['name'];
            $elem['desc'] = $row['descript'];
            $elem['type'] = $row['type'];
            
            $loc = array();
            $locRaw = $row['AsText(geo)'];
            
            $locRaw = substr($locRaw, 0, -2);
            $locRaw = substr($locRaw, 9);
            $locArray = explode(",", $locRaw);
            foreach ($locArray as $value) {

                $value = explode(" ", $value);
                if (count($value) != 2){
                    continue;
                }
                $locData = array();
                $locData['longitude'] = $value[0];
                $locData['latitude'] = $value[1];
                array_push($loc, $locData);
            }
            $elem['position'] = $loc;

            array_push($item, $elem);
        }

        $result["result"] = "success";
        $result["item"] = $item;
        echo(json_encode($result));
    }
    else {
        $result["result"] = "fail";
        $result["comment"] = "session error";
        echo(json_encode($result));
    }
        
    disconnect($conn);
?>
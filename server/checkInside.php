<?php
function isInside($location, $polygon){
    //crosses는 점q와 오른쪽 반직선과 다각형과의 교점의 개수
    $crosses = 0;
    $polygonSize = count($polygon);
    for($i = 0 ; $i < $polygonSize; $i++){
        $j = ($i + 1) % $polygonSize;
        //점 B가 선분 (p[i], p[j])의 y좌표 사이에 있음
        if(($polygon[$i]['y'] > $location['y']) != ($polygon[$j]['y'] > $location['y']) ) {
            //atX는 점 B를 지나는 수평선과 선분 (p[i], p[j])의 교점
            $atX = ($polygon[$j]['x']- $polygon[$i]['x'])*($location['y'] - $polygon[$i]['y']) / ($polygon[$j]['y'] - $polygon[$i]['y']) + $polygon[$i]['x'];
            //atX가 오른쪽 반직선과의 교점이 맞으면 교점의 개수를 증가시킨다.
            if($location['x'] < $atX) {
                $crosses++;
            }
        }
    }
    return $crosses % 2 > 0;
}

function totalSearch($location, $polygonList) {
    $result = false;

    foreach ($polygonList as $poly) {
        $result |= isInside($location, $polygonList);
    }

    return $result;
}

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

$locationJson = $json['location'];
$session = $json['session'];
$location = array();
$location['x'] = floatval($locationJson['longitude']);
$location['y'] = floatval($locationJson['latitude']);


if (checkSession($conn, $session)) {
    $polyList = array();

    $sql = "SELECT AsText(geo) from dangerouslocation";
    $data = mysqli_query($conn, $sql);
    while ($row = mysqli_fetch_array($data)) {
        $locRaw = $row['AsText(geo)'];
        $locRaw = substr($locRaw, 0, -2);
        $locRaw = substr($locRaw, 9);
        $locArray = explode(",", $locRaw);

        // polygon
        $item = array();
        foreach ($locArray as $value) {
            // point
            $elem = array();
            $value = explode(" ", $value);
            $elem['x'] = floatval($value[0]);
            $elem['y'] = floatval($value[1]);
            array_push($item, $elem);
        }
        array_push($polyList, $item);
    }
    // load all polygon data
    $isCollid = totalSearch($location, $polyList);
    
    $result["result"] = "success";
    $result["collid"] = $isCollid;
    echo(json_encode($result));
}else {
    $result["result"] = "fail";
    $result["comment"] = "session error";
    echo(json_encode($result));
}

disconnect($conn);
?>
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

$radius = $decode['radius'];

$lat = $decode['latitude'];
$long = $decode['longitude'];

$sql = <<<EOD
SELECT *, ( 6371 * 1000 * acos( cos( radians($lat) ) * cos( radians( latitude ) ) 
    * cos( radians( longitude ) - radians($long) ) + sin( radians($lat) ) * sin(radians(latitude)) ) ) AS distance 
FROM pingInfo
HAVING distance < ($radius)
ORDER BY distance 
EOD;

$query = mysqli_query($conn, $sql);
if ($query){
    $result['result'] = 'success';

    $value = array();
    while ($row = mysqli_fetch_array($query)) {
        $data = array();
        $data['id'] = (int)$row['id'];
        $loc = array();
        
        $loc['longitude'] = $row['longitude'];
        $loc['latitude'] = $row['latitude'];

        $data['location'] = $loc;
        $data['level'] = $row['level'];
        $data['distance'] = $row['distance'];
        $tagInfo = array();
        $tags = explode(',', $row['tag']);
        for ($i = 0; $i < count($tags); $i += 1) {
            array_push($tagInfo, intval($tags[$i]));
        }
        // "useful": { "true": count, "false": count }

        $useful = array();
        $useful['true'] = rand(0, 1000);
        $useful['false'] = rand(0, 1000);

        $data['useful'] = $useful; 
        $data['tag'] = $tagInfo;
        array_push($value, $data);
    }
    
    $result['data'] = $value;
}else {
    $result['result'] = 'fail';
    $result["comment"] = mysqli_error($conn);
}

echo(json_encode($result));

disconnect($conn);
?>
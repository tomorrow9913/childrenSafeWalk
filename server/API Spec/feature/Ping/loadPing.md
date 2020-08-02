# 위험지역 정보 가져오기.

## API 명세서 
### 입력 값 :
```
{
    "radius": "unit of Meter",
    "latitude": "location of latitude",
    "longitude: "location of longitude"
}
```
### 2. 출력 값 :
   1. 성공 한 경우
```
{
    "result": "success",
    "data": [ 
        {
            "id": num,
            "location": { "latitude": "num", "longitude": "num" },
            "level": "0 ~ 1",
            "distnace": "unit of base is meter",
            "tag": [ 
                { num, ... },
                ...
            ]
            "useful": { "true": count, "false": count }
        },
        ...
    ]
}
```
   1. 실패 한 경우

```
{
    "result": "fail",
    "comment": "comment of error"
}

```
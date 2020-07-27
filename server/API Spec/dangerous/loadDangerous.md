# 위험 지역 값 읽기 문서

## API 명세서 
### 입력 값 :
```
{
    "session": "encript session Id"
    "type": num
}
```
### 2. 출력 값 :
   1. 성공 한 경우
```
{
    "result": "success"
    "item": [
        { "id": num,
            "color": { "r": 0, "g": 0, "b": 0, "a": 0 },
            "name": "title",
            "desc", "description",
            "type": typeNum,
            "position": [
                { "latitude": "location of latitude", "longitude": "location of longitude" },
                ...
            ]
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
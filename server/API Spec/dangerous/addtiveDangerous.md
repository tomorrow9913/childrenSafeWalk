# 위험 지역 추가 문서

## API 명세서 
### 입력 값 :
```
{
    "session": "encript session Id"
    "name": "dangerous Title",
    "desc": "dangerous Desc", 
    "type": 0, // 0
    "position": [
        { latitude: "location of latitude", longitude: "location of longitude" },
        ...
    ]
}
```
### 2. 출력 값 :
   1. 성공 한 경우
```
{
    "id": 0,
    "color": { "r": 0, "g": 0, "b": 0, "a": 0 },
    "result": "success"
}
```
   1. 실패 한 경우

```
{
    "result": "fail",
    "comment": "comment of error"
}

```
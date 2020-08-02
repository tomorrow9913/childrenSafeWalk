# 위험 지역 추가 문서

## API 명세서 
### 입력 값 :
```
{
    "session": "encript session Id"
    "location": { "latitude": "num", "longitude": "num" },
    "level": "0 ~ 1",
    "tag": [ 
        { "id": num, "name": "name" },
        ...
    ]
}
```
### 2. 출력 값 :
   1. 성공 한 경우
```
{
    "result": "success",
    "id": num
}
```
   1. 실패 한 경우

```
{
    "result": "fail",
    "comment": "comment of error"
}

```
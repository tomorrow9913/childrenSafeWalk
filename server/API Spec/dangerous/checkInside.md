# 위험 지역에 도달 유무 확인 문서

## API 명세서 
### 입력 값 :
```
{
    "session": "encript session Id"
    "location": { "longitude": "value", "latitude": "value" }
}
```
### 2. 출력 값 :
   1. 성공 한 경우
```
{
    "result": "success",
    "collid": 0 or 1
}
```
   1. 실패 한 경우

```
{
    "result": "fail",
    "comment": "comment of error"
}

```
# 로그인 관련 문서

## API 명세서 :
### 입력 값 :
```
{
    "id": "user Id",
    "session": "encripted of session"
}

```
### 2. 출력 값 :
   1. 성공 한 경우
```
{
    "result": "success",
}
```
   1. 실패 한 경우
```
{
    "result": "fail",
    "comment": "session is expire"
}
```
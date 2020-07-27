# 회원 가입 관련 문서

## API 명세서 :
### 입력 값 :
```
{
    "id": "user Id",
    "pwd": "SHA256"
}
```
### 2. 출력 값 :
   1. 성공 한 경우
```
{
    "result": "success",
    "session": "encript session Id"
}
```
   1. 실패 한 경우

```
{
    "result": "fail",
    "comment": "reason for error"
}
```
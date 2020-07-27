# 회원 가입 관련 문서

## API 명세서 :
### 입력 값 :
1. email : nullable

```
{
    "id": "user Id",
    "pwd": "SHA256",
    "email": "email@data.com",
    "callNum": "num",
    "nickname": "name",
    "name": "name"
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
    "comment": "reason for error"
}
```
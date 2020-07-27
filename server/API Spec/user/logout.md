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
- 비상 연락망이 없는 경우
```
{
    "result": "success",
    "session": "encript session Id",
    "nickname": "nickname@고유번호",
    "name": "name",
    "email": "email",    
    "callNum": null
}
```
- 비상 연락망이 있는 경우
```
{
    "result": "success",
    "session": "encript session Id",
    "nickname": "nickname@고유번호",
    "name": "name",
    "email": "email",   
    "call-num": "긴급 연락처" 
}
```
   1. 실패 한 경우

```
{
    "result": "fail",
    "comment": "reason for error"
}
```
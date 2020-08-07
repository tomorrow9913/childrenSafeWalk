# 좋아요! 나빠요! 정보 이벤트

## API 명세서 
### 입력 값 :
```
{
    "id": num,
    "status": // 1 -> Good, 0 -> Bad
}
```
### 2. 출력 값 :
   1. 성공 한 경우
```
{
    "result": "success"
}
```
   1. 실패 한 경우 해당 id와 파일이 없는 경우
```
{
    "result": "fail",
    "comment": "comment of error"
}
```
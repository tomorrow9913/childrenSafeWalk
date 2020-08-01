# 위험 지역 값 읽기 문서 (테스트)

## API 명세서 
### 입력 값 :
```
없음.
```
### 2. 출력 값 :
   1. 성공 한 경우
```
{
    "result": "success"
    "item": [
        { 
            "id": num,
            "source": {
                "latitude": "location of latitude",
                "longitude": "location of longitude"
            },
            "target": {
                "latitude": "location of latitude",
                "longitude": "location of longitude"
            }
        },
        ...
    ]
}
```
   1. 실패 한 경우

```
GET 형식이여서 실패할 경우는 없다.
```
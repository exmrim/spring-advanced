# SPRING ADVANCED
```
1. Lv5. ‘내’가 정의한 문제와 해결 과정
  1) [문제 인식 및 정의]
     동일한 예외처리가 각각의 메소드마다 동일하게 반복되고 있다.
  2) [해결 방안]
   2-1. [의사결정 과정]
        : ManagerService의 각각의 메소드마다 TodoId 의 InvalidRequestException 가 반복적으로 예외 처리 되고있다.
   2-2. [해결 과정]
        : 반복적인 예외 처리문을 TodoRepository에 만들고 로직을 분리시킨다.	
  3) [해결 완료]
   3-1. [회고]
        - 반복성을 제거함으로써 프로그램이 가벼워진다. 
        - 가독성이 향상된다.
        - 유지보수가 편리해진다.
   3-2. [전후 데이터 비교]
        : 반복적인 부분을 분리시켰으므로 데이터의 변화는 없지만, 데이터를 불러오는데 시간이 보다 더 빨라졌음을 알 수 있다.
```
2. Test Coverage
![coverageTest](https://github.com/user-attachments/assets/ea5a1e81-5db8-41ff-959e-c8793e15188e)

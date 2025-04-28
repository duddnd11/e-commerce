## 📑 병목 쿼리 분석

---

### 📌 Context (상황 설명)

- 상위 상품 조회 API 에서 데이터가 많을 시 상위 상품을 도출 하는 계산 과정에서 조회가 오래 걸릴 가능성이 있음.

---

### 📌 Problem (문제점)

- 병목 예상 쿼리:
    
    ```sql
    
    SELECT product_id FROM order_detail WHERE order_id IN (
    	SELECT id FROM orders
      WHERE order_at >= DATE_SUB(NOW(), INTERVAL 3 DAY)
      AND status = 'CONFIRMED')
      group by product_id 
      order by sum(quantity) desc limit 5;

    SELECT product_id, sum(quantity)
    FROM orders o INNER JOIN order_detail od ON o.id = od.order_id
    WHERE o.order_at >= DATE_SUB(NOW(), INTERVAL 3 DAY)
      AND o.status = 'CONFIRMED' GROUP BY od.product_id 
      ORDER BY sum(od.quantity) DESC LIMIT 5;
  
    ```
    
- orders 전체 44만 건 데이터 중 최근 3일 내 가장 많이 판매 된 상품 5개 조회 → 약 1 초 소요
- orders 전체 100만 건 데이터 중 최근 3일 내 가장 많이 판매 된 상품 5개 조회 → 약 3.2 초 소요

---

### 📌 Decision (결정)

- 최근 3일 간 데이터를 조회 시 사용되는  order 테이블의 order_at 컬럼 인덱스 추가
- 최근 3일 주문 데이터를 기준으로 order_detail 테이블에서 조회 해야하기 때문에 order_detail 테이블의 order_id 컬럼 인덱스 추가, 최근 상품별 총 주문 개수 조회 시 사용되는  product_id 컬럼 인덱스 추가

---

### 📌 Consequences (결과)

- 주문 데이터 44만 건 쿼리 실행 시간 1초→ 0.375 개선
- 주문 데이터 100만건으로 증가후 기존 인덱스로는 오히려 8초로 조회 시간이 증가함. -> 완료된 주문인지 확인하는 status 컬럼 인덱스 추가 후 조회 시간 0.3초로 개선

---

### 📌 Appendix (첨부)

- `EXPLAIN ANALYZE` 결과 비교
- 인덱스 적용 전 '-> Limit: 5 row(s)  (actual time=1000..1000 rows=5 loops=1)\n    -> Sort: `sum(order_detail.quantity)` DESC, limit input to 5 row(s) per chunk  (actual time=1000..1000 rows=5 loops=1)\n        -> Table scan on <temporary>  (actual time=1000..1000 rows=100 loops=1)\n            -> Aggregate using temporary table  (actual time=1000..1000 rows=100 loops=1)\n                -> Nested loop inner join  (cost=197771 rows=48703) (actual time=0.89..990 rows=20207 loops=1)\n                    -> Filter: (order_detail.order_id is not null)  (cost=44342 rows=438368) (actual time=0.834..131 rows=440000 loops=1)\n                        -> Table scan on order_detail  (cost=44342 rows=438368) (actual time=0.832..110 rows=440000 loops=1)\n                    -> Filter: ((orders.`status` = \'CONFIRMED\') and (orders.order_at >= <cache>((now() - interval 3 day))))  (cost=0.25 rows=0.111) (actual time=0.00189..0.00189 rows=0.0459 loops=440000)\n                        -> Single-row index lookup on orders using PRIMARY (id=order_detail.order_id)  (cost=0.25 rows=1) (actual time=0.0017..0.00171 rows=0.617 loops=440000)\n'
- 인덱스 적용 후 '-> Limit: 5 row(s)  (actual time=370..370 rows=5 loops=1)\n    -> Sort: `sum(order_detail.quantity)` DESC, limit input to 5 row(s) per chunk  (actual time=370..370 rows=5 loops=1)\n        -> Table scan on <temporary>  (actual time=370..370 rows=100 loops=1)\n            -> Aggregate using temporary table  (actual time=370..370 rows=100 loops=1)\n                -> Nested loop inner join  (cost=55314 rows=54108) (actual time=8.93..361 rows=20233 loops=1)\n                    -> Filter: (orders.`status` = \'CONFIRMED\')  (cost=36376 rows=26945) (actual time=8.37..137 rows=29324 loops=1)\n                        -> Index range scan on orders using index_order over (\'2025-04-14 15:39:57.000000\' <= order_at), with index condition: (orders.order_at >= <cache>((now() - interval 3 day)))  (cost=36376 rows=80836) (actual time=8.36..131 rows=41748 loops=1)\n                    -> Index lookup on order_detail using index_detail (order_id=[orders.id](http://orders.id/))  (cost=0.502 rows=2.01) (actual time=0.00719..0.00749 rows=0.69 loops=29324)\n'

order 테이블 인덱스
![image](https://github.com/user-attachments/assets/65451c6c-caef-4ce3-bc68-e45057cdbed9)

order_detail 테이블 인덱스
![image](https://github.com/user-attachments/assets/536df6ce-0ce5-4adf-b6a9-1e51500c8e57)


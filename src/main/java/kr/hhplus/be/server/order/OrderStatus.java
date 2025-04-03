package kr.hhplus.be.server.order;

public enum OrderStatus {
    PENDING,   // 결제 대기 (주문은 생성되었지만 결제 전)
    CONFIRMED, // 결제 완료 (확정됨)
    CANCELED   // 주문 취소
}


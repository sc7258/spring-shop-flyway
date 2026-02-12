package com.sc7258.springshopflyway.service

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MockPaymentService {

    fun processPayment(amount: Int): String {
        // 20% 확률로 결제 실패 (랜덤 로직은 테스트 시 제어하기 어려우므로, 실제 구현에서는 제거하거나 조건부로 변경)
        // 여기서는 단순히 성공으로 가정하고, 테스트에서 Mocking으로 실패 케이스를 만듭니다.
        return UUID.randomUUID().toString()
    }

    fun cancelPayment(paymentId: String) {
        // 결제 취소 로직 (로그 출력 등)
        println("Payment cancelled: $paymentId")
    }
}

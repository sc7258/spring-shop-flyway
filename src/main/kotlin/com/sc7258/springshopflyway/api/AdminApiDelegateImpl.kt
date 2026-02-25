package com.sc7258.springshopflyway.api

import com.sc7258.springshopflyway.common.audit.AuditLog
import com.sc7258.springshopflyway.model.BookResponse
import com.sc7258.springshopflyway.model.CreateBookRequest
import com.sc7258.springshopflyway.model.MemberResponse
import com.sc7258.springshopflyway.model.OrderResponse
import com.sc7258.springshopflyway.model.UpdateBookRequest
import com.sc7258.springshopflyway.service.CatalogService
import com.sc7258.springshopflyway.service.MemberService
import com.sc7258.springshopflyway.service.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.net.URI

@Service
@PreAuthorize("hasRole('ADMIN')")
class AdminApiDelegateImpl(
    private val catalogService: CatalogService,
    private val memberService: MemberService,
    private val orderService: OrderService
) : AdminApiDelegate {

    @AuditLog(action = "CREATE_BOOK")
    override fun createBook(createBookRequest: CreateBookRequest): ResponseEntity<Unit> {
        val bookId = catalogService.createBook(createBookRequest)
        return ResponseEntity.created(URI.create("/api/v1/admin/books/$bookId")).build()
    }

    @AuditLog(action = "UPDATE_BOOK", targetIdArgName = "bookId")
    override fun updateBook(bookId: Long, updateBookRequest: UpdateBookRequest): ResponseEntity<BookResponse> {
        val response = catalogService.updateBook(bookId, updateBookRequest)
        return ResponseEntity.ok(response)
    }

    @AuditLog(action = "DELETE_BOOK", targetIdArgName = "bookId")
    override fun deleteBook(bookId: Long): ResponseEntity<Unit> {
        catalogService.deleteBook(bookId)
        return ResponseEntity.noContent().build()
    }

    @AuditLog(action = "VIEW_ALL_MEMBERS")
    override fun getAllMembers(): ResponseEntity<List<MemberResponse>> {
        val members = memberService.getAllMembers()
        return ResponseEntity.ok(members)
    }

    @AuditLog(action = "DELETE_MEMBER", targetIdArgName = "memberId")
    override fun deleteMember(memberId: Long): ResponseEntity<Unit> {
        memberService.deleteMember(memberId)
        return ResponseEntity.noContent().build()
    }

    @AuditLog(action = "VIEW_ALL_ORDERS")
    override fun getAllOrders(): ResponseEntity<List<OrderResponse>> {
        val orders = orderService.getAllOrders()
        return ResponseEntity.ok(orders)
    }

    @AuditLog(action = "CANCEL_ORDER", targetIdArgName = "orderId")
    override fun cancelOrderAdmin(orderId: Long): ResponseEntity<Unit> {
        orderService.cancelOrderByAdmin(orderId)
        return ResponseEntity.noContent().build()
    }
}

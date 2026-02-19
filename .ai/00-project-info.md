# Project Information

**Project Name:** spring-shop-flyway
**Group:** com.sc7258
**Version:** 0.2.0
**Description:** Online Bookstore Order & Delivery API Service

## Overview
이 프로젝트는 아마존(Amazon)과 유사한 **온라인 도서 주문 및 배송 서비스**를 제공하기 위한 백엔드 API 서버입니다.
사용자는 도서를 검색하고 장바구니에 담아 주문할 수 있으며, 이후 배송 상태를 추적할 수 있습니다.
Spring Boot와 Kotlin을 기반으로 하며, 대용량 트래픽을 고려한 확장성 있는 아키텍처를 지향합니다.

## Key Domains
- **Catalog:** 도서 정보 및 재고 관리
- **Member:** 사용자 인증 및 프로필 관리
- **Order:** 장바구니, 주문 생성 및 결제 처리
- **Delivery:** 배송 상태 추적 및 물류 관리

## Key Directories
- `src/main/kotlin`: Source code
- `src/main/resources`: Configuration files and resources
- `src/test/kotlin`: Tests

## User Preferences
- **Role:** Friend (친구처럼 편하게 대화)
- **Documentation:** Critical for Recovery (문서는 백업 및 복구 용도로 필수 유지)

## AI Rules & Workflow
Refer to **`.ai/03-rules.md`** for detailed workflow and behavioral guidelines.
- **Sync PRD First:** Always update `10-prd.md` before implementation.
- **Documentation:** Keep technical docs (`20-architecture.md`, etc.) up-to-date for recovery.

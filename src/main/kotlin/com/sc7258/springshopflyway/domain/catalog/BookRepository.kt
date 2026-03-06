package com.sc7258.springshopflyway.domain.catalog

import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Long>

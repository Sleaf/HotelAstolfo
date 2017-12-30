package com.enihsyou.astolfo.hotel.domain

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters
import java.time.LocalDateTime
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "COMMENT")
data class Comment(
    @Id @GeneratedValue
    var id: Int = 0,

    var body: String = "",

    @CreatedBy
    var userId: Int?= null,

    @CreatedDate
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter::class)
    var createdDate: LocalDateTime = LocalDateTime.now()
)

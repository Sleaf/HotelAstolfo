package com.enihsyou.astolfo.hotel.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.NaturalId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters
import java.time.LocalDateTime
import javax.persistence.AttributeConverter
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.Table


@Entity
@Table(name = "USER")
data class User(
    @Id
    @GeneratedValue
    var id: Int = 0,

    @NaturalId
    @Column(name = "phone_number", nullable = false)
    var phoneNumber: String = "",

    var nickname: String = "",

    @Column(nullable = false)
    @JsonIgnore
    var password: String = "",

    @CreatedDate
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter::class)
    val register_date: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Convert(converter = UserRoleConverter::class)
    var role: UserRole = UserRole.未注册,

    @ManyToMany(cascade = [CascadeType.ALL])
    var guests: MutableList<Guest> = mutableListOf()
) {
    enum class UserRole {
        经理, 前台, 注册用户, 未注册
    }

    class UserRoleConverter : AttributeConverter<UserRole, String> {
        override fun convertToEntityAttribute(dbData: String?): UserRole {
            return dbData?.let { UserRole.valueOf(dbData) } ?: UserRole.未注册
        }

        override fun convertToDatabaseColumn(attribute: UserRole?): String {
            return attribute.toString()
        }
    }

}

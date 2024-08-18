package com.teamhide.kream.user.domain.model

import com.teamhide.kream.common.config.database.BaseTimestampEntity
import com.teamhide.kream.user.domain.vo.Address
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "user")
data class User(
    @Column("email")
    val email: String,

    @Column("nickname")
    val nickname: String,

    @Column("password")
    val password: String,

    @Transient
    val address: Address,

    @Id
    val id: Long = 0L,
) : BaseTimestampEntity() {
    companion object {
        fun create(
            email: String,
            nickname: String,
            password1: String,
            password2: String,
            baseAddress: String,
            detailAddress: String,
        ): User {
            if (password1 != password2) {
                throw PasswordDoesNotMatchException()
            }
            return User(
                email = email,
                nickname = nickname,
                password = password1,
                address = Address(base = baseAddress, detail = detailAddress),
            )
        }
    }
}

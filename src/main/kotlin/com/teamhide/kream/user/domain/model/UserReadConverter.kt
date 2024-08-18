package com.teamhide.kream.user.domain.model

import com.teamhide.kream.user.domain.vo.Address
import io.r2dbc.spi.Row
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class UserReadConverter : Converter<Row, User> {
    override fun convert(source: Row): User {
        return User(
            id = source["id"] as Long,
            email = source["email"] as String,
            nickname = source["nickname"] as String,
            password = source["password"] as String,
            address = Address(
                base = source["base_address"] as String,
                detail = source["detail_address"] as String,
            ),
        )
    }
}

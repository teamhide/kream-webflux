package com.teamhide.kream.user.domain.model

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.r2dbc.mapping.OutboundRow
import org.springframework.r2dbc.core.Parameter

@WritingConverter
class UserWriteConverter : Converter<User, OutboundRow> {
    override fun convert(source: User): OutboundRow {
        return OutboundRow()
            .append("id", Parameter.from(source.id))
            .append("email", Parameter.from(source.email))
            .append("nickname", Parameter.from(source.nickname))
            .append("password", Parameter.from(source.password))
            .append("base_address", Parameter.from(source.address.base))
            .append("detail_address", Parameter.from(source.address.detail))
    }
}

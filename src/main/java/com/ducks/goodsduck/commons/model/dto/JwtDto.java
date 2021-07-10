package com.ducks.goodsduck.commons.model.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtDto {

    private Long id;

    public JwtDto(Long id) {
        this.id = id;
    }
}

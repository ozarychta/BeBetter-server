package com.ozarychta.bebetter.dto;

import com.ozarychta.bebetter.enums.SortType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSearchDTO {

    private String search;

    private SortType sortType;
}

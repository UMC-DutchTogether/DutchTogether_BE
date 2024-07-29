package com.umc.DutchTogether.domain.settlement.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class SingleSettlementCreateResponseDto {
    private Long settlementId;
    private String meetingName;
    private String bankName;
    private Long accountNumber;
    private String accountHolder;
    private int amount;
    private int participants;
}

package com.umc.DutchTogether.domain.settlement.service;

import com.umc.DutchTogether.domain.meeting.entity.Meeting;
import com.umc.DutchTogether.domain.meeting.repository.MeetingRepository;
import com.umc.DutchTogether.domain.payer.entity.Payer;
import com.umc.DutchTogether.domain.payer.repository.PayerRepository;
import com.umc.DutchTogether.domain.settlement.converter.SettlementConverter;
import com.umc.DutchTogether.domain.settlement.dto.SettlementRequest;
import com.umc.DutchTogether.domain.settlement.dto.SettlementResponse;
import com.umc.DutchTogether.domain.settlement.entity.Settlement;
import com.umc.DutchTogether.domain.settlement.repository.SettlementRepository;
import com.umc.DutchTogether.global.apiPayload.code.status.ErrorStatus;
import com.umc.DutchTogether.global.apiPayload.exception.handler.SettlementHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SettlementCommandServiceImpl implements SettlementCommandService {

    private final SettlementRepository settlementRepository;
    private final MeetingRepository meetingRepository;
    private final PayerRepository payerRepository;
    @Override

    public SettlementResponse.SettlementDTO CreateSingleSettlement(SettlementRequest.SettlementDTO request) {
        Optional<Meeting> meeting = meetingRepository.findById(request.getMeetingNum());

        // 결제자 저장
        Payer payer = Payer.builder()
                .name(request.getPayer())
                .accountNum(request.getAccountNumber())
                .bank(request.getBankName())
                .build();
        Settlement settlement = Settlement.builder()
                .meeting(meeting.get())
                .payer(payer)
                .totalAmount(request.getTotalAmount())
                .numPeople(request.getNumPeople())
                .build();

        payerRepository.save(payer);
        settlementRepository.save(settlement);

        return SettlementConverter.toSettlementDTO(settlement);
    }
}

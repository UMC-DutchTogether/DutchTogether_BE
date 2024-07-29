package com.umc.DutchTogether.domain.settlement.service;

import com.umc.DutchTogether.domain.meeting.entity.Meeting;
import com.umc.DutchTogether.domain.meeting.repository.MeetingRepository;
import com.umc.DutchTogether.domain.payer.entity.Payer;
import com.umc.DutchTogether.domain.payer.repository.PayerRepository;
import com.umc.DutchTogether.domain.settlement.dto.SingleSettlementCreateRequestDto;
import com.umc.DutchTogether.domain.settlement.dto.SingleSettlementCreateResponseDto;
import com.umc.DutchTogether.domain.settlement.dto.SingleSettlementInfoResponseDto;
import com.umc.DutchTogether.domain.settlement.entity.Settlement;
import com.umc.DutchTogether.domain.settlement.repository.SettlementRepository;
import com.umc.DutchTogether.global.apiPayload.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final MeetingRepository meetingRepository;
    private final PayerRepository payerRepository;

    public SettlementService(SettlementRepository settlementRepository, MeetingRepository meetingRepository, PayerRepository payerRepository) {
        this.settlementRepository = settlementRepository;
        this.meetingRepository = meetingRepository;
        this.payerRepository = payerRepository;
    }


    // 정산 생성
    @Transactional
    public SingleSettlementCreateResponseDto createSettlement(SingleSettlementCreateRequestDto request) {
        Meeting meeting = meetingRepository.findById(request.getMeeting_num())
                .orElseThrow(() -> new ResourceNotFoundException("Meeting not found"));

        Payer payer = Payer.builder()
                .name(request.getAccountHolder())
                .accountNum(request.getAccountNumber())
                .bank(request.getBankName())
                .build();

        payerRepository.save(payer);

        Settlement settlement = Settlement.builder()
                .meeting(meeting)
                .payer(payer)
                .totalAmount(request.getTotalAmount())
                .numPeople(request.getParticipants())
                .build();

        settlementRepository.save(settlement);

        /**
         * 리다이렉트 사용 가능할 경우 -> url로 데이터를 전달해서 리다이렉트로 데이터 활용
         * 리다이렉트 사용 불가능할 경우 -> 조회
         */
        return SingleSettlementCreateResponseDto.builder()
                .meetingName(meeting.getName())
                .bankName(payer.getBank())
                .accountHolder(payer.getName())
                .accountNumber(payer.getAccountNum())
                .amount(settlement.getTotalAmount())
                .participants(settlement.getNumPeople())
                .settlementId(settlement.getId())
                .build();

    }

    public SingleSettlementInfoResponseDto getSingleSettlementInfo(Long settlementId) {

        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new ResourceNotFoundException("Settlement not found"));

        Meeting meeting = meetingRepository.findById(settlement.getMeeting().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Meeting not found"));

        Payer payer = payerRepository.findById(settlement.getPayer().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Payer not found"));

        return SingleSettlementInfoResponseDto.builder()
                .meetingName(meeting.getName())
                .payer(payer.getName())
                .bank(payer.getBank())
                .account_num(payer.getAccountNum())
                .total_amount(settlement.getTotalAmount())
                .participants(settlement.getNumPeople())
                .build();
    }


}

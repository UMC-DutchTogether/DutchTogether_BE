package com.umc.DutchTogether.domain.meeting.service;

import com.umc.DutchTogether.domain.meeting.converter.MeetingConverter;
import com.umc.DutchTogether.domain.meeting.dto.MeetingResponse;
import com.umc.DutchTogether.domain.meeting.entity.Meeting;
import com.umc.DutchTogether.domain.meeting.repository.MeetingRepository;
import com.umc.DutchTogether.domain.payer.entity.Payer;
import com.umc.DutchTogether.domain.payer.repository.PayerRepository;
import com.umc.DutchTogether.domain.receipt.entity.Receipt;
import com.umc.DutchTogether.domain.receipt.repository.ReceiptRepository;
import com.umc.DutchTogether.domain.settlement.entity.Settlement;
import com.umc.DutchTogether.domain.settlement.repository.SettlementRepository;
import com.umc.DutchTogether.global.apiPayload.exception.handler.MeetingHandler;
import com.umc.DutchTogether.global.apiPayload.exception.handler.PayerHandler;
import com.umc.DutchTogether.global.apiPayload.exception.handler.SettlerHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.umc.DutchTogether.global.apiPayload.code.status.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingQueryServiceImpl implements MeetingQueryService{

    private final MeetingRepository meetingRepository;
    private final SettlementRepository settlementRepository;
    private final PayerRepository payerRepository;
    private final ReceiptRepository receiptRepository;

    @Override
    public Meeting getMeeting(Long meetingNum) {
        return meetingRepository.findById(meetingNum)
                .orElseThrow(() -> new MeetingHandler(MEETING_NOT_FOUND));
    }

    @Override
    public MeetingResponse.SingleSettlementResultDTO getSingleSettlement(String link) {
        Meeting meeting = meetingRepository.findByLink("" + link).orElse(null);
        Settlement settlement = settlementRepository.findByMeetingId(meeting.getId()).orElse(null);
        Payer payer = payerRepository.findById(settlement.getPayer().getId()).orElse(null);
        Receipt receipt = null;
        if(settlement.getReceipt()!=null) {
            receiptRepository.findById(settlement.getReceipt().getId()).orElse(null);
        }
        return MeetingConverter.toSingleSettlementResultDTO(meeting, settlement, payer, receipt);
    }

    @Override
    public MeetingResponse.MeetingInfoResultDTO getMeetingInfo(Long meetingNum) {
        Meeting meeting = meetingRepository.findById(meetingNum).orElseThrow(()->new MeetingHandler(MEETING_NOT_FOUND));
        Settlement settlement = settlementRepository.findByMeetingId(meetingNum).orElseThrow(()->new SettlerHandler(SETTLEMENT_NOT_FOUND_BY_MEETING));
        Payer payer = payerRepository.findById(settlement.getPayer().getId()).orElseThrow(()->new PayerHandler(PAYER_LIST_NOT_FOUND));
        // 컨버터 작성
        return MeetingConverter.toMeetingInfoResultDTO(meeting, settlement, payer);

    }
}

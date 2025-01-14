package com.umc.DutchTogether.domain.meeting.service;

import com.umc.DutchTogether.domain.meeting.converter.MeetingConverter;
import com.umc.DutchTogether.domain.meeting.dto.MeetingRequest;
import com.umc.DutchTogether.domain.meeting.entity.Meeting;
import com.umc.DutchTogether.domain.meeting.repository.MeetingRepository;
import com.umc.DutchTogether.global.apiPayload.code.status.ErrorStatus;
import com.umc.DutchTogether.global.apiPayload.exception.handler.MeetingHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class MeetingCommandServiceImpl implements MeetingCommandService{

    private final MeetingRepository meetingRepository;

    @Override
    public Meeting createMeeting(MeetingRequest.MeetingDT0 request) {
        String meetingId = request.getMeetingId();
        String password = request.getPassword();
        if( (meetingId == null || meetingId.isEmpty() )&& (password == null || password.isEmpty())) {
            return null;
        }
        Meeting newMeeting = MeetingConverter.toMeetingDTO(request);

        String generatedLink = generateLink();
        newMeeting.setLink(generatedLink);

        return meetingRepository.save(newMeeting);
    }

    @Override
    public Meeting updateMeetingName(MeetingRequest.PutMeetingNameDT0 request) {
        Meeting existingMeeting = meetingRepository.findById(request.getMeetingNum())
                .orElse(null);

        if (existingMeeting == null) {
            existingMeeting = Meeting.builder()
                    .name(request.getMeetingName())
                    .link(generateLink())
                    .build();
        } else {
            // Meeting이 있을 경우 이름만 업데이트
            existingMeeting.setName(request.getMeetingName());
        }

        return meetingRepository.save(existingMeeting);
    }

    private String generateLink() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}


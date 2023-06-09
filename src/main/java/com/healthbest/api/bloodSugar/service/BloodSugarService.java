package com.healthbest.api.bloodSugar.service;

import com.healthbest.api.bloodSugar.domain.BloodSugar;
import com.healthbest.api.bloodSugar.dto.BloodSugarRequest;
import com.healthbest.api.bloodSugar.repository.BloodSugarRepository;
import com.healthbest.api.common.dto.TimeDto;
import com.healthbest.api.common.utils.TimeUtil;
import com.healthbest.api.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.healthbest.api.bloodSugar.dto.BloodSugarResponse.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BloodSugarService {

    private final BloodSugarRepository bloodSugarRepository;

    @Transactional
    public Create create(User user, BloodSugarRequest.Create request) {
        LocalDateTime date = TimeUtil.toLocalDateTime(request.getTime());
        BloodSugar bloodSugar = bloodSugarRepository.save(BloodSugar.builder()
                .date(date)
                .sugar(request.getSugar())
                .mealTime(request.getMealTime())
                .mealType(request.getMealType())
                .user(user)
                .build()
        );

        return new Create(bloodSugar.getId());
    }

    @Transactional(readOnly = true)
    public BloodSugarInfos findByDate(User user, int year, int month, int day) {
        LocalDateTime startedTime = TimeUtil.startedTime(new TimeDto.Date(year, month, day));
        LocalDateTime endTime = TimeUtil.endTime(new TimeDto.Date(year, month, day));

        List<BloodSugarInfo> infos = bloodSugarRepository.findBloodSugarByDate(user, startedTime, endTime)
                .stream()
                .map(this::toDto)
                .toList();

        return new BloodSugarInfos(infos);
    }

    private BloodSugarInfo toDto(BloodSugar bloodSugar) {
        return new BloodSugarInfo(
                bloodSugar.getMealType().name(),
                bloodSugar.getMealTime().name(),
                bloodSugar.getDate(),
                bloodSugar.getSugar()
        );
    }
}

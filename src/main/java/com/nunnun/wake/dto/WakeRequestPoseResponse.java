package com.nunnun.wake.dto;

import com.nunnun.wake.entity.DailyPose;
import com.nunnun.wake.entity.Pose;
import com.nunnun.wake.entity.WakeRequest;
import java.time.LocalDate;

public record WakeRequestPoseResponse(LocalDate date, String code, String description) {

    public static WakeRequestPoseResponse from(DailyPose dailyPose) {
        return new WakeRequestPoseResponse(
                dailyPose.getPoseDate(),
                dailyPose.getPose().getCode(),
                dailyPose.getPose().getDescription()
        );
    }

    public static WakeRequestPoseResponse from(WakeRequest request, DailyPose legacyDailyPose) {
        Pose pose = request.getPose() != null ? request.getPose() : legacyDailyPose.getPose();
        return new WakeRequestPoseResponse(
                request.getRequestedAt().toLocalDate(),
                pose.getCode(),
                pose.getDescription()
        );
    }
}

package com.nunnun.wake.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateWakeGroupResponse(
        Long id,
        String name,
        @JsonProperty("invite_code") String inviteCode,
        Short capacity,
        @JsonProperty("current_members") long currentMembers
) {
}

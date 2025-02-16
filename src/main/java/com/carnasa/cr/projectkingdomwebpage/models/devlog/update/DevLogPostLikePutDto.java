package com.carnasa.cr.projectkingdomwebpage.models.devlog.update;

import java.util.UUID;

public class DevLogPostLikePutDto {
    UUID userId;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}

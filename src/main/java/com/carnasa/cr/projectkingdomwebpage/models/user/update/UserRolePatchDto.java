package com.carnasa.cr.projectkingdomwebpage.models.user.update;

public class UserRolePatchDto {

    private Boolean isAdmin;
    private Boolean isDeveloper;
    private Boolean isModerator;

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getDeveloper() {
        return isDeveloper;
    }

    public void setDeveloper(Boolean developer) {
        isDeveloper = developer;
    }

    public Boolean getModerator() {
        return isModerator;
    }

    public void setModerator(Boolean moderator) {
        isModerator = moderator;
    }
}

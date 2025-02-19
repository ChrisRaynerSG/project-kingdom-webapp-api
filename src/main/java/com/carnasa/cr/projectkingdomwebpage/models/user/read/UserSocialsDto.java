package com.carnasa.cr.projectkingdomwebpage.models.user.read;

public class UserSocialsDto {

    private String facebookUrl;
    private String youtubeUrl;
    private String instagramUrl;
    private String xUrl;
    private String tikTokUrl;
    private String steamUrl;
    private String discordName;

    public UserSocialsDto(
            String facebookUrl,
            String youtubeUrl,
            String instagramUrl,
            String xUrl,
            String tikTokUrl,
            String steamUrl,
            String discordName) {

        this.facebookUrl = facebookUrl;
        this.youtubeUrl = youtubeUrl;
        this.instagramUrl = instagramUrl;
        this.xUrl = xUrl;
        this.tikTokUrl = tikTokUrl;
        this.steamUrl = steamUrl;
        this.discordName = discordName;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public String getxUrl() {
        return xUrl;
    }

    public void setxUrl(String xUrl) {
        this.xUrl = xUrl;
    }

    public String getTikTokUrl() {
        return tikTokUrl;
    }

    public void setTikTokUrl(String tikTokUrl) {
        this.tikTokUrl = tikTokUrl;
    }

    public String getSteamUrl() {
        return steamUrl;
    }

    public void setSteamUrl(String steamUrl) {
        this.steamUrl = steamUrl;
    }

    public String getDiscordName() {
        return discordName;
    }

    public void setDiscordName(String discordName) {
        this.discordName = discordName;
    }
}

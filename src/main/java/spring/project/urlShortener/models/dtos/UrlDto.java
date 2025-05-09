package spring.project.urlShortener.models.dtos;

public class UrlDto {
    private String longUrl;
    private String customUrlString;

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getCustomUrlString() {
        return customUrlString;
    }

    public void setCustomUrlString(String customUrlSTring) {
        this.customUrlString = customUrlSTring;
    }

}

package spring.project.urlShortener.config;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class URLValidator {

    public boolean isValidUrl(String url){
        if (url.trim().isEmpty() || url==null) {
            return false;
        }
        try {
            URL validUrl = new URL(url);
            validUrl.toURI(); // validates url syntax

            HttpURLConnection huc = (HttpURLConnection) validUrl.openConnection();
            huc.setRequestMethod("HEAD");
            huc.setConnectTimeout(3000);
            huc.setReadTimeout(3000);
            int responseCode = huc.getResponseCode();

            return (responseCode >= 200 && responseCode < 400);
        } catch (Exception e) {
            return false;
        }
    }
}



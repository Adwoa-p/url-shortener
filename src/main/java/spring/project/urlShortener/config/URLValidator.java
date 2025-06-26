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
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}



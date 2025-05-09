package spring.project.urlShortener.config;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class StringConfig {

    public static String generateString(){

        SecureRandom secureRandom = new SecureRandom();

        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int randomInt = secureRandom.nextInt();

        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < 8; i++){
            stringBuilder.append(characters.charAt(Math.abs(randomInt)% characters.length()));
            randomInt /= characters.length();
        }

        return stringBuilder.toString();
    }

}

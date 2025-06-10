package spring.project.urlShortener.config;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class StringGenerator {

    public String generateString(){

        SecureRandom secureRandom = new SecureRandom();

        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < 8; i++){
            int randomInt = secureRandom.nextInt(characters.length());
            stringBuilder.append(characters.charAt(randomInt));
        }

        return stringBuilder.toString();
    }

}

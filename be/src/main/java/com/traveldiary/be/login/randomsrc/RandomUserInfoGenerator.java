package com.traveldiary.be.login.randomsrc;

import java.util.List;
import java.util.Random;

public class RandomUserInfoGenerator {
    private static final List<String> NICKNAMES = List.of(
            "대머리 여행가", "풍성한 여행가","게으른 여행가", "부지런한 여행가", "졸린 여행가","행복한 여행가","유머러스한 여행가","키다리 여행가","슬픈 여행가","강한 여행가","대단한 여행가","지친 여행가","똑똑한 여행가"
    );

    private static final List<String> PROFILE_IMAGES = List.of(
            "/images/1.png", "/images/2.png", "/images/3.png"
    );

    private static final Random RANDOM = new Random();

    public static String getRandomNickname() {
        return NICKNAMES.get(RANDOM.nextInt(NICKNAMES.size()));
    }

    public static String getRandomProfileImage() {
        return PROFILE_IMAGES.get(RANDOM.nextInt(PROFILE_IMAGES.size()));
    }
}

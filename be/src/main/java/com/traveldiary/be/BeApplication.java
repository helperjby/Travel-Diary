package com.traveldiary.be;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BeApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        // Set system properties
        System.setProperty("NAVER_CLIENT_ID", dotenv.get("NAVER_CLIENT_ID"));
        System.setProperty("NAVER_CLIENT_SECRET", dotenv.get("NAVER_CLIENT_SECRET"));
        System.setProperty("GOOGLE_CLIENT_ID", dotenv.get("GOOGLE_CLIENT_ID"));
        System.setProperty("GOOGLE_CLIENT_SECRET", dotenv.get("GOOGLE_CLIENT_SECRET"));
        System.setProperty("KAKAO_CLIENT_ID", dotenv.get("KAKAO_CLIENT_ID"));
        System.setProperty("KAKAO_CLIENT_SECRET", dotenv.get("KAKAO_CLIENT_SECRET"));
        System.setProperty("DATASOURCE_URL", dotenv.get("DATASOURCE_URL"));
        System.setProperty("DATASOURCE_USERNAME", dotenv.get("DATASOURCE_USERNAME"));
        System.setProperty("DATASOURCE_PASSWORD", dotenv.get("DATASOURCE_PASSWORD"));

        // Set AWS system properties
        System.setProperty("AWS_S3_BUCKET_NAME", dotenv.get("AWS_S3_BUCKET_NAME"));
        System.setProperty("AWS_S3_UPLOAD_DIR", dotenv.get("AWS_S3_UPLOAD_DIR"));
        System.setProperty("REPRESENTATIVE_IMAGE_URL", dotenv.get("REPRESENTATIVE_IMAGE_URL"));
        System.setProperty("CLOUD_AWS_CREDENTIALS_ACCESS_KEY", dotenv.get("CLOUD_AWS_CREDENTIALS_ACCESS_KEY"));
        System.setProperty("CLOUD_AWS_CREDENTIALS_SECRET_KEY", dotenv.get("CLOUD_AWS_CREDENTIALS_SECRET_KEY"));
        System.setProperty("CLOUD_AWS_REGION_STATIC_S3", dotenv.get("CLOUD_AWS_REGION_STATIC_S3"));

        SpringApplication.run(BeApplication.class, args);
    }

}

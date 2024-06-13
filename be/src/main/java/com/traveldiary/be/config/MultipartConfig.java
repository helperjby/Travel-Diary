//package com.traveldiary.be.config;
//
//import jakarta.servlet.MultipartConfigElement;
//import org.springframework.boot.web.servlet.MultipartConfigFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.unit.DataSize;
//import org.springframework.web.multipart.MultipartResolver;
//import org.springframework.web.multipart.support.StandardServletMultipartResolver;
//
//import java.io.File;
//
//@Configuration
//public class MultipartConfig {
//
//    //MultipartResolver 빈을 생성하여 멀티파트 요청을 처리할 수 있도록 설정
//    @Bean
//    public MultipartResolver multipartResolver() {
//        return new StandardServletMultipartResolver();
//    }
//
//    //멀티파트 파일 업로드 설정을 구성하는 MultipartConfigElement 빈을 생성
//    @Bean
//    public MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//
//        // 파일이 업로드될 경로를 설정
//        String location = "C:/Users/NT551/Desktop/Travel-Diary/Travel-Diary/be/uploads/temp";
//        factory.setLocation(location);
//
//        //최대 요청 크기(메가바이트 단위) : 10MB
//        factory.setMaxRequestSize(DataSize.ofMegabytes(10L));
//        factory.setMaxFileSize(DataSize.ofMegabytes(10L));
//
//        File uploadDirectory = new File(location);
//        if (!uploadDirectory.exists()) {
//            uploadDirectory.mkdirs(); // 디렉토리가 존재하지 않으면 생성
//        }
//
//        return factory.createMultipartConfig();
//    }
//}

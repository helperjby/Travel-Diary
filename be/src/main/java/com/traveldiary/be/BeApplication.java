package com.traveldiary.be;

<<<<<<< HEAD
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class BeApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        System.setProperty("DATASOURCE_URL", dotenv.get("DATASOURCE_URL"));
        System.setProperty("DATASOURCE_USERNAME", dotenv.get("DATASOURCE_USERNAME"));
        System.setProperty("DATASOURCE_PASSWORD", dotenv.get("DATASOURCE_PASSWORD"));

        SpringApplication.run(BeApplication.class, args);
    }
}




//package com.traveldiary.be;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class BeApplication {
//    public static void main(String[] args) {
//        SpringApplication.run(BeApplication.class, args);
//    }
//}
=======
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BeApplication {
    public static void main(String[] args) {
        SpringApplication.run(BeApplication.class, args);
    }
}
>>>>>>> a2538ef5a02415223ec2a562b56d0f18b1e1be09

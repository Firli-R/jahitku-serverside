package co.id.jahitku.serverside;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JahitkuServersideApplication {

    public static void main(String[] args) {
        SpringApplication.run(JahitkuServersideApplication.class, args);
        System.out.println("Serverside Running");
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

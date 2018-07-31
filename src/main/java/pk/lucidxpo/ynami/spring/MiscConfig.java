package pk.lucidxpo.ynami.spring;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MiscConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
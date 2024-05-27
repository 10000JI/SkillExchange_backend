package place.skillexchange.backend;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class WebRestController {
    private final Environment env;

    @GetMapping("/profile")
    public String getProfile(){
        //ex> set1, set2
        return Arrays.stream(env.getActiveProfiles()).findFirst().orElse("");
    }
}
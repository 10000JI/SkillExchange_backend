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
    public String getProfile() {
        return Arrays.stream(env.getActiveProfiles())
                .skip(1) // 첫번째 profile 스킵
                .findFirst() // 이후 나온 profile 적용 (ex> set1, set2)
                .orElse(""); // pollscm 테스트용 주석 2
    }
}

package place.skillexchange.backend.common.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordGeneratorUtil {
    private static final String LOWERCASE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+";
    private static final SecureRandom random = new SecureRandom();

    public String generatePassword() {
        StringBuilder password = new StringBuilder();
        // 각 문자 카테고리에서 무작위로 하나씩 선택
        password.append(LOWERCASE_CHARACTERS.charAt(random.nextInt(LOWERCASE_CHARACTERS.length())));
        password.append(UPPERCASE_CHARACTERS.charAt(random.nextInt(UPPERCASE_CHARACTERS.length())));
        password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        // 나머지 길이는 4자 이상으로 설정하여 총 길이가 8 이상이 되도록 함
        int remainingLength = random.nextInt(5) + 4; // 4~8
        // 나머지 길이만큼 나머지 문자를 무작위로 추가
        for (int i = 0; i < remainingLength; i++) {
            int categoryIndex = random.nextInt(4); // 모든 카테고리 중 하나를 선택
            switch (categoryIndex) {
                case 0:
                    password.append(LOWERCASE_CHARACTERS.charAt(random.nextInt(LOWERCASE_CHARACTERS.length())));
                    break;
                case 1:
                    password.append(UPPERCASE_CHARACTERS.charAt(random.nextInt(UPPERCASE_CHARACTERS.length())));
                    break;
                case 2:
                    password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
                    break;
                case 3:
                    password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));
                    break;
                default:
                    // 여기에는 도달하지 않아야 함
            }
        }

        // 비밀번호를 무작위로 섞음
        for (int i = password.length() - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = password.charAt(index);
            password.setCharAt(index, password.charAt(i));
            password.setCharAt(i, temp);
        }

        return password.toString();
    }
}

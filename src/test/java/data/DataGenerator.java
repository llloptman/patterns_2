package data;


import com.github.javafaker.Faker;
import lombok.Data;
import lombok.Value;

import java.util.Locale;

public class DataGenerator {
    private DataGenerator() {
    }

    /**
     * Return a login
     */
    public static String generateLogin(String locale) {

        Faker faker = new Faker(new Locale(locale));
        String login = faker.name().firstName();
        return login;
    }

    /**
     * Return a password
     */
    public static String generatePassword(String locale) {

        Faker faker = new Faker(new Locale(locale));
        String password = faker.bothify("?????##");
        return password;
    }

    /**
     * Class for registration of new Users (Active/Blocked)
     */
    public static class Registration {
        private Registration() {
        }

        public static UserInfo generateActiveUser(String locale) {
            UserInfo user = new UserInfo(generateLogin(locale),
                    generatePassword(locale),
                    "active");

            return user;
        }

        public static UserInfo generateBlockedUser(String locale) {
            UserInfo user = new UserInfo(generateLogin(locale),
                    generatePassword(locale),
                    "blocked");

            return user;
        }
        public static UserInfo RegistrationDto(String login, String password, String status){
            UserInfo user = new UserInfo(login,password,status);

            return user;
        }

    }

    @Data
    @Value
    public static class UserInfo {
        String login;
        String password;
        String status;

        public UserInfo(String login, String password, String status) {
            this.login = login;
            this.password = password;
            this.status = status;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public String getStatus() {
            return status;
        }
    }
}
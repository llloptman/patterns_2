package data;


import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Data;
import lombok.Value;

import java.util.Locale;

import static io.restassured.RestAssured.given;

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
    }

    public static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static void addUserInfo(DataGenerator.UserInfo userInfo) {
        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(userInfo) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }
}
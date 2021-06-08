import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import data.DataGenerator;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static data.DataGenerator.generatePassword;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

class AuthorizationTest {
    static DataGenerator.UserInfo userInfo = DataGenerator.Registration.generateActiveUser("en-EN");

    public void login(DataGenerator.UserInfo userInfo) {
        $("[data-test-id='login'] input").setValue(userInfo.getLogin());
        $("[data-test-id='password'] input").setValue(userInfo.getPassword());
        $("[data-test-id='action-login']").click();
    }

    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    static void addUserInfo(DataGenerator.UserInfo userInfo) {
        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(userInfo) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }
    static void addUserInfoStrings(String login,String password,String status) {
        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(login + password + status) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }// TODO: 08.06.2021  f

    @BeforeAll
    static void setUpAll() {
        // сам запрос
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .body(userInfo) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK
    }

    @BeforeEach
    void init() {
        open("http://localhost:9999/");
    }


    @Test
    public void successLogin() {
        login(userInfo);
        $("h2").shouldHave(Condition.text("Личный кабинет"));
    }

    @Test
    public void emptyLogin() {
        $("[data-test-id='password'] input").setValue(userInfo.getPassword());
        $("[data-test-id='action-login']").click();
        $("[data-test-id='login'] .input__sub").shouldBe(Condition.text("Поле обязательно для заполнения"));
    }

    @Test
    public void emptyPassword() {
        $("[data-test-id='login'] input").setValue(userInfo.getLogin());
        $("[data-test-id='action-login']").click();
        $("[data-test-id='password'] .input__sub").shouldBe(Condition.text("Поле обязательно для заполнения"));
    }

    @Test
    public void emptyForm() {
        $("[data-test-id='action-login']").click();
        $("[data-test-id='login'] .input__sub").shouldBe(Condition.text("Поле обязательно для заполнения"));
        $("[data-test-id='password'] .input__sub").shouldBe(Condition.text("Поле обязательно для заполнения"));
    }

    @Test
    public void failLoginNoSuchUser() {
        DataGenerator.UserInfo userData = DataGenerator.Registration.generateActiveUser("en-EN");
        login(userData);
        $("[data-test-id='error-notification'] .notification__content").shouldBe(Condition.visible);
        $("[data-test-id='error-notification'] .notification__content").shouldHave(Condition.
                text("Неверно указан логин или пароль"));

    }

    @Test
    public void failLoginWrongPassword() {
        DataGenerator.UserInfo userData = DataGenerator.Registration.generateActiveUser("en-EN");
        $("[data-test-id='login'] input").setValue(userInfo.getLogin());
        $("[data-test-id='password'] input").setValue(userData.getPassword());
        $("[data-test-id='action-login']").click();

        $("[data-test-id='error-notification'] .notification__content").shouldBe(Condition.visible);
        $("[data-test-id='error-notification'] .notification__content").shouldHave(Condition.
                text("Неверно указан логин или пароль"));
    }

    @Test
    public void failLoginBlockedStatus() {
        DataGenerator.UserInfo userData = DataGenerator.Registration.generateBlockedUser("en-EN");
        addUserInfo(userData);
        $("[data-test-id='login'] input").setValue(userData.getLogin());
        $("[data-test-id='password'] input").setValue(userData.getPassword());
        $("[data-test-id='action-login']").click();

        $("[data-test-id='error-notification'] .notification__content").shouldBe(Condition.visible);
        $("[data-test-id='error-notification'] .notification__content").shouldHave(Condition.
                text("Пользователь заблокирован"));
    }

    @Test
    public void successLoginAfterPasswordChange(){
DataGenerator.UserInfo updateUser = DataGenerator.Registration.RegistrationDto(userInfo.getLogin(),
        DataGenerator.generatePassword("en-EN"),userInfo.getStatus());

addUserInfo(updateUser);

        $("[data-test-id='login'] input").setValue(updateUser.getLogin());
        $("[data-test-id='password'] input").setValue(updateUser.getPassword());
        $("[data-test-id='action-login']").click();

        $("h2").shouldHave(Condition.text("Личный кабинет"));


    }

}



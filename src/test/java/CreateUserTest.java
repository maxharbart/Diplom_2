import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import user.User;
import user.UserClient;
import user.UserToken;

import static org.hamcrest.Matchers.equalTo;

@Epic("Регистрация юзера")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CreateUserTest {
    public static String userAccessToken;

    @Test
    @DisplayName("Тест регистрации юзера")
    public void newUserRegistrationTest() {
        Response response = UserClient.sendPostRequestToUserRegistration(User.getRandomUser());
        response.then().assertThat().statusCode(200).assertThat().body("success", equalTo(true));
        userAccessToken = response.then().extract().body().path("accessToken").toString().split(" ")[1];
        Assert.assertNotNull(userAccessToken);
    }

    @Test
    @DisplayName("Тест регистрации существующего юзера")
    public void existUserRegistrationTest() {
        UserClient.sendPostRequestToUserRegistration(User.existingUser()).then()
                .assertThat().statusCode(403)
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Тест регистрации юзера без пароля (= null)")
    public void nullPasswordUserRegistrationTest() {
        UserClient.sendPostRequestToUserRegistration(User.getUserWithNullPassword()).then()
                .assertThat().statusCode(403)
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Тест регистрации юзера без email (= null)")
    public void nullLoginUserRegistrationTest() {
        UserClient.sendPostRequestToUserRegistration(User.getUserWithNullEmail()).then()
                .assertThat().statusCode(403)
                .assertThat().body("success", equalTo(false))
                .assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Тест восстановления и сброса пароля")
    public void passwordResetTest() {
        UserClient.sendPostRequestToPasswordReset(User.getUserToSuccessLogin()).then()
                .assertThat().statusCode(200)
                .assertThat().body("success", equalTo(true))
                .assertThat().body("message", equalTo("Reset email sent"));
    }

    @After
    public void tearDown(){
        if (userAccessToken != null)
            UserClient.sendPostRequestToUserDelete(new UserToken(userAccessToken));
    }
}

package user;
import org.apache.commons.lang3.RandomStringUtils;

public class User {
    private String email;
    private String password;
    private String name;

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static User getRandomUser() {
        return new User(
                RandomStringUtils.randomAlphabetic(5, 10).toLowerCase() + "@example.com",
                "password1",
                "Max"
        );
    }

    public static User getUserWithNullEmail() {
        return new User(
                null,
                "password1",
                "Max"
        );
    }

    public static User getUserWithNullPassword() {
        return new User(
                "maxharbart@yandex.ru",
                null,
                RandomStringUtils.randomAlphabetic(7).toLowerCase()
        );
    }

//    public static User getUserWhichExist() {
//        return new User(
//                "maxharbart@yandex.ru",
//                "password1",
//                "Max"
//        );
//    }

    public static User getUserToSuccessLogin() {
        return new User(
                "maxharbart1@yandex.ru",
                "password1",
                "Max"
        );
    }

    public static User existingUser() {
        return new User(
                "maxharbart1@yandex.ru",
                "password1",
                "Max"
        );
    }

    public static User getUserWithEmptyLogin() {
        return new User(
                "",
                "password1",
                "Max"
        );
    }

    public static User getUserWithEmptyPassword() {
        return new User(
                "maxharbart@yandex.ru",
                "",
                "Max"
        );
    }

    public static User getUserWithInvalidLogin() {
        return new User(
                "maxharbart000@yandex.ru",
                "12345",
                "Max"
        );
    }

    public static User getUserWithInvalidPassword() {
        return new User(
                "maxharbart@yandex.ru",
                "qwertyui",
                "Max"
        );
    }

    @Override
    public String toString() {
        return "[User] Email: " + this.email + "; Password: " + this.password + "; Name: " + this.name + ";";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

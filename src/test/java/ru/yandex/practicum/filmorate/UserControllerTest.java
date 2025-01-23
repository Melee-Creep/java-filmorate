//package ru.yandex.practicum.filmorate;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.controller.UserController;
//import ru.yandex.practicum.filmorate.exception.NotFoundException;
//import ru.yandex.practicum.filmorate.exception.ValidationException;
//import ru.yandex.practicum.filmorate.model.User;
//
//import java.time.LocalDate;
//
//public class UserControllerTest {
//    UserController userController = new UserController();
//
//    @DisplayName("Добавления пользователя")
//    @Test
//    void createUser() {
//        User user = new User(1L, "email@email.ru", "login", "name",
//                LocalDate.of(2020,10,10));
//        userController.create(user);
//        Assertions.assertEquals(1, userController.findAll().size());
//    }
//
//    @DisplayName("Добавлнение пользователя с пустой почтой")
//    @Test
//    void createUserWithEmptyEmail() {
//        User user = new User(1L, " ", "login", "name",
//                LocalDate.of(2020,10,10));
//        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
//    }
//
//    @DisplayName("Добавлнение пользователя с почтой без собаки")
//    @Test
//    void createUserWithoutDog() {
//        User user = new User(1L, "email_email.ru", "login", "name",
//                LocalDate.of(2020,10,10));
//        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
//    }
//
//    @DisplayName("Добавления пользователя c пустым логином")
//    @Test
//    void createUserWithEmptyLogin() {
//        User user = new User(1L, "email@email.ru", " ", "name",
//                LocalDate.of(2020,10,10));
//        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
//    }
//
//    @DisplayName("Добавления пользователя с пробелами в логине")
//    @Test
//    void createUserWithSpaceInLogin() {
//        User user = new User(1L, "email@email.ru", "log in", "name",
//                LocalDate.of(2020, 10, 10));
//        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
//    }
//
//    @DisplayName("Добавления пользователя с датой рождения в будущем")
//    @Test
//    void createUserWithBirthdayInFuture() {
//        User user = new User(1L, "email@email.ru", "login", "name",
//                LocalDate.of(2100, 10, 10));
//        Assertions.assertThrows(ValidationException.class, () -> userController.create(user));
//    }
//
//    @DisplayName("Обновлять пользователя")
//    @Test
//    void updateUser() {
//        User user = new User(1L, "email@email.ru", "login", "name",
//                LocalDate.of(2020,10,10));
//        User newUser = new User(1L, "New@email.ru", "NewLogin", "NewName",
//                LocalDate.of(2020,10,10));
//        userController.create(user);
//        userController.update(newUser);
//        Assertions.assertEquals(newUser.getName(), userController.findAll().stream().toList().getFirst().getName());
//    }
//
//    @DisplayName("Обновление пользователя с пустой почтой")
//    @Test
//    void updateUserWithEmptyEmail() {
//        User user = new User(1L, "email@email.ru", "login", "name",
//                LocalDate.of(2020, 10, 10));
//        User newUser = new User(1L, " ", "NewLogin", "NewName",
//                LocalDate.of(2020, 10, 10));
//        userController.create(user);
//        Assertions.assertThrows(ValidationException.class, () -> userController.update(newUser));
//    }
//
//    @DisplayName("Обновление пользователя с почтой без собаки")
//    @Test
//    void updateUserWithoutDog() {
//        User user = new User(1L, "email@email.ru", "login", "name",
//                LocalDate.of(2020, 10, 10));
//        User newUser = new User(1L, "New_email.ru", "NewLogin", "NewName",
//                LocalDate.of(2020, 10, 10));
//        userController.create(user);
//        Assertions.assertThrows(ValidationException.class, () -> userController.update(newUser));
//    }
//
//    @DisplayName("Обновление пользователя с пустым логином")
//    @Test
//    void updateUserWithEmptyLogin() {
//        User user = new User(1L, "email@email.ru", "login", "name",
//                LocalDate.of(2020, 10, 10));
//        User newUser = new User(1L, "New@email.ru", " ", "NewName",
//                LocalDate.of(2020, 10, 10));
//        userController.create(user);
//        Assertions.assertThrows(ValidationException.class, () -> userController.update(newUser));
//    }
//
//    @DisplayName("Обновление пользователя с пробелами в логине")
//    @Test
//    void updateUserWithSpaceInLogin() {
//        User user = new User(1L, "email@email.ru", "login", "name",
//                LocalDate.of(2020, 10, 10));
//        User newUser = new User(1L, "New@email.ru", "New Login", "NewName",
//                LocalDate.of(2020, 10, 10));
//        userController.create(user);
//        Assertions.assertThrows(ValidationException.class, () -> userController.update(newUser));
//    }
//
//    @DisplayName("Обновление пользователя с датой рождения в будущем")
//    @Test
//    void updateUserWithBirthdayInFuture() {
//        User user = new User(1L, "email@email.ru", "login", "name",
//                LocalDate.of(2020, 10, 10));
//        User newUser = new User(1L, "New@email.ru", "NewLogin", "NewName",
//                LocalDate.of(2100, 10, 10));
//        userController.create(user);
//        Assertions.assertThrows(ValidationException.class, () -> userController.update(newUser));
//    }
//
//    @DisplayName("Обновление пользователя без айди")
//    @Test
//    void updateUserWithoutId() {
//        User user = new User(1L, "email@email.ru", "login", "name",
//                LocalDate.of(2020, 10, 10));
//        User newUser = new User(null, "New@email.ru", "NewLogin", "NewName",
//                LocalDate.of(2020, 10, 10));
//        userController.create(user);
//        Assertions.assertThrows(NotFoundException.class, () -> userController.update(newUser));
//    }
//
//    @DisplayName("Обновление пользователя c неверным айди")
//    @Test
//    void updateUserWithIncorrectId() {
//        User user = new User(1L, "email@email.ru", "login", "name",
//                LocalDate.of(2020, 10, 10));
//        User newUser = new User(10L, "New@email.ru", "NewLogin", "NewName",
//                LocalDate.of(2020, 10, 10));
//        userController.create(user);
//        Assertions.assertThrows(NotFoundException.class, () -> userController.update(newUser));
//    }
//}

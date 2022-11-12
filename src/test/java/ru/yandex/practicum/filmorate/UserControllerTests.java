package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.exceptions.OperationAlreadyCompletedException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class UserControllerTests {
	private static final LocalDate CORRECT_DATE = LocalDate.of(1990, 10, 3);
	private static final LocalDate UN_CORRECT_DATE_OF_BIRTHDAY = LocalDate.of(3990, 12, 31);

	private static Validator validator;
	private final UserController userController;

	@Autowired
	public UserControllerTests(UserController userController) {
		this.userController = userController;
	}

	@BeforeAll
	static void buildValidator() {
		try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
			validator = validatorFactory.usingContext().getValidator();
		}
	}

	@Test
	void user_validation_correct() {
		User user = new User(null, "aaa@mail.com", null, null, CORRECT_DATE, new ArrayList<>());
		assertNotEquals(0, validator.validate(user).size(),
				"не корректная валидация по пустому логину");

		User user1 = new User(null, "aaa@mail.com", "login login", null, CORRECT_DATE, new ArrayList<>());
		assertNotEquals(0, validator.validate(user1).size(),
				"не корректная валидация по логину с пробелами");

		User user2 = new User(null, null, "login", null, CORRECT_DATE, new ArrayList<>());
		assertNotEquals(0, validator.validate(user2).size(),
				"не корректная валидация по пустой почте");

		User user3 = new User(null, "это-неправильный?эмейл@", "login", null, CORRECT_DATE, new ArrayList<>());
		assertNotEquals(0, validator.validate(user3).size(),
				"не корректная валидация по формату почты");

		User user4 = new User(null, "aaa@mail.com", "login", "Mortie",
				UN_CORRECT_DATE_OF_BIRTHDAY, new ArrayList<>());
		assertNotEquals(0, validator.validate(user4).size(),
				"не корректная валидация по неверной дате");

		User user5 = new User(null, "aaa@mail.com", "login", "Mortie", CORRECT_DATE, new ArrayList<>());
		assertEquals(0, validator.validate(user5).size(), "отсеивается корректный пользователь");
	}

	@Test
	void user_controller_correct() {
		User user = new User(null, "aaa@mail.com", "login", "Mortie", CORRECT_DATE, new ArrayList<>());
		User user1 = new User(null, "bbb@mail.com", "login", null, CORRECT_DATE, new ArrayList<>());
		userController.createUser(user);
		userController.createUser(user1);
		user.setId(1);
		user1.setId(2);
		user1.setName(user1.getLogin());

		List<User> expectedUsers = new ArrayList<>();
		expectedUsers.add(user);
		expectedUsers.add(user1);

		assertEquals(expectedUsers, userController.getUsers(),
				"не корректно создается пользователь");
		assertEquals(expectedUsers.get(1), userController.getUsers().get(1),
				"пустое имя пользователя не меняется на логин");

		User user2 = new User(1, "aaa@mail.com", "login1", "Mortie", CORRECT_DATE, new ArrayList<>());
		userController.updateUser(user2);

		assertEquals("login1", userController.getUsers().get(0).getLogin(),
				"обновление проходит не корректно");

		User user3 = new User(1000, "aaa@mail.com", "login", "Mortie", CORRECT_DATE, new ArrayList<>());
		assertThrows(EntityNotExistException.class, () -> userController.updateUser(user3),
				"обновляется несуществующий юзер");

		User user4 = new User(3, "aaa@mail.com", "login", "Mortie", CORRECT_DATE, new ArrayList<>());
		userController.createUser(user4);
		userController.addAsFriend(1, 2);

		assertEquals(List.of(), userController.getCommonFriendsList(1, 2),
				"не верно формируется список общих друзей без общих друзей");
		assertThrows(OperationAlreadyCompletedException.class, () -> userController.addAsFriend(1, 2),
				"не формируется исключение при добавлении в друзья уже друзей");

		userController.addAsFriend(2, 3);
		assertEquals(1, userController.getUserByID(1).getFriends().size(),
				"не корректно добавляются друзья");
		assertEquals(2, userController.getUserByID(1).getFriends().get(0).getFriendId(),
				"не корректно добавляются друзья");
		userController.addAsFriend(2, 1);
		assertEquals(List.of(userController.getUserByID(3), userController.getUserByID(1)),
				userController.getFriends(2), "не корректно выводится список друзей");
		userController.addAsFriend(3, 2);
		assertEquals(List.of(userController.getUserByID(2)),
				userController.getCommonFriendsList(1, 3),
				"не корректно формируется список общих друзей");

		userController.removeFromFriends(1, 2);
		assertEquals(List.of(), userController.getUserByID(1).getFriends(),
				"не корректно удаляются друзья");
		assertThrows(OperationAlreadyCompletedException.class, () -> userController.removeFromFriends(1, 2),
				"не формируется исключение при удалении из друзей не друзей");
	}
}

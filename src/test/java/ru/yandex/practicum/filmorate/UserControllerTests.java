package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.exceptions.OperationAlreadyCompletedException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTests extends BdClassTests{
	private final UserController userController;
	@Test
	void user_controller_correct() {
		User user = User.builder().email("aaa@mail.com").login("login").name("Mortie")
				.birthday(CORRECT_DATE).build();
		User user1 = User.builder().email("bbb@mail.com").login("login0")
				.birthday(CORRECT_DATE).build();

		userController.create(user);
		userController.create(user1);
		user = user.toBuilder().id(1).build();
		user1 = user1.toBuilder().id(2).build();
		user1 = user1.toBuilder().name(user1.getLogin()).build();

		List<User> expectedUsers = new ArrayList<>();
		expectedUsers.add(user);
		expectedUsers.add(user1);

		assertEquals(expectedUsers, userController.get(),
				"не корректно создается пользователь");
		assertEquals(expectedUsers.get(1), userController.get().get(1),
				"пустое имя пользователя не меняется на логин");

		User user2 = User.builder().id(1).email("aaa@mail12.com").login("login1").name("Mortie")
				.birthday(CORRECT_DATE).build();
		userController.update(user2);


		assertEquals("login1", userController.get().get(0).getLogin(),
				"обновление проходит не корректно");

		User user3 = User.builder().email("aaa@mail12.com").login("login12").name("Mortie")
				.birthday(CORRECT_DATE).build();
		assertThrows(EntityNotExistException.class, () -> userController.update(user3),
				"обновляется несуществующий юзер");

		User user4 = User.builder().email("aaa@mail1.com").login("login123").name("Mortie")
				.birthday(CORRECT_DATE).build();
		userController.create(user4);
		userController.addAsFriend(1, 2);

		assertEquals(List.of(), userController.getCommonFriends(1, 2),
				"не верно формируется список общих друзей без общих друзей");
		assertThrows(OperationAlreadyCompletedException.class, () -> userController.addAsFriend(1, 2),
				"не формируется исключение при добавлении в друзья уже друзей");

		userController.addAsFriend(2, 3);
		assertEquals(1, userController.getFriends(1).size(),
				"не корректно добавляются друзья");
		assertEquals(2, userController.getFriends(1).get(0).getId(),
				"не корректно добавляются друзья");
		userController.addAsFriend(2, 1);
		assertEquals(List.of(userController.getByID(3), userController.getByID(1)),
				userController.getFriends(2), "не корректно выводится список друзей");
		userController.addAsFriend(3, 2);
		assertEquals(List.of(userController.getByID(2)),
				userController.getCommonFriends(1, 3),
				"не корректно формируется список общих друзей");

		userController.removeFromFriends(1, 2);
		assertEquals(List.of(), userController.getFriends(1),
				"не корректно удаляются друзья");
		assertThrows(OperationAlreadyCompletedException.class, () -> userController.removeFromFriends(1, 2),
				"не формируется исключение при удалении из друзей не друзей");
	}
}

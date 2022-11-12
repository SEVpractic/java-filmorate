# java-filmorate

ER-diagram: https://dbdiagram.io/d/635e50b75170fb6441c05e44

SQL requests examples:

1) get all users:
```
SELECT *
FROM Users;
```

2) get all films:
```
SELECT *
FROM Films;
```

3) get user by ID:
```
SELECT * 
FROM users AS u WHERE u.user_id = ?;
```

4) get friends:
```
SELECT * FROM users AS u 
INNER JOIN friends AS f ON u.user_id = f.friend_id
WHERE f.user_id = ?;
```

5) get popular films:
```
SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rate, f.mpa_id, COUNT(l.film_id)
FROM films AS f
LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id
GROUP BY f.film_id
ORDER BY COUNT(l.film_id) DESC
LIMIT ?;
```

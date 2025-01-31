package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class User {

    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private List<Friendship> friendships;
}

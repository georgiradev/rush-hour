package com.prime.rushhour.provider;

import com.prime.rushhour.dto.RoleRequestDto;
import com.prime.rushhour.dto.UserRequestDto;
import com.prime.rushhour.entity.Role;
import com.prime.rushhour.entity.User;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@Setter
public class UserProvider {

  private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public static User getInstance() {
    Role role = new Role();
    role.setName("ROLE_USER");
    role.setId(1);
    RoleRequestDto requestDto = new RoleRequestDto();
    requestDto.setName(role.getName());

    UserRequestDto userDto = new UserRequestDto();
    userDto.setEmail("gogo@abv.bg");
    userDto.setFirstName("Gogo");
    userDto.setLastName("Ivanov");
    userDto.setPassword("12345");
    userDto.setRole(requestDto);

    User user = new User();
    user.setFirstName(userDto.getFirstName());
    user.setLastName(userDto.getLastName());
    user.setEmail(userDto.getEmail());
    user.setPassword(passwordEncoder.encode(userDto.getPassword()));
    user.setRole(role);
    user.setId(1);

    return user;
  }

  public static List<User> getUsersInstance() {
    return Arrays.asList(getInstance(), getInstance());
  }
}

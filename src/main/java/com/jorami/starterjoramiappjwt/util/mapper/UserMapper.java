package com.jorami.starterjoramiappjwt.util.mapper;

import com.jorami.starterjoramiappjwt.dto.UserDto;
import com.jorami.starterjoramiappjwt.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

}

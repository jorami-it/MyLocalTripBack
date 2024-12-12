package com.jorami.mylocaltripback.util.mapper;

import com.jorami.mylocaltripback.dto.UserDto;
import com.jorami.mylocaltripback.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

}

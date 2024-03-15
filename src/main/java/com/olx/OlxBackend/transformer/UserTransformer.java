package com.olx.OlxBackend.transformer;

import com.olx.OlxBackend.DTO.UserDto;
import com.olx.OlxBackend.model.ApplicationUser;
import lombok.Builder;
import org.springframework.stereotype.Component;

public class UserTransformer {
    public static ApplicationUser UserDtoToUserObject(UserDto userDto) {
        return ApplicationUser.builder()
                .emailId(userDto.getEmailId())
                .phoneNumber(userDto.getPhoneNumber())
                .name(userDto.getName())
                .build();
    }
}

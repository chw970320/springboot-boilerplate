package com.boilerplate.user.mapper;

import com.boilerplate.auth.dto.AuthResponse;
import com.boilerplate.auth.dto.SignupRequest;
import com.boilerplate.user.domain.User;
import com.boilerplate.user.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * User 엔티티와 DTO 간의 매핑을 담당하는 MapStruct 매퍼 인터페이스.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    /**
     * User 엔티티를 UserResponse DTO로 변환합니다.
     */
    UserResponse toDto(User user);

    /**
     * User 엔티티 리스트를 UserResponse DTO 리스트로 변환합니다.
     */
    List<UserResponse> toDtoList(List<User> users);

    /**
     * SignupRequest DTO를 User 엔티티로 변환합니다.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    User signupRequestToUser(SignupRequest signupRequest);

    /**
     * User 엔티티를 AuthResponse DTO로 변환합니다.
     */
    @Mapping(source = "id", target = "userId")
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    @Mapping(target = "accessToken", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "tokenType", constant = "Bearer")
    AuthResponse toAuthResponse(User user);

    /**
     * User 엔티티의 특정 필드를 업데이트합니다.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    void updateFromUser(User updateUser, @MappingTarget User targetUser);
}

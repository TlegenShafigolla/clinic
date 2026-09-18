package kz.tlegen.clinic.mapper;

import kz.tlegen.clinic.dto.user.UserRequest;
import kz.tlegen.clinic.dto.user.UserResponse;
import kz.tlegen.clinic.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRequest request) {
        return new User(request.getEmail(),
                request.getPassword(),
                request.getRole(),
                request.isActive());
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(),
                user.getEmail(),
                user.getRole(),
                user.isActive());
    }
}

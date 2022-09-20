package net.arville.easybill.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.dto.util.ConvertibleFromOriginalEntitiy;
import net.arville.easybill.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class UserResponse implements ConvertibleFromOriginalEntitiy<UserResponse, User> {

    private Long id;

    private String username;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    @Override
    public UserResponse fromOriginalEntity(User entity) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(entity.getId());
        userResponse.setUsername(entity.getUsername());
        userResponse.setCreatedAt(entity.getCreatedAt());
        userResponse.setUpdatedAt(entity.getUpdatedAt());
        return userResponse;
    }
}

package net.arville.easybill.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.arville.easybill.dto.util.ConvertibleFromOriginalEntitiy;
import net.arville.easybill.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class UserRegistrationResponse implements ConvertibleFromOriginalEntitiy<UserRegistrationResponse, User> {
    private Long id;
    private String username;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Override
    public UserRegistrationResponse fromOriginalEntity(User entity) {
        this.id = entity.getId();
        this.username = entity.getUsername();
        this.createdAt = entity.getCreatedAt();;
        return this;
    }
}

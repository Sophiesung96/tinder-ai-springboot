package com.sophie.api.tinderaibackend.conversations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConversationRequestDto {
    @NotBlank(message = "Profile id should not be blank")
    private String profileId;
}

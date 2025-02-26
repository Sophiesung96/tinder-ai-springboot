package com.sophie.api.tinderaibackend.conversations;

import com.sophie.api.tinderaibackend.profiles.Profile;

import java.time.LocalDateTime;

public record ChatMessage(
        String messageText,
        String authorId,
        LocalDateTime messageTime


) {

}

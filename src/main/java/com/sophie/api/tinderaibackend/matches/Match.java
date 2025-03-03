package com.sophie.api.tinderaibackend.matches;

import com.sophie.api.tinderaibackend.profiles.Profile;

public record Match (String id, Profile profile, String conversationId) {
}

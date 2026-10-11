package com.twittvl.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

//utility class to get id of currently logged-in user
public class CurrentUser {
    //private constructor, to ensure no inheritance
    private CurrentUser() {}

    //get ID of logged-in user if there is none (anon) return null
    public static Long idOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails details) {
            return details.getId();
        }
        return null;
    }
}

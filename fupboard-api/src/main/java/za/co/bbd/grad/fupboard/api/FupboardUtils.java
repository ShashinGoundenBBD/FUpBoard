package za.co.bbd.grad.fupboard.api;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;

public class FupboardUtils {
    public static final int SHORT_NAME_LENGTH = 64;
    public static final int LONG_NAME_LENGTH = 128;
    public static final int DESCRIPTION_LENGTH = 255;

    public static boolean HasPermission(String permission) {
        List<String> permissions = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            .stream().map(p -> p.getAuthority()).toList();
        return permissions.contains(permission);
    }
}

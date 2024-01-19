//package tech.orbfin.api.gateway.auditing;
//
//import org.jetbrains.annotations.NotNull;
//import org.springframework.stereotype.Component;
//import tech.orbfin.api.gateway.user.User;
//
//import org.springframework.data.domain.AuditorAware;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.util.Optional;
//
//@Component
//public class ApplicationAuditAware implements AuditorAware<Integer> {
//    @NotNull
//    @Override
//    public Optional<Integer> getCurrentAuditor() {
//        Authentication authentication =
//                SecurityContextHolder
//                        .getContext()
//                        .getAuthentication();
//        if (authentication == null ||
//                !authentication.isAuthenticated() ||
//                authentication instanceof AnonymousAuthenticationToken
//        ) {
//            return Optional.empty();
//        }
//
//        User userPrincipal = (User) authentication.getPrincipal();
//        return Optional.ofNullable(userPrincipal.getId());
//    }
//}
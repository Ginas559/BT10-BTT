// path: src/main/java/vn/iotstar/config/AuthInterceptor.java
package vn.iotstar.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import vn.iotstar.entity.User;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        String uri = req.getRequestURI();

        // Cho phép công khai
        if (uri.startsWith("/login") || uri.startsWith("/error")
                || uri.startsWith("/css") || uri.startsWith("/js") || uri.startsWith("/images")) {
            return true;
        }

        // Chặn /graphql theo phương án B: yêu cầu phải đăng nhập
        if (uri.startsWith("/graphql")) {
            HttpSession ses = req.getSession(false);
            if (ses == null || ses.getAttribute("currentUser") == null) {
                resp.sendRedirect("/login");
                return false;
            }
            return true;
        }

        // Các URL còn lại: /admin/**, /user/** ...
        HttpSession ses = req.getSession(false);
        if (ses == null || ses.getAttribute("currentUser") == null) {
            resp.sendRedirect("/login");
            return false;
        }
        User u = (User) ses.getAttribute("currentUser");

        // Phân quyền theo prefix
        if (uri.startsWith("/admin") && !"ADMIN".equalsIgnoreCase(u.getRole())) {
            resp.sendRedirect("/login");
            return false;
        }
        if (uri.startsWith("/user")
                && !("USER".equalsIgnoreCase(u.getRole()) || "ADMIN".equalsIgnoreCase(u.getRole()))) {
            resp.sendRedirect("/login");
            return false;
        }

        return true;
    }
}

// path: src/main/java/vn/iotstar/controller/LoginController.java
package vn.iotstar.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.iotstar.entity.User;
import vn.iotstar.repository.UserRepository;

@Controller
public class LoginController {
    private final UserRepository userRepo;

    public LoginController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (logout != null) model.addAttribute("msg", "Đã đăng xuất.");
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        User u = userRepo.findByEmail(email).orElse(null);
        if (u == null || !u.getPassword().equals(password)) {
            model.addAttribute("error", "Sai tài khoản hoặc mật khẩu");
            return "login";
        }
        session.setAttribute("currentUser", u);
        if ("ADMIN".equalsIgnoreCase(u.getRole())) {
            return "redirect:/admin/home";
        }
        return "redirect:/user/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=1";
    }
}

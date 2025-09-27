package vn.iotstar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PlaygroundController {
    @GetMapping({"/playground", "/graphiql"})
    public String playground() {
        return "playground"; // templates/playground.html
    }
}

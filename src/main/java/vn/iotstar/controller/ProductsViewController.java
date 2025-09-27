package vn.iotstar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductsViewController {

    @GetMapping("/products")
    public String productsPage() {
        // -> templates/admin/categories/list.html
        return "admin/categories/list";
    }
}

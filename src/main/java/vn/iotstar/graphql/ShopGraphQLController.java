// path: src/main/java/vn/iotstar/graphql/ShopGraphQLController.java
package vn.iotstar.graphql;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.User;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.UserRepository;

import java.util.Comparator;
import java.util.List;

@Controller
public class ShopGraphQLController {

    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    public ShopGraphQLController(CategoryRepository c, ProductRepository p, UserRepository u) {
        this.categoryRepo = c;
        this.productRepo = p;
        this.userRepo = u;
    }

    /* =========================================================
     *                      QUERIES
     * ========================================================= */

    @QueryMapping
    public List<Product> productsSortedByPriceAsc() {
        // Sắp xếp ở Java để giữ nguyên repository hiện có
        return productRepo.findAll().stream()
                .sorted(Comparator.comparing(p -> p.getPrice() == null ? 0.0 : p.getPrice()))
                .toList();
    }

    @QueryMapping
    public List<Product> productsByCategory(@Argument Long categoryId) {
        // Giữ đúng tên query mà UI đang gọi (ajax.html) :contentReference[oaicite:1]{index=1}
        return productRepo.findAll().stream()
                .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(categoryId))
                .sorted(Comparator.comparing(p -> p.getPrice() == null ? 0.0 : p.getPrice()))
                .toList();
    }

    @QueryMapping
    public List<Category> categories() {
        return categoryRepo.findAll();
    }

    @QueryMapping
    public List<User> users() {
        return userRepo.findAll();
    }

    /* =========================================================
     *                    INPUT + VALIDATION
     *  (Đặt ràng buộc vào GraphQL Input vì UI gọi mutation qua AJAX)
     * ========================================================= */

    public record UserInput(
            @NotBlank(message = "{user.fullname.notBlank}") String fullname,
            @NotBlank(message = "{user.email.notBlank}") @Email(message = "{user.email.invalid}") String email,
            @NotNull @Size(min = 6, message = "{user.password.size}") String password,
            @Size(max = 20, message = "{user.phone.size}") String phone
    ) {}

    public record CategoryInput(
            @NotBlank(message = "{category.name.notBlank}") @Size(max = 100) String name,
            @Size(max = 255, message = "{category.images.size}") String images
    ) {}

    public record ProductInput(
            @NotBlank(message = "{product.title.notBlank}") String title,
            @PositiveOrZero(message = "{product.quantity.min}") Integer quantity,
            @Size(max = 500) String desc,
            @NotNull @PositiveOrZero(message = "{product.price.min}") Double price,
            Long userId,
            @NotNull(message = "{product.categoryId.notNull}") Long categoryId
    ) {}

    /* =========================================================
     *                      MUTATIONS: USER
     * ========================================================= */

    @MutationMapping
    public User createUser(@Argument @Valid UserInput input) {
        User u = User.builder()
                .fullname(input.fullname())
                .email(input.email())
                .password(input.password())
                .phone(input.phone())
                .build();
        return userRepo.save(u);
    }

    @MutationMapping
    public User updateUser(@Argument Long id, @Argument @Valid UserInput input) {
        User u = userRepo.findById(id).orElseThrow();
        if (input.fullname()!=null) u.setFullname(input.fullname());
        if (input.email()!=null) u.setEmail(input.email());
        if (input.password()!=null) u.setPassword(input.password());
        if (input.phone()!=null) u.setPhone(input.phone());
        return userRepo.save(u);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        if (!userRepo.existsById(id)) return false;
        userRepo.deleteById(id);
        return true;
    }

    /* =========================================================
     *                   MUTATIONS: CATEGORY
     * ========================================================= */

    @MutationMapping
    public Category createCategory(@Argument @Valid CategoryInput input) {
        Category c = Category.builder().name(input.name()).images(input.images()).build();
        return categoryRepo.save(c);
    }

    @MutationMapping
    public Category updateCategory(@Argument Long id, @Argument @Valid CategoryInput input) {
        Category c = categoryRepo.findById(id).orElseThrow();
        if (input.name()!=null) c.setName(input.name());
        if (input.images()!=null) c.setImages(input.images());
        return categoryRepo.save(c);
    }

    @MutationMapping
    public Boolean deleteCategory(@Argument Long id) {
        if (!categoryRepo.existsById(id)) return false;
        categoryRepo.deleteById(id);
        return true;
    }

    /* =========================================================
     *                    MUTATIONS: PRODUCT
     * ========================================================= */

    @MutationMapping
    public Product createProduct(@Argument @Valid ProductInput input) {
        Product p = new Product();
        p.setTitle(input.title());
        p.setQuantity(input.quantity());
        p.setDesc(input.desc());
        p.setPrice(input.price());
        if (input.userId()!=null)
            p.setUser(userRepo.findById(input.userId()).orElse(null));
        if (input.categoryId()!=null)
            p.setCategory(categoryRepo.findById(input.categoryId()).orElse(null));
        return productRepo.save(p);
    }

    @MutationMapping
    public Product updateProduct(@Argument Long id, @Argument @Valid ProductInput input) {
        Product p = productRepo.findById(id).orElseThrow();
        if (input.title()!=null) p.setTitle(input.title());
        if (input.quantity()!=null) p.setQuantity(input.quantity());
        if (input.desc()!=null) p.setDesc(input.desc());
        if (input.price()!=null) p.setPrice(input.price());
        if (input.userId()!=null) p.setUser(userRepo.findById(input.userId()).orElse(null));
        if (input.categoryId()!=null) p.setCategory(categoryRepo.findById(input.categoryId()).orElse(null));
        return productRepo.save(p);
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument Long id) {
        if (!productRepo.existsById(id)) return false;
        productRepo.deleteById(id);
        return true;
    }
}

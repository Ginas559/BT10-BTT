package vn.iotstar.graphql;

import lombok.RequiredArgsConstructor;
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

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ShopGraphQLController {

    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepo;

    // ---------- Queries ----------
    @QueryMapping
    public List<Product> productsSortedByPriceAsc() {
        return productRepo.findAllByOrderByPriceAsc();
    }

    @QueryMapping
    public List<Product> productsByCategory(@Argument Long categoryId) {
        return productRepo.findByCategory_Id(categoryId);
    }

    @QueryMapping
    public List<User> users() { return userRepo.findAll(); }

    @QueryMapping
    public List<Category> categories() { return categoryRepo.findAll(); }

    @QueryMapping
    public List<Product> products() { return productRepo.findAll(); }

    // ---------- Mutations: User ----------
    public record UserInput(String fullname, String email, String password, String phone) {}

    @MutationMapping
    public User createUser(@Argument UserInput input) {
        User u = User.builder()
                .fullname(input.fullname())
                .email(input.email())
                .password(input.password())
                .phone(input.phone())
                .build();
        return userRepo.save(u);
    }

    @MutationMapping
    public User updateUser(@Argument Long id, @Argument UserInput input) {
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

    // ---------- Mutations: Category ----------
    public record CategoryInput(String name, String images) {}

    @MutationMapping
    public Category createCategory(@Argument CategoryInput input) {
        Category c = Category.builder().name(input.name()).images(input.images()).build();
        return categoryRepo.save(c);
    }

    @MutationMapping
    public Category updateCategory(@Argument Long id, @Argument CategoryInput input) {
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

    // ---------- Mutations: Product ----------
    public record ProductInput(String title, Integer quantity, String desc,
                               Double price, Long userId, Long categoryId) {}

    @MutationMapping
    public Product createProduct(@Argument ProductInput input) {
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
    public Product updateProduct(@Argument Long id, @Argument ProductInput input) {
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

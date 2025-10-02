// path: src/main/java/vn/iotstar/entity/User.java
package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "[user]") // tránh xung đột từ khóa với SQL Server
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullname;
    @Column(unique = true)
    private String email;
    private String password;
    private String phone;

    // NEW: thêm role nhưng KHÔNG đụng tới các mapping sẵn có
    // Giá trị: "USER" hoặc "ADMIN"
    private String role;

    // 1-n User -> Product (GIỮ NGUYÊN)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

    // n-n User <-> Category (GIỮ NGUYÊN theo đề)
    @ManyToMany
    @JoinTable(
            name = "user_categories",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();
}

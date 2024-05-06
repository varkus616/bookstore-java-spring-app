package com.example.demo.security.model;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.example.demo.model.Cart;
import com.example.demo.model.PurchaseHistory;
import com.example.demo.model.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Setter
@Getter
public class User implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String username;

    private String password;

    private String email;

    @Column(name = "account_non_locked")
    private boolean accountNonLocked;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private PurchaseHistory purchaseHistory;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id", referencedColumnName = "id")
    private Cart cart;

    @Getter
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id"))
    private Collection<Role> roles = new ArrayList<>(); // Inicjalizacja kolekcji roli


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Collection<Review> reviews;

    public User() {
    }

    public User(String username, String password, boolean accountNonLocked) {
        this.username = username;
        this.password = password;
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        List<Role> roles = (List<Role>) getRoles();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role role : roles){
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }
    @Override public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override public boolean isEnabled() {
        return true;
    }
    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }
    public boolean getAccountNonLocked() {
        return accountNonLocked;
    }

    public void setRoles(List<Role> roles){
        this.roles.addAll(roles);
    }
    public boolean hasRole(String name)
    {
        for (Role role : this.roles)
            if (role.getName().equals(name))
                return true;
        return false;
    }

}
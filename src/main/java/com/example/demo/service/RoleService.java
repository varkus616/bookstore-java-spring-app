package com.example.demo.service;

import com.example.demo.security.model.Role;
import com.example.demo.security.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long id) {
        Optional<Role> roleOptional = roleRepository.findById(id);
        return roleOptional.orElse(null);
    }

    public void addRole(Role role) {
        roleRepository.save(role);
    }

    public void updateRole(Long id, Role updatedRole) {
        Role role = getRoleById(id);
        if (role != null) {
            updatedRole.setId(id);
            roleRepository.save(updatedRole);
        }
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}

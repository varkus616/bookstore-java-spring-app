package com.example.demo.security.service;

import com.example.demo.security.model.Privilege;
import com.example.demo.security.repository.PrivilegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrivilegeService {

    @Autowired
    private PrivilegeRepository privilegeRepository;

    public List<Privilege> getAllPrivileges() {
        return privilegeRepository.findAll();
    }

    public Privilege getPrivilegeById(Long id) {
        Optional<Privilege> privilegeOptional = privilegeRepository.findById(id);
        return privilegeOptional.orElse(null);
    }

    public void addPrivilege(Privilege privilege) {
        privilegeRepository.save(privilege);
    }

    public void updatePrivilege(Long id, Privilege updatedPrivilege) {
        Privilege privilege = getPrivilegeById(id);
        if (privilege != null) {
            updatedPrivilege.setId(id);
            privilegeRepository.save(updatedPrivilege);
        }
    }

    public void deletePrivilege(Long id) {
        privilegeRepository.deleteById(id);
    }
}

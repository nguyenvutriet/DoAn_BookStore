package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Customers;
import com.example.project_bookstore.Entity.Users;
import com.example.project_bookstore.Repository.IUsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsersService implements UserDetailsService {

    @Autowired
    private IUsersRepository repo;

    public boolean saveUser(String userName, String password, String fullName, String role, Customers cus){
        Users us = repo.findById(userName).orElse(null);
        if(us!=null){
            return false;
        }
        Users user = new Users(userName, password, role, fullName, cus, new Date());
        repo.save(user);
        return true;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = repo.findById(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Collection<GrantedAuthority> authorities = List.of( new SimpleGrantedAuthority(user.getRole()));

        return new User(user.getUserName(), user.getPassword(), authorities);

    }


}

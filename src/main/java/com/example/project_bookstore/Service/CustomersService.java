package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Customers;
import com.example.project_bookstore.Repository.ICustomersRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CustomersService {

    @Autowired
    private ICustomersRepository repo;

    public boolean save(String fullName, String email, String address, Date dateOfBirth, String phone) {

        List<Customers> dsId = repo.findAll();

        List<Integer> dsSo = new ArrayList();

        for(Customers c : dsId){
            int id = Integer.parseInt(c.getCustomerId().substring(2));
            dsSo.add(id);
        }

        Customers cus = repo.getLastCustomer();
        System.out.println(cus.toString());
        String id;
        if (cus == null) {
            id = "CU01";
        } else {
            String lastId = cus.getCustomerId();
            int num = Integer.parseInt(lastId.substring(2));
            id = String.format("CU%02d", num + 1);
        }

        repo.save(new Customers(id, fullName, email, phone, address, dateOfBirth));
        return true;
    }


    public Customers getCustomerByEmail(String email){
        return repo.findByEmail(email);
    }




}

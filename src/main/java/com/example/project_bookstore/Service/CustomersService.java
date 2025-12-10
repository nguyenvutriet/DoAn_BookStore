package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Customers;
import com.example.project_bookstore.Repository.ICustomersRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class CustomersService {

    @Autowired
    private ICustomersRepository repo;

    public void save(String fullName, String email, String address, Date dateOfBirth, String phone) {

        List<Customers> dsId = repo.findAll();
        List<Integer> dsSo = new ArrayList();

        for(Customers c : dsId){
            int id = Integer.parseInt(c.getCustomerId().substring(2));
            dsSo.add(id);
        }

        int idMax = Collections.max(dsSo);
        Customers cus2 = new Customers();
        do{
            idMax = idMax+1;
            cus2 = repo.findById("CU"+idMax).orElse(null);
        }
        while(cus2 != null);

        Customers cus = new Customers();
        cus.setCustomerId("CU" + idMax);
        cus.setFullName(fullName);
        cus.setEmail(email);
        cus.setAddress(address);
        cus.setDateOfBirth(dateOfBirth);
        cus.setPhone(phone);
        repo.save(cus);
    }


    public Customers getCustomerByEmail(String email){
        return repo.findByEmail(email);
    }

    public Customers getCustomerById(String id){
        return repo.findById(id).orElse(null);
    }

    public void update(Customers cus){
        repo.updateCus(cus.getCustomerId(), cus.getFullName(), cus.getPhone(), cus.getEmail(), cus.getAddress(), cus.getDateOfBirth());
    }

    public Customers getCustomerByPhone(String phone){
        return repo.findByPhone(phone);
    }


}

package com.example.project_bookstore.Service;

import com.example.project_bookstore.Repository.IReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    @Autowired
    private IReviewRepository repo;
}

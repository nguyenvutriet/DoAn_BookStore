package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Repository.IBooksRepository;
import com.example.project_bookstore.Repository.IOrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {

    @Autowired
    private IOrderDetailRepository orderDetailRepository;

    @Autowired
    private IBooksRepository booksRepository;

    public List<Books> recommendBooks(String bookId){

        List<Books> books =
                orderDetailRepository.recommendBooks(bookId);

        if (!books.isEmpty()) {
            return books;
        }

        return booksRepository.findTopBestSelling(
                PageRequest.of(0, 4)
        );
    }
}
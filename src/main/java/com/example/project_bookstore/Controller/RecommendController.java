package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Service.BooksService;
import com.example.project_bookstore.Service.PythonService;
import com.example.project_bookstore.Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RecommendController {

    @Autowired
    private PythonService pythonService;
    @Autowired
    private BooksService booksService;

    @Autowired
    private ReviewService reviewService;

//
//    @GetMapping("/recommend")
//    public String recommendPage(@RequestParam String bookId, Model model) throws Exception {
//
//        String json = pythonService.runPythonModel(bookId);
//
//        ObjectMapper mapper = new ObjectMapper();
//
//        List<Map<String, Object>> books =
//                mapper.readValue(json, new TypeReference<>() {});
//
//        model.addAttribute("books", books);
//        model.addAttribute("bookId", bookId);
//
//        return "recommend";   // thư mục templates/recommend.html
//    }

    @GetMapping("/recommend")
    public String recommendPage(@RequestParam String bookId, Model model) throws Exception {

        System.out.println("=== /recommend CALLED ===");
        System.out.println("bookId = " + bookId);

        String json = pythonService.runPythonModel(bookId);
        ObjectMapper mapper = new ObjectMapper();

        List<Map<String,Object>> pyBooks =
                mapper.readValue(json, new TypeReference<>(){} );

        System.out.println("PYTHON LIST SIZE = " + pyBooks.size());

        List<Books> recommended = new ArrayList<>();

        Map<String,Integer> avgRatings = new HashMap<>();
        Map<String,Long> ratingCounts = new HashMap<>();

        for(Map<String,Object> map : pyBooks){

            String id = map.get("bookId").toString();
            Books book = booksService.getBookById(id);

            if(book != null){

                recommended.add(book);

                avgRatings.put(id, reviewService.getAverageRatingRounded(id));
                ratingCounts.put(id, reviewService.getReviewCountForBook(id));
            }
        }
        System.out.println("FINAL RECOMMENDED SIZE = " + recommended.size());
        model.addAttribute("recommendedBooks", recommended);
        model.addAttribute("avgRatings", avgRatings);
        model.addAttribute("ratingCounts", ratingCounts);
        model.addAttribute("bookId", bookId);

        return "recommend";
    }
}
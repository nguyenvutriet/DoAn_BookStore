package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Service.PythonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/api")
public class PythonController {

    @Autowired
    private PythonService pythonService;

    @GetMapping("/search")
    public String searchPage() {
        return "search";
    }

    // autocomplete dropdown (search live)
    @GetMapping("/find-book")
    @ResponseBody
    public ResponseEntity<String> findBook(
            @RequestParam String title
    ) {
        String result = pythonService.findBookByTitle(title);
        return ResponseEntity.ok(result);
    }

    // recommend list page
    @GetMapping("/recommend")
    @ResponseBody
    public ResponseEntity<String> recommend(
            @RequestParam String bookId
    ) {
        String result = pythonService.runPythonModel(bookId);
        return ResponseEntity.ok(result);
    }
}

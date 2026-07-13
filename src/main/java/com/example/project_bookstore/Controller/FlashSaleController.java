package com.example.project_bookstore.Controller;

import com.example.project_bookstore.Service.FlashSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/flash-sale")
public class FlashSaleController {

    @Autowired
    private FlashSaleService flashSaleService;

    @GetMapping
    public String view(Model model) {
        flashSaleService.getCurrentActive().ifPresentOrElse(flashSale -> {
            var pageData = flashSaleService.getFlashSaleDetailPageData(flashSale.getFlashSaleId());
            model.addAttribute("flashSale", pageData.getFlashSale());
            model.addAttribute("details", pageData.getDetails());
        }, () -> {
            model.addAttribute("flashSale", null);
            model.addAttribute("details", java.util.Collections.emptyList());
        });
        return "flash_sale";
    }
}

package vttp.nus.miniproject2.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vttp.nus.miniproject2.services.QuotesService;

@RestController
@RequestMapping("/api")
public class QuotesController {

    @Autowired
    private QuotesService quotesService;

    @GetMapping("/quote")
    public String getQuote() {
        return quotesService.getQuote();
    }
}
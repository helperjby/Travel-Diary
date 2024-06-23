package com.traveldiary.be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.traveldiary.be.service.FaqService;
import com.traveldiary.be.dto.FaqDTO;

import java.util.List;

@RestController
@RequestMapping("/api/settings/faqs")
public class FaqController {

    @Autowired
    private FaqService faqService;

    //모든 FAQ를 가져옴
    @GetMapping
    public List<FaqDTO> getFAQs() {
        return faqService.getAllFAQs();
    }
}

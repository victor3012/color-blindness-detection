package me.victor.cbd.controller;

import me.victor.cbd.model.model.CreatedTestData;
import me.victor.cbd.service.IshiharaTestService;
import org.springframework.web.bind.annotation.*;

@RestController("/")
public class IshiharaController {
    private final IshiharaTestService service;

    public IshiharaController(IshiharaTestService service) {
        this.service = service;
    }

    @PostMapping("ishihara-tests")
    public CreatedTestData createTest() {
        return service.createTest();
    }

    @PutMapping("ishihara-tests/{id}")
    public void submitAnswers(@PathVariable int id) {

    }

    @GetMapping("results/{resultUrl}")
    public void getTestResults(@PathVariable String resultUrl) {

    }
}

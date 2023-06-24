package me.victor.cbd.controller;

import me.victor.cbd.exception.DataFormatException;
import me.victor.cbd.model.model.IshiharaCompletedTestData;
import me.victor.cbd.model.model.IshiharaTestData;
import me.victor.cbd.model.model.TestURlData;
import me.victor.cbd.service.IshiharaTestService;
import org.springframework.web.bind.annotation.*;

@RestController("/")
public class IshiharaController {
    private final IshiharaTestService service;

    public IshiharaController(IshiharaTestService service) {
        this.service = service;
    }

    @PostMapping("ishihara-tests")
    public IshiharaTestData createTest() {
        return this.service.createTest();
    }

    @PutMapping("ishihara-tests/{id}")
    public TestURlData submitAnswers(@PathVariable Integer id, @RequestBody IshiharaTestData testData) {
        if (id == null || id <= 0) {
            throw new DataFormatException("Invalid id");
        }

        return new TestURlData(this.service.submitAnswers(id, testData));
    }

    @GetMapping("results/{resultUrl}")
    public IshiharaCompletedTestData getTestResults(@PathVariable String resultUrl) {
        return this.service.getTestResult(resultUrl);
    }
}

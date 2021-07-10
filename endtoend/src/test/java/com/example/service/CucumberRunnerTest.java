package com.example.service;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "classpath:features",
    glue = {"com.example.service"},
    plugin = {
      "pretty",
      "html:target/reports/cucumber-html-reports/test-runner.html",
      "json:target/reports/cucumber-html-reports/TestRunner.json"
    })
public class CucumberRunnerTest {}

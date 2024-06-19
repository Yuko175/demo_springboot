package com.example.demo.sample.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("")
public class SearchController {
    @GetMapping("search/{word}")
    public ResponseEntity<String> searchWord(@PathVariable String word) throws IOException {
        System.out.println(word);

//        aaa = service.search("https://ejje.weblio.jp/content/",".content-explanation")
//        bbb =
//        result_list = [aaa,bbb,ccc]

        Document document = Jsoup.connect("https://ejje.weblio.jp/content/" + word).get();
        String result = document.select(".content-explanation").text();

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }
}


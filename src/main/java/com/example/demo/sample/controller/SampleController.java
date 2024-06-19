package com.example.demo.sample.controller;

import com.example.demo.sample.response.UserResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class SampleController {
    @GetMapping("sample")
    public ResponseEntity<UserResponse> getString() {
        UserResponse userResponse = new UserResponse(1,"guest");
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userResponse);
    }

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


    @GetMapping("get/{word}")
    public ResponseEntity<UserResponse> callAPI(@PathVariable String word) throws IOException {
        System.out.println(word);

        Document document = Jsoup.connect("https://ejje.weblio.jp/content/" + word).get();
        System.out.println(document.select(".content-explanation").text());

        // 持ってくる
        UserResponse userResponse = null;
        RestTemplate restTemplate = new RestTemplate();
        try {
            //正常な時
            userResponse = restTemplate.getForObject("http://localhost:8090/api/sample", UserResponse.class);

        } catch (HttpClientErrorException e){
            //エラーの時、エラーメッセージのセット　返す
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(userResponse);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        new UserResponse(
                                Objects.requireNonNull(userResponse).id() ,
                                userResponse.name()+":"+word
                        )
                );
    }
}

package com.example.demo.sample.controller;


import com.example.demo.sample.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping("search/{word}")
    public ResponseEntity<String> searchWord(@PathVariable String word) throws IOException {

        //TODO:formを作る
        //TODO:小文字への変換

        List<List<String>> searchResultList = new ArrayList<>();


        //TODO:".content-explanation"のところ、空かくをどのように記入するか調べる
        searchResultList.add(
                searchService.searchWord(
                        "https://ejje.weblio.jp/content/" + word,
                        ".content-explanation"
                ).result
        );


        searchResultList.add(searchService.searchWord(
                "https://www.ei-navi.jp/dictionary/content/" + word,
                ".list-group-item-text"
                ).result
        );

        //TODO:繰り返しアクセスするとエラーが起きるので、サイトを変える
        searchResultList.add(searchService.searchWord(
                        "https://www.linguee.jp/%E6%97%A5%E6%9C%AC%E8%AA%9E-%E8%8B%B1%E8%AA%9E/search?source=auto&query=" + word,
                        ".exact .dictLink"
                ).result
        );

        //TODO:「、」と「　」と「,」を区切りもじにした配列をにする
        //TODO:被り(完全一致)の削除

        boolean isContainWord = searchService.isContainWord(searchResultList);

        //検索結果がない(null)場合
        if (!isContainWord){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .build();
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(searchResultList.toString());
    }
}


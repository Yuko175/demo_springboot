package com.example.demo.sample.controller;

import com.example.demo.sample.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("")
public class SearchController {
    @Autowired
    private SearchService searchService;


    //TODO:日本語 -> 英語も作成しましょう！
    // wordがアルファベットかそれ以外かで、関数の振り分けを行う。
    @GetMapping("search/{word}")
    public ResponseEntity<List<List<String>>> searchWord(@PathVariable String word) throws IOException {

        //TODO:formを作る
        //TODO:小文字検索/大文字検索 をどちらも出力してくっつけて表示する

        List<List<String>> searchResultList = new ArrayList<>();

        //TODO:".content-explanation"のところ、空白をどのように記入するか調べる
        //->わかんないいいいいい
        //TODO:serviceに持っていく
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

        //TODO:要素(意味)がたくさん取れているので、制限をもっときつく！
        searchResultList.add(searchService.searchWord(
                        "https://dictionary.goo.ne.jp/word/en/" + word,
                        ".list-meanings .in-ttl-b"
                ).result
        );

        //TODO:「、」と「　」と「,」を区切りもじにした配列をにする
        //TODO:被り(完全一致)の削除
        if (searchResultList.get(0).size() > 1) {
            searchResultList.set(0, Arrays.asList(searchResultList.get(0).get(0).split(";\\s*|、|；")));
        }


        boolean isContainWord = searchService.isContainSearchWord(searchResultList);

        //検索結果がない(null)場合
        if (isContainWord) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(searchResultList);

        } else {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .build();
        }
    }


    @GetMapping("search/hiragana/{word}")
    public ResponseEntity<List<String>> changeHiragana(@PathVariable String word) throws IOException {
        List<String> hiraganaAnswerList = searchService.changeHiragana(
                "https://www.weblio.jp/content/" + word,
                ".kiji"
        ).result;

        //検索結果がない(null)場合
        if (!searchService.isContainWord(hiraganaAnswerList)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .build();
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(hiraganaAnswerList);
    }


    @GetMapping("search/change/je/{word}")
    public ResponseEntity<List<String>> translateJapaneseToEnglish(@PathVariable String word) throws IOException {

        ArrayList<String> translatedWords = searchService.translateJapaneseToEnglish(
                "https://ejje.weblio.jp/content/" + word,
                ".content-explanation.je"
        ).result;


        //TODO:大文字を小文字に変換する
        //TODO:長音が取れていない！

        //検索結果がない(null)場合
        if (searchService.isContainWord(translatedWords)) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(translatedWords);
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @GetMapping("search/change/ej/{word}")
    public ResponseEntity<List<String>> translateEnglishToJapanese(@PathVariable String word) throws IOException {

        ArrayList<String> translatedWordsList = searchService.translateEnglishToJapanese(
                "https://ejje.weblio.jp/content/" + word,
                ".content-explanation.ej"
        ).result;

        //TODO:大文字を小文字に変換する
        //TODO:長音が取れていない！

        //検索結果がない(null)場合
        if (searchService.isContainWord(translatedWordsList)) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(translatedWordsList);
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}


package com.example.demo.sample.service;

import com.example.demo.sample.dto.SearchDto;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

    public SearchDto searchWord(String url, String className) throws IOException {
        List<String> resultList = new ArrayList<>();
        Document document;
        try {
            document = Jsoup.connect(url).get();
        } catch (HttpStatusException e) {
            // HttpStatusのエラー(400~,500~など)
            //TODO:エラーコードが帰ってくる場合とnullになるパターン
            resultList.add("Error:StatusCode is " + e.getStatusCode());
            return new SearchDto(resultList);
        }
        Elements elements = document.select(className);
        resultList.add(elements.text());

        return new SearchDto(resultList);
    }



    public boolean isContainWord(List<List<String>> searchResultList) {
        for (List<String> searchResult : searchResultList){
            for (String result : searchResult){
                if (StringUtils.hasText(result)) {
                    return true;
                }
            }
        }
        return false;
    }
}


package com.example.demo.sample.service;

import com.example.demo.sample.dto.SearchDto;
import com.example.demo.sample.dto.WordDto;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    public boolean isContainSearchWord(List<List<String>> searchResultList) {
        for (List<String> searchResult : searchResultList) {
            for (String result : searchResult) {
                if (StringUtils.hasText(result)) {
                    return true;
                }
            }
        }
        return false;
    }

    public WordDto changeWord(String url, String className) throws IOException {
        Document document;
        try {
            document = Jsoup.connect(url).get();
        } catch (HttpStatusException e) {
            // HttpStatusのエラー(400~,500~など)
            return new WordDto("Error:StatusCode is " + e.getStatusCode());
        }
        Elements elements = document.select(className);
        return new WordDto(elements.text());
    }

    public boolean isContainWord(List<String> resultList) {
        for (String result : resultList) {
            if (StringUtils.hasText(result)) {
                return true;
            }
        }
        return false;
    }

    public boolean isContainChangeWord(String result) {
        return StringUtils.hasText(result);
    }

    public SearchDto changeHiragana(String url, String className) throws IOException {
        Document document;
        try {
            document = Jsoup.connect(url).get();
        } catch (HttpStatusException e) {
            // HttpStatusのエラー(400~,500~など)
            return new SearchDto(List.of("Error:StatusCode is " + e.getStatusCode()));
        }

        Elements elements = document.select(className);
        String text = elements.text();

        Pattern pattern = Pattern.compile("読み方：(\\p{InHiragana}+)");
        Matcher matcher = pattern.matcher(text);

        List<String> resultList = new ArrayList<>();
        while (matcher.find()) {
            String reading = matcher.group(1);
            resultList.add(reading);
        }
        return new SearchDto(resultList);
    }
}


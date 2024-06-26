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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SearchService {

    public SearchDto searchWord(String url, String className) throws IOException {
        ArrayList<String> resultList = new ArrayList<>();
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

    public SearchDto translateJapaneseToEnglish(String url, String className) throws IOException {
        Document document;
        try {
            document = Jsoup.connect(url).get();
        } catch (HttpStatusException e) {
            // HttpStatusのエラー(400~,500~など)
            return new SearchDto(new ArrayList<>(List.of("Error:StatusCode is " + e.getStatusCode())));
        }
        Elements elements = document.select(className);
        //elementsの中で()に囲まれた部分と、ドットの削除
        Pattern parenthesis_pattern = Pattern.compile("\\([^()]*\\)");
        Matcher matcher = parenthesis_pattern.matcher(elements.text());
        String without_parenthesis = matcher.replaceAll("");
        String translatedWords = without_parenthesis.replaceAll("\\.", "");
        ArrayList<String> translatedWordList = new ArrayList<>(List.of(translatedWords.split(";\\s*|、|；")));
        ArrayList<String> resultList = new ArrayList<>(
                List.of(translatedWordList
                        .stream()
                        .distinct()
                        .toArray(String[]::new)
                )
        );
        return new SearchDto(resultList);
    }

    public SearchDto translateEnglishToJapanese(String url, String className) throws IOException {
        Document document;
        try {
            document = Jsoup.connect(url).get();
        } catch (HttpStatusException e) {
            // HttpStatusのエラー(400~,500~など)
            return new SearchDto(new ArrayList<>(List.of("Error:StatusCode is " + e.getStatusCode())));
        }
        Elements elements = document.select(className);
        //elementsの中で()に囲まれた部分と、ドットの削除
        Pattern parenthesis_pattern = Pattern.compile("\\([^()]*\\)");
        Matcher matcher = parenthesis_pattern.matcher(elements.text());
        String without_parenthesis = matcher.replaceAll("");
        String translatedWords = without_parenthesis.replaceAll("\\.", "");
        ArrayList<String> kanjiList = new ArrayList<>(List.of(translatedWords.split(";\\s*|、|；")));
        ArrayList<String> resultList = new ArrayList<>();
        for (String translatedWordsKanji : kanjiList) {
            ArrayList<String> hiraganaList = changeHiragana(
                    "https://www.weblio.jp/content/" + translatedWordsKanji,
                    ".kiji"
            ).result;
            resultList.addAll(hiraganaList);
        }
        return new SearchDto(resultList);
    }

    public boolean isContainWord(List<String> resultList) {
        for (String result : resultList) {
            if (StringUtils.hasText(result)) {
                return true;
            }
        }
        return false;
    }

    public SearchDto changeHiragana(String url, String className) throws IOException {
        Document document;
        try {
            document = Jsoup.connect(url).get();
        } catch (HttpStatusException e) {
            // HttpStatusのエラー(400~,500~など)
            return new SearchDto(new ArrayList<>(List.of("Error:StatusCode is " + e.getStatusCode())));
        }

        Elements elements = document.select(className);
        String text = elements.text();

        Pattern pattern = Pattern.compile("読み方：(\\p{InHiragana}+)");
        Matcher matcher = pattern.matcher(text);

        ArrayList<String> hiraganaList = new ArrayList<>();
        while (matcher.find()) {
            String reading = matcher.group(1);
            hiraganaList.add(reading);
        }
        ArrayList<String> resultList = new ArrayList<>(
                List.of(hiraganaList
                        .stream()
                        .distinct()
                        .toArray(String[]::new)
                )
        );

        return new SearchDto(resultList);
    }
}


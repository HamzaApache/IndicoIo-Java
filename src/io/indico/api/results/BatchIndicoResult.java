package io.indico.api.results;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.indico.api.Api;
import io.indico.api.image.FacialEmotion;
import io.indico.api.text.Category;
import io.indico.api.text.Language;
import io.indico.api.text.PoliticalClass;
import io.indico.api.text.TextTag;
import io.indico.api.utils.EnumParser;
import io.indico.api.utils.IndicoException;

/**
 * Created by Chris on 6/23/15.
 */
public class BatchIndicoResult {
    Map<Api, List<?>> results;

    @SuppressWarnings("unchecked")
    public BatchIndicoResult(Api api, Map<String, ?> response) throws IndicoException {
        this.results = new HashMap<>();
        if (api.getResults() == null)
            results.put(api, (List<?>) response.get("results"));
        else {
            if (response.containsKey("error")) {
                throw new IndicoException(api.name + " failed with error " + response.get("error"));
            }
            Map<String, ?> responses = (Map<String, ?>) response.get("results");
            for (Api res : api.results) {
                if (!responses.containsKey(res.name))
                    continue;
                Map<String, ?> apiResponse = (Map<String, ?>) responses.get(res.name);
                if (apiResponse.containsKey("error"))
                    throw new IndicoException(res.name + " failed with error " + apiResponse.get("error"));
                results.put(res, (List<?>) apiResponse.get("results"));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<Double> getSentiment() throws IndicoException {
        return (List<Double>) get(Api.Sentiment);
    }

    @SuppressWarnings("unchecked")
    public List<Double> getSentimentHQ() throws IndicoException {
        return (List<Double>) get(Api.SentimentHQ);
    }

    @SuppressWarnings("unchecked")
    public List<Map<PoliticalClass, Double>> getPolitical() throws IndicoException {
        return EnumParser.parse(PoliticalClass.class, ((List<Map<String, Double>>) get(Api.Political)));
    }

    @SuppressWarnings("unchecked")
    public List<Map<Language, Double>> getLanguage() throws IndicoException {
        return EnumParser.parse(Language.class, ((List<Map<String, Double>>) get(Api.Language)));
    }

    @SuppressWarnings("unchecked")
    public List<Map<TextTag, Double>> getTextTags() throws IndicoException {
        return EnumParser.parse(TextTag.class, ((List<Map<String, Double>>) get(Api.TextTags)));
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Map<Category, Double>>> getNamedEntities() throws IndicoException {
        List<Map<String, Map<Category, Double>>> result = new ArrayList<>();

        List<Map<String, Map<String, Object>>> responses = (List<Map<String, Map<String, Object>>>) get(Api.NamedEntities);
        for (Map<String, Map<String, Object>> response : responses) {
            Map<String, Map<Category, Double>> each = new HashMap<>();
            for (Map.Entry<String, Map<String, Object>> entry : response.entrySet()) {
                Map<String, Double> res = new HashMap<>();

                res.putAll((Map<String, Double>) entry.getValue().remove("categories"));
                res.put("confidence", (Double) entry.getValue().get("confidence"));
                each.put(entry.getKey(), EnumParser.parse(Category.class, res));
            }
            result.add(each);
        }

        return result;
    }


    @SuppressWarnings("unchecked")
    public List<Map<FacialEmotion, Double>> getFer() throws IndicoException {
        return EnumParser.parse(FacialEmotion.class, ((List<Map<String, Double>>) get(Api.FER)));
    }

    @SuppressWarnings("unchecked")
    public List<Map<Point, Map<FacialEmotion, Double>>> getLocalizedFer() throws IndicoException {
        List<Map<Point, Map<FacialEmotion, Double>>> ret = new ArrayList<>();

        try {
            List<List<Map<String, Object>>> result = (List<List<Map<String, Object>>>) get(Api.FER);
            for (List<Map<String, Object>> res : result) {
                Map<Point, Map<FacialEmotion, Double>> parsed = new HashMap<>();
                for (Map<String, Object> each : res) {
                    int[] point = (int[]) each.get("location");
                    parsed.put(new Point(point[0], point[1]), EnumParser.parse(FacialEmotion.class, (Map<String, Double>) each.get("emotions")));
                }
                ret.add(parsed);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        return ret;
    }

    @SuppressWarnings("unchecked")
    public List<List<Double>> getImageFeatures() throws IndicoException {
        return (List<List<Double>>) get(Api.ImageFeatures);
    }

    @SuppressWarnings("unchecked")
    public List<List<Double>> getFacialFeatures() throws IndicoException {
        return (List<List<Double>>) get(Api.FacialFeatures);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Double>> getKeywords() throws IndicoException {
        return (List<Map<String, Double>>) get(Api.Keywords);
    }

    @SuppressWarnings("unchecked")
    public List<Double> getContentFIltering() throws IndicoException {
        return (List<Double>) get(Api.ContentFiltering);
    }

    private List<?> get(Api name) throws IndicoException{
        if (!results.containsKey(name))
            throw new IndicoException(name.name + " was not included in the request");
        return results.get(name);
    }
}


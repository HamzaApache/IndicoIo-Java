package io.indico.api.results;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.indico.api.Api;
import io.indico.api.image.FacialEmotion;
import io.indico.api.text.Language;
import io.indico.api.text.PoliticalClass;
import io.indico.api.text.TextTag;
import io.indico.api.utils.EnumParser;
import io.indico.api.utils.IndicoException;

/**
 * Created by Chris on 6/22/15.
 */
public class IndicoResult {
    Map<Api, Object> results;

    @SuppressWarnings("unchecked")
    public IndicoResult(Api api, Map<String, ?> response) throws IndicoException {
        this.results = new HashMap<>();
        if (api.getResults() == null)
            results.put(api, response.get("results"));
        else {
            Map<String, ?> responses = (Map<String, ?>) response.get("results");
            for (Api res : api.results) {
                Map<String, ?> apiResponse = (Map<String, ?>) responses.get(res.name);
                if (apiResponse == null)
                    continue;
                if (apiResponse.containsKey("error"))
                    throw new IndicoException(api.name + " failed with error " + apiResponse.get("error"));
                results.put(res, apiResponse.get("results"));
            }
        }
    }
    public Double getSentiment() throws IndicoException {
        return (Double) get(Api.Sentiment);
    }

    public Double getSentimentHQ() throws IndicoException {
        return (Double) get(Api.SentimentHQ);
    }

    @SuppressWarnings("unchecked")
    public Map<PoliticalClass, Double> getPolitical() throws IndicoException {
        return EnumParser.politinum((Map<String, Double>) get(Api.Political));
    }

    @SuppressWarnings("unchecked")
    public Map<Language, Double> getLanguage() throws IndicoException {
        return EnumParser.langnum((Map<String, Double>) get(Api.Language));
    }

    @SuppressWarnings("unchecked")
    public Map<TextTag, Double> getTextTags() throws IndicoException {
        return EnumParser.tagnum((Map<String, Double>) get(Api.TextTags));
    }

    @SuppressWarnings("unchecked")
    public Map<FacialEmotion, Double> getFer() throws IndicoException {
        return EnumParser.fernum((Map<String, Double>) get(Api.FER));
    }

    @SuppressWarnings("unchecked")
    public List<Double> getImageFeatures() throws IndicoException {
        return (List<Double>) get(Api.ImageFeatures);
    }

    @SuppressWarnings("unchecked")
    public List<Double> getFacialFeatures() throws IndicoException {
        return (List<Double>) get(Api.FacialFeatures);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Double> getKeywords() throws IndicoException {
        return (Map<String, Double>) get(Api.Keywords);
    }

    @SuppressWarnings("unchecked")
    public Double getNudityDetection() throws IndicoException {
        return (Double) get(Api.NudityDetection);
    }

    private Object get(Api name) throws IndicoException{
        if (!results.containsKey(name))
            throw new IndicoException(name.name + " was not included in the request");
        return results.get(name);
    }
}

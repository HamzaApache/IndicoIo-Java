package io.indico.test;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import io.indico.Indico;
import io.indico.api.results.BatchIndicoResult;
import io.indico.api.utils.IndicoException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Chris on 9/3/15.
 */
public class TestPlaces {

    @Test
    public void testSingle() throws IOException, IndicoException {
        Indico test = new Indico(new File("config.properties"));

        String example = "Lets all go to Virginia beach before it gets too cold to wander outside.";
        List<Map<String, Object>> results = test.places.predict(example).getPlaces();

        boolean expected = false;
        for (Map<String, Object> obj : results) {
            if (obj.get("text").equals("Virginia")) {
                expected = true;
                break;
            }
        }

        assertTrue(expected);
    }

    @Test
    public void testSingleV1() throws IOException, IndicoException {
        Indico test = new Indico(new File("config.properties"));
        Map params = new HashMap();
        params.put("version", 1);

        String example = "Lets all go to Virginia beach before it gets too cold to wander outside.";
        List<Map<String, Object>> results = test.places.predict(example, params).getPlaces();

        boolean expected = false;
        for (Map<String, Object> obj : results) {
            if (obj.get("text").equals("Virginia")) {
                expected = true;
                break;
            }
        }

        assertTrue(expected);
    }

    @Test
    public void testBatch() throws IOException, IndicoException {
        Indico test = new Indico(new File("config.properties"));

        final String example = "Lets all go to Virginia beach before it gets too cold to wander outside.";
        BatchIndicoResult result = test.places.predict(new String[] {example, example});
        List<List<Map<String, Object>>> results = result.getPlaces();
        assertTrue(results.size() == 2);
        boolean expected = false;
        for (Map<String, Object> obj : results.get(0)) {
            if (obj.get("text").equals("Virginia")) {
                expected = true;
                break;
            }
        }

        assertTrue(expected);
    }

    @Test
    public void testBatchV1() throws IOException, IndicoException {
        Indico test = new Indico(new File("config.properties"));
        Map params = new HashMap();
        params.put("version", 1);

        final String example = "Lets all go to Virginia beach before it gets too cold to wander outside.";
        BatchIndicoResult result = test.places.predict(new String[] {example, example}, params);
        List<List<Map<String, Object>>> results = result.getPlaces();
        assertTrue(results.size() == 2);
        boolean expected = false;
        for (Map<String, Object> obj : results.get(0)) {
            if (obj.get("text").equals("Virginia")) {
                expected = true;
                break;
            }
        }

        assertTrue(expected);
    }
}

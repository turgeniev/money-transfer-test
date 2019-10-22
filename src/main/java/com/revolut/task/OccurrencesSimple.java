package com.revolut.task;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OccurrencesSimple implements Occurrences {

    @Override
    public Map<Character, Integer> count(String s) {
        if (s == null || s.length() == 0) {
            return Collections.emptyMap();
        }

        Map<Character, Integer> result = new HashMap<>();

        for(int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            Integer count = result.get(ch);

            if (count == null) {
                result.put(ch, 1);
            } else {
                result.put(ch, count + 1);
            }
        }

        return result;
    }
}

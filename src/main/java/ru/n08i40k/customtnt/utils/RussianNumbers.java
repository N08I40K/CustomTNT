package ru.n08i40k.customtnt.utils;

import ru.n08i40k.npluginlocale.LocaleRequestBuilder;

public class RussianNumbers {
    private final static LocaleRequestBuilder localeRoot =
            new LocaleRequestBuilder(null, "russian_words");

    public static String getWord(String wordKey, int count) {
        int endNum = Math.floorMod(count < 0 ? -count : count, 10);

        LocaleRequestBuilder wordRoot = localeRoot.extend(wordKey);

        String word = wordRoot.get("word").getSingle().get();

        String endKey = "other";
        if (endNum == 0)
            endKey = "zero";
        else if (endNum == 1)
            endKey = "one";
        else if (endNum < 5)
            endKey = "below-five";

        return count + " " + word + wordRoot.extend("ends").get(endKey).getSingle().get();
    }

    public static String getWord(String wordKey, float count) {
        return getWord(wordKey, (int) Math.floor(count));
    }

    public static String getWord(String wordKey, double count) {
        return getWord(wordKey, (int) Math.floor(count));
    }
}

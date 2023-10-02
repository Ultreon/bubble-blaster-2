package com.ultreon.libs.text.v1;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;

class AttributedStringUtil {

    public static AttributedString concat(AttributedString first, AttributedString second, String separation) {
        String firstString = AttributedStringUtil.getString(first);
        String secondString = AttributedStringUtil.getString(second);
        String resultString = firstString + separation + secondString;
        AttributedString result = new AttributedString(resultString);
        AttributedStringUtil.addAttributes(result, first, second, separation.length());
        return result;
    }

    public static AttributedString concat(AttributedString first, AttributedString second) {
        return AttributedStringUtil.concat(first, second, "");
    }

    private static void addAttributes(AttributedString result, AttributedString first, AttributedString second, int separationOffset) {
        AttributedCharacterIterator resultIterator = result.getIterator();
        AttributedCharacterIterator firstIterator = first.getIterator();
        AttributedCharacterIterator secondIterator = second.getIterator();

        char resultCharacter = resultIterator.current();
        int truePosition = 0;
        int usePosition;

        while (resultCharacter != CharacterIterator.DONE) {
            usePosition = truePosition;
            AttributedCharacterIterator it = AttributedStringUtil.getIterator(firstIterator, secondIterator);
            if (it == null) {
                break;
            }
            if (it == secondIterator) {
                usePosition += separationOffset;
            }
            result.addAttributes(it.getAttributes(), usePosition, usePosition + 1);
            resultCharacter = resultIterator.next();
            it.next();
            truePosition++;
        }
    }

    private static AttributedCharacterIterator getIterator(AttributedCharacterIterator firstIterator, AttributedCharacterIterator secondIterator) {
        if (firstIterator.current() != CharacterIterator.DONE) {
            return firstIterator;
        }
        if (secondIterator.current() != CharacterIterator.DONE) {
            return secondIterator;
        }
        return null;

    }

    public static String getString(AttributedString attributedString) {
        AttributedCharacterIterator it = attributedString.getIterator();
        StringBuilder stringBuilder = new StringBuilder();

        char ch = it.current();
        while (ch != CharacterIterator.DONE) {
            stringBuilder.append(ch);
            ch = it.next();
        }
        return stringBuilder.toString();
    }
}
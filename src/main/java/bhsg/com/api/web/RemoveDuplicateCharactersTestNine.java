package bhsg.com.api.web;

import java.util.*;

public class RemoveDuplicateCharactersTestNine {

    public static void main(String args[]){
        System.out.println(removeDuplicateCharactersTestNine("programming"));
    }

    private static String removeDuplicateCharactersTestNine(final String stringToNormalize) {
        Set<Character> normalized = new LinkedHashSet<>();

        for (char c : stringToNormalize.toCharArray()) {
            normalized.add(c);
        }

        StringBuilder result = new StringBuilder();
        for (char c : normalized) {
            result.append(c);
        }

        return result.toString();
    }

}

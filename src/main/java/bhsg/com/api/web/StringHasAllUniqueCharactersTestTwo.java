package bhsg.com.api.web;

import java.util.HashSet;
import java.util.Set;

public class StringHasAllUniqueCharactersTestTwo {

    public static void main(String args[]){
        System.out.println(allCharactersAreUnique("What if you"));
    }

    private static boolean allCharactersAreUnique(final String input){
        Set<Character> seen = new HashSet<>();

        for (char c : input.toCharArray()) {
            if (!seen.add(c)) {
                return false; // Duplicate found
            }
        }

        return true; // All characters were unique
    }

}

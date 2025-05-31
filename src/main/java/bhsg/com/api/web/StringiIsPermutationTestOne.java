package bhsg.com.api.web;

import java.util.*;

public class StringiIsPermutationTestOne {

    public static void main(String args[]){
        System.out.println(isPermutation("abc", "abz1"));
    }

    private static boolean isPermutation(final String stringOne, final String stringTwo){

        if(stringOne.length() != stringTwo.length()){
            return false;
        }

        Map<Character, Integer> map = new HashMap<>();

        for (char character : stringOne.toCharArray()) {
            map.put(character, map.getOrDefault(character, 0) + 1);
        }


        for (char character : stringTwo.toCharArray()) {
            if (!map.containsKey(character)) {
                return false;
            }

            map.put(character, map.get(character) - 1);

            if (map.get(character) < 0) {
                return false;
            }
        }

        return true;

    }

}

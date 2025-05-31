package bhsg.com.api.web;

import java.util.HashMap;
import java.util.Map;

public class IsStringPermutation {

    public static void main(String args[]){
        System.out.println(isPermutation("listen", "silent"));
    }

    public static boolean isPermutation(String stringOne, String stringTwo) {
        if (stringOne.length() != stringTwo.length()) return false;

        Map<Character, Integer> map = new HashMap<>();

        for (char c : stringOne.toCharArray()) {
            map.put(c, map.getOrDefault(c, 0) + 1);
        }

        for (char c : stringTwo.toCharArray()) {
            if (!map.containsKey(c)) return false;
            map.put(c, map.get(c) - 1);
            if (map.get(c) < 0) return false;
        }

        return true;
    }

}

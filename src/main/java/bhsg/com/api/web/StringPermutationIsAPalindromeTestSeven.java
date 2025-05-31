package bhsg.com.api.web;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class StringPermutationIsAPalindromeTestSeven {

    public static void main(String args[]){
        System.out.println(isPermutationOfPalindrome("hello"));
    }

    private static boolean isPermutationOfPalindrome(final String string){

        final var normalizedString = string.toLowerCase().replaceAll("\\s+", "").split("");

        Map<String, Integer> map = new HashMap<>();

        for (String character : normalizedString) {
            map.put(character, map.getOrDefault(character, 0) + 1);
        }

        AtomicInteger oddCount = new AtomicInteger(0);
        map.values().forEach(value -> {

            if((value % 2) != 0){
                oddCount.getAndIncrement();
            }

        });

        return oddCount.get() <= 0;
    }
}

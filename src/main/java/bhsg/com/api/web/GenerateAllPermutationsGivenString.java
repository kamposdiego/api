package bhsg.com.api.web;

import java.util.ArrayList;
import java.util.List;

public class GenerateAllPermutationsGivenString {

    public static void main(String[] args) {
        String input = "2\nabc\ndfri";
        List<String> permutations = generatePermutations(input);
        permutations.forEach(System.out::println);
    }

    public static List<String> generatePermutations(String rawInput) {
        String[] lines = rawInput.strip().split("\\R");

        List<String> result = new ArrayList<>();

        for (int i = 1; i < lines.length; i++) {
            String word = lines[i].strip();

            if (!word.isEmpty()){
                List<String> permutation = new ArrayList<>();
                permute(word.toCharArray(), 0, permutation);
                result.add(String.join(", ", permutation));
            }

        }

        return result;
    }

    private static void permute(char[] chars, int index, List<String> result) {
        if (index == chars.length - 1) {
            result.add(new String(chars));
            return;
        }

        for (int i = index; i < chars.length; i++) {
            swap(chars, i, index);
            permute(chars, index + 1, result);
            swap(chars, i, index); // backtrack
        }
    }

    private static void swap(char[] chars, int i, int j) {
        char temp = chars[i];
        chars[i] = chars[j];
        chars[j] = temp;
    }
}

package bhsg.com.api.web;

public class OneEditAwayTestEight {

    public static void main(String args[]){
        System.out.println(oneEditAway("pale", "pales"));
    }

    private static boolean oneEditAway(final String s1, final String s2) {
        final var str1 = s1.toLowerCase().replaceAll("\\s+", "");
        final var str2 = s2.toLowerCase().replaceAll("\\s+", "");

        if (Math.abs(str1.length() - str2.length()) > 1) {
            return false;
        }

        String shorter = str1.length() < str2.length() ? str1 : str2;
        String longer  = str1.length() < str2.length() ? str2 : str1;

        int index1 = 0;
        int index2 = 0;
        boolean foundDifference = false;

        while (index1 < shorter.length() && index2 < longer.length()) {
            if (shorter.charAt(index1) != longer.charAt(index2)) {
                if (foundDifference) return false;
                foundDifference = true;

                if (shorter.length() == longer.length()) {
                    index1++; // Replace case
                }
            } else {
                index1++; // Only move shorter pointer if matching
            }

            index2++; // Always move longer pointer
        }

        return true;
    }

}

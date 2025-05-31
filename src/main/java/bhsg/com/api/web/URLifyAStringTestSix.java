package bhsg.com.api.web;

public class URLifyAStringTestSix {

    public static void main(String args[]){
        System.out.println(urlifyAStringTestSix("Mr John Smith    ", 13));
    }

    private static String urlifyAStringTestSix(String str, int trueLength) {
        if (str == null || trueLength == 0) return str;

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < trueLength; i++) {
            char c = str.charAt(i);
            if (c == ' ') {
                result.append("%20");
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}

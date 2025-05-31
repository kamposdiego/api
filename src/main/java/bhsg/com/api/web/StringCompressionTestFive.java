package bhsg.com.api.web;

public class StringCompressionTestFive {

    public static void main(String args[]){
        System.out.println(stringCompression("abc"));
    }

    private static String stringCompression(String value){
        if (value == null || value.isEmpty()) return value;

        char[] chars = value.toCharArray();
        StringBuilder compressed = new StringBuilder();

        char previousChar = chars[0];
        int count = 1;

        for (int i = 1; i < chars.length; i++) {
            if (chars[i] == previousChar) {
                count++;
            } else {
                compressed.append(previousChar).append(count);
                previousChar = chars[i];
                count = 1;
            }
        }

        // Append the last sequence
        compressed.append(previousChar).append(count);

        return compressed.length() < value.length() ? compressed.toString() : value;

    }

}

package bhsg.com.api.web;

public class StringIsRotationTestThree {

    public static void main(String args[]){

        System.out.println(isRotation("pale", "ple"));

    }

    private static boolean isRotation(final String inputOne, final String inputTwo){
        return (inputOne+inputOne).contains(inputTwo);
    }

}

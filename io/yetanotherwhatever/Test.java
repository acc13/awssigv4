package io.yetanotherwhatever;

/**
 * Created by achang on 10/7/2016.
 */
public class Test {

    public static void main(String args[])
    {
        String myString = "foobar";
        System.out.println(findFirstNonRepeating(myString));


    }
    private static String findFirstNonRepeating(String in)
    {
        boolean start = true;
        char lastC = '\0';
        for (char c : in.toCharArray())
        {
            if(start != true)
            {
                if (lastC != c)
                {
                    StringBuilder sb = new StringBuilder();
                    sb.append(c);
                    return sb.toString();
                }
            }
            start = false;
            lastC = c;

        }

        return null;
    }
}

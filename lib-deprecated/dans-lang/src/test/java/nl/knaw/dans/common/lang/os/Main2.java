package nl.knaw.dans.common.lang.os;

public class Main2
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println("MAIN2 > Hello, this is " + Main2.class.getName());
        System.out.println("MAIN2 > I'm going to print digits from 0 to 9 with a pause of 1 second between each.");
        for (int i = 0; i < 10; i++)
        {
            seconds(1L);
            System.out.println("MAIN2 > current digit is " + i);
        }

        System.out.println("MAIN2 > Now I'm going to raise an error!");
        throw new IllegalStateException("I have no more lines of code to execute!");
    }

    public static void seconds(long s)
    {
        milliSeconds(s * 1000);
    }

    public static void milliSeconds(long ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (InterruptedException e)
        {
            System.err.println("Wait time interrupted: ");
            e.printStackTrace();
        }
    }

}

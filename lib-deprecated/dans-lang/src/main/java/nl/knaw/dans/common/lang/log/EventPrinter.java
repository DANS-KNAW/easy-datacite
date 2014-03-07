package nl.knaw.dans.common.lang.log;

public interface EventPrinter
{

    String SPACE = " ";
    String SEPARATOR = ";";
    String NL = "\n";
    String NLT = "\n\t";
    String EMPTY_CELL = "[--]";
    String LONG_LINE = "--------------------------------------------------------------------------------------";

    String printHeader(Event event);

    String print(Event event);

}

package nl.knaw.dans.easy.domain.deposit.discipline;

import java.util.List;

public class ChoiceListDump
{

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        ChoiceList cl = ChoiceListCache.getInstance().getList(ChoiceList.LID_ARCHAEOLOGY_DC_SUBJECT);
        List<KeyValuePair> choices = cl.getChoices();
        String enumeration = new KvpPrinter().printXsEnumeration(choices);
        System.err.println(enumeration);
    }

}

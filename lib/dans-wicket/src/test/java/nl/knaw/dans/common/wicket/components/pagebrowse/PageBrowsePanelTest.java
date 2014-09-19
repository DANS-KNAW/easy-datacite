package nl.knaw.dans.common.wicket.components.pagebrowse;

import static org.junit.Assert.assertEquals;

import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

public class PageBrowsePanelTest {
    @Test
    public void computeLinks() {
        new WicketTester();
        int pageSize = 10;
        int[] totalHits = {1, 15, 15, 15, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
        int[] pageStart = {1, 1, 10, 14, 1, 15, 25, 35, 45, 55, 61, 80, 90, 91};
        String[] expected = {"1 false 1|", "1 false 1|2 true 2|2 true next|", "1 false 1|2 true 2|2 true next|", "1 true previous|1 true 1|2 false 2|",
                "1 false 1|2 true 2|3 true 3|4 true 4|5 true 5|-1 false ...|10 true 10|2 true next|",
                "1 true previous|1 true 1|2 false 2|3 true 3|4 true 4|5 true 5|-1 false ...|10 true 10|3 true next|",
                "2 true previous|1 true 1|2 true 2|3 false 3|4 true 4|5 true 5|-1 false ...|10 true 10|4 true next|",
                "3 true previous|1 true 1|2 true 2|3 true 3|4 false 4|5 true 5|6 true 6|-1 false ...|10 true 10|5 true next|",
                "4 true previous|1 true 1|-1 false ...|3 true 3|4 true 4|5 false 5|6 true 6|7 true 7|-1 false ...|10 true 10|6 true next|",
                "5 true previous|1 true 1|-1 false ...|4 true 4|5 true 5|6 false 6|7 true 7|8 true 8|-1 false ...|10 true 10|7 true next|",
                "6 true previous|1 true 1|-1 false ...|5 true 5|6 true 6|7 false 7|8 true 8|9 true 9|10 true 10|8 true next|",
                "7 true previous|1 true 1|-1 false ...|6 true 6|7 true 7|8 false 8|9 true 9|10 true 10|9 true next|",
                "8 true previous|1 true 1|-1 false ...|6 true 6|7 true 7|8 true 8|9 false 9|10 true 10|10 true next|",
                "9 true previous|1 true 1|-1 false ...|6 true 6|7 true 7|8 true 8|9 true 9|10 false 10|"

        };
        for (int i = 0; i < expected.length; i++) {
            PageBrowseData data = new PageBrowseData(pageStart[i], pageSize, totalHits[i], 2);
            PageBrowsePanel panel = new PageBrowsePanel("test", new Model<PageBrowseData>(data), null);
            assertEquals(expected[i], panel.printLinks());
        }
    }

}

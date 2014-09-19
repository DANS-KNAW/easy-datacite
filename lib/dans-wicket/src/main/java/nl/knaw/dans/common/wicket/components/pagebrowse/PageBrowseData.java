package nl.knaw.dans.common.wicket.components.pagebrowse;

import java.io.Serializable;

/**
 * A model for browsing through items on pages. In this model there is the notion of a window of pages following the current page. In the next example the
 * <code>currentPage</code> is page# 5, the <code>windowSize</code> in this example is 2, and the <code>separator</code> is '|'.
 * 
 * <pre>
 *                                                         currentPage
 *                                                       /
 *      previous   |   1   |   ...   |   3   |   4   |   5   |   6   |   7   |   ...   |   9   |   next
 *                                       |_______________|
 *                                          windowSize
 * </pre>
 * 
 * @see PageBrowseData
 * @author ecco
 * @author lobo (refactored)
 */
public class PageBrowseData implements Serializable {
    private static final long serialVersionUID = -9163020926411003131L;

    public static final int FIRSTPAGE = 1;
    // window size is symmetrical: size 2 means 2 on each side of the start, thus 4
    public static final int DEFAULT_WINDOWSIZE = 4;

    private int windowSize;
    private int windowStart;
    private int windowEnd;
    private int currentPage;
    private int lastPage;
    private int pageSize;
    private int totalItems;

    /**
     * @param itemStart
     * @param pageSize
     * @param totalItems
     */
    public PageBrowseData(int itemStart, int pageSize, int totalItems) {
        init(itemStart, pageSize, totalItems);
    }

    /**
     * @param itemStart
     * @param pageSize
     * @param totalItems
     */
    public PageBrowseData(int itemStart, int pageSize, int totalItems, int windowSize) {
        init(itemStart, pageSize, totalItems, windowSize);
    }

    public void init(int itemStart, int pageSize, int totalItems) {
        init(itemStart, pageSize, totalItems, DEFAULT_WINDOWSIZE);
    }

    public void init(int itemStart, int pageSize, int totalItems, int windowSize) {
        this.pageSize = pageSize;
        this.windowSize = windowSize;
        this.totalItems = totalItems;
        if (pageSize == 0 || itemStart == 0) {
            currentPage = 1;
            lastPage = 1;
            windowStart = 1;
            windowEnd = 1;
        } else {
            currentPage = itemStart / pageSize + (itemStart % pageSize != 0 ? 1 : 0);
            lastPage = totalItems / pageSize + (totalItems % pageSize != 0 ? 1 : 0);
            calculateWindow();
        }
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
        calculateWindow();
    }

    public boolean hasPrevious() {
        return currentPage > FIRSTPAGE;
    }

    public boolean hasNext() {
        return currentPage < lastPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public int getWindowStart() {
        return windowStart;
    }

    public int getWindowEnd() {
        return windowEnd;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    private void calculateWindow() {
        windowStart = calcWindowStart();
        windowEnd = calcWindowEnd();
    }

    private int calcWindowStart() {
        int start = currentPage - windowSize;
        if (start > lastPage - 2 * windowSize) {
            start = lastPage - 2 * windowSize;
        }
        if (start < FIRSTPAGE) {
            start = FIRSTPAGE;
        }
        if (start > currentPage) {
            start = currentPage;
        }
        return start;
    }

    private int calcWindowEnd() {
        int end = windowStart + 2 * windowSize;
        if (end > lastPage) {
            end = lastPage;
        }
        return end;
    }

}

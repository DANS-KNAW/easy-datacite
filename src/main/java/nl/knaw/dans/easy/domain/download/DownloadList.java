package nl.knaw.dans.easy.domain.download;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.jibx.AbstractTimestampedJiBXObject;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadList extends AbstractTimestampedJiBXObject<DownloadList> implements MetadataUnit
{

    public static final String UNIT_ID = "DLHL";

    public static final String UNIT_LABEL = "A list of downloads";

    public static final String UNIT_FORMAT = "http://easy.dans.knaw.nl/easy/download-list/";

    public static final URI UNIT_FORMAT_URI = URI.create(UNIT_FORMAT);

    public enum Level
    {
        FILE_ITEM, DATASET, COLLECTION, STORE, GLOBAL
    }

    public static final int TYPE_ALL = 0;
    public static final int TYPE_YEAR = 1;
    public static final int TYPE_MONTH = 2;
    public static final int TYPE_WEEK = 3;

    private static final long serialVersionUID = -319269942867841432L;

    private static final Logger logger = LoggerFactory.getLogger(DownloadList.class);

    private final int listType;
    private final Level level;
    private DateTime startDate;

    @SuppressWarnings("unused")
    private int year;
    @SuppressWarnings("unused")
    private int month;
    @SuppressWarnings("unused")
    private int week;

    private int downloadCount;
    private long totalBytes;
    private List<DownloadRecord> records = new ArrayList<DownloadRecord>();

    public DownloadList()
    {
        this(0, null);
    }

    public DownloadList(int listType)
    {
        this(listType, null);
    }

    public DownloadList(int listType, Level level)
    {
        this(listType, level, new DateTime());
    }

    public DownloadList(int listType, Level level, DateTime startDate)
    {
        this.listType = listType;
        this.level = level;
        this.startDate = startDate;
        year = getYear();
        month = getMonth();
        week = getWeek();
    }

    public int getListType()
    {
        return listType;
    }

    public DateTime getStartDate()
    {
        return startDate;
    }

    public Level getLevel()
    {
        return level;
    }

    public int getDownloadCount()
    {
        return downloadCount;
    }

    public long getTotalBytes()
    {
        return totalBytes;
    }

    public int getYear()
    {
        int y;
        switch (listType)
        {
        case TYPE_ALL:
            y = 0;
            break;
        case TYPE_YEAR:
            y = startDate.getYear();
            break;
        case TYPE_MONTH:
            y = startDate.getYear();
            break;
        case TYPE_WEEK:
            y = startDate.getWeekyear();
            break;
        default:
            y = -1;
            break;
        }
        return y;
    }

    public int getMonth()
    {
        int m;
        switch (listType)
        {
        case TYPE_ALL:
            m = 0;
            break;
        case TYPE_YEAR:
            m = 0;
            break;
        case TYPE_MONTH:
            m = startDate.getMonthOfYear();
            break;
        case TYPE_WEEK:
            m = 0;
            break;
        default:
            m = -1;
            break;
        }
        return m;
    }

    public int getWeek()
    {
        int w;
        switch (listType)
        {
        case TYPE_ALL:
            w = 0;
            break;
        case TYPE_YEAR:
            w = 0;
            break;
        case TYPE_MONTH:
            w = 0;
            break;
        case TYPE_WEEK:
            w = startDate.getWeekOfWeekyear();
            break;
        default:
            w = -1;
            break;
        }
        return w;
    }

    public String printPeriod()
    {
        return printPeriod(listType, startDate);
    }

    public static String printPeriod(int listType, DateTime startDate)
    {
        String period;
        switch (listType)
        {
        case TYPE_ALL:
            period = "all";
            break;
        case TYPE_YEAR:
            period = "year=" + startDate.getYear();
            break;
        case TYPE_MONTH:
            period = "year=" + startDate.getYear() + " month=" + startDate.getMonthOfYear();
            break;
        case TYPE_WEEK:
            period = "year=" + startDate.getWeekyear() + " week=" + startDate.getWeekOfWeekyear();
            break;
        default:
            period = "undefined";
            break;
        }
        return period;
    }

    public boolean accepts(DateTime downloadTime)
    {
        boolean belongsTo;
        switch (listType)
        {
        case TYPE_ALL:
            belongsTo = true;
            break;
        case TYPE_YEAR:
            belongsTo = startDate.getYear() == downloadTime.getYear();
            break;
        case TYPE_MONTH:
            belongsTo = startDate.getYear() == downloadTime.getYear() && startDate.getMonthOfYear() == downloadTime.getMonthOfYear();
            break;
        case TYPE_WEEK:
            belongsTo = startDate.getWeekyear() == downloadTime.getWeekyear() && startDate.getWeekOfWeekyear() == downloadTime.getWeekOfWeekyear();
            break;
        default:
            belongsTo = false;
            break;
        }
        return belongsTo;
    }

    public List<DownloadRecord> getRecords()
    {
        return Collections.unmodifiableList(records);
    }

    public Map<DateTime, List<DownloadRecord>> getDownloadsByTime()
    {
        Map<DateTime, List<DownloadRecord>> downloads = new LinkedHashMap<DateTime, List<DownloadRecord>>();
        for (DownloadRecord record : records)
        {
            List<DownloadRecord> list = downloads.get(record.getDownloadTime());
            if (list == null)
            {
                list = new ArrayList<DownloadRecord>();
                downloads.put(record.getDownloadTime(), list);
            }
            list.add(record);
        }
        return downloads;
    }

    public Map<String, List<DownloadRecord>> getDownloadsByUser()
    {
        Map<String, List<DownloadRecord>> downloads = new LinkedHashMap<String, List<DownloadRecord>>();
        for (DownloadRecord record : records)
        {
            List<DownloadRecord> list = downloads.get(record.getDownloaderId());
            if (list == null)
            {
                list = new ArrayList<DownloadRecord>();
                downloads.put(record.getDownloaderId(), list);
            }
            list.add(record);
        }
        return downloads;
    }

    public void addDownload(List<? extends ItemVO> downloadedItemVOs, User user, DateTime downloadTime)
    {
        checkDownloadTime(downloadTime);

        downloadCount++;
        for (ItemVO itemVO : downloadedItemVOs)
        {
            if (itemVO instanceof FileItemVO)
            {
                createRecord((FileItemVO) itemVO, user, downloadTime);
            }
        }
    }

    public void addDownload(FileItemVO downloadedFileItemVO, User user, DateTime downloadTime)
    {
        checkDownloadTime(downloadTime);

        downloadCount++;
        createRecord(downloadedFileItemVO, user, downloadTime);
    }

    public void addMigrationRecord(final DownloadRecord downloadRecord)
    {
        checkDownloadTime(downloadRecord.getDownloadTime());
        downloadCount++;
        add(downloadRecord);
    }

    private void checkDownloadTime(DateTime downloadTime)
    {
        if (!accepts(downloadTime))
        {
            throw new IllegalArgumentException("Download does not belong to this list." + " List period: " + printPeriod() + ", downloadTime: " + downloadTime);
        }
    }

    private void createRecord(FileItemVO fileItemVO, User user, DateTime downloadTime)
    {
        totalBytes += fileItemVO.getSize();
        if (Level.FILE_ITEM.equals(level))
        {
            addDownloadAtFileLivel(user, downloadTime);
        }
        else if (Level.DATASET.equals(level))
        {
            addDownloadAtDatasetLevel(fileItemVO, user, downloadTime);
        }
        else
        {
            addDownloadAtOtherLevel(fileItemVO, user, downloadTime);
        }
    }

    protected boolean add(DownloadRecord record)
    {
        return records.add(record);
    }

    private void addDownloadAtFileLivel(User user, DateTime downloadTime)
    {
        DownloadRecord record = new DownloadRecord();
        addRecord(user, downloadTime, record);
    }

    private void addDownloadAtDatasetLevel(FileItemVO fileItemVO, User user, DateTime downloadTime)
    {
        DownloadRecord record = new DownloadRecord();
        addRecord(fileItemVO, user, downloadTime, record);
    }

    private void addDownloadAtOtherLevel(FileItemVO fileItemVO, User user, DateTime downloadTime)
    {
        DownloadRecord record = new DownloadRecord();
        record.setDatasetId(fileItemVO.getDatasetSid());
        addRecord(fileItemVO, user, downloadTime, record);
    }

    private void addRecord(FileItemVO fileItemVO, User user, DateTime downloadTime, DownloadRecord record)
    {
        record.setFileItemId(fileItemVO.getSid());
        // TODO FileItemVO in single download has no path. Add path in Fedora DB
        try
        {
            record.setPath(fileItemVO.getPath());
        }
        catch (IllegalStateException e)
        {
            record.setPath(fileItemVO.getName());
        }
        record.setMimeType(fileItemVO.getMimetype());
        record.setSize(fileItemVO.getSize());
        addRecord(user, downloadTime, record);
    }

    private void addRecord(User user, DateTime downloadTime, DownloadRecord record)
    {
        record.setDownloaderId(user == null || user.isAnonymous() ? null : user.getId());
        record.setDownloadTime(downloadTime);
        add(record);
    }

    public String getUnitFormat()
    {
        return UNIT_FORMAT;
    }

    public URI getUnitFormatURI()
    {
        return UNIT_FORMAT_URI;
    }

    public String getUnitId()
    {
        return UNIT_ID;
    }

    public String getUnitLabel()
    {
        return UNIT_LABEL;
    }

    public boolean isVersionable()
    {
        return false;
    }

    public void setVersionable(boolean versionable)
    {
        logger.warn(this.getClass().getName() + " is permanently not versionable.");
    }

    @Override
    public boolean isDirty()
    {
        return true;
    }

}

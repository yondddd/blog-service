package com.yond.common.utils.date;

import com.yond.common.utils.json.adapter.DateAdapter;
import com.yond.common.utils.json.adapter.DateInitManager;
import org.apache.commons.collections.CollectionUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * DateFormatSupport
 * <p>
 *
 * @version 1.0
 * @created 2021/01/28 17:03
 **/
class DateFormatSupport implements DateAdapter {

    private final List<DateFormat> dateFormats = new ArrayList<DateFormat>();

    private static final DateFormatSupport INSTANCE = new DateFormatSupport();

    private final SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy");

    public DateFormatSupport() {
        dateFormats.add(DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US));

        List<String> dateFormatList = DateInitManager.getDateFormats();
        if (CollectionUtils.isNotEmpty(dateFormatList)) {
            for (String dateFormat : dateFormatList) {
                dateFormats.add(new SimpleDateFormat(dateFormat));
            }
        }
    }

    public static Date parse(String data) {
        return INSTANCE.parse0(data);
    }

    private Date parse0(String val) {
        return convert(val, dateFormats, yearDateFormat);
    }
}

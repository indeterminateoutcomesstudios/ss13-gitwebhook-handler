package io.github.spair.service.dmi.report;

import io.github.spair.ReadFileUtil;
import io.github.spair.service.dmi.entities.ReportEntry;
import io.github.spair.service.dmi.report.StatesNumberAppender;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StatesNumberAppenderTest {

    private final StatesNumberAppender appender = new StatesNumberAppender();

    @Test
    public void testAppend() {
        StringBuilder sb = new StringBuilder();
        ReportEntry reportEntry = new ReportEntry("icons/file.dmi");

        reportEntry.setOldStatesNumber(23);
        reportEntry.setNewStatesNumber(30);

        appender.append(sb, reportEntry);

        String expectedReport = ReadFileUtil.readFile("appenders-reports/states-number-report.txt");
        assertEquals(expectedReport, sb.toString());
    }

    @Test
    public void testAppendWithOverflow() {
        StringBuilder sb = new StringBuilder();
        ReportEntry reportEntry = new ReportEntry("icons/file.dmi");

        reportEntry.setOldStatesNumber(570);
        reportEntry.setNewStatesNumber(30);

        appender.append(sb, reportEntry);

        String expectedReport = ReadFileUtil.readFile("appenders-reports/states-number-overflow-report.txt");
        assertEquals(expectedReport, sb.toString());
    }
}
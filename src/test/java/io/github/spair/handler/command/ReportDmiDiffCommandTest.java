package io.github.spair.handler.command;

import io.github.spair.service.dmi.DmiService;
import io.github.spair.service.dmi.entity.DmiDiffStatus;
import io.github.spair.service.dmi.entity.ModifiedDmi;
import io.github.spair.service.github.GitHubService;
import io.github.spair.service.github.entity.IssueComment;
import io.github.spair.service.github.entity.PullRequestFile;
import io.github.spair.service.pr.entity.PullRequest;
import io.github.spair.service.report.ReportRenderService;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReportDmiDiffCommandTest {

    @Mock
    private GitHubService gitHubService;
    @Mock
    private DmiService dmiService;
    @Mock
    private ReportRenderService<DmiDiffStatus> reportService;

    private ReportDmiDiffCommand command;

    @Before
    public void setUp() {
        command = new ReportDmiDiffCommand(gitHubService, dmiService, reportService);
    }

    @Test
    public void testExecuteWithDiffsAndWithoutComment() {
        List<PullRequestFile> prFilesList = getPullRequestFileList();

        when(gitHubService.listPullRequestFiles(1)).thenReturn(prFilesList);
        when(dmiService.createModifiedDmi(any(PullRequestFile.class))).thenReturn(mock(ModifiedDmi.class));
        when(dmiService.createDmiDiffStatus(any(ModifiedDmi.class))).thenReturn(Optional.of(mock(DmiDiffStatus.class)));
        when(gitHubService.listIssueComments(1)).thenReturn(Lists.emptyList());
        when(reportService.renderStatus(anyList())).thenReturn("Fake Report");

        command.execute(PullRequest.builder().number(1).build());

        verify(gitHubService).createIssueComment(1, "Fake Report");
    }

    @Test
    public void testExecuteWithDiffsAndWithComment() {
        List<PullRequestFile> prFilesList = getPullRequestFileList();

        when(gitHubService.listPullRequestFiles(1)).thenReturn(prFilesList);
        when(dmiService.createModifiedDmi(any(PullRequestFile.class))).thenReturn(mock(ModifiedDmi.class));
        when(dmiService.createDmiDiffStatus(any(ModifiedDmi.class))).thenReturn(Optional.of(mock(DmiDiffStatus.class)));
        when(gitHubService.listIssueComments(1)).thenReturn(getIssueCommentList());
        when(reportService.renderStatus(anyList())).thenReturn("Fake Report");

        command.execute(PullRequest.builder().number(1).build());

        verify(gitHubService).editIssueComment(3, "Fake Report");
    }

    @Test
    public void testExecuteWithoutDiffs() {
        when(gitHubService.listPullRequestFiles(1)).thenReturn(Lists.emptyList());;

        command.execute(PullRequest.builder().number(1).build());

        verify(gitHubService, never()).editIssueComment(anyInt(), anyString());
        verify(gitHubService, never()).createIssueComment(anyInt(), anyString());
    }

    private List<PullRequestFile> getPullRequestFileList() {
        PullRequestFile prFile1 = new PullRequestFile();
        prFile1.setFilename("test/filename1.png");
        PullRequestFile prFile2 = new PullRequestFile();
        prFile2.setFilename("test/filename2.dmi");
        return Lists.newArrayList(prFile1, prFile2);
    }

    private List<IssueComment> getIssueCommentList() {
        IssueComment comment1 = new IssueComment();
        comment1.setBody("#123");
        comment1.setId(4);
        IssueComment comment2 = new IssueComment();
        comment2.setBody("## DMI Diff Report");
        comment2.setId(3);
        return Lists.newArrayList(comment1, comment2);
    }
}
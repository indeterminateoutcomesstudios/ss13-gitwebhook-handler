package io.github.spair.handler.command.changelog;

import io.github.spair.handler.command.HandlerCommand;
import io.github.spair.service.changelog.ChangelogService;
import io.github.spair.service.changelog.entity.Changelog;
import io.github.spair.service.config.ConfigService;
import io.github.spair.service.github.GitHubService;
import io.github.spair.service.pr.entity.PullRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UpdateChangelogCommand implements HandlerCommand<PullRequest> {

    private final ChangelogService changelogService;
    private final ConfigService configService;
    private final GitHubService gitHubService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateChangelogCommand.class);

    @Autowired
    public UpdateChangelogCommand(
            final ChangelogService changelogService,
            final ConfigService configService,
            final GitHubService gitHubService) {
        this.changelogService = changelogService;
        this.configService = configService;
        this.gitHubService = gitHubService;
    }

    @Override
    public void execute(final PullRequest pullRequest) {
        Optional<Changelog> changelogOpt = changelogService.createFromPullRequest(pullRequest);

        changelogOpt.ifPresent(changelog -> {
            if (!changelog.isEmpty()) {
                String changelogPath = configService.getConfig().getChangelogConfig().getPathToChangelog();
                String currentChangelogHtml = gitHubService.readDecodedFile(changelogPath);
                String newChangelogHtml = changelogService.mergeHtmlWithChangelog(currentChangelogHtml, changelog);

                String updateMessage = "Automatic changelog generation for PR #" + pullRequest.getNumber();
                gitHubService.updateFile(changelogPath, updateMessage, newChangelogHtml);

                LOGGER.info("Changelog generated for PR #" + pullRequest.getNumber());
            }
        });
    }
}

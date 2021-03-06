package io.github.spair.service.changelog;

import io.github.spair.service.changelog.entity.Changelog;
import io.github.spair.service.changelog.entity.ChangelogRow;
import io.github.spair.service.changelog.entity.ChangelogValidationStatus;
import io.github.spair.service.pr.entity.PullRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChangelogService {

    private final HtmlChangelogGenerator htmlChangelogGenerator;
    private final ChangelogValidator changelogValidator;
    private final ChangelogGenerator changelogGenerator;

    @Autowired
    public ChangelogService(
            final HtmlChangelogGenerator htmlChangelogGenerator,
            final ChangelogValidator changelogValidator,
            final ChangelogGenerator changelogGenerator) {
        this.htmlChangelogGenerator = htmlChangelogGenerator;
        this.changelogValidator = changelogValidator;
        this.changelogGenerator = changelogGenerator;
    }

    public Optional<Changelog> createFromPullRequest(final PullRequest pullRequest) {
        return changelogGenerator.generate(pullRequest);
    }

    public ChangelogValidationStatus validateChangelog(final Changelog changelog) {
        return changelogValidator.validate(changelog);
    }

    public String mergeHtmlWithChangelog(final String html, final Changelog changelog) {
        return htmlChangelogGenerator.mergeHtmlWithChangelog(html, changelog);
    }

    public String addTestChangelogToHtml(final String html, final Changelog changelog) {
        return htmlChangelogGenerator.addTestChangelogToHtml(html, changelog);
    }

    public String removeTestChangelogFromHtml(final String html, final int prNumber) {
        return htmlChangelogGenerator.removeTestChangelogFromHtml(html, prNumber);
    }

    public Set<String> getChangelogClassesList(final PullRequest pullRequest) {
        Optional<Changelog> changelog = changelogGenerator.generate(pullRequest);

        if (changelog.isPresent() && !changelog.get().isEmpty()) {
            return changelog.get().getChangelogRows()
                    .stream().map(ChangelogRow::getClassName).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }
}

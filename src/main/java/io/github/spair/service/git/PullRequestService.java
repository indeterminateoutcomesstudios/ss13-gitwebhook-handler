package io.github.spair.service.git;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.spair.service.EnumUtil;
import io.github.spair.service.git.entities.PullRequest;
import io.github.spair.service.git.entities.PullRequestType;
import org.springframework.stereotype.Service;

@Service
public class PullRequestService {

    public PullRequest convertWebhookJson(final ObjectNode webhookJson) {
        JsonNode pullRequestNode = webhookJson.get(GitHubPayload.PULL_REQUEST);

        String author = pullRequestNode.get(GitHubPayload.USER).get(GitHubPayload.LOGIN).asText();
        int number = pullRequestNode.get(GitHubPayload.NUMBER).asInt();
        String title = pullRequestNode.get(GitHubPayload.TITLE).asText();
        PullRequestType type = identifyType(webhookJson);
        String link = pullRequestNode.get(GitHubPayload.HTML_URL).asText();
        String diffLink = pullRequestNode.get(GitHubPayload.DIFF_URL).asText();
        String body = pullRequestNode.get(GitHubPayload.BODY).asText();

        return PullRequest.builder()
                .author(author).number(number).title(title).type(type).link(link).diffLink(diffLink).body(body)
                .build();
    }

    private PullRequestType identifyType(final ObjectNode webhookJson) {
        String action = webhookJson.get(GitHubPayload.ACTION).asText();
        PullRequestType prType = EnumUtil.valueOfOrDefault(PullRequestType.values(), action, PullRequestType.UNDEFINED);
        boolean isMerged = webhookJson.get(GitHubPayload.PULL_REQUEST).get(GitHubPayload.MERGED).asBoolean();

        if (prType == PullRequestType.CLOSED && isMerged) {
            return PullRequestType.MERGED;
        } else {
            return prType;
        }
    }
}

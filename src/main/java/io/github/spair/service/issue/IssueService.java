package io.github.spair.service.issue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.spair.service.EnumUtil;
import io.github.spair.service.git.GitHubPayload;
import io.github.spair.service.git.entities.Issue;
import io.github.spair.service.git.entities.IssueType;
import org.springframework.stereotype.Service;

@Service
public class IssueService {

    public Issue convertWebhookJson(final ObjectNode webhookJson) {
        final JsonNode issueNode = webhookJson.get(GitHubPayload.ISSUE);

        final int number = issueNode.get(GitHubPayload.NUMBER).asInt();
        final String title = issueNode.get(GitHubPayload.TITLE).asText();
        final IssueType issueType = identifyType(webhookJson);

        return new Issue(number, title, issueType);
    }

    private IssueType identifyType(final ObjectNode webhookJson) {
        final String action = webhookJson.get(GitHubPayload.ACTION).asText();
        return EnumUtil.valueOfOrDefault(IssueType.values(), action, IssueType.UNDEFINED);
    }
}
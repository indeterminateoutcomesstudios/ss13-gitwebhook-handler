package io.github.spair.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.spair.handler.Handler;
import io.github.spair.handler.IssueHandler;
import io.github.spair.handler.PullRequestHandler;
import io.github.spair.service.InvalidSignatureException;
import io.github.spair.service.SignatureService;
import io.github.spair.service.git.GitHubConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.Arrays;

@RestController
@RequestMapping("/handler")
public class HandlerWebController {

    private static final int SIGN_PREFIX_LENGTH = 5;

    private final SignatureService signatureService;
    private final ObjectMapper objectMapper;
    private final Handler pullRequestHandler;
    private final Handler issueHandler;

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlerWebController.class);

    @Autowired
    public HandlerWebController(final SignatureService signatureService,
                                final ObjectMapper objectMapper,
                                @Qualifier(Handler.PULL_REQUEST) final Handler pullRequestHandler,
                                @Qualifier(Handler.ISSUE) final Handler issueHandler) {
        this.signatureService = signatureService;
        this.objectMapper = objectMapper;
        this.pullRequestHandler = pullRequestHandler;
        this.issueHandler = issueHandler;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public String processPullRequestWebhook(
            @RequestHeader(GitHubConstants.SIGNATURE_HEADER) final String signature,
            @RequestHeader(GitHubConstants.EVENT_HEADER) final String event,
            @RequestBody final String webhookPayload) throws IOException {
        signatureService.validate(sanitizeSignature(signature), webhookPayload);

        final ObjectNode webhookJson = objectMapper.readValue(webhookPayload, ObjectNode.class);

        switch (event) {
            case GitHubConstants.PING_EVENT:
                String zen = webhookJson.get("zen").asText();
                LOGGER.info("Ping event caught. Zen: " + zen);
                return "Pong. Zen was: " + zen;
            case GitHubConstants.PULL_REQUEST_EVENT:
                pullRequestHandler.handle(webhookJson);
                return "Pull Request handled";
            case GitHubConstants.ISSUES_EVENT:
                issueHandler.handle(webhookJson);
                return "Issue handled";
            default:
                LOGGER.info("Unhandled event caught: " + event);
                return "Unhandled event caught. Event: " + event;
        }
    }

    @ExceptionHandler(InvalidSignatureException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String catchInvalidSignature() {
        return "Invalid signature was provided";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String catchGeneralException(final Exception e) {
        LOGGER.error("Uncaught exception happened", e);
        return Arrays.toString(e.getStackTrace());
    }

    // Signature header from GitHub always starts with 'sha1=' part, which should be cut down.
    private String sanitizeSignature(final String sign) {
        return sign.substring(SIGN_PREFIX_LENGTH);
    }
}

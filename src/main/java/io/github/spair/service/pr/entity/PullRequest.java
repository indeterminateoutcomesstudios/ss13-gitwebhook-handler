package io.github.spair.service.pr.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PullRequest {

    private String author;
    private String branchName;
    private int number;
    private String title;
    private PullRequestType type;
    private String link;
    private String diffLink;
    private String body;
}

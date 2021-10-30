package com.selfxdsd.core.managers;

import com.selfxdsd.api.Event;
import com.selfxdsd.api.Issue;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.pm.PreconditionCheck;
import com.selfxdsd.api.pm.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Step which checks if an Issue's estimation has been changed.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.96
 * @todo #1265:30min Write some unit tests for this class.
 */
public final class IssueEstimationChanged extends PreconditionCheck {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        IssueEstimationChanged.class
    );

    /**
     * Ctor.
     * @param onTrue Step that should be performed next if the check is true.
     * @param onFalse Step that should be performed next if the check is false.
     */
    public IssueEstimationChanged(final Step onTrue, final Step onFalse) {
        super(onTrue, onFalse);
    }

    @Override
    public void perform(final Event event) {
        final Issue issue = event.issue();
        final String issueId = issue.issueId();
        final Project project = event.project();
        final Task task = project.tasks().getById(
            issueId,
            project.repoFullName(),
            project.provider(),
            issue.isPullRequest()
        );
        final int oldEstimation = task.estimation();
        final int newEstimation = issue.estimation().minutes();
        if (oldEstimation != newEstimation) {
            LOG.debug(String.format(
                "The estimation of Issue [#%s-%s-%s] has changed from %s min. "
                + "to %s min.",
                issue.issueId(),
                issue.repoFullName(),
                issue.provider(),
                oldEstimation,
                newEstimation
            ));
            this.onTrue().perform(event);
        } else {
            LOG.debug(String.format(
                "The estimation of Issue [#%s-%s-%s] was NOT changed.",
                issue.issueId(),
                issue.repoFullName(),
                issue.provider()
            ));
            this.onFalse().perform(event);
        }

    }
}

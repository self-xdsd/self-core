package com.selfxdsd.api;

/**
 * A Project is a User's Repository which has been
 * registered (activated) on the Self platform.
 *
 * Once activated, a project will be managed by one of
 * Self's Project Managers.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 * @todo #3:30min Continue implementing Self's API (Java Interfaces).
 *  We should continue with an Iterable of Project (interface Projects)
 *  and also introduce the Contributor. A Project has one or many Contributors.
 */
public interface Project {

    /**
     * This project's repository.
     * @return Repo.
     */
    Repo repo();

    /**
     * Deactivate this project, tell  Self to stop
     * managing it.
     * @return This project's repository.
     */
    Repo deactivate();
}

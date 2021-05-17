/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.selfxdsd.core;

import com.selfxdsd.api.*;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Invitations to a Gitlab Repo.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.45
 */
final class GitlabRepoInvitations implements Invitations {

    /**
     * Gitlab.
     */
    private final Provider gitlab;

    /**
     * Manager authenticated in Gitlab.
     */
    private final User manager;

    /**
     * Ctor.
     * @param gitlab Gitlab.
     * @param manager Authenticated manager.
     */
    GitlabRepoInvitations(
        final Provider gitlab,
        final User manager
    ) {
        this.gitlab = gitlab;
        this.manager = manager;
    }

    /**
     * In the case of Gitlab, we use the 'Add Member' endpoint
     * to invite the PM to the repo. This endpoint adds the PM
     * to the project directly, so <b>there are no actual invitations
     * to accept</b>. See method GitlabCollaborators.invite(...).<br><br>
     *
     * However, we return a mock invitation for each Project managed by the PM,
     * wrapped in StarRepo, FollowProjectOwner and others in order to respect
     * the architecture (starring a repo or following the PO should happen when
     * accepting invitations).
     *
     * @return Invitation iterator.
     */
    @Override
    public Iterator<Invitation> iterator() {
        final List<Invitation> invitations = new ArrayList<>();
        for(final Project project : this.manager.projects()) {
            final String name = project.repoFullName();
            final Repo repo = this.gitlab.repo(
                name.split("/")[0],
                name.split("/")[1]
            );
            if(!repo.isStarred()) {
                invitations.add(
                    new CreateRepoLabels(
                        new FollowProjectOwner(
                            new StarRepo(
                                new Invitation() {
                                    @Override
                                    public JsonObject json() {
                                        return Json.createObjectBuilder()
                                            .build();
                                    }

                                    @Override
                                    public String inviter() {
                                        return project.owner().username();
                                    }

                                    @Override
                                    public Repo repo() {
                                        return repo;
                                    }

                                    @Override
                                    public void accept() {
                                    }
                                }
                            )
                        )
                    )
                );
            }
        }
        return invitations.iterator();
    }
}

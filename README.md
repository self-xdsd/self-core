## Self's Core Implementation

[![Build Status](https://travis-ci.org/self-xdsd/self-core.svg?branch=master)](https://travis-ci.org/self-xdsd/self-core)
[![Coverage Status](https://coveralls.io/repos/github/self-xdsd/self-core/badge.svg?branch=master)](https://coveralls.io/github/self-xdsd/self-core?branch=master)
[![PDD status](http://www.0pdd.com/svg?name=self-xdsd/self-core)](http://www.0pdd.com/p?name=self-xdsd/self-core)

[![DevOps By Rultor.com](http://www.rultor.com/b/self-xdsd/self-core)](http://www.rultor.com/p/self-xdsd/self-core)
[![We recommend IntelliJ IDEA](http://amihaiemil.github.io/images/intellij-idea-recommend.svg)](https://www.jetbrains.com/idea/)


Self's Core. This repo contains the main Java SE 11 Implementation, clean of any framework.

The platform's domain model is implemented as a set of public Java Interfaces which, besides being the skeleton of the core implementation, can also be used to implement Selenium tests or implement a Java client for the RESTful API.

Once ready, this core will easily be pluggable into any framework or platform (Spring, Jakarta EE etc).

## Contributing 

If you would like to contribute, just open an issue or a PR.

You will need Java 11.
Make sure the maven build:

``$mvn clean install -Pcheckstyle,itcases``

passes before making a PR. [Checkstyle](http://checkstyle.sourceforge.net/) will make sure
you're following our code style and guidlines.

It's better to make changes on a separate branch (derived from ``master``), so you won't have to cherry pick commits in case your PR is rejected.

## Cash Bounties

Some of the tickets have a cash bounty assigned to them. If you want to solve a ticket and get the bounty, tell me and I'll assign it to you. You will have 10 days to provide a PR and close the ticket. Pay attention: if the 10 days pass, I **might** take it away from you and give it to someone else -- if this happens, you won't get any cash. 

**You don't have to solve the whole ticket!** Many times, it will happen that the ticket requires more effort than what the bounty is worth. If this is the case, solve the ticket only **partially** and leave "todo" markers in the code -- these will automatically be transformed into Github Issues. However, you will have to leave the code in a consistent state, the build has to pass always.

More details [here](https://amihaiemil.com/2020/02/15/solve-github-issues-and-get-cash.html).

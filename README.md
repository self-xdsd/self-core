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

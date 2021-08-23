<img alt="self-xsdsd-logo" src="https://self-xdsd.com/files/self-xdsd.png" width="80" height="80"/>

# Self's Core Implementation

[![Build Status](https://travis-ci.com/self-xdsd/self-core.svg?branch=master)](https://travis-ci.com/self-xdsd/self-core)
[![Coverage Status](https://coveralls.io/repos/github/self-xdsd/self-core/badge.svg?branch=master)](https://coveralls.io/github/self-xdsd/self-core?branch=master)

[![Managed By Self XDSD](https://self-xdsd.com/b/mbself.svg)](https://self-xdsd.com/p/self-xdsd/self-core?provider=github) 
[![DevOps By Rultor.com](http://www.rultor.com/b/self-xdsd/self-core)](http://www.rultor.com/p/self-xdsd/self-core)
[![We recommend IntelliJ IDEA](http://amihaiemil.github.io/images/intellij-idea-recommend.svg)](https://www.jetbrains.com/idea/)


Self's Core, version `0.0.91`.

This repo contains the main Java SE 11 implementation, clean of any framework.

The platform's domain model is implemented as a set of public Java Interfaces which, besides being the skeleton of the core implementation, can also be used to implement Selenium tests or implement a Java client for the RESTful API.

Once ready, this core will easily be pluggable into any framework or platform (Spring, Jakarta EE etc).

## Contributing 

If you would like to contribute, just open an issue or a PR.

You will need Java 11.
Make sure the maven build:

``$mvn clean install -Pcheckstyle,itcases``

passes before making a PR. [Checkstyle](http://checkstyle.sourceforge.net/) will make sure
you're following our code style and guidelines.

It's better to make changes on a separate branch (derived from ``master``), so you won't have to cherry pick commits in case your PR is rejected.

## LICENSE

This product's code is open source. However, the [LICENSE](https://github.com/self-xdsd/self-core/blob/master/LICENSE) only allows you to read the code. Copying, downloading or forking the repo is strictly forbidden unless you are one of the project's contributors.

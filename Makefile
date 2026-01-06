.PHONY: build clean test publish help

GRADLEW := ./gradlew

build:
	$(GRADLEW) build

clean:
	$(GRADLEW) clean
	$(GRADLEW) purgeMavenLocal

test:
	$(GRADLEW) test

publish:
ifdef GITHUB_ACTIONS
	$(GRADLEW) publishAllPublicationsToGitHubPackagesRepository --no-daemon --stacktrace --info --scan --no-configuration-cache
else
	$(GRADLEW) publishToMavenLocal
endif

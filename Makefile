.PHONY: help tidy test tui
.DEFAULT_GOAL := help

jar_file = target/tui-lanterna-1.0-SNAPSHOT-jar-with-dependencies.jar

help:
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

upgrade-list:	## List available upgrades
	#go list -u -m all
	mvn versions:display-plugin-updates
	mvn versions:display-dependency-updates

upgrade-perform:	## Perform upgrade of dependencies
	go get -u ./...
	go get -t -u ./...

tidy:	## Tidy up the modules
	go mod tidy

test:	## Run all tests
	go test -v

tui:	## Launch the terminal UI
	go run . -tui

clean:	## Clean up
	mvn clean

watch-java-test:	## Reruns Java tests when files change
	fswatch src | while read file; do clear; mvn --offline --batch-mode test; done

$(jar_file): src/main/java/lantern/*.java pom.xml
	mvn test assembly:single

run: $(jar_file)	## Run the app
	time java -jar $(jar_file)

flame: $(jar_file)	## Generate a flamegraph
	jbang --javaagent=ap-loader@jvm-profiling-tools/ap-loader=start,event=cpu,file=profile.html -m lantern.App $(jar_file)

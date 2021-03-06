# Contextproject Multimedia Services

## Build Status
#### Master:
![Master](https://travis-ci.org/daveystruijk/contextproject-ms3.svg?branch=master)
#### Develop:
![Master](https://travis-ci.org/daveystruijk/contextproject-ms3.svg?branch=develop)

## Planning
Development is planned in sprints. All sprint plans and reflections can be found in the [SE-deliverables](https://github.com/daveystruijk/contextproject-ms3/tree/develop/contextproject-ms3/SE-deliverables) directory.

## Group Members (MS3)
- R.S. Graafmans
- Emiel Rietdijk
- D.R. Struijk
- F.A. van Doorn
- M.J.E. van Osch

## Branching
Branching will be done somewhat like: [A successful Git branching model](http://nvie.com/posts/a-successful-git-branching-model/) (except omitting release branches). This means we will have **feature branches** that merge into **develop** using pull requests. Once we've reached a new milestone, it will be tagged and pushed to **master**.

Master must *always have a running version* and pass all tests. If something critical needs to be fixed immediately, we create a **hotfix branch** and make a pull request into master. Feature branches are prefixed "feature-", and hotfix branches are prefixed "hotfix-".

## Dependencies
### TarsosDSP
For audio processing, we use [TarsosDSP](https://github.com/JorenSix/TarsosDSP) (and [TarsosTranscoder for mp3 decoding](https://github.com/JorenSix/TarsosTranscoder)). Because they are not maven projects, you need to download them manually to your local maven repository. To achieve this, run:
```
cd contextproject-ms3
wget http://0110.be/releases/TarsosDSP/TarsosDSP-2.2/TarsosDSP-2.2.jar
mvn install:install-file -DgroupId=be.tarsos.dsp -DartifactId=TarsosDSP -Dpackaging=jar -Dversion=2.2 -Dfile=TarsosDSP-2.2.jar -DgeneratePom=true
wget http://0110.be/releases/TarsosTranscoder/TarsosTranscoder-1.2.jar
mvn install:install-file -DgroupId=be.tarsos.transcoder -DartifactId=TarsosTranscoder -Dpackaging=jar -Dversion=1.2 -Dfile=TarsosTranscoder-1.2.jar -DgeneratePom=true
```

### JavaFX
It helps to have JavaFX integrated into Eclipse itself, by going to ```Help -> Install New Software``` with URL: ```http://download.eclipse.org/efxclipse/updates-released/1.2.0/site```

## Integration
### Travis CI
Maven tests are ran each time someone pushes to github, using [Travis CI](https://travis-ci.org/). (configuration can be found in the *.travis.yml* file)

### Static Code Analysis
You can run ```mvn site``` to create reports for:

- Checkstyle (coding standards)
- Cobertura (code coverage)
- FindBugs (bug discovery)
- CPD (duplicate code detection)
- PMD (verification of coding rules)

Results can be found under *target/site/index.html* -> Project Reports.

### Octopull
The Travis configuration is compatible with [Octopull](http://www.rmhartog.me/octopull/), which will add all mentioned code style warnings to a pull request.

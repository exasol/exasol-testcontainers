<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                  | License          |
| --------------------------- | ---------------- |
| [Testcontainers Core][0]    | [MIT][1]         |
| [Testcontainers :: JDBC][0] | [MIT][1]         |
| [SLF4J JDK14 Binding][4]    | [MIT License][5] |
| [database-cleaner][6]       | [MIT][7]         |
| [BucketFS Java][8]          | [MIT][7]         |

## Test Dependencies

| Dependency                                     | License                           |
| ---------------------------------------------- | --------------------------------- |
| [JUnit Jupiter Engine][10]                     | [Eclipse Public License v2.0][11] |
| [JUnit Jupiter Params][10]                     | [Eclipse Public License v2.0][11] |
| [Testcontainers :: JUnit Jupiter Extension][0] | [MIT][1]                          |
| [Hamcrest][16]                                 | [BSD License 3][17]               |
| [mockito-junit-jupiter][18]                    | [The MIT License][19]             |

## Runtime Dependencies

| Dependency                    | License                 |
| ----------------------------- | ----------------------- |
| [EXASolution JDBC Driver][20] | [EXAClient License][21] |

## Plugin Dependencies

| Dependency                                              | License                                        |
| ------------------------------------------------------- | ---------------------------------------------- |
| [SonarQube Scanner for Maven][22]                       | [GNU LGPL 3][23]                               |
| [Apache Maven Compiler Plugin][24]                      | [Apache License, Version 2.0][25]              |
| [Apache Maven Enforcer Plugin][26]                      | [Apache License, Version 2.0][25]              |
| [Maven Flatten Plugin][28]                              | [Apache Software Licenese][29]                 |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][30] | [ASL2][29]                                     |
| [Reproducible Build Maven Plugin][32]                   | [Apache 2.0][29]                               |
| [Maven Surefire Plugin][34]                             | [Apache License, Version 2.0][25]              |
| [Versions Maven Plugin][36]                             | [Apache License, Version 2.0][25]              |
| [Apache Maven Deploy Plugin][38]                        | [Apache License, Version 2.0][25]              |
| [Apache Maven GPG Plugin][40]                           | [Apache License, Version 2.0][25]              |
| [Apache Maven Source Plugin][42]                        | [Apache License, Version 2.0][25]              |
| [Apache Maven Javadoc Plugin][44]                       | [Apache License, Version 2.0][25]              |
| [Nexus Staging Maven Plugin][46]                        | [Eclipse Public License][47]                   |
| [Project keeper maven plugin][48]                       | [The MIT License][49]                          |
| [OpenFastTrace Maven Plugin][50]                        | [GNU General Public License v3.0][51]          |
| [Maven Failsafe Plugin][52]                             | [Apache License, Version 2.0][25]              |
| [JaCoCo :: Maven Plugin][54]                            | [Eclipse Public License 2.0][55]               |
| [error-code-crawler-maven-plugin][56]                   | [MIT][7]                                       |
| [Maven Clean Plugin][58]                                | [The Apache Software License, Version 2.0][29] |
| [Maven Resources Plugin][60]                            | [The Apache Software License, Version 2.0][29] |
| [Maven JAR Plugin][62]                                  | [The Apache Software License, Version 2.0][29] |
| [Maven Install Plugin][64]                              | [The Apache Software License, Version 2.0][29] |
| [Maven Site Plugin 3][66]                               | [The Apache Software License, Version 2.0][29] |

[8]: https://github.com/exasol/bucketfs-java
[21]: LICENSE-exasol-jdbc.txt
[29]: http://www.apache.org/licenses/LICENSE-2.0.txt
[34]: https://maven.apache.org/surefire/maven-surefire-plugin/
[46]: http://www.sonatype.com/public-parent/nexus-maven-plugins/nexus-staging/nexus-staging-maven-plugin/
[58]: http://maven.apache.org/plugins/maven-clean-plugin/
[7]: https://opensource.org/licenses/MIT
[18]: https://github.com/mockito/mockito
[52]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[28]: https://www.mojohaus.org/flatten-maven-plugin/
[36]: http://www.mojohaus.org/versions-maven-plugin/
[48]: https://github.com/exasol/project-keeper/
[17]: http://opensource.org/licenses/BSD-3-Clause
[24]: https://maven.apache.org/plugins/maven-compiler-plugin/
[1]: http://opensource.org/licenses/MIT
[50]: https://github.com/itsallcode/openfasttrace-maven-plugin
[55]: https://www.eclipse.org/legal/epl-2.0/
[38]: https://maven.apache.org/plugins/maven-deploy-plugin/
[47]: http://www.eclipse.org/legal/epl-v10.html
[23]: http://www.gnu.org/licenses/lgpl.txt
[54]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[19]: https://github.com/mockito/mockito/blob/main/LICENSE
[32]: http://zlika.github.io/reproducible-build-maven-plugin
[49]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[51]: https://www.gnu.org/licenses/gpl-3.0.html
[62]: http://maven.apache.org/plugins/maven-jar-plugin/
[5]: http://www.opensource.org/licenses/mit-license.php
[22]: http://sonarsource.github.io/sonar-scanner-maven/
[25]: https://www.apache.org/licenses/LICENSE-2.0.txt
[26]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[20]: http://www.exasol.com
[11]: https://www.eclipse.org/legal/epl-v20.html
[64]: http://maven.apache.org/plugins/maven-install-plugin/
[10]: https://junit.org/junit5/
[30]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[40]: https://maven.apache.org/plugins/maven-gpg-plugin/
[0]: https://testcontainers.org
[42]: https://maven.apache.org/plugins/maven-source-plugin/
[16]: http://hamcrest.org/JavaHamcrest/
[4]: http://www.slf4j.org
[66]: http://maven.apache.org/plugins/maven-site-plugin/
[60]: http://maven.apache.org/plugins/maven-resources-plugin/
[44]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[6]: https://github.com/exasol/database-cleaner
[56]: https://github.com/exasol/error-code-crawler-maven-plugin

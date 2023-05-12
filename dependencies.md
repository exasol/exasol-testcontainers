<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                  | License          |
| --------------------------- | ---------------- |
| [Testcontainers Core][0]    | [MIT][1]         |
| [Testcontainers :: JDBC][0] | [MIT][1]         |
| [JSch][2]                   | [Revised BSD][3] |
| [database-cleaner][4]       | [MIT License][5] |
| [BucketFS Java][6]          | [MIT License][7] |

## Test Dependencies

| Dependency                                     | License                          |
| ---------------------------------------------- | -------------------------------- |
| [JUnit Jupiter Engine][8]                      | [Eclipse Public License v2.0][9] |
| [JUnit Jupiter Params][8]                      | [Eclipse Public License v2.0][9] |
| [Testcontainers :: JUnit Jupiter Extension][0] | [MIT][1]                         |
| [Hamcrest][10]                                 | [BSD License 3][11]              |
| [mockito-junit-jupiter][12]                    | [The MIT License][13]            |
| [junit-pioneer][14]                            | [Eclipse Public License v2.0][9] |
| [SLF4J JDK14 Binding][15]                      | [MIT License][16]                |

## Runtime Dependencies

| Dependency                    | License                 |
| ----------------------------- | ----------------------- |
| [EXASolution JDBC Driver][17] | [EXAClient License][18] |

## Plugin Dependencies

| Dependency                                              | License                                        |
| ------------------------------------------------------- | ---------------------------------------------- |
| [SonarQube Scanner for Maven][19]                       | [GNU LGPL 3][20]                               |
| [Apache Maven Compiler Plugin][21]                      | [Apache-2.0][22]                               |
| [Apache Maven Enforcer Plugin][23]                      | [Apache-2.0][22]                               |
| [Maven Flatten Plugin][24]                              | [Apache Software Licenese][22]                 |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][25] | [ASL2][26]                                     |
| [Maven Surefire Plugin][27]                             | [Apache-2.0][22]                               |
| [Versions Maven Plugin][28]                             | [Apache License, Version 2.0][22]              |
| [duplicate-finder-maven-plugin Maven Mojo][29]          | [Apache License 2.0][30]                       |
| [Apache Maven Deploy Plugin][31]                        | [Apache-2.0][22]                               |
| [Apache Maven GPG Plugin][32]                           | [Apache License, Version 2.0][22]              |
| [Apache Maven Source Plugin][33]                        | [Apache License, Version 2.0][22]              |
| [Apache Maven Javadoc Plugin][34]                       | [Apache-2.0][22]                               |
| [Nexus Staging Maven Plugin][35]                        | [Eclipse Public License][36]                   |
| [Project keeper maven plugin][37]                       | [The MIT License][38]                          |
| [OpenFastTrace Maven Plugin][39]                        | [GNU General Public License v3.0][40]          |
| [Maven Failsafe Plugin][41]                             | [Apache-2.0][22]                               |
| [JaCoCo :: Maven Plugin][42]                            | [Eclipse Public License 2.0][43]               |
| [error-code-crawler-maven-plugin][44]                   | [MIT License][45]                              |
| [Reproducible Build Maven Plugin][46]                   | [Apache 2.0][26]                               |
| [Maven Clean Plugin][47]                                | [The Apache Software License, Version 2.0][26] |
| [Maven Resources Plugin][48]                            | [The Apache Software License, Version 2.0][26] |
| [Maven JAR Plugin][49]                                  | [The Apache Software License, Version 2.0][26] |
| [Maven Install Plugin][50]                              | [The Apache Software License, Version 2.0][26] |
| [Maven Site Plugin 3][51]                               | [The Apache Software License, Version 2.0][26] |

[0]: https://testcontainers.org
[1]: http://opensource.org/licenses/MIT
[2]: http://www.jcraft.com/jsch/
[3]: http://www.jcraft.com/jsch/LICENSE.txt
[4]: https://github.com/exasol/database-cleaner/
[5]: https://github.com/exasol/database-cleaner/blob/main/LICENSE
[6]: https://github.com/exasol/bucketfs-java/
[7]: https://github.com/exasol/bucketfs-java/blob/main/LICENSE
[8]: https://junit.org/junit5/
[9]: https://www.eclipse.org/legal/epl-v20.html
[10]: http://hamcrest.org/JavaHamcrest/
[11]: http://opensource.org/licenses/BSD-3-Clause
[12]: https://github.com/mockito/mockito
[13]: https://github.com/mockito/mockito/blob/main/LICENSE
[14]: https://junit-pioneer.org/
[15]: http://www.slf4j.org
[16]: http://www.opensource.org/licenses/mit-license.php
[17]: http://www.exasol.com
[18]: https://repo1.maven.org/maven2/com/exasol/exasol-jdbc/7.1.20/exasol-jdbc-7.1.20-license.txt
[19]: http://sonarsource.github.io/sonar-scanner-maven/
[20]: http://www.gnu.org/licenses/lgpl.txt
[21]: https://maven.apache.org/plugins/maven-compiler-plugin/
[22]: https://www.apache.org/licenses/LICENSE-2.0.txt
[23]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[24]: https://www.mojohaus.org/flatten-maven-plugin/
[25]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[26]: http://www.apache.org/licenses/LICENSE-2.0.txt
[27]: https://maven.apache.org/surefire/maven-surefire-plugin/
[28]: https://www.mojohaus.org/versions/versions-maven-plugin/
[29]: https://github.com/basepom/duplicate-finder-maven-plugin
[30]: http://www.apache.org/licenses/LICENSE-2.0.html
[31]: https://maven.apache.org/plugins/maven-deploy-plugin/
[32]: https://maven.apache.org/plugins/maven-gpg-plugin/
[33]: https://maven.apache.org/plugins/maven-source-plugin/
[34]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[35]: http://www.sonatype.com/public-parent/nexus-maven-plugins/nexus-staging/nexus-staging-maven-plugin/
[36]: http://www.eclipse.org/legal/epl-v10.html
[37]: https://github.com/exasol/project-keeper/
[38]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[39]: https://github.com/itsallcode/openfasttrace-maven-plugin
[40]: https://www.gnu.org/licenses/gpl-3.0.html
[41]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[42]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[43]: https://www.eclipse.org/legal/epl-2.0/
[44]: https://github.com/exasol/error-code-crawler-maven-plugin/
[45]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[46]: http://zlika.github.io/reproducible-build-maven-plugin
[47]: http://maven.apache.org/plugins/maven-clean-plugin/
[48]: http://maven.apache.org/plugins/maven-resources-plugin/
[49]: http://maven.apache.org/plugins/maven-jar-plugin/
[50]: http://maven.apache.org/plugins/maven-install-plugin/
[51]: http://maven.apache.org/plugins/maven-site-plugin/

<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                            | License          |
| ------------------------------------- | ---------------- |
| [Testcontainers Core][0]              | [MIT][1]         |
| [Apache Commons Compress][2]          | [Apache-2.0][3]  |
| [Testcontainers :: JDBC][0]           | [MIT][1]         |
| [JSch][4]                             | [Revised BSD][5] |
| [database-cleaner][6]                 | [MIT License][7] |
| [BucketFS Java][8]                    | [MIT License][9] |
| [Apache Derby Client JDBC Driver][10] | [Apache 2][11]   |

## Test Dependencies

| Dependency                                     | License                           |
| ---------------------------------------------- | --------------------------------- |
| [JUnit Jupiter Engine][12]                     | [Eclipse Public License v2.0][13] |
| [JUnit Jupiter Params][12]                     | [Eclipse Public License v2.0][13] |
| [Testcontainers :: JUnit Jupiter Extension][0] | [MIT][1]                          |
| [Hamcrest][14]                                 | [BSD License 3][15]               |
| [mockito-junit-jupiter][16]                    | [MIT][17]                         |
| [junit-pioneer][18]                            | [Eclipse Public License v2.0][13] |
| [Apache Derby Network Server][10]              | [Apache 2][11]                    |
| [Exasol UDF API for Java][19]                  | [MIT License][20]                 |
| [SLF4J JDK14 Binding][21]                      | [MIT License][22]                 |

## Runtime Dependencies

| Dependency                    | License                 |
| ----------------------------- | ----------------------- |
| [EXASolution JDBC Driver][23] | [EXAClient License][24] |

## Plugin Dependencies

| Dependency                                              | License                                        |
| ------------------------------------------------------- | ---------------------------------------------- |
| [SonarQube Scanner for Maven][25]                       | [GNU LGPL 3][26]                               |
| [Apache Maven Compiler Plugin][27]                      | [Apache-2.0][3]                                |
| [Apache Maven Enforcer Plugin][28]                      | [Apache-2.0][3]                                |
| [Maven Flatten Plugin][29]                              | [Apache Software Licenese][3]                  |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][30] | [ASL2][11]                                     |
| [Maven Surefire Plugin][31]                             | [Apache-2.0][3]                                |
| [Versions Maven Plugin][32]                             | [Apache License, Version 2.0][3]               |
| [duplicate-finder-maven-plugin Maven Mojo][33]          | [Apache License 2.0][34]                       |
| [Apache Maven Deploy Plugin][35]                        | [Apache-2.0][3]                                |
| [Apache Maven GPG Plugin][36]                           | [Apache-2.0][3]                                |
| [Apache Maven Source Plugin][37]                        | [Apache License, Version 2.0][3]               |
| [Apache Maven Javadoc Plugin][38]                       | [Apache-2.0][3]                                |
| [Nexus Staging Maven Plugin][39]                        | [Eclipse Public License][40]                   |
| [Project keeper maven plugin][41]                       | [The MIT License][42]                          |
| [OpenFastTrace Maven Plugin][43]                        | [GNU General Public License v3.0][44]          |
| [Maven Failsafe Plugin][45]                             | [Apache-2.0][3]                                |
| [JaCoCo :: Maven Plugin][46]                            | [Eclipse Public License 2.0][47]               |
| [error-code-crawler-maven-plugin][48]                   | [MIT License][49]                              |
| [Reproducible Build Maven Plugin][50]                   | [Apache 2.0][11]                               |
| [Maven Clean Plugin][51]                                | [The Apache Software License, Version 2.0][11] |
| [Maven Resources Plugin][52]                            | [The Apache Software License, Version 2.0][11] |
| [Maven JAR Plugin][53]                                  | [The Apache Software License, Version 2.0][11] |
| [Maven Install Plugin][54]                              | [The Apache Software License, Version 2.0][11] |
| [Maven Site Plugin 3][55]                               | [The Apache Software License, Version 2.0][11] |

[0]: https://java.testcontainers.org
[1]: http://opensource.org/licenses/MIT
[2]: https://commons.apache.org/proper/commons-compress/
[3]: https://www.apache.org/licenses/LICENSE-2.0.txt
[4]: http://www.jcraft.com/jsch/
[5]: http://www.jcraft.com/jsch/LICENSE.txt
[6]: https://github.com/exasol/database-cleaner/
[7]: https://github.com/exasol/database-cleaner/blob/main/LICENSE
[8]: https://github.com/exasol/bucketfs-java/
[9]: https://github.com/exasol/bucketfs-java/blob/main/LICENSE
[10]: http://db.apache.org/derby/
[11]: http://www.apache.org/licenses/LICENSE-2.0.txt
[12]: https://junit.org/junit5/
[13]: https://www.eclipse.org/legal/epl-v20.html
[14]: http://hamcrest.org/JavaHamcrest/
[15]: http://opensource.org/licenses/BSD-3-Clause
[16]: https://github.com/mockito/mockito
[17]: https://github.com/mockito/mockito/blob/main/LICENSE
[18]: https://junit-pioneer.org/
[19]: https://github.com/exasol/udf-api-java/
[20]: https://github.com/exasol/udf-api-java/blob/main/LICENSE
[21]: http://www.slf4j.org
[22]: http://www.opensource.org/licenses/mit-license.php
[23]: http://www.exasol.com
[24]: https://repo1.maven.org/maven2/com/exasol/exasol-jdbc/7.1.20/exasol-jdbc-7.1.20-license.txt
[25]: http://sonarsource.github.io/sonar-scanner-maven/
[26]: http://www.gnu.org/licenses/lgpl.txt
[27]: https://maven.apache.org/plugins/maven-compiler-plugin/
[28]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[29]: https://www.mojohaus.org/flatten-maven-plugin/
[30]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[31]: https://maven.apache.org/surefire/maven-surefire-plugin/
[32]: https://www.mojohaus.org/versions/versions-maven-plugin/
[33]: https://basepom.github.io/duplicate-finder-maven-plugin
[34]: http://www.apache.org/licenses/LICENSE-2.0.html
[35]: https://maven.apache.org/plugins/maven-deploy-plugin/
[36]: https://maven.apache.org/plugins/maven-gpg-plugin/
[37]: https://maven.apache.org/plugins/maven-source-plugin/
[38]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[39]: http://www.sonatype.com/public-parent/nexus-maven-plugins/nexus-staging/nexus-staging-maven-plugin/
[40]: http://www.eclipse.org/legal/epl-v10.html
[41]: https://github.com/exasol/project-keeper/
[42]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[43]: https://github.com/itsallcode/openfasttrace-maven-plugin
[44]: https://www.gnu.org/licenses/gpl-3.0.html
[45]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[46]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[47]: https://www.eclipse.org/legal/epl-2.0/
[48]: https://github.com/exasol/error-code-crawler-maven-plugin/
[49]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[50]: http://zlika.github.io/reproducible-build-maven-plugin
[51]: http://maven.apache.org/plugins/maven-clean-plugin/
[52]: http://maven.apache.org/plugins/maven-resources-plugin/
[53]: http://maven.apache.org/plugins/maven-jar-plugin/
[54]: http://maven.apache.org/plugins/maven-install-plugin/
[55]: http://maven.apache.org/plugins/maven-site-plugin/

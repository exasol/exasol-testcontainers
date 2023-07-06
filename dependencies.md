<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                           | License          |
| ------------------------------------ | ---------------- |
| [Testcontainers Core][0]             | [MIT][1]         |
| [Testcontainers :: JDBC][0]          | [MIT][1]         |
| [JSch][2]                            | [Revised BSD][3] |
| [database-cleaner][4]                | [MIT License][5] |
| [BucketFS Java][6]                   | [MIT License][7] |
| [Apache Derby Client JDBC Driver][8] | [Apache 2][9]    |

## Test Dependencies

| Dependency                                     | License                           |
| ---------------------------------------------- | --------------------------------- |
| [JUnit Jupiter Engine][10]                     | [Eclipse Public License v2.0][11] |
| [JUnit Jupiter Params][10]                     | [Eclipse Public License v2.0][11] |
| [Testcontainers :: JUnit Jupiter Extension][0] | [MIT][1]                          |
| [Hamcrest][12]                                 | [BSD License 3][13]               |
| [mockito-junit-jupiter][14]                    | [The MIT License][15]             |
| [junit-pioneer][16]                            | [Eclipse Public License v2.0][11] |
| [Apache Derby Network Server][8]               | [Apache 2][9]                     |
| [Exasol UDF API for Java][17]                  | [MIT License][18]                 |
| [SLF4J JDK14 Binding][19]                      | [MIT License][20]                 |

## Runtime Dependencies

| Dependency                    | License                 |
| ----------------------------- | ----------------------- |
| [EXASolution JDBC Driver][21] | [EXAClient License][22] |

## Plugin Dependencies

| Dependency                                              | License                                       |
| ------------------------------------------------------- | --------------------------------------------- |
| [SonarQube Scanner for Maven][23]                       | [GNU LGPL 3][24]                              |
| [Apache Maven Compiler Plugin][25]                      | [Apache-2.0][26]                              |
| [Apache Maven Enforcer Plugin][27]                      | [Apache-2.0][26]                              |
| [Maven Flatten Plugin][28]                              | [Apache Software Licenese][26]                |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][29] | [ASL2][9]                                     |
| [Maven Surefire Plugin][30]                             | [Apache-2.0][26]                              |
| [Versions Maven Plugin][31]                             | [Apache License, Version 2.0][26]             |
| [duplicate-finder-maven-plugin Maven Mojo][32]          | [Apache License 2.0][33]                      |
| [Apache Maven Deploy Plugin][34]                        | [Apache-2.0][26]                              |
| [Apache Maven GPG Plugin][35]                           | [Apache License, Version 2.0][26]             |
| [Apache Maven Source Plugin][36]                        | [Apache License, Version 2.0][26]             |
| [Apache Maven Javadoc Plugin][37]                       | [Apache-2.0][26]                              |
| [Nexus Staging Maven Plugin][38]                        | [Eclipse Public License][39]                  |
| [Project keeper maven plugin][40]                       | [The MIT License][41]                         |
| [OpenFastTrace Maven Plugin][42]                        | [GNU General Public License v3.0][43]         |
| [Maven Failsafe Plugin][44]                             | [Apache-2.0][26]                              |
| [JaCoCo :: Maven Plugin][45]                            | [Eclipse Public License 2.0][46]              |
| [error-code-crawler-maven-plugin][47]                   | [MIT License][48]                             |
| [Reproducible Build Maven Plugin][49]                   | [Apache 2.0][9]                               |
| [Maven Clean Plugin][50]                                | [The Apache Software License, Version 2.0][9] |
| [Maven Resources Plugin][51]                            | [The Apache Software License, Version 2.0][9] |
| [Maven JAR Plugin][52]                                  | [The Apache Software License, Version 2.0][9] |
| [Maven Install Plugin][53]                              | [The Apache Software License, Version 2.0][9] |
| [Maven Site Plugin 3][54]                               | [The Apache Software License, Version 2.0][9] |

[0]: https://testcontainers.org
[1]: http://opensource.org/licenses/MIT
[2]: http://www.jcraft.com/jsch/
[3]: http://www.jcraft.com/jsch/LICENSE.txt
[4]: https://github.com/exasol/database-cleaner/
[5]: https://github.com/exasol/database-cleaner/blob/main/LICENSE
[6]: https://github.com/exasol/bucketfs-java/
[7]: https://github.com/exasol/bucketfs-java/blob/main/LICENSE
[8]: http://db.apache.org/derby/
[9]: http://www.apache.org/licenses/LICENSE-2.0.txt
[10]: https://junit.org/junit5/
[11]: https://www.eclipse.org/legal/epl-v20.html
[12]: http://hamcrest.org/JavaHamcrest/
[13]: http://opensource.org/licenses/BSD-3-Clause
[14]: https://github.com/mockito/mockito
[15]: https://github.com/mockito/mockito/blob/main/LICENSE
[16]: https://junit-pioneer.org/
[17]: https://github.com/exasol/udf-api-java/
[18]: https://github.com/exasol/udf-api-java/blob/main/LICENSE
[19]: http://www.slf4j.org
[20]: http://www.opensource.org/licenses/mit-license.php
[21]: http://www.exasol.com
[22]: https://repo1.maven.org/maven2/com/exasol/exasol-jdbc/7.1.20/exasol-jdbc-7.1.20-license.txt
[23]: http://sonarsource.github.io/sonar-scanner-maven/
[24]: http://www.gnu.org/licenses/lgpl.txt
[25]: https://maven.apache.org/plugins/maven-compiler-plugin/
[26]: https://www.apache.org/licenses/LICENSE-2.0.txt
[27]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[28]: https://www.mojohaus.org/flatten-maven-plugin/
[29]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[30]: https://maven.apache.org/surefire/maven-surefire-plugin/
[31]: https://www.mojohaus.org/versions/versions-maven-plugin/
[32]: https://basepom.github.io/duplicate-finder-maven-plugin
[33]: http://www.apache.org/licenses/LICENSE-2.0.html
[34]: https://maven.apache.org/plugins/maven-deploy-plugin/
[35]: https://maven.apache.org/plugins/maven-gpg-plugin/
[36]: https://maven.apache.org/plugins/maven-source-plugin/
[37]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[38]: http://www.sonatype.com/public-parent/nexus-maven-plugins/nexus-staging/nexus-staging-maven-plugin/
[39]: http://www.eclipse.org/legal/epl-v10.html
[40]: https://github.com/exasol/project-keeper/
[41]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[42]: https://github.com/itsallcode/openfasttrace-maven-plugin
[43]: https://www.gnu.org/licenses/gpl-3.0.html
[44]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[45]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[46]: https://www.eclipse.org/legal/epl-2.0/
[47]: https://github.com/exasol/error-code-crawler-maven-plugin/
[48]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[49]: http://zlika.github.io/reproducible-build-maven-plugin
[50]: http://maven.apache.org/plugins/maven-clean-plugin/
[51]: http://maven.apache.org/plugins/maven-resources-plugin/
[52]: http://maven.apache.org/plugins/maven-jar-plugin/
[53]: http://maven.apache.org/plugins/maven-install-plugin/
[54]: http://maven.apache.org/plugins/maven-site-plugin/

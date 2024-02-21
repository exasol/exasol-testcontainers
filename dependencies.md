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
| [mockito-junit-jupiter][12]                    | [MIT][13]                        |
| [junit-pioneer][14]                            | [Eclipse Public License v2.0][9] |
| [Apache Derby Network Server][15]              | [Apache 2][16]                   |
| [Apache Derby Client JDBC Driver][15]          | [Apache 2][16]                   |
| [Exasol UDF API for Java][17]                  | [MIT License][18]                |
| [SLF4J JDK14 Binding][19]                      | [MIT License][20]                |

## Runtime Dependencies

| Dependency                    | License                 |
| ----------------------------- | ----------------------- |
| [EXASolution JDBC Driver][21] | [EXAClient License][22] |

## Plugin Dependencies

| Dependency                                              | License                               |
| ------------------------------------------------------- | ------------------------------------- |
| [SonarQube Scanner for Maven][23]                       | [GNU LGPL 3][24]                      |
| [Apache Maven Toolchains Plugin][25]                    | [Apache License, Version 2.0][26]     |
| [Apache Maven Compiler Plugin][27]                      | [Apache-2.0][26]                      |
| [Apache Maven Enforcer Plugin][28]                      | [Apache-2.0][26]                      |
| [Maven Flatten Plugin][29]                              | [Apache Software Licenese][26]        |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][30] | [ASL2][16]                            |
| [Maven Surefire Plugin][31]                             | [Apache-2.0][26]                      |
| [Versions Maven Plugin][32]                             | [Apache License, Version 2.0][26]     |
| [duplicate-finder-maven-plugin Maven Mojo][33]          | [Apache License 2.0][34]              |
| [Apache Maven Deploy Plugin][35]                        | [Apache-2.0][26]                      |
| [Apache Maven GPG Plugin][36]                           | [Apache-2.0][26]                      |
| [Apache Maven Source Plugin][37]                        | [Apache License, Version 2.0][26]     |
| [Apache Maven Javadoc Plugin][38]                       | [Apache-2.0][26]                      |
| [Nexus Staging Maven Plugin][39]                        | [Eclipse Public License][40]          |
| [Project Keeper Maven plugin][41]                       | [The MIT License][42]                 |
| [OpenFastTrace Maven Plugin][43]                        | [GNU General Public License v3.0][44] |
| [Maven Failsafe Plugin][45]                             | [Apache-2.0][26]                      |
| [JaCoCo :: Maven Plugin][46]                            | [Eclipse Public License 2.0][47]      |
| [error-code-crawler-maven-plugin][48]                   | [MIT License][49]                     |
| [Reproducible Build Maven Plugin][50]                   | [Apache 2.0][16]                      |

[0]: https://java.testcontainers.org
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
[13]: https://opensource.org/licenses/MIT
[14]: https://junit-pioneer.org/
[15]: http://db.apache.org/derby/
[16]: http://www.apache.org/licenses/LICENSE-2.0.txt
[17]: https://github.com/exasol/udf-api-java/
[18]: https://github.com/exasol/udf-api-java/blob/main/LICENSE
[19]: http://www.slf4j.org
[20]: http://www.opensource.org/licenses/mit-license.php
[21]: http://www.exasol.com
[22]: https://repo1.maven.org/maven2/com/exasol/exasol-jdbc/7.1.20/exasol-jdbc-7.1.20-license.txt
[23]: http://sonarsource.github.io/sonar-scanner-maven/
[24]: http://www.gnu.org/licenses/lgpl.txt
[25]: https://maven.apache.org/plugins/maven-toolchains-plugin/
[26]: https://www.apache.org/licenses/LICENSE-2.0.txt
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

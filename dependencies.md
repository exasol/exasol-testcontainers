<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                  | License                                      |
| --------------------------- | -------------------------------------------- |
| [Testcontainers Core][0]    | [MIT][1]                                     |
| [Testcontainers :: JDBC][0] | [MIT][1]                                     |
| [JSch][2]                   | [Revised BSD][3]; [Revised BSD][4]; [ISC][5] |
| [database-cleaner][6]       | [MIT License][7]                             |
| [BucketFS Java][8]          | [MIT License][9]                             |

## Test Dependencies

| Dependency                                     | License                           |
| ---------------------------------------------- | --------------------------------- |
| [JUnit Jupiter Params][10]                     | [Eclipse Public License v2.0][11] |
| [Testcontainers :: JUnit Jupiter Extension][0] | [MIT][1]                          |
| [Hamcrest][12]                                 | [BSD-3-Clause][13]                |
| [mockito-junit-jupiter][14]                    | [MIT][15]                         |
| [junit-pioneer][16]                            | [Eclipse Public License v2.0][11] |
| [Apache Derby Network Server][17]              | [Apache 2][18]                    |
| [Apache Derby Client JDBC Driver][17]          | [Apache 2][18]                    |
| [Exasol UDF API for Java][19]                  | [MIT License][20]                 |
| [EqualsVerifier \| release normal jar][21]     | [Apache License, Version 2.0][22] |
| [to-string-verifier][23]                       | [MIT License][24]                 |
| [Matcher for SQL Result Sets][25]              | [MIT License][26]                 |
| [SLF4J JDK14 Binding][27]                      | [MIT License][24]                 |

## Runtime Dependencies

| Dependency               | License                                                                                                        |
| ------------------------ | -------------------------------------------------------------------------------------------------------------- |
| [Eclipse Parsson][28]    | [Eclipse Public License 2.0][29]; [GNU General Public License, version 2 with the GNU Classpath Exception][30] |
| [Exasol JDBC Driver][31] | [EXAClient License][32]                                                                                        |

## Plugin Dependencies

| Dependency                                              | License                                        |
| ------------------------------------------------------- | ---------------------------------------------- |
| [SonarQube Scanner for Maven][33]                       | [GNU LGPL 3][34]                               |
| [Apache Maven Toolchains Plugin][35]                    | [Apache-2.0][22]                               |
| [Apache Maven Compiler Plugin][36]                      | [Apache-2.0][22]                               |
| [Apache Maven Enforcer Plugin][37]                      | [Apache-2.0][22]                               |
| [Maven Flatten Plugin][38]                              | [Apache Software License][22]                  |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][39] | [ASL2][18]                                     |
| [Maven Surefire Plugin][40]                             | [Apache-2.0][22]                               |
| [Project Keeper Maven plugin][41]                       | [The MIT License][42]                          |
| [OpenFastTrace Maven Plugin][43]                        | [GNU General Public License v3.0][44]          |
| [Versions Maven Plugin][45]                             | [Apache License, Version 2.0][22]              |
| [duplicate-finder-maven-plugin Maven Mojo][46]          | [Apache License 2.0][47]                       |
| [Apache Maven Artifact Plugin][48]                      | [Apache-2.0][22]                               |
| [Apache Maven Deploy Plugin][49]                        | [Apache-2.0][22]                               |
| [Apache Maven Source Plugin][50]                        | [Apache-2.0][22]                               |
| [Apache Maven Javadoc Plugin][51]                       | [Apache-2.0][22]                               |
| [spdx-maven-plugin Maven Plugin][52]                    | [The Apache Software License, Version 2.0][18] |
| [Build Helper Maven Plugin][53]                         | [The MIT License][54]                          |
| [Apache Maven GPG Plugin][55]                           | [Apache-2.0][22]                               |
| [Central Publishing Maven Plugin][56]                   | [The Apache License, Version 2.0][22]          |
| [Maven Failsafe Plugin][57]                             | [Apache-2.0][22]                               |
| [JaCoCo :: Maven Plugin][58]                            | [EPL-2.0][59]                                  |
| [error-code-crawler-maven-plugin][60]                   | [MIT License][61]                              |
| [Git Commit Id Maven Plugin][62]                        | [GNU Lesser General Public License 3.0][63]    |
| [Apache Maven Clean Plugin][64]                         | [Apache-2.0][22]                               |
| [Apache Maven Resources Plugin][65]                     | [Apache-2.0][22]                               |
| [Apache Maven Install Plugin][66]                       | [Apache-2.0][22]                               |
| [Apache Maven Site Plugin][67]                          | [Apache-2.0][22]                               |

[0]: https://java.testcontainers.org
[1]: http://opensource.org/licenses/MIT
[2]: https://github.com/mwiede/jsch
[3]: https://github.com/mwiede/jsch/blob/master/LICENSE.txt
[4]: https://github.com/mwiede/jsch/blob/master/LICENSE.JZlib.txt
[5]: https://github.com/mwiede/jsch/blob/master/LICENSE.jBCrypt.txt
[6]: https://github.com/exasol/database-cleaner/
[7]: https://github.com/exasol/database-cleaner/blob/main/LICENSE
[8]: https://github.com/exasol/bucketfs-java/
[9]: https://github.com/exasol/bucketfs-java/blob/main/LICENSE
[10]: https://junit.org/
[11]: https://www.eclipse.org/legal/epl-v20.html
[12]: http://hamcrest.org/JavaHamcrest/
[13]: https://raw.githubusercontent.com/hamcrest/JavaHamcrest/master/LICENSE
[14]: https://github.com/mockito/mockito
[15]: https://opensource.org/licenses/MIT
[16]: https://junit-pioneer.org/
[17]: http://db.apache.org/derby/
[18]: http://www.apache.org/licenses/LICENSE-2.0.txt
[19]: https://github.com/exasol/udf-api-java/
[20]: https://github.com/exasol/udf-api-java/blob/main/LICENSE
[21]: https://www.jqno.nl/equalsverifier
[22]: https://www.apache.org/licenses/LICENSE-2.0.txt
[23]: https://github.com/jparams/to-string-verifier
[24]: http://www.opensource.org/licenses/mit-license.php
[25]: https://github.com/exasol/hamcrest-resultset-matcher/
[26]: https://github.com/exasol/hamcrest-resultset-matcher/blob/main/LICENSE
[27]: http://www.slf4j.org
[28]: https://github.com/eclipse-ee4j/parsson
[29]: https://projects.eclipse.org/license/epl-2.0
[30]: https://projects.eclipse.org/license/secondary-gpl-2.0-cp
[31]: https://www.exasol.com/
[32]: https://repo1.maven.org/maven2/com/exasol/exasol-jdbc/26.2.8/exasol-jdbc-26.2.8-license.txt
[33]: https://docs.sonarsource.com/sonarqube-server/latest/extension-guide/developing-a-plugin/plugin-basics/sonar-scanner-maven/sonar-maven-plugin/
[34]: http://www.gnu.org/licenses/lgpl.txt
[35]: https://maven.apache.org/plugins/maven-toolchains-plugin/
[36]: https://maven.apache.org/plugins/maven-compiler-plugin/
[37]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[38]: https://www.mojohaus.org/flatten-maven-plugin/
[39]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[40]: https://maven.apache.org/surefire/maven-surefire-plugin/
[41]: https://github.com/exasol/project-keeper/
[42]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[43]: https://github.com/itsallcode/openfasttrace-maven-plugin
[44]: https://www.gnu.org/licenses/gpl-3.0.html
[45]: https://www.mojohaus.org/versions/versions-maven-plugin/
[46]: https://basepom.github.io/duplicate-finder-maven-plugin
[47]: http://www.apache.org/licenses/LICENSE-2.0.html
[48]: https://maven.apache.org/plugins/maven-artifact-plugin/
[49]: https://maven.apache.org/plugins/maven-deploy-plugin/
[50]: https://maven.apache.org/plugins/maven-source-plugin/
[51]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[52]: https://github.com/spdx/spdx-maven-plugin
[53]: https://www.mojohaus.org/build-helper-maven-plugin/
[54]: https://spdx.org/licenses/MIT.txt
[55]: https://maven.apache.org/plugins/maven-gpg-plugin/
[56]: https://central.sonatype.org
[57]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[58]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[59]: https://www.eclipse.org/legal/epl-2.0/
[60]: https://github.com/exasol/error-code-crawler-maven-plugin/
[61]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[62]: https://github.com/git-commit-id/git-commit-id-maven-plugin
[63]: http://www.gnu.org/licenses/lgpl-3.0.txt
[64]: https://maven.apache.org/plugins/maven-clean-plugin/
[65]: https://maven.apache.org/plugins/maven-resources-plugin/
[66]: https://maven.apache.org/plugins/maven-install-plugin/
[67]: https://maven.apache.org/plugins/maven-site-plugin/

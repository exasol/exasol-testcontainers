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

| Dependency                    | License                 |
| ----------------------------- | ----------------------- |
| [Apache Commons Compress][28] | [Apache-2.0][22]        |
| [Apache Commons Lang][29]     | [Apache-2.0][22]        |
| [Exasol JDBC Driver][30]      | [EXAClient License][31] |

## Plugin Dependencies

| Dependency                                              | License                                     |
| ------------------------------------------------------- | ------------------------------------------- |
| [Apache Maven Clean Plugin][32]                         | [Apache-2.0][22]                            |
| [Apache Maven Install Plugin][33]                       | [Apache-2.0][22]                            |
| [Apache Maven Resources Plugin][34]                     | [Apache-2.0][22]                            |
| [Apache Maven Site Plugin][35]                          | [Apache-2.0][22]                            |
| [SonarQube Scanner for Maven][36]                       | [GNU LGPL 3][37]                            |
| [Apache Maven Toolchains Plugin][38]                    | [Apache-2.0][22]                            |
| [Apache Maven Compiler Plugin][39]                      | [Apache-2.0][22]                            |
| [Apache Maven Enforcer Plugin][40]                      | [Apache-2.0][22]                            |
| [Maven Flatten Plugin][41]                              | [Apache Software License][22]               |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][42] | [ASL2][18]                                  |
| [Maven Surefire Plugin][43]                             | [Apache-2.0][22]                            |
| [Versions Maven Plugin][44]                             | [Apache License, Version 2.0][22]           |
| [duplicate-finder-maven-plugin Maven Mojo][45]          | [Apache License 2.0][46]                    |
| [Apache Maven Artifact Plugin][47]                      | [Apache-2.0][22]                            |
| [Apache Maven Deploy Plugin][48]                        | [Apache-2.0][22]                            |
| [Apache Maven GPG Plugin][49]                           | [Apache-2.0][22]                            |
| [Apache Maven Source Plugin][50]                        | [Apache License, Version 2.0][22]           |
| [Apache Maven Javadoc Plugin][51]                       | [Apache-2.0][22]                            |
| [Central Publishing Maven Plugin][52]                   | [The Apache License, Version 2.0][22]       |
| [Project Keeper Maven plugin][53]                       | [The MIT License][54]                       |
| [OpenFastTrace Maven Plugin][55]                        | [GNU General Public License v3.0][56]       |
| [Maven Failsafe Plugin][57]                             | [Apache-2.0][22]                            |
| [JaCoCo :: Maven Plugin][58]                            | [EPL-2.0][59]                               |
| [Quality Summarizer Maven Plugin][60]                   | [MIT License][61]                           |
| [error-code-crawler-maven-plugin][62]                   | [MIT License][63]                           |
| [Git Commit Id Maven Plugin][64]                        | [GNU Lesser General Public License 3.0][65] |

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
[28]: https://commons.apache.org/proper/commons-compress/
[29]: https://commons.apache.org/proper/commons-lang/
[30]: https://www.exasol.com/
[31]: https://repo1.maven.org/maven2/com/exasol/exasol-jdbc/25.2.5/exasol-jdbc-25.2.5-license.txt
[32]: https://maven.apache.org/plugins/maven-clean-plugin/
[33]: https://maven.apache.org/plugins/maven-install-plugin/
[34]: https://maven.apache.org/plugins/maven-resources-plugin/
[35]: https://maven.apache.org/plugins/maven-site-plugin/
[36]: http://docs.sonarqube.org/display/PLUG/Plugin+Library/sonar-scanner-maven/sonar-maven-plugin
[37]: http://www.gnu.org/licenses/lgpl.txt
[38]: https://maven.apache.org/plugins/maven-toolchains-plugin/
[39]: https://maven.apache.org/plugins/maven-compiler-plugin/
[40]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[41]: https://www.mojohaus.org/flatten-maven-plugin/
[42]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[43]: https://maven.apache.org/surefire/maven-surefire-plugin/
[44]: https://www.mojohaus.org/versions/versions-maven-plugin/
[45]: https://basepom.github.io/duplicate-finder-maven-plugin
[46]: http://www.apache.org/licenses/LICENSE-2.0.html
[47]: https://maven.apache.org/plugins/maven-artifact-plugin/
[48]: https://maven.apache.org/plugins/maven-deploy-plugin/
[49]: https://maven.apache.org/plugins/maven-gpg-plugin/
[50]: https://maven.apache.org/plugins/maven-source-plugin/
[51]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[52]: https://central.sonatype.org
[53]: https://github.com/exasol/project-keeper/
[54]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[55]: https://github.com/itsallcode/openfasttrace-maven-plugin
[56]: https://www.gnu.org/licenses/gpl-3.0.html
[57]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[58]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[59]: https://www.eclipse.org/legal/epl-2.0/
[60]: https://github.com/exasol/quality-summarizer-maven-plugin/
[61]: https://github.com/exasol/quality-summarizer-maven-plugin/blob/main/LICENSE
[62]: https://github.com/exasol/error-code-crawler-maven-plugin/
[63]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[64]: https://github.com/git-commit-id/git-commit-id-maven-plugin
[65]: http://www.gnu.org/licenses/lgpl-3.0.txt

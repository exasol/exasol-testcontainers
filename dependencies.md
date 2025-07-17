<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                   | License                                      |
| ---------------------------- | -------------------------------------------- |
| [Testcontainers Core][0]     | [MIT][1]                                     |
| [Apache Commons Compress][2] | [Apache-2.0][3]                              |
| [Apache Commons Lang][4]     | [Apache-2.0][3]                              |
| [Testcontainers :: JDBC][0]  | [MIT][1]                                     |
| [JSch][5]                    | [Revised BSD][6]; [Revised BSD][7]; [ISC][8] |
| [database-cleaner][9]        | [MIT License][10]                            |
| [BucketFS Java][11]          | [MIT License][12]                            |

## Test Dependencies

| Dependency                                     | License                           |
| ---------------------------------------------- | --------------------------------- |
| [JUnit Jupiter API][13]                        | [Eclipse Public License v2.0][14] |
| [JUnit Jupiter Params][13]                     | [Eclipse Public License v2.0][14] |
| [Testcontainers :: JUnit Jupiter Extension][0] | [MIT][1]                          |
| [Hamcrest][15]                                 | [BSD-3-Clause][16]                |
| [mockito-junit-jupiter][17]                    | [MIT][18]                         |
| [junit-pioneer][19]                            | [Eclipse Public License v2.0][14] |
| [Apache Derby Network Server][20]              | [Apache 2][21]                    |
| [Apache Derby Client JDBC Driver][20]          | [Apache 2][21]                    |
| [Exasol UDF API for Java][22]                  | [MIT License][23]                 |
| [EqualsVerifier \| release normal jar][24]     | [Apache License, Version 2.0][3]  |
| [to-string-verifier][25]                       | [MIT License][26]                 |
| [Matcher for SQL Result Sets][27]              | [MIT License][28]                 |
| [SLF4J JDK14 Binding][29]                      | [MIT License][26]                 |

## Runtime Dependencies

| Dependency               | License                 |
| ------------------------ | ----------------------- |
| [Exasol JDBC Driver][30] | [EXAClient License][31] |

## Plugin Dependencies

| Dependency                                              | License                                     |
| ------------------------------------------------------- | ------------------------------------------- |
| [Apache Maven Clean Plugin][32]                         | [Apache-2.0][3]                             |
| [Apache Maven Install Plugin][33]                       | [Apache-2.0][3]                             |
| [Apache Maven Resources Plugin][34]                     | [Apache-2.0][3]                             |
| [Apache Maven Site Plugin][35]                          | [Apache-2.0][3]                             |
| [SonarQube Scanner for Maven][36]                       | [GNU LGPL 3][37]                            |
| [Apache Maven Toolchains Plugin][38]                    | [Apache-2.0][3]                             |
| [Apache Maven Compiler Plugin][39]                      | [Apache-2.0][3]                             |
| [Apache Maven Enforcer Plugin][40]                      | [Apache-2.0][3]                             |
| [Maven Flatten Plugin][41]                              | [Apache Software Licenese][3]               |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][42] | [ASL2][21]                                  |
| [Maven Surefire Plugin][43]                             | [Apache-2.0][3]                             |
| [Versions Maven Plugin][44]                             | [Apache License, Version 2.0][3]            |
| [duplicate-finder-maven-plugin Maven Mojo][45]          | [Apache License 2.0][46]                    |
| [Apache Maven Artifact Plugin][47]                      | [Apache-2.0][3]                             |
| [Apache Maven Deploy Plugin][48]                        | [Apache-2.0][3]                             |
| [Apache Maven GPG Plugin][49]                           | [Apache-2.0][3]                             |
| [Apache Maven Source Plugin][50]                        | [Apache License, Version 2.0][3]            |
| [Apache Maven Javadoc Plugin][51]                       | [Apache-2.0][3]                             |
| [Central Publishing Maven Plugin][52]                   | [The Apache License, Version 2.0][3]        |
| [Project Keeper Maven plugin][53]                       | [The MIT License][54]                       |
| [OpenFastTrace Maven Plugin][55]                        | [GNU General Public License v3.0][56]       |
| [Maven Failsafe Plugin][57]                             | [Apache-2.0][3]                             |
| [JaCoCo :: Maven Plugin][58]                            | [EPL-2.0][59]                               |
| [Quality Summarizer Maven Plugin][60]                   | [MIT License][61]                           |
| [error-code-crawler-maven-plugin][62]                   | [MIT License][63]                           |
| [Git Commit Id Maven Plugin][64]                        | [GNU Lesser General Public License 3.0][65] |

[0]: https://java.testcontainers.org
[1]: http://opensource.org/licenses/MIT
[2]: https://commons.apache.org/proper/commons-compress/
[3]: https://www.apache.org/licenses/LICENSE-2.0.txt
[4]: https://commons.apache.org/proper/commons-lang/
[5]: https://github.com/mwiede/jsch
[6]: https://github.com/mwiede/jsch/blob/master/LICENSE.txt
[7]: https://github.com/mwiede/jsch/blob/master/LICENSE.JZlib.txt
[8]: https://github.com/mwiede/jsch/blob/master/LICENSE.jBCrypt.txt
[9]: https://github.com/exasol/database-cleaner/
[10]: https://github.com/exasol/database-cleaner/blob/main/LICENSE
[11]: https://github.com/exasol/bucketfs-java/
[12]: https://github.com/exasol/bucketfs-java/blob/main/LICENSE
[13]: https://junit.org/
[14]: https://www.eclipse.org/legal/epl-v20.html
[15]: http://hamcrest.org/JavaHamcrest/
[16]: https://raw.githubusercontent.com/hamcrest/JavaHamcrest/master/LICENSE
[17]: https://github.com/mockito/mockito
[18]: https://opensource.org/licenses/MIT
[19]: https://junit-pioneer.org/
[20]: http://db.apache.org/derby/
[21]: http://www.apache.org/licenses/LICENSE-2.0.txt
[22]: https://github.com/exasol/udf-api-java/
[23]: https://github.com/exasol/udf-api-java/blob/main/LICENSE
[24]: https://www.jqno.nl/equalsverifier
[25]: https://github.com/jparams/to-string-verifier
[26]: http://www.opensource.org/licenses/mit-license.php
[27]: https://github.com/exasol/hamcrest-resultset-matcher/
[28]: https://github.com/exasol/hamcrest-resultset-matcher/blob/main/LICENSE
[29]: http://www.slf4j.org
[30]: http://www.exasol.com/
[31]: https://repo1.maven.org/maven2/com/exasol/exasol-jdbc/25.2.4/exasol-jdbc-25.2.4-license.txt
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

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
| [JUnit Jupiter Engine][10]                     | [Eclipse Public License v2.0][11] |
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
| [Exasol JDBC Driver][29]      | [EXAClient License][30] |

## Plugin Dependencies

| Dependency                                              | License                               |
| ------------------------------------------------------- | ------------------------------------- |
| [Apache Maven Clean Plugin][31]                         | [Apache-2.0][22]                      |
| [Apache Maven Install Plugin][32]                       | [Apache-2.0][22]                      |
| [Apache Maven Resources Plugin][33]                     | [Apache-2.0][22]                      |
| [Apache Maven Site Plugin][34]                          | [Apache License, Version 2.0][22]     |
| [SonarQube Scanner for Maven][35]                       | [GNU LGPL 3][36]                      |
| [Apache Maven Toolchains Plugin][37]                    | [Apache-2.0][22]                      |
| [Apache Maven Compiler Plugin][38]                      | [Apache-2.0][22]                      |
| [Apache Maven Enforcer Plugin][39]                      | [Apache-2.0][22]                      |
| [Maven Flatten Plugin][40]                              | [Apache Software Licenese][22]        |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][41] | [ASL2][18]                            |
| [Maven Surefire Plugin][42]                             | [Apache-2.0][22]                      |
| [Versions Maven Plugin][43]                             | [Apache License, Version 2.0][22]     |
| [duplicate-finder-maven-plugin Maven Mojo][44]          | [Apache License 2.0][45]              |
| [Apache Maven Deploy Plugin][46]                        | [Apache-2.0][22]                      |
| [Apache Maven GPG Plugin][47]                           | [Apache-2.0][22]                      |
| [Apache Maven Source Plugin][48]                        | [Apache License, Version 2.0][22]     |
| [Apache Maven Javadoc Plugin][49]                       | [Apache-2.0][22]                      |
| [Nexus Staging Maven Plugin][50]                        | [Eclipse Public License][51]          |
| [Project Keeper Maven plugin][52]                       | [The MIT License][53]                 |
| [OpenFastTrace Maven Plugin][54]                        | [GNU General Public License v3.0][55] |
| [Maven Failsafe Plugin][56]                             | [Apache-2.0][22]                      |
| [JaCoCo :: Maven Plugin][57]                            | [EPL-2.0][58]                         |
| [Quality Summarizer Maven Plugin][59]                   | [MIT License][60]                     |
| [error-code-crawler-maven-plugin][61]                   | [MIT License][62]                     |
| [Reproducible Build Maven Plugin][63]                   | [Apache 2.0][18]                      |

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
[10]: https://junit.org/junit5/
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
[29]: http://www.exasol.com/
[30]: https://repo1.maven.org/maven2/com/exasol/exasol-jdbc/24.1.1/exasol-jdbc-24.1.1-license.txt
[31]: https://maven.apache.org/plugins/maven-clean-plugin/
[32]: https://maven.apache.org/plugins/maven-install-plugin/
[33]: https://maven.apache.org/plugins/maven-resources-plugin/
[34]: https://maven.apache.org/plugins/maven-site-plugin/
[35]: http://sonarsource.github.io/sonar-scanner-maven/
[36]: http://www.gnu.org/licenses/lgpl.txt
[37]: https://maven.apache.org/plugins/maven-toolchains-plugin/
[38]: https://maven.apache.org/plugins/maven-compiler-plugin/
[39]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[40]: https://www.mojohaus.org/flatten-maven-plugin/
[41]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[42]: https://maven.apache.org/surefire/maven-surefire-plugin/
[43]: https://www.mojohaus.org/versions/versions-maven-plugin/
[44]: https://basepom.github.io/duplicate-finder-maven-plugin
[45]: http://www.apache.org/licenses/LICENSE-2.0.html
[46]: https://maven.apache.org/plugins/maven-deploy-plugin/
[47]: https://maven.apache.org/plugins/maven-gpg-plugin/
[48]: https://maven.apache.org/plugins/maven-source-plugin/
[49]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[50]: http://www.sonatype.com/public-parent/nexus-maven-plugins/nexus-staging/nexus-staging-maven-plugin/
[51]: http://www.eclipse.org/legal/epl-v10.html
[52]: https://github.com/exasol/project-keeper/
[53]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[54]: https://github.com/itsallcode/openfasttrace-maven-plugin
[55]: https://www.gnu.org/licenses/gpl-3.0.html
[56]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[57]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[58]: https://www.eclipse.org/legal/epl-2.0/
[59]: https://github.com/exasol/quality-summarizer-maven-plugin/
[60]: https://github.com/exasol/quality-summarizer-maven-plugin/blob/main/LICENSE
[61]: https://github.com/exasol/error-code-crawler-maven-plugin/
[62]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[63]: http://zlika.github.io/reproducible-build-maven-plugin

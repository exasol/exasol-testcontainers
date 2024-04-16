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
| [Hamcrest][12]                                 | [BSD License 3][13]               |
| [mockito-junit-jupiter][14]                    | [MIT][15]                         |
| [junit-pioneer][16]                            | [Eclipse Public License v2.0][11] |
| [Apache Derby Network Server][17]              | [Apache 2][18]                    |
| [Apache Derby Client JDBC Driver][17]          | [Apache 2][18]                    |
| [Exasol UDF API for Java][19]                  | [MIT License][20]                 |
| [EqualsVerifier \| release normal jar][21]     | [Apache License, Version 2.0][22] |
| [SLF4J JDK14 Binding][23]                      | [MIT License][24]                 |

## Runtime Dependencies

| Dependency                    | License                 |
| ----------------------------- | ----------------------- |
| [Apache Commons Compress][25] | [Apache-2.0][22]        |
| [Apache Commons Codec][26]    | [Apache-2.0][22]        |
| [Exasol JDBC Driver][27]      | [EXAClient License][28] |

## Plugin Dependencies

| Dependency                                              | License                               |
| ------------------------------------------------------- | ------------------------------------- |
| [SonarQube Scanner for Maven][29]                       | [GNU LGPL 3][30]                      |
| [Apache Maven Toolchains Plugin][31]                    | [Apache License, Version 2.0][22]     |
| [Apache Maven Compiler Plugin][32]                      | [Apache-2.0][22]                      |
| [Apache Maven Enforcer Plugin][33]                      | [Apache-2.0][22]                      |
| [Maven Flatten Plugin][34]                              | [Apache Software Licenese][22]        |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][35] | [ASL2][18]                            |
| [Maven Surefire Plugin][36]                             | [Apache-2.0][22]                      |
| [Versions Maven Plugin][37]                             | [Apache License, Version 2.0][22]     |
| [duplicate-finder-maven-plugin Maven Mojo][38]          | [Apache License 2.0][39]              |
| [Apache Maven Deploy Plugin][40]                        | [Apache-2.0][22]                      |
| [Apache Maven GPG Plugin][41]                           | [Apache-2.0][22]                      |
| [Apache Maven Source Plugin][42]                        | [Apache License, Version 2.0][22]     |
| [Apache Maven Javadoc Plugin][43]                       | [Apache-2.0][22]                      |
| [Nexus Staging Maven Plugin][44]                        | [Eclipse Public License][45]          |
| [Project Keeper Maven plugin][46]                       | [The MIT License][47]                 |
| [OpenFastTrace Maven Plugin][48]                        | [GNU General Public License v3.0][49] |
| [Maven Failsafe Plugin][50]                             | [Apache-2.0][22]                      |
| [JaCoCo :: Maven Plugin][51]                            | [EPL-2.0][52]                         |
| [error-code-crawler-maven-plugin][53]                   | [MIT License][54]                     |
| [Reproducible Build Maven Plugin][55]                   | [Apache 2.0][18]                      |

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
[13]: http://opensource.org/licenses/BSD-3-Clause
[14]: https://github.com/mockito/mockito
[15]: https://opensource.org/licenses/MIT
[16]: https://junit-pioneer.org/
[17]: http://db.apache.org/derby/
[18]: http://www.apache.org/licenses/LICENSE-2.0.txt
[19]: https://github.com/exasol/udf-api-java/
[20]: https://github.com/exasol/udf-api-java/blob/main/LICENSE
[21]: https://www.jqno.nl/equalsverifier
[22]: https://www.apache.org/licenses/LICENSE-2.0.txt
[23]: http://www.slf4j.org
[24]: http://www.opensource.org/licenses/mit-license.php
[25]: https://commons.apache.org/proper/commons-compress/
[26]: https://commons.apache.org/proper/commons-codec/
[27]: http://www.exasol.com/
[28]: https://repo1.maven.org/maven2/com/exasol/exasol-jdbc/24.1.0/exasol-jdbc-24.1.0-license.txt
[29]: http://sonarsource.github.io/sonar-scanner-maven/
[30]: http://www.gnu.org/licenses/lgpl.txt
[31]: https://maven.apache.org/plugins/maven-toolchains-plugin/
[32]: https://maven.apache.org/plugins/maven-compiler-plugin/
[33]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[34]: https://www.mojohaus.org/flatten-maven-plugin/
[35]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[36]: https://maven.apache.org/surefire/maven-surefire-plugin/
[37]: https://www.mojohaus.org/versions/versions-maven-plugin/
[38]: https://basepom.github.io/duplicate-finder-maven-plugin
[39]: http://www.apache.org/licenses/LICENSE-2.0.html
[40]: https://maven.apache.org/plugins/maven-deploy-plugin/
[41]: https://maven.apache.org/plugins/maven-gpg-plugin/
[42]: https://maven.apache.org/plugins/maven-source-plugin/
[43]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[44]: http://www.sonatype.com/public-parent/nexus-maven-plugins/nexus-staging/nexus-staging-maven-plugin/
[45]: http://www.eclipse.org/legal/epl-v10.html
[46]: https://github.com/exasol/project-keeper/
[47]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[48]: https://github.com/itsallcode/openfasttrace-maven-plugin
[49]: https://www.gnu.org/licenses/gpl-3.0.html
[50]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[51]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[52]: https://www.eclipse.org/legal/epl-2.0/
[53]: https://github.com/exasol/error-code-crawler-maven-plugin/
[54]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[55]: http://zlika.github.io/reproducible-build-maven-plugin

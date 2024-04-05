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
| [Apache Commons Compress][21] | [Apache-2.0][22]        |
| [Apache Commons Codec][23]    | [Apache-2.0][22]        |
| [Exasol JDBC Driver][24]      | [EXAClient License][25] |

## Plugin Dependencies

| Dependency                                              | License                               |
| ------------------------------------------------------- | ------------------------------------- |
| [SonarQube Scanner for Maven][26]                       | [GNU LGPL 3][27]                      |
| [Apache Maven Toolchains Plugin][28]                    | [Apache License, Version 2.0][22]     |
| [Apache Maven Compiler Plugin][29]                      | [Apache-2.0][22]                      |
| [Apache Maven Enforcer Plugin][30]                      | [Apache-2.0][22]                      |
| [Maven Flatten Plugin][31]                              | [Apache Software Licenese][22]        |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][32] | [ASL2][16]                            |
| [Maven Surefire Plugin][33]                             | [Apache-2.0][22]                      |
| [Versions Maven Plugin][34]                             | [Apache License, Version 2.0][22]     |
| [duplicate-finder-maven-plugin Maven Mojo][35]          | [Apache License 2.0][36]              |
| [Apache Maven Deploy Plugin][37]                        | [Apache-2.0][22]                      |
| [Apache Maven GPG Plugin][38]                           | [Apache-2.0][22]                      |
| [Apache Maven Source Plugin][39]                        | [Apache License, Version 2.0][22]     |
| [Apache Maven Javadoc Plugin][40]                       | [Apache-2.0][22]                      |
| [Nexus Staging Maven Plugin][41]                        | [Eclipse Public License][42]          |
| [Project Keeper Maven plugin][43]                       | [The MIT License][44]                 |
| [OpenFastTrace Maven Plugin][45]                        | [GNU General Public License v3.0][46] |
| [Maven Failsafe Plugin][47]                             | [Apache-2.0][22]                      |
| [JaCoCo :: Maven Plugin][48]                            | [EPL-2.0][49]                         |
| [error-code-crawler-maven-plugin][50]                   | [MIT License][51]                     |
| [Reproducible Build Maven Plugin][52]                   | [Apache 2.0][16]                      |

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
[21]: https://commons.apache.org/proper/commons-compress/
[22]: https://www.apache.org/licenses/LICENSE-2.0.txt
[23]: https://commons.apache.org/proper/commons-codec/
[24]: http://www.exasol.com/
[25]: https://repo1.maven.org/maven2/com/exasol/exasol-jdbc/24.1.0/exasol-jdbc-24.1.0-license.txt
[26]: http://sonarsource.github.io/sonar-scanner-maven/
[27]: http://www.gnu.org/licenses/lgpl.txt
[28]: https://maven.apache.org/plugins/maven-toolchains-plugin/
[29]: https://maven.apache.org/plugins/maven-compiler-plugin/
[30]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[31]: https://www.mojohaus.org/flatten-maven-plugin/
[32]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[33]: https://maven.apache.org/surefire/maven-surefire-plugin/
[34]: https://www.mojohaus.org/versions/versions-maven-plugin/
[35]: https://basepom.github.io/duplicate-finder-maven-plugin
[36]: http://www.apache.org/licenses/LICENSE-2.0.html
[37]: https://maven.apache.org/plugins/maven-deploy-plugin/
[38]: https://maven.apache.org/plugins/maven-gpg-plugin/
[39]: https://maven.apache.org/plugins/maven-source-plugin/
[40]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[41]: http://www.sonatype.com/public-parent/nexus-maven-plugins/nexus-staging/nexus-staging-maven-plugin/
[42]: http://www.eclipse.org/legal/epl-v10.html
[43]: https://github.com/exasol/project-keeper/
[44]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[45]: https://github.com/itsallcode/openfasttrace-maven-plugin
[46]: https://www.gnu.org/licenses/gpl-3.0.html
[47]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[48]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[49]: https://www.eclipse.org/legal/epl-2.0/
[50]: https://github.com/exasol/error-code-crawler-maven-plugin/
[51]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[52]: http://zlika.github.io/reproducible-build-maven-plugin

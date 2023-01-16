<!-- @formatter:off -->
# Dependencies

## Compile Dependencies

| Dependency                  | License          |
| --------------------------- | ---------------- |
| [Testcontainers Core][0]    | [MIT][1]         |
| [Testcontainers :: JDBC][0] | [MIT][1]         |
| [JSch][2]                   | [Revised BSD][3] |
| [SLF4J JDK14 Binding][4]    | [MIT License][5] |
| [database-cleaner][6]       | [MIT License][7] |
| [BucketFS Java][8]          | [MIT License][9] |

## Test Dependencies

| Dependency                                     | License                           |
| ---------------------------------------------- | --------------------------------- |
| [JUnit Jupiter Engine][10]                     | [Eclipse Public License v2.0][11] |
| [JUnit Jupiter Params][10]                     | [Eclipse Public License v2.0][11] |
| [Testcontainers :: JUnit Jupiter Extension][0] | [MIT][1]                          |
| [Hamcrest][12]                                 | [BSD License 3][13]               |
| [mockito-junit-jupiter][14]                    | [The MIT License][15]             |
| [junit-pioneer][16]                            | [Eclipse Public License v2.0][11] |

## Runtime Dependencies

| Dependency                    | License                 |
| ----------------------------- | ----------------------- |
| [EXASolution JDBC Driver][17] | [EXAClient License][18] |

## Plugin Dependencies

| Dependency                                              | License                                        |
| ------------------------------------------------------- | ---------------------------------------------- |
| [SonarQube Scanner for Maven][19]                       | [GNU LGPL 3][20]                               |
| [Apache Maven Compiler Plugin][21]                      | [Apache License, Version 2.0][22]              |
| [Apache Maven Enforcer Plugin][23]                      | [Apache License, Version 2.0][22]              |
| [Maven Flatten Plugin][24]                              | [Apache Software Licenese][22]                 |
| [org.sonatype.ossindex.maven:ossindex-maven-plugin][25] | [ASL2][26]                                     |
| [Maven Surefire Plugin][27]                             | [Apache License, Version 2.0][22]              |
| [Versions Maven Plugin][28]                             | [Apache License, Version 2.0][22]              |
| [Apache Maven Deploy Plugin][29]                        | [Apache License, Version 2.0][22]              |
| [Apache Maven GPG Plugin][30]                           | [Apache License, Version 2.0][22]              |
| [Apache Maven Source Plugin][31]                        | [Apache License, Version 2.0][22]              |
| [Apache Maven Javadoc Plugin][32]                       | [Apache License, Version 2.0][22]              |
| [Nexus Staging Maven Plugin][33]                        | [Eclipse Public License][34]                   |
| [Project keeper maven plugin][35]                       | [The MIT License][36]                          |
| [OpenFastTrace Maven Plugin][37]                        | [GNU General Public License v3.0][38]          |
| [Maven Failsafe Plugin][39]                             | [Apache License, Version 2.0][22]              |
| [JaCoCo :: Maven Plugin][40]                            | [Eclipse Public License 2.0][41]               |
| [error-code-crawler-maven-plugin][42]                   | [MIT License][43]                              |
| [Reproducible Build Maven Plugin][44]                   | [Apache 2.0][26]                               |
| [Maven Clean Plugin][45]                                | [The Apache Software License, Version 2.0][26] |
| [Maven Resources Plugin][46]                            | [The Apache Software License, Version 2.0][26] |
| [Maven JAR Plugin][47]                                  | [The Apache Software License, Version 2.0][26] |
| [Maven Install Plugin][48]                              | [The Apache Software License, Version 2.0][26] |
| [Maven Site Plugin 3][49]                               | [The Apache Software License, Version 2.0][26] |

[0]: https://testcontainers.org
[1]: http://opensource.org/licenses/MIT
[2]: http://www.jcraft.com/jsch/
[3]: http://www.jcraft.com/jsch/LICENSE.txt
[4]: http://www.slf4j.org
[5]: http://www.opensource.org/licenses/mit-license.php
[6]: https://github.com/exasol/database-cleaner/
[7]: https://github.com/exasol/database-cleaner/blob/main/LICENSE
[8]: https://github.com/exasol/bucketfs-java/
[9]: https://github.com/exasol/bucketfs-java/blob/main/LICENSE
[10]: https://junit.org/junit5/
[11]: https://www.eclipse.org/legal/epl-v20.html
[12]: http://hamcrest.org/JavaHamcrest/
[13]: http://opensource.org/licenses/BSD-3-Clause
[14]: https://github.com/mockito/mockito
[15]: https://github.com/mockito/mockito/blob/main/LICENSE
[16]: https://junit-pioneer.org/
[17]: http://www.exasol.com
[18]: https://docs.exasol.com/db/latest/connect_exasol/drivers/jdbc.htm
[19]: http://sonarsource.github.io/sonar-scanner-maven/
[20]: http://www.gnu.org/licenses/lgpl.txt
[21]: https://maven.apache.org/plugins/maven-compiler-plugin/
[22]: https://www.apache.org/licenses/LICENSE-2.0.txt
[23]: https://maven.apache.org/enforcer/maven-enforcer-plugin/
[24]: https://www.mojohaus.org/flatten-maven-plugin/
[25]: https://sonatype.github.io/ossindex-maven/maven-plugin/
[26]: http://www.apache.org/licenses/LICENSE-2.0.txt
[27]: https://maven.apache.org/surefire/maven-surefire-plugin/
[28]: https://www.mojohaus.org/versions-maven-plugin/
[29]: https://maven.apache.org/plugins/maven-deploy-plugin/
[30]: https://maven.apache.org/plugins/maven-gpg-plugin/
[31]: https://maven.apache.org/plugins/maven-source-plugin/
[32]: https://maven.apache.org/plugins/maven-javadoc-plugin/
[33]: http://www.sonatype.com/public-parent/nexus-maven-plugins/nexus-staging/nexus-staging-maven-plugin/
[34]: http://www.eclipse.org/legal/epl-v10.html
[35]: https://github.com/exasol/project-keeper/
[36]: https://github.com/exasol/project-keeper/blob/main/LICENSE
[37]: https://github.com/itsallcode/openfasttrace-maven-plugin
[38]: https://www.gnu.org/licenses/gpl-3.0.html
[39]: https://maven.apache.org/surefire/maven-failsafe-plugin/
[40]: https://www.jacoco.org/jacoco/trunk/doc/maven.html
[41]: https://www.eclipse.org/legal/epl-2.0/
[42]: https://github.com/exasol/error-code-crawler-maven-plugin/
[43]: https://github.com/exasol/error-code-crawler-maven-plugin/blob/main/LICENSE
[44]: http://zlika.github.io/reproducible-build-maven-plugin
[45]: http://maven.apache.org/plugins/maven-clean-plugin/
[46]: http://maven.apache.org/plugins/maven-resources-plugin/
[47]: http://maven.apache.org/plugins/maven-jar-plugin/
[48]: http://maven.apache.org/plugins/maven-install-plugin/
[49]: http://maven.apache.org/plugins/maven-site-plugin/

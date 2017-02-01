# Saiku Report Viewer Server

Saiku Report Viewer Server is an OSGi module implementation capable of handling Pentaho's PRPT files, process them and export the reports in HTML, PDF and XLS formats. It exports its functionalities as a RESTful webservice and thus can be used any other services within an OSGi container.

#### Deployment (Apache Karaf)

1. Clone the repository:
    - `git clone https://github.com/OSBI/saiku-report-viewer-server.git`
2. Build the project:
    - `cd saiku-report-viewer-server`
    - `mvn clean install`
3. Open Karaf and install some required features:
    - `cd KARAF_HOME`
    - `./bin/karaf debug`
    - `feature:install http http-whiteboard war`
    - `feature:repo-add cxf 3.1.6`
    - `feature:install cxf cxf-tools cxf-commands`
4. Copy the features to Karaf's deploy directory:
    - `cp feature/target/feature/feature.xml KARAF_HOME/deploy`
5. At Karaf's console install Saiku Report Viewer Server feature:
    - `feature:install saiku-report-viewer-server`
6. If everything worked correctly, you should be able to render a test report:
    - Open on a browser: [http://localhost:8181/cxf/reportviewer/render/test.pdf](http://localhost:8181/cxf/reportviewer/render/test.pdf)
        - :warning: **Observation:** If you have this error **`Error executing command: Error`**, when you run the above command, it's necessary to make a change:
            - Open the file `KARAF_HOME/etc/org.ops4j.pax.url.mvn.cfg` and append the last block:

                ```
                org.ops4j.pax.url.mvn.repositories= \
                http://repo1.maven.org/maven2@id=central, \
                http://repository.springsource.com/maven/bundles/release@id=spring.ebr.release, \
                http://repository.springsource.com/maven/bundles/external@id=spring.ebr.external, \
                http://zodiac.springsource.com/maven/bundles/release@id=gemini, \
                http://repository.apache.org/content/groups/snapshots-group@id=apache@snapshots@noreleases, \
                https://oss.sonatype.org/content/repositories/snapshots@id=sonatype.snapshots.deploy@snapshots@noreleases, \
                https://oss.sonatype.org/content/repositories/ops4j-snapshots@id=ops4j.sonatype.snapshots.deploy@snapshots@noreleases, \
                http://repository.springsource.com/maven/bundles/external@id=spring-ebr-repository@snapshots@noreleases, \
                http://nexus.pentaho.org/content/groups/omni/@id=pentaho@snapshots
                ```

#### Service Endpoint Operations

Endpoint URL | HTTP Method | Parameters | Description
-------------|:-------------:|------------|------------
`/reportviewer/upload` | POST | PRPT File (multipart form data) | Uploads a PRPT file and assigns it a unique ID
`/reportviewer/list` | GET | | Retrieves the uploaded files IDs
`/reportviewer/render/{id}.{format}` | GET | PRPT ID and format (XLS, PDF or HTML) | Process and exports the report to the desired format

#### Important Note:

- Saiku Report Viewer Server supports **only JNDI datasources**, those datasources must be registered within the same OSGi container as the report server, otherwise it won't be able to retrieve data to fill the report.

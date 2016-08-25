# Saiku Report Viewer Server

Saiku Report Viewer Server is an OSGi module implementation capable of handling Pentaho's PRPT files, process them and export the reports in HTML, PDF and XLS formats. It exports its functionalities as a RESTful webservice and thus can be used any other services within an OSGi container.

#### Deployment (Apache Karaf)

1. Clone the repository: 
    - `git clone https://github.com/OSBI/saiku-report-viewer-server.git`
2. Build the project:
    - `cd saiku-report-viewer-server`
    - `mvn clean install`
3. Copy the features to Karaf's deploy directory:
    - `cp feature/target/feature/feature.xml KARAF_HOME/deploy`
4. Open Karaf and install Saiku Report Viewer Server feature:
    - `cd KARAF_HOME`
    - `./bin/karaf debug`
    - `feature:install saiku-report-viewer-server`

#### Service Endpoint Operations

Endpoint URL | HTTP Method | Parameters | Description
-------------|-------------|------------|------------
`/reportviewer/upload` | POST | PRPT File (multipart form data) | Uploads a PRPT file and assigns it a unique ID
`/reportviewer/list` | GET | | Retrieves the uploaded files IDs
`/reportviewer/render/{id}.{format}` | GET | PRPT ID and format (xls,pdf or html) | Process and exports the report to the desired format

#### Important Note:

- Saiku Report Viewer Server supports **only JNDI datasources**, those datasources must be registered within the same OSGi container as the report server, otherwise it won't be able to retrieve data to fill the report.

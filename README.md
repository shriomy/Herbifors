Please create your Consumers and Producers as new modules inside relevent directory(Consumer/Producer).

Maintaining the file structure is highly important here.

Here is the folder structure
Herbifors/                         # Root project folder
│
├── producer/                       # Folder for all Producer modules
│   ├── temperature-producer/       # Module for Temperature Producer
│   │   ├── src/
│   │   ├── pom.xml                 # POM for the temperature-producer module
│   │   └── ...                     # Other necessary files for the module
│   │
│   ├── moisture-producer/          # Module for Moisture Producer
│   │   ├── src/
│   │   ├── pom.xml                 # POM for the moisture-producer module
│   │   └── ...
│   │
│   ├── light-producer/             # Module for Light Producer
│   │   ├── src/
│   │   ├── pom.xml                 # POM for the light-producer module
│   │   └── ...
│   │
│   └── ...                         # Any other producer modules
│
├── consumer/                       # Folder for all Consumer modules
│   ├── temperature-consumer/       # Module for Temperature Consumer
│   │   ├── src/
│   │   ├── pom.xml                 # POM for the temperature-consumer module
│   │   └── ...
│   │
│   ├── moisture-consumer/          # Module for Moisture Consumer
│   │   ├── src/
│   │   ├── pom.xml                 # POM for the moisture-consumer module
│   │   └── ...
│   │
│   ├── light-consumer/             # Module for Light Consumer
│   │   ├── src/
│   │   ├── pom.xml                 # POM for the light-consumer module
│   │   └── ...
│   │
│   └── ...                         # Any other consumer modules
│
├── pom.xml                         # Parent POM file for managing common dependencies and module configurations
├── target/                         # Compiled JAR files will be placed here
└── .gitignore                      # Git ignore file to avoid unwanted files in version control


folder structure for OSGi root project

How to Run,

1. Start Felix Gogo Shell
    navigate felix framework directory and enter  java -jar bin/felix.jar
2. Compile code into jar bundles
    mvn clean install
3. Install jar files to felix
    install file:/<jar files path>.jar , Note: install event admin
4. Start consumers & Services
    start <bundle Id of event admin>
    start <bundle Id>
5. Stop consumers & Services
    stop <bundle Id>
6. Uninstall bundles
    uninstall <bundle Id>

Installation and starting order

mysql-connector-j-8.3.0.jar
org.apache.felix.eventadmin-1.6.4.jar
org.osgi.enterprise-5.0.0.jar
org.osgi.service.event-1.4.1.jar
org.osgi.service.jdbc-1.0.0.jar
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

commit from nimasha

# Table of Contents

1. [Introduction](#overview)
2. [Installation](#installation)
3. [Architecture](#Architecture)
4. [Usage](#options)
5. [Contact](#contact)

## Overview <a name="overview"></a>
This project is a desktop application developed in Java for managing contacts and groups. It utilizes the JDBC (Java Database Connectivity) API for interacting with an H2 database. The application provides a log.txt file for tracking its logs, which is implemented using Log4j. It is built as a Maven project and can be easily imported and managed in IntelliJ IDEA.


## Installation <a name="installation"></a>
### Cloning the project locally with Git Bash:

1. Open Git Bash on your system.
2. Use the following command to clone the project:
 git clone https://github.com/Samiha128/contact-management.git

### Note on opening the project in different IDEs:

You can open the project in different IDEs such as IntelliJ IDEA, Eclipse, or others.

In general, one IDE may execute faster than the other. The speed difference is notable between IntelliJ IDEA and Eclipse, with IntelliJ IDEA known for its high speed and responsiveness, while Eclipse may be slightly slower, especially when building large projects. However, the choice between the two often depends on personal preferences and specific project needs.







## Usage <a name="Architecture"></a>
Application Architecture

The application follows a structured approach, organizing its functionality into distinct layers, each with a specific role:
Database Layer

This layer facilitates interactions with the H2 database. It manages tasks such as data storage, retrieval, and connection management. Serving as a crucial link between the application and the database, it ensures seamless communication.
Controller Layer

Positioned between the user interface and the application's core logic, this layer acts as a mediator. It receives inputs from the user interface, processes them, and routes them to the relevant parts of the application. Additionally, it handles tasks like data validation and transformation before passing them to the Business Objects layer.
Business Objects (BO) Layer

At the core of the application lies this layer, housing essential logic and rules governing its behavior. It encapsulates functionalities related to managing contacts and groups, ensuring the application operates in line with its intended purpose.


## Options <a name="options"></a>

## Explore the capabilities of our application with the following key features:

- **Effortless Contact Management:** Seamlessly create, update, and delete contacts. Enter essential contact details such as names, phone numbers, and email addresses with ease.

- **Streamlined Group Management:** Take control of your contacts by managing them within customizable groups. Easily add, delete, and organize contacts into groups tailored to your needs.

- **Smart Group Creation:** Say goodbye to manual organization tasks. Our application intelligently creates groups based on contact last names. When you add a new contact, the application automatically assigns it to the appropriate group or creates a new one if needed.

- **Advanced Contact Search:** Find contacts swiftly using a range of search criteria including names, phone numbers, and email addresses. With our intuitive search feature, locating specific contacts has never been easier.

- **Comprehensive Contact Details:** Dive deep into contact information with our detailed view feature. Access comprehensive details of each contact, ensuring you have all the necessary information at your fingertips.

Enhance your experience with our application by utilizing enhanced search capabilities. We've optimized the use of the SOUNDEX function in the SQL query and integrated cutting-edge algorithms such as Double Metaphone and Jaro-Winkler Distance to significantly improve search accuracy. Discover the power of precise and efficient contact management today.



## Contact <a name="contact"></a>

In this section, you can include contact information.

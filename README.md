# Apriori-Extension

Extension of the eTutor++ for the Apriori algorithm (incl. the derivation of association rules). The extension is an independent application whose sub-functions can be called from eTutor++.

Features for students are:

    - Facility to solve and submit assignments
    - Facility to draw up individual apriori tasks to practice

Features for tutors are:

    - Facility to create datasets and assignments for students
    - Facility to manage apriori data sets and tasks

## Spring Boot Application

Port: 8085 //TODO adapt port

## Security

In order to enable secure communication, the same password must apply in both the eTutor++ and the extension (stored in the ```app.constants.ts``` file of the eTutor++ and in the ```aprioriConfig.properties``` configuration file of the extension). //TODO adapt file

## Dependencies

The JavaScript library ```CryptoJS``` was additionally installed in eTutor++ for security purposes.

The extension has dependencies for the Spring framework (web, data jpa, thymeleaf), for the postgres data base, for the apache jena rdf data base, for additional hibernate data types and for combinatorics. Please consult the ```pom.xml``` file.

## Databases

The extension uses the Postgres and the RDF databases of the eTutor++.

## Properties

  The Spring specific configuration is in the ```application.properties``` file. 
  The properties of the extension are in the ```aprioriConfig.properties``` file.
  The relevant properties of the eTutor++ Angular front-end in relation to the extension are located in the ```app.constants.ts``` file of the platform. //TODO adapt file

## Remark for developers

In contrast to other eTutor++ modules, the extension is rendered on the server (SSR) and was implemented using Thymeleaf.

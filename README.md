# Hotel Database Management System

## Introduction

For this project, my colleague and I modeled and built a Database Management System for a fictitious hotel chain. This system is capable of tracking various information regarding hotels, rooms, and customers. These include a customer's recent booking history, a manager's ability to place Room Repair Requests, and the ability for all clients to book a room. A description of all the functionalities of client interface can be found in this file.


To complete the client interface, we used the Java Database Connectivity (JDBC) tool and the Command Line Interface (CLI). A template of the Java User Interface code was given to us by Dr. Vassilis Tsotras in order to accelerate the setup process. This includes creating a class that defines an embedded SQL utility class that is designed to work with PostgreSQL JDBC drivers. The rest of the functionality was completed by my colleague and I.

## Entity-Relationship Diagram

The following is our Entity-Relationship (ER) diagram for our database. This diagram allowed us to have a visual representation of how all the hotel and customer data is organized. 

<p align = "center">
    <img src = "Images/ERDiagram.png" width = "400" />
</p>

The following is a list of assumptions that we made when constructing this ER Diagram:

* Viewing and booking a room are two different relationships that a customer can have
* “View Room” and “Books” relationships are optional for all entities that participate in these relationships
* Manager can place multiple Maintenance Requests
* A repair that was made due to a Maintenance Request can only be made by one manager 
* A manager placing a Maintenance Request is optional
* A repair that was made due to a Maintenance Request is mandatory in the aggregation

The following is a table of Cardinality Constraints and Participation Constraints. Here, 'CC' stands for Cardinality Constraint and'PC' stands for Participation Constraint

| Entity A | Relationship | Entity B | CC | A's PC | B's PC |
|:----------:|:----------:|:----------:|:----------|:----------:|:----------:|
| Hotel  | has  | Room | one-to-many | mandatory | mandatory |
| Manager   | manages   | Hotel | one-to-many | mandatory | mandatory |
| Manager | places maintenance request | "handles" aggregation | one-to-many | optional | mandatory|
| Customer | books | Room | one-to-many | optional | optional |
| Customer | views room | Room | many-to-many | optional | optional |
| Customer | views history | Booking History | one-to-many | optional | mandatory |
| Maintenance Company | handles | Repair | one-to-many | mandatory | mandatory |



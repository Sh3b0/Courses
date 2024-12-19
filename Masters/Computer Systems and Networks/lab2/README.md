# Lab 2 - Architectural Patterns (Part A)

> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Task 1

**1.1. Provide 3 unique definitions for software architecture [[ref.](https://en.wikipedia.org/wiki/Software_architecture)].**

- "Software architecture is the set of structures needed to reason about a software system and the discipline of creating such structures and systems."
- It's about "Making fundamental structural choices that are costly to change once implemented."
- It's about "Designing the infrastructure within which application functionality can be realized and executed".

**1.2. List and explain with sample diagrams, 3 types of software architecture [[ref.](https://www.geeksforgeeks.org/types-of-software-architecture-patterns/)].**

- **Cilent/Server architecture:** a server machine is always running, listening, processing, and serving clients' requests.

  ![](https://upload.wikimedia.org/wikipedia/commons/c/c9/Client-server-model.svg)

- **Peer-to-Peer architecture:** nodes (peers) passing messages around the network in an unstructured or semi-structured manner. 

  ![](https://upload.wikimedia.org/wikipedia/commons/thumb/9/9e/P2P_network.svg/276px-P2P_network.svg.png)

- **Microservices architecture:** application functionality is comprised from multiple loose-coupled services (typically running in isolated containers and exposing an API for unified interaction). 

  ![](https://microservices.io/i/Microservice_Architecture.png)

- Image sources (in order)
  - https://en.wikipedia.org/wiki/Client%E2%80%93server_model
  - https://en.wikipedia.org/wiki/Peer-to-peer
  - https://microservices.io/i/Microservice_Architecture.png

**1.3. What are the differences between System architecture and Software architecture** [[ref.](https://www.geeksforgeeks.org/difference-between-system-architecture-and-software-architecture/)]

- System architecture describes the structure and behavior of multiple interrelated components (e.g., network, hardware, software) that make up the functionality of the system
- Software architecture describes a high-level overview of a software application. A software is typically a part of a larger system.  

**1.4. Differentiate between the following in the context of software architecture and provide examples**

- Software correctness & Software robustness [[ref.](https://softwareengineering.stackexchange.com/questions/367191/robustness-vs-correctness-competition)]
  - A correct software provides accurate reliable answers when queried/used. Every software should be correct.
  - A robust software resists failures and is generally available for use, but may not necessarily be correct every time.

- Topic & queue [[ref.](https://learn.microsoft.com/en-us/azure/service-bus-messaging/service-bus-queues-topics-subscriptions)]
  - A topic provides a one-to-many form of communication between a publisher/producer and multiple subscribers/consumers.
  - A queue stores objects (e.g., messages or tasks) to be processed/consumed in order by one receiver.

- Architecture & design [[ref.](https://stackoverflow.com/questions/704855/software-design-vs-software-architecture)]
  - Software architecture describes a high-level overview of the software components
  - Software design is more focused on the operation of a single component.

- User, primary stakeholder and secondary stakeholder [[ref.](https://en.wikipedia.org/wiki/Stakeholder_analysis#Stakeholder_types)]
  - An end-user represents the customer utilizing the software being developed.
  - Primary stakeholder is the most affected (positively or negatively) by the organization's actions
  - Secondary stakeholders are intermediaries (e.g., partners) who will be indirectly affected by the organization's actions

- Cohesion & coupling [[ref.](https://www.geeksforgeeks.org/software-engineering-coupling-and-cohesion/)].
  - Cohesion is the degree to which elements within a single module work together to fulfill a common functionality. Tight cohesion is generally desired for components of a single module.
  - Coupling is the degree of independence between different software modules, loose-coupling is generally desired for a fault-tolerant system. 


## Task 2

**2.1. List and explain the key concepts of Object-Oriented programming.** **[[ref.](https://en.wikipedia.org/wiki/Object-oriented_programming)]**

- **Abstraction:** representing complex entities by modeling their essential characteristics and hiding unnecessary details.
- **Encapsulation:** bundling attributes and methods as a class with access controls.
- **Inheritance:** subclasses inheriting properties and behaviors from super-classes, promoting code reuse
- **Polymorphism:** objects of different classes can be treated as if they belong to a common base class.

**2.2. What is an architectural view? Explain characteristics of architecture patterns in pattern based design.** **[[ref.1](https://en.wikipedia.org/wiki/View_model), [ref.2](https://en.wikipedia.org/wiki/Architectural_pattern)]**

- An architectural view is a way of describing system components and their interactions, for easier understanding and communication (e.g., to stakeholders or clients).
- An architectural pattern is a known resolution to a common problem in software architecture, such pattern should be general, reusable, strictly described, and commonly available.

**2.3. Which are the main approaches to describing a software architecture? List and explain with sample diagrams. [[ref.](https://en.wikipedia.org/wiki/Software_architecture_description)]**

- To describe the architecture of a system, one may use different approaches including but not limited to:

  - **Architecture viewpoints** showing diagrams like logical, development, process, and physical views.

    ![img](https://upload.wikimedia.org/wikipedia/commons/thumb/e/e6/4%2B1_Architectural_View_Model.svg/320px-4%2B1_Architectural_View_Model.svg.png)

  - **Description languages:** using formal modeling tools such as UML.

    ![](https://upload.wikimedia.org/wikipedia/commons/thumb/e/ed/UML_diagrams_overview.svg/800px-UML_diagrams_overview.svg.png)

  - **Architecture frameworks: ** there are established frameworks for describing software architecture, including [Arc42](https://arc42.org/), [C4 Model](https://c4model.com/), and [TOGAF](https://en.wikipedia.org/wiki/The_Open_Group_Architecture_Framework) (shown below).

    ![](https://upload.wikimedia.org/wikipedia/commons/thumb/a/a1/TOGAF_ADM.jpg/469px-TOGAF_ADM.jpg)



**2.4. Provide the advantages and disadvantages of the main approaches explained in question 2.3.**

|                   | Architecture viewpoints                                      | Description Languages                                        | Architecture framework                                       |
| ----------------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **Advantages**    | Tailored to different stakeholders, separation of concerns, flexibility, scalability | Formal, unambiguous, tool support, consistency, good documentation | Comprehensive, standardized, incorporates best practices, alignment with business goals |
| **Disadvantages** | Potential redundancy, hard to integrate views                | Complexity, rigidity, tool dependency, overhead              | Inflexible, complex, initial overhead, may not suit agile environments |


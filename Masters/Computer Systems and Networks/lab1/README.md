# Lab 1 - Identification of a System

> Ahmed Nouralla - a.shaaban@innopolis.university



**1- What is a System? Can you give an example? [[ref.](https://en.wikipedia.org/wiki/Category:Computer_systems)]**

- A computer system is the combination of hardware, software, user, and data.
- One example may can be a database: it has a
  - **Hardware:** Database Server (e.g., PostgreSQL).
  - **Software:** Database Management System (DBMS) installed on the server
  - **User:** Database Administrator (DBA) or users querying the database.
  - **Data:** raw values stored in the database



**2- What are the essential elements of a functional system? Can you draw a diagram of it?**

- As mentioned above, a computer system requires the interaction between hardware, software, user, and data.

- Here is a diagram that shows typical relationships between the aforementioned components.

  

  ![](https://i.postimg.cc/ZYcPBZCz/Untitled-Diagram-drawio.png)



**3- What is feedback in a system? What does it help us with? [[ref.](https://en.wikipedia.org/wiki/Feedback)]**

- A feedback means taking an an output from the system and feeding it back as an input

- It helps to modify and potentially enhance/optimize the system, essentially making it better at doing its functionality.

- The term is most popular in the context of Control Systems, the fundamental science behind multiple disciplines in robotics.

  

**4- What is an Information System? Can you give an example? [[ref.](https://en.wikipedia.org/wiki/Information_system)]**

- An information system may collect, process, store, and/or distribute information.
- A major example is a search engine, which is an information retrieval system that is concerned with finding documents that satisfy an information need from within large collections. 



**5- What are the essential elements of a functional Information System? [[ref1.](https://en.wikipedia.org/wiki/Information_system), [ref2.](https://document360.com/blog/people-process-technology-framework/)] Can you draw a diagram of it?**

- From a sociotechnical perspective, Information systems generally consisted of four components: task, people, structure, and technology

- Historically, Leavitt's Diamond model represented these components, though it later evolved into different names such as PPT (People, Processes, and Technology/IT). 

  ![](https://document360.com/wp-content/uploads/2024/05/Leavitts-Diamond-Model.webp)



**6- From your point of view, what are the components of a distributed file system? Can you draw a diagram connecting these components?**

- A distributed system is a a collection of autonomous computing elements (nodes) that appears to its users as a single coherent system. They communicate via a network (by passing messages) and collaborate in achieving tasks.

- Based on that definition, a distributed file system cannot exist without **nodes** storing/sharing **files** over some **overlay network** (abstraction over a physical network).

  ![](https://i.postimg.cc/g2CgpZzH/Untitled-Diagram-drawio-2.png)



**7- What is the difference between a Centralized, Decentralized, and Distributed system? Can you give an example of each one? [[ref.](https://www.geeksforgeeks.org/comparison-centralized-decentralized-and-distributed-systems/)]**

> My answer for that particular question is quite long as it was a research interest of mine.

- Centralization vs. decentralization refer to different levels of **control**

|              | Centralized System                                           | Decentralized System                                         |
| ------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **Property** | has a single point of control                                | does not have a single point of control                      |
| **Example**  | Google LLC is the single organization in control of all Google systems and services. | There is no single organization that controls the blockchain network. |

- Distribution vs. co-location refer to differences of **location**.

|              | Distributed System                                           | Co-located System                              |
| ------------ | ------------------------------------------------------------ | ---------------------------------------------- |
| **Property** | components are spread across multiple geographical locations | components exist in the same physical location |
| **Example**  | BitTorrent file storage system                               | Local FTP server in some company.              |

- You can mix and match between the categories. Showing some examples

|                 | Centralized                                                  | Decentralized                                                |
| --------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **Distributed** | [Google LLC](https://en.wikipedia.org/wiki/Google) systems (*) | [BitTorrent](https://en.wikipedia.org/wiki/BitTorrent) file storage system |
| **Co-located**  | Local [FTP](https://en.wikipedia.org/wiki/File_Transfer_Protocol) server in some company. | An Internet Exchange Point ([IXP](https://en.wikipedia.org/wiki/Internet_exchange_point)) (**) |

- (*) Google systems are centralized in control but mostly geographically distributed for efficiency and redundancy
- (**) An IXP is a common ground (co-located) for multiple ISPs (decentralized in control), in which they exchange data over a unified protocol (BGP) for the Internet to work.



**8- What does transparency mean in distributed systems? Can you give an example for each form of transparency? [[ref.](https://en.wikipedia.org/wiki/Transparency_(human%E2%80%93computer_interaction)#Types_of_transparency_in_distributed_system))]**

- Transparency is about hiding internal workings of the distributed system for it to appear to the user as a single coherent system. Full transparency is almost impossible to implement and is not generally required.

- Table shows common types of distributed transparency.

  | Type        | Description                                                  |
  | ----------- | ------------------------------------------------------------ |
  | Access      | Hiding differences between each node’s internal representation of data |
  | Location    | Hide where object is located.                                |
  | Relocation  | Hide the process of moving objects between nodes **while in use**. |
  | Migration   | Hide the process of moving objects between nodes             |
  | Replication | Hide the process of **cloning/replicating** objects across nodes |
  | Concurrency | Hide the fact that the object might be shared between users  |
  | Failure     | Minimize problems that user experience when system fails and recovers, also try to minimize the number of users affected |

  

**9- In system documentation, what is the difference between Structure and Behavior documentation? [[ref,](https://en.wikipedia.org/wiki/Software_documentation)]**

- When documenting the structure of a system, we focus on it's components and how they interact
- When documenting the behavior of a system, we focus on the functionality of the system during runtime.
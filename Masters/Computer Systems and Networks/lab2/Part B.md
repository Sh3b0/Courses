# Lab 2 - Architectural Patterns - Part B

## Description

This task is based on a real world application used by customers and costing millions with regards development and deployment. It will be done in a team of four (4).

Devices such as cameras, electric switches, fridges, thermostats, intercoms and more, all have an individual respective application with which it can be managed from a smart phone. Regardless, having a unified view of all devices on a single display would be easier on a user.

A startup company called Is3 develops a dashboard system which in almost real time, reports on the status of it’s clients’ devices. The application gathers stateless information from registered IoT devices, and formats the data into the visually beautiful dashboard which provides information on the happenings of the customer’s devices.

Furthermore, some pre-defined queries can be executed by the customer in order to gain access to more information with respects to the devices.

Kindly note that no data is added or updating by the customer. The information with regards the device status is received directly from the devices in the field and this data is simply presented to the end-user. 

Also, the sales team will add customers into the system manually after an intense verification process; hence, you can assume that the devices are already registered as there is no registration process for the system.

You are the architect and it’s your duty to design the architecture in order to ensure the Is3 application will push the business and lead eventually to a successful initial public offering.

## Task

### 3.1 **Define the Functional and Non-Functional Requirements**

#### **Functional Requirements**

These are the features that the system must provide to meet the business and user needs.

1. **Dashboard Display**
   - Display real-time information from IoT devices in a visually appealing dashboard (e.g., graphs, tables, status indicators).
   - Support multiple devices per customer, with the ability to view device status in a single, unified interface.
   - Allow users to filter, sort, and organize the data displayed on the dashboard based on device type, status, or other predefined metrics.
   - Provide drill-down functionality for more detailed views of individual device status.
2. **Predefined Queries**
   - Provide customers with predefined queries that allow them to request additional information about their devices (e.g., historical data, trends, device usage).
   - Queries should be executed in real-time or near-real-time and return results quickly.
3. **Customer Management**
   - The sales team can manually add customers to the system after verifying their details.
   - Ensure each customer is linked to their registered IoT devices, with a seamless experience when accessing device data.
4. **Real-Time Data Collection**
   - IoT devices send data in real-time (or near-real-time) to the system.
   - Ensure the system can handle a high frequency of device data updates and process them efficiently.
5. **Data Security and Privacy**
   - Ensure data is transmitted securely between the devices, backend, and frontend (using encryption protocols such as TLS/SSL).
   - Ensure that customer data is protected according to applicable privacy regulations (e.g., GDPR, HIPAA).
6. **Scalability**
   - The system should be able to handle an increasing number of customers and IoT devices without significant degradation in performance.
7. **User Interface**
   - Provide an easy-to-navigate web interface for customers to access the dashboard.
   - Support mobile-friendly design, given that the system will be primarily accessed from smartphones.
8. **Notification System (Optional)**
   - Allow customers to set up alerts for certain conditions (e.g., device malfunction, status change) and receive notifications in real-time.

------

#### **Non-Functional Requirements**

These are the performance, scalability, reliability, and operational attributes that the system should meet.

1. **Performance**
   - The system should be able to handle thousands of devices sending data every second.
   - Dashboard updates should occur in near-real-time (ideally within 1–3 seconds of receiving data from the device).
   - Queries should return results within a reasonable time frame (e.g., less than 5 seconds for predefined queries).
2. **Availability**
   - The system should be highly available with minimal downtime.
   - Ensure system reliability by implementing load balancing, failover mechanisms, and redundancy in critical components.
3. **Scalability**
   - The system must scale horizontally to accommodate growth in both the number of customers and devices.
   - Use cloud infrastructure and microservices to support easy scaling and resource allocation as needed.
4. **Security**
   - All data in transit must be encrypted using industry-standard encryption protocols.
   - Implement strong authentication mechanisms for users accessing the system, such as two-factor authentication (2FA).
   - Ensure that only authorized users can access specific customer data (role-based access control).
5. **Maintainability**
   - The system should be designed in a way that is easy to maintain and extend, with well-defined APIs and a modular architecture.
   - It should be easy to monitor the health and performance of the system and receive alerts when issues arise.
6. **Cost Efficiency**
   - The solution should be cost-effective, especially during the initial phase of the startup. Leveraging cloud-native services can help optimize costs.

------

### 3.2 **Map the Components of the Architecture**

1. **Frontend (User Interface)**
   - **Web Application**: A responsive dashboard that displays data in real-time or near-real-time.
   - **Mobile Application (Optional)**: If required, a mobile application that provides a similar interface for device monitoring.
   - **Query Execution**: Allows users to run predefined queries to gather additional information on their devices.
2. **Backend (Server-Side)**
   - **API Layer**: Exposes RESTful APIs to interact with frontend and other systems. The API layer handles user requests and serves data from the backend.
   - **Query Service**: A service to handle and execute predefined queries against device data.
   - **Device Data Processor**: Processes real-time data from IoT devices and formats it for the dashboard.
   - **Customer Management Service**: Manages customer data and links it to their respective IoT devices.
   - **Notification Service (Optional)**: Sends real-time alerts to users when certain device conditions are met.
3. **Data Store (Database)**
   - **Relational Database**: Stores customer information, device metadata, and user profiles.
   - **Time-Series Database**: Stores device status data over time for performance and scalability in real-time data collection.
   - **Cache Layer**: Caches frequently accessed data to reduce latency (e.g., Redis for fast querying).
4. **IoT Device Interface**
   - **Device Communication Layer**: Collects data from IoT devices. This may include direct HTTP communication, MQTT brokers, or WebSockets, depending on the device protocol.

------

### 3.3 **Choose the Technology Stack**

1. **Frontend**
   - **React** or **Vue.js** (for building responsive, real-time dashboards).
   - **D3.js** or **Chart.js** (for rendering real-time charts and data visualizations).
   - **Bootstrap** or **Tailwind CSS** (for responsive UI components).
2. **Backend**
   - **Node.js** with **Express** (for RESTful APIs and real-time interactions).
   - **Python** with **Flask/Django** (optional, for data processing or if there’s heavy analytics involved).
   - **GraphQL** (optional, for flexible querying of data by the frontend).
3. **Data Store**
   - **PostgreSQL** or **MySQL** (for relational data storage such as customer information and device metadata).
   - **InfluxDB** or **TimescaleDB** (for time-series data storage, ideal for real-time device status data).
   - **Redis** (for caching and fast retrieval of frequently accessed data).
4. **IoT Device Communication**
   - **Vendor dependent**, some devices may use MQTT, WebSocket, or other protocols.
5. **Cloud Infrastructure**
   - The backend should be hosted somewhere (e.g., **AWS** or **Google Cloud**)
   - Depending on the chosen provider, we may utilize additional services for containerization, database management, cloud functions, etc.
6. **Security**
   - **OAuth2.0** with **JWT** (for secure authentication and authorization).
   - **TLS/SSL** for encrypted communication between devices and the system.

------

### 3.4 **Design the Architecture**

- Imagined high-level structure for application components and their interactions.

![](https://i.postimg.cc/8P3wS0N6/Untitled-Diagram-drawio-5.png)

------

### 3.5 **Write a Detailed Architecture Document**

**Overview:** The Is3 application is designed to provide a real-time, unified dashboard for monitoring the status of IoT devices. The system uses a cloud-based, scalable architecture to handle real-time data ingestion, customer management, and dynamic dashboard updates.

**Core Components:**

1. **Frontend (Web and Mobile)**:
   - A React-based web dashboard is used to display real-time device status data. This dashboard includes dynamic charts, tables, and device status indicators. The web interface is mobile-friendly.
2. **Backend (API and Data Processing)**:
   - The backend consists of a Node.js API that handles user requests, executes predefined queries, and communicates with the data store. The backend also processes real-time device data and pushes updates to the frontend.
3. **Data Store**:
   - A relational database (PostgreSQL) stores customer and device metadata. A time-series database (InfluxDB) is used for storing and querying device status data over time.
4. **Real-Time Device Data**:
   - Devices communicate with the system using MQTT or WebSocket protocols to push status updates to the server. The data is ingested by a message queue and processed by an event processing engine.
5. **Cloud Infrastructure**:
   - The system is hosted on cloud infrastructure (AWS or Google Cloud) to support scalability and high availability.
6. **Security**:
   - OAuth2.0 and JWT are used for secure user authentication and role-based access control. All communication between devices and the system is encrypted using TLS/SSL.

**Conclusion:** This architecture ensures that the Is3 system can handle the real-time data processing and scalability requirements necessary to support a large number of IoT devices and customers. It is designed to be secure, maintainable, and cost-efficient, aligning with both business and technical goals.

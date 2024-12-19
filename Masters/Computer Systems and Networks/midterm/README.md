# Research on Kubernetes Architecture

>  Overleaf: https://www.overleaf.com/project/673c76f4e487db22c87f4f2c

[TOC]

## Task

- Look for 2-3 references (scientific papers or books –use Google Scholar) (at least 2, sites do not count)
- Prepare 15 slides (please use overleaf)
- Follow the recommended structure

## 1. Description of the system

- Kubernetes (aka. K8s or Kube) is an open-source system for automating the deployment, scaling, and management of **containerized** applications.
- Originally designed by **Google** and is currently the most used container orchestration platform.
- From a high-level perspective, K8s provides **abstractions** to manage workloads running on a cluster of machines.

## 2. Context

![Components of Kubernetes](https://kubernetes.io/images/docs/components-of-kubernetes.svg)

> Image source: https://kubernetes.io/images/docs/components-of-kubernetes.svg

a. Describe a possible scenario for using the system

- The cluster is controlled and managed by a master node, storing cluster metadata in a key-value store (`etcd`) and exposing an API for interaction.
- A typical use case involves creating/querying [objects](https://kubernetes.io/docs/concepts/overview/working-with-objects/kubernetes-objects/) via the API 
  - `kubectl apply -f deployment.yaml` to initiate application deployment on a cluster of nodes
  -  `kubectl describe <deployment_name>` to query its status.

b. Mention some security issues in the scenario

- Kubernetes API is the central point with full control over the cluster. If access control is improperly configured, it can give an attacker a full control over the infrastructure.

## 3. Main architectural drivers

- Kubernetes was designed with a huge focus on
  - **High availability:** striving for 0 downtime through replication and fault tolerance mechanisms.
  - **Automatic scaling:** through components such as the HorizontalPodAutoscaler (HPA).
  - **Self-healing:** containers periodical LivenessProbes and automatic restarting.

## 4. Structure

(which patterns? components/connectors?)

- A single node in a K8s cluster may be responsible for multiple objects

![](https://kubernetes.io/docs/tutorials/kubernetes-basics/public/images/module_03_nodes.svg)

> Image source: https://kubernetes.io/docs/tutorials/kubernetes-basics/public/images/module_03_nodes.svg

Popular object **kind**s (check `kubectl api-resources`)

| Object                                                       | Overview                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [**Pod**](https://kubernetes.io/docs/concepts/workloads/pods/) | Represents a logical host that typically runs one containerized application, but may run additional **[sidecar](https://kubernetes.io/docs/concepts/workloads/pods/#workload-resources-for-managing-pods)** containers. |
| [**ReplicaSet**](https://kubernetes.io/docs/concepts/workloads/controllers/replicaset/) | Ensures that a specified number of pod replicas are running at one time. |
| [**Deployment**](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/) | Represents an application running in the cluster, provides declarative updates for Pods and ReplicaSets. |
| [**Service**](https://kubernetes.io/docs/concepts/services-networking/service/) | Represents a network service that makes a set of pods accessible using a single DNS name and can load-balance between them. |
| [**ConfigMap**](https://kubernetes.io/docs/concepts/configuration/configmap/) | An API object used to store non-confidential  data as key-value pairs that are accessible by pods (e.g., as environment variables). |
| [**Secret**](https://kubernetes.io/docs/concepts/configuration/secret/) | Similar to ConfigMaps, but are specifically intended to hold confidential data (e.g., passwords and tokens). |
| [**Ingress**](https://kubernetes.io/docs/concepts/services-networking/ingress/) | An API object that manages external access to the services in a cluster, typically HTTP. |
| **[StatefulSet](https://kubernetes.io/docs/concepts/workloads/controllers/statefulset/)** | A deployment for stateful applications; provides guarantees about the ordering and uniqueness of deployed Pods. |
| **[DaemonSet](https://kubernetes.io/docs/concepts/workloads/controllers/daemonset/)** | DaemonSet ensures that a copy of a certain pod (e.g., logs collector, metrics exporter, etc) is available on every node in the cluster. |
| **[PersistentVolume](https://kubernetes.io/docs/concepts/storage/persistent-volumes/)** | Abstraction of a persistent storage that can use a local or remote (cloud) storage as a backend. Pods can acquire portions of that storage using a PersistentVolumeClaim |
| **[LimitRange](https://kubernetes.io/docs/concepts/policy/limit-range/)** | Enforces minimum and maximum resource usage limits per pod or container in a namespace. |

## 5. Behavior

(how does the system work?)

- Kubernetes objects are managed through a control loop
- Essentially, the master node periodically compares object ‘status‘ vs. ‘spec‘, making
  changes as needed.
- For example, if a deployment specification requires ‘repliacs: 3‘ and the controller sees that object status contains ‘replicas: 2‘, it will trigger the scheduling of an additional pod.

## 6. Rationale

(why the system has this form? History of the system?)

-  K8s started at Google as an internal cluster management tool called Borg.
- It was later open-sourced under the Cloud Native Computing Foundation (CNCF) to encourage contribution from the community as Linux containers got more popular.
- K8s current architecture was heavily influenced by its predecessors (Borg and Omega). In particular, the transition to a master-slave architecture with loosely-coupled components and the interaction only through an API.

## 7. Similar or competing systems

- AWS Elastic Container Service (ECS): a managed container orchestrator that integrates tightly with other AWS services, easier to setup and operate compared to K8s, but with less flexibility in multi-cloud or on-premises scenarios.
- RedHat OpenShift (Container Platform): a suite of components built around K8s with additional enterprise features (e.g., integration with popular cloud providers, build-related artifacts, and opinionated defaults).
- Hashicorp Nomad: a lightweight, flexible workload orchestrator for deploying and managing containers, virtual machines, and other workloads across data centers.

## 8. References

- Large-scale cluster management at Google with Borg: https://doi.org/10.1145/2741948.2741964

- Borg, Omega, and Kubernetes: https://doi.org/10.1145/2890784

- Official Kubernetes Documentation: https://kubernetes.io/docs/home/

  




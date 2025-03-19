# Lab on Kubernetes Basics

[TOC]

## Task 1

### 1.1. Application Choice

- Application source code is available in `1-app` directory. It's a simple Python/Flask app to show the current time (with added features to support the requirements for this task).
- Container is published on DockerHub https://hub.docker.com/r/sh3b0/app
- Application source code and all manifests used are available at [https://github.com/DEPI-DevOps/k8s-basics-lab](https://github.com/DEPI-DevOps/k8s-basics-lab) 

### 1.2. Basic Concepts

Short Overview on Essential K8s Concepts:

- Kubernetes (aka. K8s or Kube) is an open-source system for automating the deployment, scaling, and management of **containerized** applications.
- From a high-level perspective, K8s provides **abstractions** to manage workloads running on a cluster of machines.
  - The cluster of **worker nodes** are controlled and managed by a **master node**, storing cluster metadata in a key-value store (`etcd`) and exposing an **API** for interaction.

- A typical use case involves creating/querying [objects](https://kubernetes.io/docs/concepts/overview/working-with-objects/kubernetes-objects/) via the API 
  - `kubectl apply -f deployment.yaml` to initiate application deployment on a cluster of nodes
  - `kubectl describe <deployment_name>` to query its status.
- Kubernetes objects are managed through a **control loop**. Essentially, the controller periodically compares object `status` vs. `spec`, making changes as needed.
  - For example, if a deployment specification requires `repliacs: 3` and the controller sees that object status contains `replicas: 2`, it will trigger the **scheduling** of an additional pod.
- Kubernetes was designed with a focus on
  - High availability: striving for 0 downtime through replication and fault tolerance mechanisms.
  - Automatic scaling: adapting to varying load by increasing/decreasing nodes/pods as needed.
  - Self-healing: periodical probes to check container health and automatic restarting on failures.

### 1.3. Tool Setup

- Kubectl (https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/)

  ```bash
  curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
  sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
  alias k="kubectl" # Saves countless hours
  ```

- Minikube (https://minikube.sigs.k8s.io/docs/start)

  ```bash
  curl -LO https://github.com/kubernetes/minikube/releases/latest/download/minikube-linux-amd64
  sudo install minikube-linux-amd64 /usr/local/bin/minikube && rm minikube-linux-amd64
  minikube start
  ```


- Verification

  ![image-20250218141214909](https://i.imgur.com/hg77gC4.png)

### 1.4. Kubernetes Dashboard

- Minkube provides a simple way to access the dashboard quickly

  ```bash
  minikube addons enable metrics-server
  minikube dashboard --url
  ```

  ![image-20250216231545733](https://i.imgur.com/j0wVnEi.png)

## Task 2 - Nodes

- Get and describe cluster nodes.

  ![image-20250218154118057](https://i.imgur.com/qL1FJHB.png)

- Node conditions

  ![image-20250218154623002](https://i.imgur.com/NN6geAj.png)

- Node capacities, **OS (system) info**, Pod CIDR, and non-terminated pods

  ![image-20250218154643472](https://i.imgur.com/VEyZOY7.png)

- Node's resources requests and events

  ![image-20250218154656758](https://i.imgur.com/tHzNpGa.png)

- Find node's YAML spec at `2-node.yaml`

## Task 3 - Pod

1. Figure out the necessary Pod spec fields.

   - A pod should have at least one container, a container should have a name, image, ports
   - It's also recommended to set resource limits to prevent leaky images from consuming all cluster resources.

1. Write a Pod spec for your chosen application, deploy the application and run a pod.

   ```yaml
   apiVersion: v1
   kind: Pod
   metadata:
     name: myapp
     labels:
       name: myapp
   spec:
     containers:
     - name: myapp
       image: sh3b0/app
       resources:
         limits:
           memory: "128Mi"
           cpu: "500m"
       ports:
         - containerPort: 8080
   ```

1. With kubectl, get the pods, pod logs, describe pod, go into pod shell.

   ![image-20250218155615819](https://i.imgur.com/YwWXLu7.png)

   ![image-20250218155657361](https://i.imgur.com/5VmLcrZ.png)

1. Make sure that your app is working correctly inside Pod.

   - Verified app is working by forwarding port 8080 and contacting it from host.

     ![image-20250218155844450](https://i.imgur.com/rIyHKql.png)

## Task 4 - Service

1. Figure out the necessary Service spec fields.

   - A service should have a `type` (ClusterIP by default, for internal services)
   - It should also specify a selector (to match target pods) and ports for the service and target.

1. Write a Service spec for your pod(s) and deploy the Service.

   ```yaml
   apiVersion: v1
   kind: Service
   metadata:
     name: app-svc
   spec:
     selector:
       app: myapp
     ports:
     - port: 8080
       targetPort: 8080
   ```

1. With kubectl, get the Services and describe them.

   ![image-20250218160948622](https://i.imgur.com/eZZuR6x.png)

1. Make sure that pods can communicate between each other using DNS names, check pods addresses.

     - K8s automatically generates internal DNS names for **both** pods and services [[ref.](https://kubernetes.io/docs/concepts/services-networking/dns-pod-service/)]


     - Added a second pod, showed internal addresses, then used generated Pod DNS name to ping  the second one from the first.
    
       ![image-20250218161744233](https://i.imgur.com/WPQyujg.png)

1. Delete any Pod, recreate it and check addresses again. Make sure that traffic is routed to the
   new Pod correctly.

     - As shown above, `myapp` pod had address `10.244.0.29`


     - If we delete and recreate it, it gets a new address as shown.
    
       ![image-20250218162442031](https://i.imgur.com/84uUg1J.png)


     - However, K8s internal DNS names for **services** can be used to reach the pod regardless.


     - We can verify that by contacting the generated service name from somewhere inside the cluster (e.g., a test pod)
    
       ![image-20250218172935974](https://i.imgur.com/Ec8gKbx.png)

1. Learn about Loadbalancer and NodePort.

   - LoadBalancer service balances traffic requests between the available pods matching the service selector.
   - NodePort can be used (for debugging purposes) to expose services to outside the cluster. In production, Ingress should be used instead.

1. Deploy an external Service to access your application from outside, e.g., from your local host.

     - Changed the service type to `NodePort`

       ```yaml
         apiVersion: v1
         kind: Service
         metadata:
           name: app-svc
         spec:
           type: NodePort
           selector:
             name: myapp
           ports:
           - port: 8080
             targetPort: 8080
             nodePort: 31000
       ```


     - Now the service is accessible from outside the cluster at a fixed port number.
    
       ![image-20250218173144839](https://i.imgur.com/YVwRj02.png)


## Task 5 - Deployment

1. Figure out the necessary Deployment spec fields.

   - A deployment should specify a template (from which pods are created) and a selector to link such pods to the deployment.

1. Make sure that you wiped previous Pod manifests. Write a Deployment spec for your pod(s)
     and deploy the application.

     ```yaml
     apiVersion: apps/v1
     kind: Deployment
     metadata:
       name: app-deployment
       labels:
          app: myapp
     spec:
       selector:
         matchLabels:
           app: myapp
       template:
         metadata:
           labels:
             app: myapp
         spec:
           containers:
           - name: myapp
             image: sh3b0/app
             resources:
               limits:
                 memory: "128Mi"
                 cpu: "500m"
             ports:
             - containerPort: 8080
     ```

1. With kubectl, get the Deployments and describe them.

     ![image-20250218173824056](https://i.imgur.com/SM8WJ8u.png)

1. Update your Deployment manifest to scale your application to three replicas.

     - Added `replicas: 3` under spec and reapplied.

       ![image-20250218173932697](https://i.imgur.com/Flqf0tD.png)

5. Access pod shell and logs using Deployment labels.

   - All the pods that are part of the deployment will have the label `app: myapp` attached to them (as specified in the template above).

     - Note: this is pod label (not deployment label). If needed, we can specify labels on the deployment level and use it in a similar way.


   - We can get pod logs for the pods with a given label using the `-l` flag. Note that this matches and shows logs from the three pods (since they all have the label attached).

     ![image-20250218174903151](https://i.imgur.com/92xKqgn.png)


   - For shell however, we have to specify which pod we want to access


   - This command runs a subshell command to get the name of the first pod with the given label, and use it to obtain a shell in that pod.

     ![image-20250218175111166](https://i.imgur.com/mopKC1N.png)

5. Make any application configuration change in your Deployment yaml and try to update the
   application. Monitor what are happened with pods (--watch).

   - Watching the process as application pods get scaled from 1 to 3

     ![image-20250218175649535](https://i.imgur.com/NtGWBox.png)

5. Rollback to previous application version using Deployment.

   - Here is a small scenario, deployment was created with `sh3b0/app:v1` then updated to `sh3b0/app:v2`. Annotations were added to see the "change cause" in output later.

   - After that, a `rollout` is used to `undo` the latest change and go back to `v1`

   - Process is quite similar to `git` commits.

     ![image-20250218181351048](https://i.imgur.com/j5GtgMA.png)

5. Set up requests and limits for CPU and Memory for your application pods. Provide a PoC
   that it works properly. Explain and show what happens when app reaches the CPU usage
   limit? And memory limit?

   - We can setup resource requests and limits on the pod-level or the container-level

   - It won't make a big difference for this experiment as we have one pod per container for now.

   - Add the following snippet under `app-deployment.spec.template.spec`

     ```yaml
     resources:
       requests:
         memory: "64Mi"
         cpu: "250m"
       limits:
         memory: "128Mi"
         cpu: "500m"
     ```

   - Change application to use a stress testing image.

     ```yaml
     image: polinux/stress-ng
     args: ["--cpu", "1", "--vm", "1", "--vm-bytes", "500M", "--timeout", "60s"]
     ```

   - Pods are prevented from exceeding CPU limits by throttling (causing performance degradation)

     ![image-20250218183658681](https://i.imgur.com/t19s6X9.png)

   - If a process exceeds memory limits, it's terminated by OOM (Out-of-Memory) Killer.

     ![image-20250218184107281](https://i.imgur.com/x8u8aDf.png)

   - [Reference](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/#requests-and-limits) for the above-mentioned details.

## Task 6 - Secrets

1. Figure out the necessary Secret spec fields.

   - A secret needs a type and data (encoded secret content).
   - Opaque is commonly used for arbitrary user-defined data, but there are other [types](https://kubernetes.io/docs/concepts/configuration/secret/#secret-types) as well.

1. Create and apply a new Secret manifest.

   ```yaml
   apiVersion: v1
   kind: Secret
   metadata:
     name: app-secret
   type: Opaque
   data:
     secret-key: c2VjcmV0LXZhbHVlCg== # echo "secret-value" | base64
   ```

3. With kubectl, get and describe your secret(s)

   ![image-20250218184709541](https://i.imgur.com/Wm6BvP6.png)

4. Decode your secret

   ![image-20250218185413359](https://i.imgur.com/1GIiauj.png)

5. Update your Deployment to reference to your secret as environment variable.

   - Added the snipper under a certain container under the deployment pod template.

     ```yaml
     env:
     - name: APP_SECRET
       valueFrom:
         secretKeyRef:
           name: app-secret
           key: secret
     ```

6. Make sure that you are able to see your secret inside pod

   - Applying the config above and verifying

     ![image-20250218190303495](https://i.imgur.com/rjJMACY.png)

## Task 7 - ConfigMap

1. Figure out the necessary configMap spec fields.

   - ConfigMap needs at least data (key-value pairs)

1. Modify your Deployment manifest to set up some app configuration via environment variables.

     - We can add a regular env var to a container by specifying

       ```yaml
       env:
       - name: SOME_KEY
         value: "Some Value"
       ```


3. Create a new configMap manifest. In data spec, put some app configuration as key-value pair. In the `Deployment.Pod` spec, add the connection to key-value pair from configMap yaml file.

     - ConfigMap Manifest

       ```yaml
       apiVersion: v1
       kind: ConfigMap
       metadata:
         name: app-cm
       data:
         cm-key: cm-value
       ```


     - Added the snipper under a certain container under the deployment pod template.
    
       ```yaml
       env:
       - name: APP_SECRET
         valueFrom:
           configMapKeyRef:
             name: app-cm
             key: cm-key
       ```


     - Verify
    
       ![image-20250218192807058](https://i.imgur.com/KjMNHix.png)


4. Create a new file named `config.json` file and put some JSON data into it.

   ```bash
   echo '{"key": "value"}' > config.json
   ```

5. Create a new one configMap manifest. Connect configMap YAML file with `config.json` file
   to read the data from it.

     - We can embed the JSON as a multiline value directly in the manifest as follows

       ```yaml
       apiVersion: v1
       kind: ConfigMap
       metadata:
         name: file-cm
       data:
         config.json: |
           {
             "key": "value"
           } 
       ```


     - Alternative way to create the ConfigMap from the CLI
    
       ```bash
       kubectl create configmap file-cm --from-file=config.json
       ```


     - Without templating (e.g., helm) or some scripting, it's not possible to reference the JSON file from the YAML manifest.


6. Update your Deployment to add Volumes and VolumeMounts.

   - Specifying the configMap as a volume, then mounting that volume to the container

     ```yaml
     containers:
       - name: myapp
         image: sh3b0/app
         resources:
           limits:
             memory: "128Mi"
             cpu: "250m"
         ports:
           - containerPort: 8080
         volumeMounts:
           - name: cm-volume
             mountPath: /mnt
     volumes:
     - name: cm-volume
       configMap:
         name: app-secret
     ```

7. With kubectl, check the configMap details. Make sure that you see the data as plain text.

   ![image-20250218195159380](https://i.imgur.com/EGIR008.png)

8. Check the filesystem inside app container to show the loaded file data on the specified path.

   ![image-20250218195350433](https://i.imgur.com/sBIahXK.png)

## Task 8 - Namespace

1. Figure out the necessary Namespace spec fields.

   - Namespace needs at least a name

1. Create a two different Namespaces in your k8s cluster.

   ```yaml
   apiVersion: v1
   kind: Namespace
   metadata:
     name: ns-1
   
   ---
   apiVersion: v1
   kind: Namespace
   metadata:
     name: ns-2
   ```

1. Using kubectl, get and describe your Namespaces.

   ![image-20250218195740630](https://i.imgur.com/RDVpUtD.png)

1. Deploy two different applications in two different Namespaces with kubectl.

     - Added two application pods such that `pod-1` will be deployed in the first namespace, while `pod-2` will be deployed in the second one.

       ```yaml
       apiVersion: v1
       kind: Pod
       metadata:
         name: pod-1
         namespace: ns-1
       spec:
         containers:
         - name: app
           image: sh3b0/app
           resources:
             limits:
               memory: "128Mi"
               cpu: "500m"
           ports:
             - containerPort: 8080
       
       ---
       apiVersion: v1
       kind: Pod
       metadata:
         name: pod-2
         namespace: ns-2
       spec:
         containers:
         - name: app
           image: sh3b0/app
           resources:
             limits:
               memory: "128Mi"
               cpu: "500m"
           ports:
             - containerPort: 8080
       ```


5. With kubectl, get and describe pods from different Namespaces witn -n flag.![image-20250218200133644](https://i.imgur.com/TF0YALr.png)

   ![image-20250218200202303](https://i.imgur.com/GmoWkq6.png)

6. Can you see and can you connect to the resources from different Namespaces?

   - Namespaces isolate resources, so the `kubectl get` command will show resources from the `default` namespace by default, unless a different one is specified with the `-n` flag.


   - However, DNS names for pods and services also resolve between different namespaces.


   - Therefore, `pod-2` in `ns-2` is still reachable from `pod-1` in `ns-1` as shown

     ![image-20250218200742546](https://i.imgur.com/YzZ5vLy.png)

7. [Bonus] More convenient way to switch namespaces using `kubens`

   - This helps avoid having to add `-n` for every `kubectl` command in a namespace


   - Install `kubectx`

     ```bash
     sudo git clone https://github.com/ahmetb/kubectx /opt/kubectx
     sudo ln -s /opt/kubectx/kubectx /usr/local/bin/kubectx
     sudo ln -s /opt/kubectx/kubens /usr/local/bin/kubens
     ```


   - Example usage:

     ![image-20250218201912324](https://i.imgur.com/QPw5m4C.png)


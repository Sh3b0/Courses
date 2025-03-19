# Lab 4 - Advanced Kubernetes

> Ahmed Nouralla - a.shaaban@innopolis.university

[TOC]

## Task 1 - Preparation

- Application source code is available in `0-app` directory. It's a simple Python/Flask app to show the current time (with added features to support the requirements for this task).

### 1.1. Minimal Deployment:

- Refer to the [next section](#12-tool-installation) for tool installation

- Basic lab All-in-One

  ![image-20250216225302848](https://i.imgur.com/22d8lw2.png)

  ![image-20250216225718013](https://i.imgur.com/fz3uVMh.png)

- The manifests at `1-preparation.yaml` create the following API resources

  - **Namespace:** to group related resources together for better organization and easier cleaning later.
  - **Deployment:** abstraction over pods to pull and run `sh3b0/app` from DockerHub. Currently, 1 replica is set, but can be increased if needed.
  - **ConfigMap:** defines a non-sensitive key-value pair (`cm-key: cm-value`) that is mounted as an environment variable to the `app` container in the application pod.
  - **Secret:** defines a sensitive key-value pair (`secret-key: secret-value`) that is mounted as a file (volume) into the `app` container in the application pod.
  - **Service:** defines a NodePort service (for testing, not recommended in production) that allows direct access to the app from outside

### 1.2. Tool Installation

- Kubectl (https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/)

  ```bash
  curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
  sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
  alias k="kubectl"
  ```

- Minikube (https://minikube.sigs.k8s.io/docs/start)

  ```bash
  curl -LO https://github.com/kubernetes/minikube/releases/latest/download/minikube-linux-amd64
  sudo install minikube-linux-amd64 /usr/local/bin/minikube && rm minikube-linux-amd64
  minikube start
  ```

- Helm (https://helm.sh/docs/intro/install/)

  ```bash
  curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3
  chmod 700 get_helm.sh
  ./get_helm.sh
  ```

- Verification

  ![image-20250216225541839](https://i.imgur.com/KA9O0oG.png)

### 1.3. Kubernetes Dashboard

- Minkube provides a simple way to access the dashboard quickly

  ```bash
  minikube addons enable metrics-server
  minikube dashboard --url
  ```

  ![image-20250216231545733](https://i.imgur.com/MDx2lex.png)

## Task 2 - PersistentVolume

1. Make sure that your application writes a data to a file from app logic work. If not, modify
    your app to do it.
    
      - On each visit to the `/` endpoint, application increments counter at `volume/visits.json`
    
      - The content of `visits.json` are accessible at `/visits` endpoint 
    
2. Figure out the necessary PersistentVolumeClaim spec fields.

   - At least, one should specify `resources.request.storage` and `accessModes`

   - Without explicitly creating a PV, minikube would use the `standard` storage provisioner to create a `hostPath` FileSystem volume for us.

3. Create and apply a new PersistentVolumeClaim manifest. Here you have to specify a
   volume with some data that should be connected to app pod.
     - Check the manifest at `2-volumes.yaml`: deploys `app-pod` and `app-pvc` in `volume-ns`


4. With kubectl, get and describe PVC and PV.![image-20250216182433130](https://i.imgur.com/shczoSY.png)![image-20250216182525713](https://i.imgur.com/rVWw6T9.png)![image-20250216182553433](https://i.imgur.com/3mgi7Ic.png)

5. Provide a PoC that the data from the PertistentVolume is available for your app pod on specified path.

     - The mounted volume at `/app/volume` is accessible in the container environment as shown

       ![image-20250216175827722](https://i.imgur.com/6YnF8h1.png)


6. Show the case when your app writes the data to the path that is specified on PV. Provide a PoC

     - The app increments visitor count and writes to the file on every visit


     - We can make visits with curl (left) and verify the file content has changed in the pod (right).
    
       ![image-20250216183040162](https://i.imgur.com/yjzDyfq.png)


     - For testing, pod IP was obtained from the output of `kubectl get pod -n volume-ns -owide`


7. Play with different Access Modes and Reclaim Policies within PersistentVolumeClaim manifest and put the results into report.

   - Volumes allow different access modes include ReadWriteOnce, ReadOnlyMany, ReadWriteMany [[ref.](https://kubernetes.io/docs/concepts/storage/persistent-volumes/#access-modes)]

     - ReadWriteMany (for example) allows the volume to be mounted as read-write by many nodes.
     - To test such feature in practice, one would need multi-node cluster deployment.
     - Currently, we're running a single node cluster in Minikube.


   - When a PVC is deleted, the property `persistentVolumeReclaimPolicy` specifies what happens to the `PV`.

     - Possible values are `Delete` (default), `Retain` (keep), and `Recycle` (deprecated) [[ref.](https://kubernetes.io/docs/concepts/storage/persistent-volumes/#reclaiming)].

     - Changed the default from `Delete` to `Retain`, then deleted the PVC, the PV is still there in a "released" state.

       ![image-20250216191009582](https://i.imgur.com/dJSbyFT.png)

## Task 3 - StatefulSet

1. Figure out the necessary StatefulSet spec fields.

   - A minimal StatefulSet could be defined in the same way as deployment, only by changing the `Kind`.
   - However, it's common to place statefulset pods behind a headless service [[ref.](https://kubernetes.io/docs/concepts/services-networking/service/#headless-services)] to be able to interact with individual pods using a structured DNS name.

1. Deploy your app using StatefulSet.

   - Check the manifest at `3-statefulset.yaml`: deploys `app-sts` and `app-sts` in `sts-ns`

1. With kubectl, get and describe sts.

   ![image-20250216194810619](https://i.imgur.com/7BkIVcv.png)

1. Scale and rollout your app replicas through StatefulSet.

   - Scaling from 2 to 3 replicas

     ![image-20250216195853065](https://i.imgur.com/wIR2quZ.png)

   - Rolling a new update (e.g., after changing image tag)

     ![image-20250216200451232](https://i.imgur.com/qvQtTTz.png)

1. Implement Startup, Liveness or Readiness probes within StatefulSet. Explain their difference.

     - We can use the `visits` endpoint to define a minimal liveness probe

       - K8s will periodically do GET requests and if it returns a non-success code, the container is considered unhealthy and restarted.

         ```yaml
         livenessProbe:
           httpGet:
             path: /visits
             port: 8080
             initialDelaySeconds: 3
             periodSeconds: 3
         ```


     - Probes [[ref.](https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/)]:
       - Startup Probe: to ensures the application has started.
       - Liveness Probe: to ensures the application is running.
       - Readiness Probe: to ensures the application is ready to serve traffic.


6. Add an init container to do some work before the core app container is created and started

     - Created a sample `alpine` init container for testing the functionality


     - It downloads some HTML to a shared volume, the main app container mounts the same volume so we can verify the logic.
    
       ```yaml
       initContainers:
         - name: init
           image: alpine:latest
           command: ["wget", "http://example.com/", "-P", "/tmp/example"]
           volumeMounts:
           - name: example-volume
             mountPath: /tmp/example
       ```


     - Init container started first, downloaded the files to the shared volume, then we were able to access the content.
    
       ![image-20250216213000966](https://i.imgur.com/5qqs2Qd.png)


7. Kill application pod and add a temporary "rescue" pod using pod manifest, use the temporal pod to restore volume data.

     - Rescue pod manifest

       ```yaml
       apiVersion: v1
       kind: Pod
       metadata:
         name: rescue-pod
         namespace: sts-ns
       spec:
         volumes:
           - name: visits-volume
             persistentVolumeClaim:
               claimName: app-pvc
         containers:
           - name: busybox
             image: busybox
             args: ["tail", "-f", "/dev/null"]
             volumeMounts:
               - mountPath: /mnt
                 name: visits-volume
             resources:
               limits:
                 memory: "128Mi"
                 cpu: "500m"
       ```


8. Scale the StatefulSet to 0 (to kill the pod and ensure it won't restart), then use `kubectl cp` command to recover volume content by copying it to host. After that, the pod is deleted.![image-20250216222555900](https://i.imgur.com/G8jSa5I.png)

## Task 4 - Job

1. Figure out the necessary Job spec fields.

   - A job needs a template for the pod that will run it.
   - If not specified, default values are set for other control information (e.g., backoff limit, TTL after finished, restart policy, etc).

1. Prepare and apply a Job manifest that creates and run a new temporal pod that to some work and then it's gone. For example, it could be some unit test or a load script that sends loading to your application database or pod filesystem.

  ```yaml
  apiVersion: batch/v1
  kind: Job
  metadata:
    name: app-job
    namespace: job-ns
  spec:
    ttlSecondsAfterFinished: 100
    template:
      spec:
        containers:
        - name: app-test
          image: sh3b0/app
          command: ["pytest", "-W", "ignore"]
        restartPolicy: Never
  ```

1. With kubectl, get and describe your jobs.

   ![image-20250220125408229](https://i.imgur.com/3eobz4s.png)

## Task 5 - Ingress

1. Figure out the necessary Ingress spec fields.

1. Create Ingress spec, bind it with your Service and apply.

   ```yaml
   apiVersion: networking.k8s.io/v1
   kind: Ingress
   metadata:
     name: app-ingress
     namespace: ingress-ns
   spec:
     rules:
     - host: app.local
       http:
         paths:
         - path: /
           pathType: Prefix
           backend:
             service:
               name: app-svc
               port:
                 name: svc-port
   ```

1. With kubectl, get and describe your Ingress.

   ![image-20250220141931316](https://i.imgur.com/ropMB7m.png)

1. Get access to your app from outside network using Ingress.host value.

   - Enable minikube addon for ingress

     ```bash
     minikube addons enable ingress
     ```

   - Add an entry in `/etc/hosts` for testing, then try accessing the app with curl

     ![image-20250220142036979](https://i.imgur.com/4Aln5OY.png)

5. Question: if we don't have an opportunity to use Ingress, how we can enable access to our
   app from outside directly?
   - For debugging, we can use a NodePort service (to get a fixed port number) or use `kubectl port-forward` to forward traffic from host to a certain service in the cluster



## MySQL - Docker setup
                           
You don’t have MySQL installed to test this example? No problem, one option is to use Docker to run it.

### Pull the MySQL Docker Image

```
docker pull mysql:8.0
```

### Run the MySQL Docker

```
docker run -d -p 3306:3306 \
    --name mysql-docker-container \
    -e MYSQL_DATABASE=YNaMi \
    -e MYSQL_USER=crazy \
    -e MYSQL_PASSWORD=crazy \
    -e MYSQL_ROOT_PASSWORD=root \
    mysql:8.0
```

By the way, if you didn't pull the docker image explicitly by running the `docker pull mysql:8.0` command, nothing
to worry about as the `docker run` command will pull the image if it's not found locally.

* `docker run` command first creates a writeable container layer over the specified image i.e. `mysql:8.0`
   and then starts it using the specified command.
* `-d` prints the container ID and runs the container in the background.
* `-p` publishes/exposes a container’s port(s) to the host.
* `–name` sets the name of the container i.e. `mysql-docker-container`.
* `-e`  sets environment variables:
  * `MYSQL_USER=crazy` has been set
  * `MYSQL_PASSWORD=crazy` has been set
  * `MYSQL_DATABASE=YNaMi` has been set
  * `MYSQL_ROOT_PASSWORD=root` has been set
  * With the environment variables:
    * we have changed the root user password 
    * named a database that will be created when the image starts up 
    * created a user that will be granted superuser permissions for the database created 
    * and set the password of that user

### Connect to the MySQL Docker

Run the `docker exec` command in order to connect to the `mysql-docker-container` docker container:

```
docker exec -it mysql-docker-container bash
```

* The docker exec command runs a new command in an already running container.
* This command creates a new interactive bash shell in the `mysql-docker-container` through `-it` that tells `Docker`
  to allot a `pseudo-TTY` connected to the container’s `stdin`.
* In the bash shell:
  * enter the `mysql -u root -p` command to invoke `MySQL` with the `root` user
    * Next, you will enter the password which you had set when running the `docker run` command (i.e. `root` in our case).
  * or enter the `mysql -u crazy -p` command to invoke `MySQL` with the `crazy` user
    * Next, you will enter the password which you had set when running the `docker run` command (i.e. `crazy` in our case).
  * We can run any sql commands now, e.g., `show databases;`

That's it, we have up and running `MySQL` container happily :)

# Docker contains slow on macOS

Simplest solution given below but there are others like using NFS:

### Delegated
The delegated configuration provides the weakest set of guarantees. For directories mounted with delegated the 
container’s view of the file system is authoritative, and writes performed by containers may not be immediately 
reflected on the host file system. In situations such as NFS asynchronous mode, if a running container with a delegated 
bind mount crashes, then writes may be lost.

### Cached
The cached configuration provides all the guarantees of the delegated configuration, and some additional guarantees 
around the visibility of writes performed by containers. As such, cached typically improves the performance of 
read-heavy workloads, at the cost of some temporary inconsistency between the host and the container.

Ok, we can use them simply as volume option, for example:

```
my-delegated-volume:/var/volume2:delegated
my-cached-volume:/var/volume1:cached
```
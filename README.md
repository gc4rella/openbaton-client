# Openbaton Client

## Overview
Openbaton Client provides a command-line client, which enables you to access the project API through easy-to-use commands. 
For example, Openbaton Client provides the creation of a new Vim instance.

You can run the commands from the command line. 
Openbaton APIs are RESTful APIs, and use the HTTP protocol. 

They include methods, URIs, media types, and response codes.
Openbaton APIs are open-source Java clients, and can run on Linux or Mac OS X systems. 

## Install the Openbaton Client
Git clone the project. Navigate into the root directory of the project and execute *./gradlew build*.

### Set the required environment variables
Navigate into the project's root directory and execute *source nfvo.properties*. A dialog appears and will ask you for some properties. 

## Openbaton Client usage
    
    run a command: openbaton.sh command-name [arg-1] [arg-2] [arg-3]
    
    show the configuration: openbaton.sh -c
    
    activate debug mode: openbaton.sh -d command-name [arg-1] [arg-2] [arg-3]
    
    print help: openbaton.sh -h
    
    print help for a command: openbaton.sh command-name help
    
    list the available commands: openbaton.sh -l
    

### For help on a specific openbaton command, enter:
```sh
 $ openbaton.sh COMMAND help
```

### **Vim Instance Subcommands**
* **create**
  * Create the object of type Vim Instance

```sh
 $ openbaton.sh VimInstance-create file.json
```

* **delete**
  * Delete the object of type Vim Instance passing the id

```sh
 $ openbaton.sh VimInstance-delete id-vim-instance
```

* **update**
  * Update the object of type Vim nstance passing the new object and the id of the old object

```sh
 $ openbaton.sh VimInstance-update file.json id-vim-instance
```

* **findAll**
  * Find all the objects of type Vim Instance

```sh
 $ openbaton.sh VimInstance-findAll
```

* **findById**
  * Find the object of type Vim Instance through the id

```sh
 $ openbaton.sh VimInstance-findById id-vim-instance
```

### **Network Service Descriptor Subcommands**
* **create**
  * Create the object of type Network Service Descriptor

```sh
 $ openbaton.sh NetworkServiceDescriptor-create file.json
```

* **delete**
  * Delete the object of type Network Service Descriptor passing the id

```sh
 $ openbaton.sh NetworkServiceDescriptor-delete id-network-service-descriptor
```

* **findAll**
  * Find all the objects of type Network Service Descriptor

```sh
 $ openbaton.sh NetworkServiceDescriptor-findAll
```

* **findById**
  * Find the object of type Network Service Descriptor through the id

```sh
 $ openbaton.sh NetworkServiceDescriptor-findById id-network-service-descriptor
```

* **getVNFDependencies**                                  
  * Get all the Virtual Network Function Descriptor Dependency of a Network Service Descriptor with specific id

```sh
 $ openbaton.sh NetworkServiceDescriptor-getVNFDependencies id-network-service-descriptor
```

* **getVNFDependency**                                      
  * Get the VirtualNetwork Function Descriptor dependency with specific id of a Network Service Descriptor with specific id

```sh
 $ openbaton.sh NetworkServiceDescriptor-getVNFDependency id-network-service-descriptor id-vnfdependency
```

* **deleteVNFDependency**                                   
  * Delete the Virtual Network Function Descriptor dependency of a Network Service Descriptor with specific id
  ```sh
   $ openbaton.sh NetworkServiceDescriptor-deleteVNFDependency id-network-service-descriptor id-vnfdependency
  ```
  
* **getVirtualNetworkFunctionDescriptors**
  * Find all the objects of type VirtualNetworkFunctionDescriptor
    ```sh
     $ openbaton.sh NetworkServiceDescriptor-getVirtualNetworkFunctionDescriptors id-network-service-descriptor
    ```
* **getVirtualNetworkFunctionDescriptor**
  * Find the object of type VirtualNetworkFunctionDescriptor through the id
      ```sh
       $ openbaton.sh NetworkServiceDescriptor-getVirtualNetworkFunctionDescriptor id-network-service-descriptor id-vnfd
      ```
      
### **Virtual Network Function Descriptor Subcommands**
* **create**
  * Create the object of type Virtual Network Function Descriptor

```sh
 $ openbaton.sh VirtualNetworkFunctionDescriptor-create file.json
```

* **delete**
  * Delete the object of type Virtual Network Function Descriptor passing the id

```sh
 $ openbaton.sh VirtualNetworkFunctionDescriptor-delete id-virtual-network-function-descriptor
```

* **findAll**
  * Find all the objects of type Virtual Network Function Descriptor

```sh
 $ openbaton.sh VirtualNetworkFunctionDescriptor-findAll
```

* **findById**
  * Find the object of type Virtual Network Function Descriptor through the id

```sh
 $ openbaton.sh VirtualNetworkFunctionDescriptor-findById id-virtual-network-function-descriptor
```

### **Network Service Record Subcommands**
* **create**
  * Create the object of type Network Service Record from a file

```sh
 $ openbaton.sh NetworkServiceRecord-create file.json
```

* **create**
  * Create the object of type Network Service Record from a Network Service Descriptor stored in the orchestrator

```sh
 $ openbaton.sh NetworkServiceRecord-create id-network-service-descriptor
```

* **delete**
  * Delete the object of type Network Service Record passing the id

```sh
 $ openbaton.sh NetworkServiceRecord-delete id-network-service-record
```

* **update**
  * Update the Network Service Record
  
  ```sh
   $ openbaton.sh NetworkServiceRecord-update file.json id-network-service-record 
  ```

* **findAll**
  * Find all the objects of type Network Service Record

```sh
 $ openbaton.sh NetworkServiceRecord-findAll 
```

* **findById**
  * Find the object of type Network Service Record through the id

```sh
 $ openbaton.sh NetworkServiceRecord-findById id-network-service-record
```

* **getVirtualNetworkFunctionRecords**                         
  * Get all the Virtual Network Function Records of Network Service Record with specific id
  ```sh
   $ openbaton.sh NetworkServiceRecord-getVirtualNetworkFunctionRecords id-network-service-record
  ```
* **getVirtualNetworkFunctionRecord**                           
  * Get the Virtual Network Function Record with specific id of Network Service Record with specific id
    ```sh
     $ openbaton.sh NetworkServiceRecord-getVirtualNetworkFunctionRecord id-network-service-record id-vnfr
    ```
* **deleteVirtualNetworkFunctionRecord**                        
  * Delete the Virtual Network Function Record of NetworkS ervice Record with specific id
      ```sh
       $ openbaton.sh NetworkServiceRecord-deleteVirtualNetworkFunctionRecord id-network-service-record id-vnfr
      ```
* **deleteVNFDependency**                                      
  * Delete the Virtual Network Function Record Dependency of a Network Service Record with specific id
  ```sh
   $ openbaton.sh NetworkServiceRecord-deleteVNFDependency id-network-service-record id-vnfdependency
  ```
  
*  **createVNFCInstance**                                      
  
    * Add a VNFCInstance to a VNF by performing a SCALE_OUT operation on the VNF
  ```sh
  $ openbaton.sh NetworkServiceRecord-createVNFCInstance id-network-service-record id-virtual-network-function-record file.json
  ```


### **Event Subcommands**
* **create**
  * Create the object of type Event
        ```sh
         $ openbaton.sh Event-create file.json
        ```
* **delete**
  * Delete the object of type Event passing the id
          ```sh
           $ openbaton.sh Event-delete id-event
          ```
* **findAll**
  * Find all the objects of type Event
          ```sh
           $ openbaton.sh Event-findAll
          ```
* **findById**
  * Find the object of type Event through the id
          ```sh
           $ openbaton.sh Event-findById id-event
          ```

### **Configuration Subcommands**
* **create**
  * Create the object of type Configuration
      ```sh
       $ openbaton.sh Configuration-create file.json
      ```
* **delete**
  * Delete the object of type Configuration passing the id
    ```sh
     $ openbaton.sh Configuration-delete id-configuration
    ```
* **findAll**
  * Find all the objects of type Configuration
        ```sh
         $ openbaton.sh Configuration-findAll
        ```
* **findById**
  * Find the object of type Configuration through the id
    ```sh
     $ openbaton.sh Configuration-findById id-configuration
    ```

### **Image Subcommands**
* **create**
  * Create the object of type Image
    ```sh
     $ openbaton.sh Image-create file.json
    ```
* **delete**
  * Delete the object of type Image passing the id
      ```sh
       $ openbaton.sh Image-delete id-image
      ```
* **findAll**
  * Find all the objects of type Image
    ```sh
     $ openbaton.sh Image-findAll
    ```
* **findById**
  * Find the object of type Image through the id
    ```sh
     $ openbaton.sh Image-findById id-image 
    ```

### **VirtualLink Subcommands**
* **create**
  * Create the object of type VirtualLink
  ```sh
   $ openbaton.sh VirtualLink-create file.json 
  ```
* **delete**
  * Delete the object of type VirtualLink passing the id
    ```sh
     $ openbaton.sh VirtualLink-delete id-virtual-link 
    ```
* **update**
  * Update the object of type VirtualLink passing the new object and the id of the old object
    ```sh
     $ openbaton.sh VirtualLink-update file.json id-virtual-link
    ```
* **findAll**
  * Find all the objects of type VirtualLink
    ```sh
     $ openbaton.sh VirtualLink-findAll
    ```
* **findById**
  * Find the object of type VirtualLink through the id
    ```sh
     $ openbaton.sh VirtualLink-findById id-virtual-link
    ```
    
### **VNFPackage Subcommands**
* **create**
  * Create a VNFPackage by uploading a tar file to the NFVO
  ```sh
   $ openbaton.sh VNFPackage-upload file.tar 
  ```
* **delete**
  * Delete the object of type VNFPackage passing the id
    ```sh
     $ openbaton.sh VNFPackage-delete id-vnfPackage 
    ```
* **update**
  * Update the object of type VNFPackage passing the new object and the id of the old object
    ```sh
     $ openbaton.sh VNFPackage-update file.json id-vnfPackage
    ```
* **findAll**
  * Find all the objects of type VNFPackage
    ```sh
     $ openbaton.sh VNFPackage-findAll
    ```
* **findById**
  * Find the object of type VNFPackage through the id
    ```sh
     $ openbaton.sh VNFPackage-findById id-vnfPackage
    ```
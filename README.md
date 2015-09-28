# Karaf-Microservices

This is the demo project for the 'Microservices and OSGi' talk held at Apache Con EU 2015 and JavaLand 2015. 

It consists of following sub-modules:

* service-api: 
	Module containing the Service API.
* service-impl:
	Implementation of the API bundle. 
* service-configuration: 
	A configuration bundle for easy service configuration in this showcase.
* service-shell-command:
	Karaf shell commands for easy service testing.
* service-proxy: 
    A cxf jaxrs service proxy on top of the service API. 
* service-feature: 
	A Karaf feature module for easy installation of all above bundles.
* service-test: 
	Integration test module testing OSGi Service with Pax-Exam.
	
## Prerequesites

First of all make sure you also have the [Karaf-Microservices-Tooling](https://github.com/ANierbeck/Karaf-Microservices-Tooling) project build. The service-test module does use a specialized pre-configured Karaf instance, Karaf-Service-Runtime.

## Building

All of the Microservice samples are can be build with maven. 

    mvn clean install
    
while for Dev-Ops development with continous deployment a special deployment goal exists. 

    karaf-deployer:deploy
    
With this maven goal you can deploy any module on a Karaf instance (if it's a OSGi bundle)
These samples already does have a the usage of this goal preconfigured. 

    <plugin>
		<groupId>de.nierbeck.javaland.tools</groupId>
		<artifactId>karaf-deployer-maven-plugin</artifactId>
		<configuration>
			<url>http://192.168.59.103:8181/jolokia/</url>
			<jsonInstall>
	{
	"type":"EXEC",
	"mbean":"org.apache.karaf:type=bundle,name=root",
	"operation":"install(java.lang.String,boolean)",
	"arguments":["mvn:${project.groupId}/${project.artifactId}/${project.version}", true]
	}
			</jsonInstall>
			<user>karaf</user>
			<password>karaf</password>
			<skip>false</skip>
		</configuration>
	</plugin> 

For easy testing the pre-configured  Karaf instance can also be run as Docker image right away from the project. 
For this call 

	docker:start
	
to start the docker instance and 
   
    docker:stop
    
to stop that instance again. 

## Walkthrough

This is a short walkthrough of this sample.
 
First make sure you have docker installed and running. 
Clone the Karaf-Microservices-Tooling from the github repo and do a full build of it. 
In the Karaf-Docker-Runtime submodule start the Karaf-Docker instance with 

    mvn docker:start
    
after this docker container is started it can be used as continuous deployment container for the showcase. 
The showcase can be build with _mvn clean install_. 
To deploy the showcase bundle to the Karaf-Docker instance use: 

    mvn karaf-deployer:deploy
    
This will deploy the current bundle in the configured Karaf instance. 
In case of deploying to the Karaf-Docker instance make sure the ip address is properly configured in the 
*karaf-deployer-maven-plugin* section.

### complete showcase

For easy testing there is an additional service-configuration bundle, which is enabled by enabling the _Configuration_ profile. 
The complete showcase including the configuration can be build with 

    mvn clean install karaf-deployer:deploy -PConfiguration
    
when ssh'ing into the Karaf instance 

    ssh karaf@192.168.99.100 -p 8101
    
you'll see all bundles are installed and usable. 
For example the commands contained in the service-shell-command bundle can be used to test the service. 

    karaf@root()> microservice:Calculate
    
### proxy REST servlet

The proxy REST servlet is a specialized JAX-RS servlet to proxy calls to any service instance of the CreditCalculator type. Those services are configured via _de.nierbeck.microservices.karaf.calculator_ Service Factory PID. To get a list of all available service instances a GET request to 

    http://192.168.99.100:8181/cxf/calculator/
    
lists all available services of type CreditCalculator.

With a GET call to:  

	http://192.168.99.100:8181/cxf/calculator/find
	
a sample Calculator JSON object is returned. This JSON can be used for the calculation. 

To calculate with a dedicated service instance a POST request to the following URL is required.  

    http://192.168.99.100:8181/cxf/calculator/JavaBank
    
For a successful call make sure you have _Content-Type_ set to _application/json_ in the request header. 
The body to send via POST can be retrieved via the find call.

    {
	    "credit": {
	        "credit": 10000,
	        "interest": 2.5,
	        "retention": 10
	    }
	}
	

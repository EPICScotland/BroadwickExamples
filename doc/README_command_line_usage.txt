
-----------------------------------------
Test 0 - Type this into the command line:
-----------------------------------------

java -jar BroadwickExamples-1.0-SNAPSHOT.one-jar.jar -c Broadwick_with_DummyModel.xml


Expected results:
lots of:
[JarClassLoader] INFO: findResources..

[main] INFO  Running broadwick Version 1.1 Build (SJLDELL - unknown : 2014-06-14 11:49)  
[main] INFO  Running broadwick for the following models [Broadwick Project] 
[pool-1-thread-1] INFO  Running Broadwick Project [epic.broadwickexamples.DummyModel] 
[pool-1-thread-1] INFO  Initialise Dummy Model 
[pool-1-thread-1] INFO  Run Dummy Model 
[pool-1-thread-1] INFO  stringParam=ABCDEFG 
[pool-1-thread-1] INFO  intParam=1 
[pool-1-thread-1] INFO  doubleParam=2.0 
[pool-1-thread-1] INFO  Finalise Dummy Model 
[pool-1-thread-1] INFO  END 
[main] INFO  Simulation complete. 0:00:00.056 


-----------------------------------------
Test 1 - Type this into the command line:
-----------------------------------------

java -jar BroadwickExamples-1.0-SNAPSHOT.one-jar.jar -c Broadwick_with_BasicSIRModel.xml


Expected results:
lots of:
[JarClassLoader] INFO: findResources..

then the following:
[main] INFO  Running broadwick Version 1.1 Build (SJLDELL - unknown : 2014-06-14 11:49)  
[main] INFO  Running broadwick for the following models [Broadwick Project] 
[pool-1-thread-1] INFO  Running Broadwick Project [epic.basic.BasicSIRModel] 
[pool-1-thread-1] INFO  BasicSIRModel - init 
[pool-1-thread-1] INFO  seed	= 12345 
[pool-1-thread-1] INFO  maxTime	= 1000000.0 
[pool-1-thread-1] INFO  tauStep	= 0 
[pool-1-thread-1] INFO  N	= 1000 
[pool-1-thread-1] INFO  initI	= 1 
[pool-1-thread-1] INFO  beta	= 0.1 
[pool-1-thread-1] INFO  gamma	= 0.05 
[pool-1-thread-1] INFO  BasicSIRModel - run 
[pool-1-thread-1] INFO  BasicSIRModel - final simulation time = 275.3055756207629 
[main] INFO  Simulation complete. 0:00:00.246 

and two files will be created:
epic.basic.BasicSIRModel.log
basicsir_test.txt



-----------------------------------------
Test 2 - Type this into the command line:
-----------------------------------------

java -jar BroadwickExamples-1.0-SNAPSHOT.one-jar.jar -c Broadwick_with_IndividualSIRModel.xml


Expected results:
lots of:
[JarClassLoader] INFO: findResources..

then the following:
[main] INFO  Running broadwick Version 1.1 Build (SJLDELL - unknown : 2014-06-14 11:49)  
[main] INFO  Running broadwick for the following models [Broadwick Project] 
[pool-1-thread-1] INFO  Running Broadwick Project [epic.sir.IndividualSIRModel] 
[pool-1-thread-1] INFO  IndividualSIRModel - init 
[pool-1-thread-1] ERROR Could not find parameter susceptibility in configuration file. 
[pool-1-thread-1] INFO  Optional parameter susceptibility (=wanning immunity) is not set, but this is OK 
[pool-1-thread-1] INFO  seed	= 12347 
[pool-1-thread-1] INFO  maxTime	= 1000000.0 
[pool-1-thread-1] INFO  tauStep	= 0 
[pool-1-thread-1] INFO  N	= 1000 
[pool-1-thread-1] INFO  initI	= 1 
[pool-1-thread-1] INFO  IndividualSIRModel - run 
[pool-1-thread-1] INFO  IndividualSIRModel - final simulation time = 319.6001125147772 
[pool-1-thread-1] INFO  IndividualSIRModel - SUSCEPTIBLE:249	INFECTED:0	RECOVERED:751 
[main] INFO  Simulation complete. 0:00:00.356 

and four files will be created:
epic.sir.IndividualSIRModel.log
individualsir_test_modelState.txt
individualsir_test_transmissions.txt
individualsir_test_allEvents.txt



-----------------------------------------
Test 3 - Type this into the command line:
-----------------------------------------

(note that example_UK_cities.txt and example_UK_cities_links.txt must be in the same directory as the jar file)

java -jar BroadwickExamples-1.0-SNAPSHOT.one-jar.jar -c Broadwick_with_IndividualNetworkModel.xml


Expected results:
lots of:
[JarClassLoader] INFO: findResources..

then the following:
[main] INFO  Running broadwick Version 1.1 Build (SJLDELL - unknown : 2014-06-14 11:49)  
[main] INFO  Running broadwick for the following models [Broadwick Project] 
[pool-1-thread-1] INFO  Running Broadwick Project [epic.network.IndividualNetworkModel] 
[pool-1-thread-1] INFO  IndividualNetworkModel - init 
[pool-1-thread-1] ERROR Could not find parameter susceptibility in configuration file. 
[pool-1-thread-1] INFO  Optional parameter susceptibility (=wanning immunity) is not set, but this is OK 
[pool-1-thread-1] INFO  Network Model locationsFile = example_UK_cities.csv 
[pool-1-thread-1] INFO  Network Model linksFile = example_UK_cities_links.csv 
[pool-1-thread-1] INFO  Network Model locationType = LATLONG 
[pool-1-thread-1] INFO  170 locations read from file 
[pool-1-thread-1] INFO  181 links read from file 
[pool-1-thread-1] INFO  Initialising infection from CN000111,Stoke-on-Trent,53.0,-2.13 
[pool-1-thread-1] INFO  Number of susceptibles in network = 106 
[pool-1-thread-1] INFO  Number of infecteds in network = 1 
[pool-1-thread-1] INFO  seed	= 12349 
[pool-1-thread-1] INFO  maxTime	= 1000000.0 
[pool-1-thread-1] INFO  tauStep	= 0 
[pool-1-thread-1] INFO  N	= 107 
[pool-1-thread-1] INFO  initI	= 1 
[pool-1-thread-1] INFO  IndividualNetworkModel - run 
[pool-1-thread-1] INFO  IndividualNetworkModel - final simulation time = 152.54159155869897 
[pool-1-thread-1] INFO  IndividualNetworkModel - SUSCEPTIBLE:3	EXPOSED:0	INFECTED:0	RECOVERED:104 
[main] INFO  Simulation complete. 0:00:00.710 

and many files will be created:
epic.sir.IndividualNetworkModel.log
individualnetwork_test_modelState.txt
individualnetwork_test_transmissions.txt
individualnetwork_test_allEvents.txt
individualnetwork_test_locations.txt
individualnetwork_test_initialNetwork.net
individualnetwork_test_individualStates_initial.txt
individualnetwork_test_individualStates_final.txt
individualnetwork_test_individualStates_00000[numbers].txt



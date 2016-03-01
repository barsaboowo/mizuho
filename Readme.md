# Mizuho Middleware Candidate Take Home Project #

As a solution to this project I have created a Spring Boot REST based service.  The architecture is designed around the MVC paradigm, with a PriceService at the centre of operations which itself takes a PriceDao object as a data store.  

The implemented data store is simply a hash map.  However it should be easy to write another data store such as a database or in memory data grid to the same interface. 

For the price POJO, I had to implement the equals() and hashcode() methods in terms of their primary key {vendorId, instrumentId}.  This is because the price service implementation maintains two sets of prices internally by instrument id and by vendor id, by way of caching.  Therefore the hashing implementation must consider the prices unique according to their primary key.

A timer task runs once per minute in order to delete values more than 30 days old.  Since the price service is accessible simultaneously by multiple threads and potentially by multiple simultaneous REST API calls and incoming JMS messages, its internal caches and data stores are guarded by a Reentrant Read-Write Lock.


##Mocking / Demo##
The project is maven based, and can be run from maven using the command: 

**mvn spring-boot:run**

This will start a REST server on localhost:8080 and also fire up an in-memory ActiveMQ broker.  

I have created a mock price feed which will be instantiated on startup, and will create two prices per second: one for Reuters and one for Bloomberg, using a list of 5 test ISINs which can be found in com.sebarber.mizuho.utils.Constants.


## Price Creation ##

Prices can be created using either JMS JSON messages or REST api calls. 

**a**.  A camel endpoint has been configured to listen on the following topics:

1. **com.pricefeed.bloomberg**
2. **com.pricefeed.reuters**

Given an object message with a JSON body in the following format, a price will be parsed and created:

{"pricePk":{"instrumentId":"DE000JPM85H5","vendorId":"Bloomberg"},"idType":"ISIN","instrumentType":"Government Bond","priceType":"Clean Price","created":1456775824708,"bid":93.40,"ask":45.75,"vendorId":"Bloomberg","instrumentId":"DE000JPM85H5","active":true}

If the price cannot be parsed or something else is wrong, the message will be delivered to a dead letter queue **activemq:queue:dead**

**b**.  Using the REST service endpoint **/prices/create**, a POST operation with a JSON string payload as follows can be used to create a set of prices:

[{"pricePk":{"instrumentId":"DE000JPM85H5","vendorId":"Reuters"},"idType":"ISIN","instrumentType":"Government Bond","priceType":"Clean Price","created":1456775828719,"bid":80.63,"ask":66.27,"vendorId":"Reuters","instrumentId":"DE000JPM85H5","active":true},{"pricePk":{"instrumentId":"DE000JPM85H5","vendorId":"Bloomberg"},"idType":"ISIN","instrumentType":"Government Bond","priceType":"Clean Price","created":1456775824708,"bid":93.40,"ask":45.75,"vendorId":"Bloomberg","instrumentId":"DE000JPM85H5","active":true}]

##Price Validation##

A simple price validator makes sure that incoming prices are valid in terms of their primary key and in terms of their creation date.

##Price Retrieval##

Prices can be retrieved via the REST api as follows:

1. **/prices/instrument/DE000JPM85H5/list**
2. **/prices/vendor/Bloomberg/list**

A call to the endpoint in 1 via a GET operation will return a JSON string representing all the prices held for the instrument with the instrument Id DE000JPM85H5.

A call to the endpoint in 2 via a GET operation will return a JSON string representing all the prices held for the vendor with id Bloomberg.

A downstream subscriber can also subscribe to a realtime JMS feed of all prices on the following topic: **com.pricefeed.internal** and will receive a realtime stream of PriceImpl POJOs as they are received by the Price Service.  

When the system starts up, all prices from the store are sent to the topic.  When a price becomes deleted because it is more than 30 days old, a PriceImpl POJO will be sent with the isActive flag set to false.

##Limitations / Enhancements##
1.  If the volume of prices per vendor or the number of vendors became very high, calls to the REST API could time-out.
2.  If nothing is listening to the internal price JMS feed, the topic may become full and cause the broker to crash.
3.  Currently only a single price format is supported for the incoming JSON feeds.  It is likely that the incoming formats will differ by vendor.
4.  Currently the only creation methods are via REST POST and via JMS.  It is likely that vendors will publish prices as files which would then need to be retrieved over FTP.  However it is relatively easy to implement file based endpoints in camel.
5.  Internal subscribers may wish to receive the prices in other formats, for example via a daily file dump.  It would be relatively easy to create timer tasks to perform this using camel.
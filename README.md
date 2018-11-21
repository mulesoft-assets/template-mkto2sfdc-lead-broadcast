
# Anypoint Template: Marketo to Salesforce Lead Broadcast	

<!-- Header (start) -->

<!-- Header (end) -->

# License Agreement
This template is subject to the conditions of the <a href="https://s3.amazonaws.com/templates-examples/AnypointTemplateLicense.pdf">MuleSoft License Agreement</a>. Review the terms of the license before downloading and using this template. You can use this template for free with the Mule Enterprise Edition, CloudHub, or as a trial in Anypoint Studio. 
# Use Case
<!-- Use Case (start) -->
As a Marketo admin I want to synchronize Leads between Marketo and Salesforce orgs.

This Anypoint Template should serve as a foundation for setting an online sync of Leads from Marketo instance to another Salesforce instances. Every time there is a new Lead or a change in an already existing one, the integration will poll for changes in Marketo source instance and it will be responsible for updating the Lead on the target orgs.

Requirements have been set not only to be used as examples, but also to establish a starting point to adapt your integration to your requirements.

As implemented, this Anypoint Template leverages the [Batch Module](http://www.mulesoft.org/documentation/display/current/Batch+Processing).
The batch job is divided in *Process* and *On Complete* stages.

The integration is triggered by a scheduler defined in the endpoints.xml file. The application queries/receives newest Marketo updates/creations and adds them to one of the JMS topics depending on the country for the Lead record.

The application has two different batch jobs consuming this JMS topics, one for migrating the changes to the first Salesforce Org (Leads located in 'US') and the other one for migrating the changes to the other Salesforce Org (Leads located in other countries as 'US').

During the *Process* stage, each Salesforce Lead will be matched with an existing Lead in the target system by Email.
The last step of the *Process* stage will group the Leads and create/update them in Salesforce Org.
Finally during the *On Complete* stage the Anypoint Template will log output statistics data into the console.
<!-- Use Case (end) -->

# Considerations
<!-- Default Considerations (start) -->

<!-- Default Considerations (end) -->

<!-- Considerations (start) -->
To make this Anypoint Template run, there are certain preconditions that must be considered. All of them deal with the preparations in both source and destination systems, that must be made in order for all to run smoothly. **Failing to do so could lead to unexpected behavior of the template.**
<!-- Considerations (end) -->



## Salesforce Considerations

Here's what you need to know about Salesforce to get this template to work:

- Where can I check that the field configuration for my Salesforce instance is the right one? See: <a href="https://help.salesforce.com/HTViewHelpDoc?id=checking_field_accessibility_for_a_particular_field.htm&language=en_US">Salesforce: Checking Field Accessibility for a Particular Field</a>.
- Can I modify the Field Access Settings? How? See: <a href="https://help.salesforce.com/HTViewHelpDoc?id=modifying_field_access_settings.htm&language=en_US">Salesforce: Modifying Field Access Settings</a>.


### As a Data Destination

There are no considerations with using Salesforce as a data destination.








## Marketo Considerations

### As a Data Source

There are no considerations with using Marketo as a data origin.

# Run it!
Simple steps to get this template running.
<!-- Run it (start) -->

<!-- Run it (end) -->

## Running On Premises
In this section we help you run this template on your computer.
<!-- Running on premise (start) -->

<!-- Running on premise (end) -->

### Where to Download Anypoint Studio and the Mule Runtime
If you are new to Mule, download this software:

+ [Download Anypoint Studio](https://www.mulesoft.com/platform/studio)
+ [Download Mule runtime](https://www.mulesoft.com/lp/dl/mule-esb-enterprise)

**Note:** Anypoint Studio requires JDK 8.
<!-- Where to download (start) -->

<!-- Where to download (end) -->

### Importing a Template into Studio
In Studio, click the Exchange X icon in the upper left of the taskbar, log in with your Anypoint Platform credentials, search for the template, and click Open.
<!-- Importing into Studio (start) -->

<!-- Importing into Studio (end) -->

### Running on Studio
After you import your template into Anypoint Studio, follow these steps to run it:

+ Locate the properties file `mule.dev.properties`, in src/main/resources.
+ Complete all the properties required as per the examples in the "Properties to Configure" section.
+ Right click the template project folder.
+ Hover your mouse over `Run as`.
+ Click `Mule Application (configure)`.
+ Inside the dialog, select Environment and set the variable `mule.env` to the value `dev`.
+ Click `Run`.
<!-- Running on Studio (start) -->

<!-- Running on Studio (end) -->

### Running on Mule Standalone
Update the properties in one of the property files, for example in mule.prod.properties, and run your app with a corresponding environment variable. In this example, use `mule.env=prod`. 


## Running on CloudHub
When creating your application in CloudHub, go to Runtime Manager > Manage Application > Properties to set the environment variables listed in "Properties to Configure" as well as the mule.env value.
<!-- Running on Cloudhub (start) -->

<!-- Running on Cloudhub (end) -->

### Deploying a Template in CloudHub
In Studio, right click your project name in Package Explorer and select Anypoint Platform > Deploy on CloudHub.
<!-- Deploying on Cloudhub (start) -->

<!-- Deploying on Cloudhub (end) -->

## Properties to Configure
To use this template, configure properties such as credentials, configurations, etc.) in the properties file or in CloudHub from Runtime Manager > Manage Application > Properties. The sections that follow list example values.
### Application Configuration
<!-- Application Configuration (start) -->
+ scheduler.frequency `60000`
+ scheduler.start.delay `0`
+ watermark.default.expression `2018-05-13T03:00:59Z`
+ page.size `200`

**Salesforce Connector configuration for company A**
+ sfdc.a.username `bob.dylan@orga`
+ sfdc.a.password `DylanPassword123`
+ sfdc.a.securityToken `avsfwCUl7apQs56Xq2AKi3X`

**Salesforce Connector configuration for company B**
+ sfdc.b.username `joan.baez@orgb`
+ sfdc.b.password `JoanBaez456`
+ sfdc.b.securityToken `ces56arl7apQs56XTddf34X`

**Marketo Connector configuration**
+ mkto.clientId `clientReachPointId`
+ mkto.clientSecret `clientReachPointSecret`
+ mkto.basePath `/`
+ mkto.host `marketo.mktorest.com`
+ mkto.accessTokenUrl `https://marketo.mktorest.com/identity/oauth/token`

**JMS Connector configuration**
+ jms.username `admin`
+ jms.password `admin`
+ jms.brokerUrl `tcp://localhost:61616`
<!-- Application Configuration (end) -->

# API Calls
<!-- API Calls (start) -->
Salesforce imposes limits on the number of API Calls that can be made. Therefore calculating this amount may be an important factor to consider. The Anypoint template calls to the API can be calculated using the formula:

***1 + X + X / ${page.size}***

Being ***X*** the number of Leads to be synchronized on each run. 

The division by ***${page.size}*** is because, by default, Leads are gathered in groups of ${page.size} for each Upsert API Call in the aggregation step. Also consider that these calls are executed repeatedly every polling cycle.	

For instance if 10 records are fetched from origin instance, then 12 api calls will be made (1 + 10 + 1).
<!-- API Calls (end) -->

# Customize It!
This brief guide provides a high level understanding of how this template is built and how you can change it according to your needs. As Mule applications are based on XML files, this page describes the XML files used with this template. More files are available such as test classes and Mule application files, but to keep it simple, we focus on these XML files:

* config.xml
* businessLogic.xml
* endpoints.xml
* errorHandling.xml<!-- Customize it (start) -->

<!-- Customize it (end) -->

## config.xml
<!-- Default Config XML (start) -->
This file provides the configuration for connectors and configuration properties. Only change this file to make core changes to the connector processing logic. Otherwise, all parameters that can be modified should instead be in a properties file, which is the recommended place to make changes.<!-- Default Config XML (end) -->

<!-- Config XML (start) -->

<!-- Config XML (end) -->

## businessLogic.xml
<!-- Default Business Logic XML (start) -->
Functional aspect of the Anypoint Template is implemented in this XML, directed by mainFlow, which deduplicates Leads by Id, create two separate collections depending on the Lead's country field and send them through two different topics for further processing.
Flows processAQueueLeadsToBatchFlow and processBQueueLeadsToBatchFlow get data from specific JMS topic and start executing specific batch.
There are two batches. Both have same logic, but the first is upserting Leads into Salesforce instance A and the other one - into Salesforce instance B. 

The logic of the batches is:

1. During the *Process* stage, each Salesforce Lead will be matched with an existing Lead in the target system by Email.
2. The last step of the *Process* stage will group the Leads and create/update them in particular Salesforce Org.
3. Finally during the *On Complete* stage the batches will log output statistics data into the console.<!-- Default Business Logic XML (end) -->

<!-- Business Logic XML (start) -->

<!-- Business Logic XML (end) -->

## endpoints.xml
<!-- Default Endpoints XML (start) -->
This is file is conformed by a Flow containing the Scheduler that will periodically query Marketo for updated/created fields - firstName,lastName,email,company,country of Lead objects and then executing the mainFlow implemented in businessLogic.xml.<!-- Default Endpoints XML (end) -->

<!-- Endpoints XML (start) -->

<!-- Endpoints XML (end) -->

## errorHandling.xml
<!-- Default Error Handling XML (start) -->
This file handles how your integration reacts depending on the different exceptions. This file provides error handling that is referenced by the main flow in the business logic.<!-- Default Error Handling XML (end) -->

<!-- Error Handling XML (start) -->

<!-- Error Handling XML (end) -->

<!-- Extras (start) -->

<!-- Extras (end) -->

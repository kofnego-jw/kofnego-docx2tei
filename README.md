# Docx 2 TEI Web application

Written by Joseph Wang on behalf of the [Commission for Modern Austrian History](http://www.oesterreichische-geschichte.at).

## Introduction
This web application should enable people converting DOCX files to XML/TEI files. It has the following features:

### Backend
* A simple webserver, hooked to a certain port
* Conversion routines, while to a certain extend, a conversion can be customized. Exposed as a web service.

### Frontend
* A HTML5 client for the webservice

## How does this application work?
The basic idea is simple: The conversion is implemented as a web service:
A POST request, uploading a DOCX file and sending a list of options will result in webservice returning a byte array containing the result of the conversion. (Or a JSON result saying that something went wrong.)

The frontend does two things: Rendering the UI and managing the server interactions. There are only two server interactions:
1. Loading a list of options.
2. Calling the conversion routine.

The result of the conversion can either be viewed in a new window or downloaded in the download folder.


## Installation
You can either build the application from the source, or you can get a binary copy from joseph.wang@uibk.ac.at. Please note that right now, binaries are not to be copied.

### Requirements
* Java 8 SE
* Maven
* Internet connection
* A modern browser, capable of using HTML5 Files API, especially BLOB. According to http://caniuse.com/#feat=blobbuilder these are:
... Firefox from version 13
... Chrome from version 20
... Internet Explorer from version 10
... Safari from version 6
... Opera from version 12.1
* The conversion routines are based upon the Stylesheets of the TEI Consortium
* The following JAVA libraries are used:
... Spring Boot (Spring Framework and Spring MVC)
... Saxon XSLT Processor
... BndTools (actually not used)
... Jackson JSON Mapper
... Apache Commons IO
... Apache Tomcat (Embedded)
... slf4j
... XStream XML Mapper
* The following Javascript and CSS Libraries are used
... Bootstrap
... JQuery
... Angular JS
... FileSaver.js
... Blob.js

### Building Spring Boot Application
Just issue a ```mvn install``` and it will create a jar file called
```kofnego-tei2docx-0.0.1-SNAPSHOT.jar```.

### Running the application
Start the program with
```java -jar kofnego-tei2docx-0.0.1-SNAPSHOT.jar```
And open your browser to
[http://localhost:1340/](http://localhost:1340)
And you should see the application running.


## Configuration and Adaptation

### Change Server Port
If you want the server to use another port, change the
```server.port=1340```
in the file "src/main/resources/application.properties".

### Add other configurations
If you want to add other post processing steps, you must pack your stylesheets into the file "src/main/resources/docx2tei_add.zip". You could use a custom directory.

Say, your xslt file is called "myXslt.xsl". And you have added the file to docx2tei_add.zip/myxslt/ directory.

Then you must add a configuration option to "src/main/resources/programSetup.xml". Add

```
<ConversionOption>
  <name>MyXsltName</name>
  <description>A nice description of what this xsl is doing.</description>
  <xsltStylesheets>
    <string>myxslt/myXslt.xsl</string>
  </xsltStylesheets>
</ConversionOption>
```

to the &lt;conversionOptionSet&gt;. And after a rebuild and restart, the option should be available for choosing.

If you need multiple XSLT stylesheets to be processed (say "myXslt1.xsl" and "myXslt2.xsl"), you can change the &lt;stylesheets&gt; to
```
[...]
  <xsltStylesheets>
    <string>myxslt/myXslt1.xsl</string>
    <string>myxslt/myXslt2.xsl</string>
  </xsltStylesheets>
[...]
```

Then, by selecting this option, the XSLT processor will process myXslt1.xsl first and then myXslt2.xslt.

## License Information
The source code is dual licensed under Creative Commons Attribution 4.0 International [CC by 4.0](http://creativecommons.org/licenses/by/4.0/) and under the [BSD 2-Clause License](http://opensource.org/licenses/bsd-license.php).
In other words: You can do whatever you like with the source codes.

Since maintain runnable binaries will require me to rethink the licensing model, right now, there is no plan of distributing binaries. If you do not want to build the application yourself, please send a request to joseph.wang@uibk.ac.at.

JSON
====

Generate and parse JSON (JavaScript Object Notation) strings.  Refer to http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-404.pdf for a detailed description.

This implementation supports two structures:        
  - JSONArray is an extension of a Java List    
  - JSONObject is an extension of a Java Map where the key is a JSON field name and the value is a JSON field value     

An JSONArray element or a JSONObject field value can be any of the following:       
  - Null    
  - Boolean     
  - Number (Double, Float, Long, Integer, Short, Byte)      
  - String      
  - List        
  - Map     

The parser creates the following objects:       
  - Null if the value is 'null'
  - Boolean if the value is 'true' or 'false'     
  - Long if the value is numeric      
  - String if the value is a string (value is enclosed in double quotes)      
  - List if the value is an array (value is enclosed in square brackets)     
  - Map if the value is an object (value is enclosed in curly braces)        

The List and Map created by the parser is determined by the JSONFactory supplied when the parser is initialized.  By default, the parser will create a JSONArray for an array and a JSONObject for an object.


Build
=====

I use the Netbeans IDE but any build environment with Maven and the Java compiler available should work.  The documentation is generated from the source code using javadoc.

Here are the steps for a manual build.  You will need to install Maven 3 and Java SE Development Kit 8 if you don't already have them.

  - Create the executable: mvn clean install
  - [Optional] Create the documentation: mvn javadoc:javadoc

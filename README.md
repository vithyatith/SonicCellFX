# SonicCellFX

1. Download JavaFX first and put in a directory. For me I placed it in " D:\javafx\javafx-sdk-18.0.1\lib"
   
   https://gluonhq.com/products/javafx/


2. Quick compile.
Directory of E:\github\public\SonicCellFX\soniccellfx

06/12/2022  11:23 PM    <DIR>          .
06/12/2022  10:36 PM    <DIR>          ..
05/24/2022  12:39 AM             1,266 nbactions.xml
06/12/2022  11:15 AM             7,564 pom.xml
06/12/2022  10:36 PM    <DIR>          src
06/12/2022  10:35 PM             9,395 untitled.cell
               3 File(s)         18,225 bytes
               3 Dir(s)  294,074,400,768 bytes free

E:\github\public\SonicCellFX>mvn clean install

3. Run code
E:\github\public\SonicCellFX\soniccellfx>cd target

E:\github\public\SonicCellFX\soniccellfx\target>java --module-path D:\javafx\javafx-sdk-18.0.1\lib --add-modules javafx.controls -jar soniccellfx.jar

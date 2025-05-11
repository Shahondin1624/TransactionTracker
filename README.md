To speed up testing the application, create the file "debug-data" in composeApp/src/commonMain/composeResources/files:

```
inputFilePath=*.CSV
outputFilePath=*.xlsx
firstName=*
lastName=*
startDate=dd.MM.yyyy
endDate=dd.MM.yyyy
initialAmount=0.0
```

The values in this file will be injected into the viewmodel when the application is run with the argument "debug"
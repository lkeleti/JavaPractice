# EmployeeManager

Employee Manager frontend

# Docker
## Build
`ng build --prod`

`docker build -t employeesfrontend .`

`docker run --name employeesfrontend --network employeesapp-net -d -p 4200:4200 employeesfrontend`
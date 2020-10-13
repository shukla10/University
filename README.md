# Class Registration System in University.

DB : persistent H2 via local file set up in application.properties.

List of APIs :

# Register a student
```
  Ex :
  POST /student
  {
    "firstName": "Alice",
    "lastName": "Smith",
    "contactNumber": 1234,
    "address": "123, NY"
   }
```
 
 
# Register a professor
```
  Ex :
  POST /professor
  {
    "firstName": "Bob",
    "lastName": "Smith",
    "contactNumber": 1234,
    "address": "123, NY"
  }
```

# Create a course
```
  Ex :
  POST /course
  {
    "courseName": "Physics"
  }
```
    
# Assign professor to specified course
```
  Ex :
  POST /assignProfessor
  {
     "courseId": 1,
     "professorId": 2
  }
```

# Register student to specified course
```
  Ex :
  POST /registerStudent
  {
    "courseId": 1,
    "studentId": 2
  }
```
# List all the professors
```
   GET /professors
```
# List all the Students
```
   GET /students
```
# List all the Courses
```
   GET /courses
```
# List all courses assigned to a professor
```
   GET /courses/{professorId}
```
# List all student assigned to a course
```
   GET /students/{courseId}
```


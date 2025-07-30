# TaskMaster - Android Application

**TaskMaster** is an Android application that simplifies task management between admins and employees. It allows the admin to assign tasks to employees and track their progress, while employees can view, update, and request deletion of tasks.

---

## 🚀 Splash & First Screen

### 🔹 Splash Screen  
The app launches with a branded splash screen.  
![Splash Screen](src/images/splash.jpeg)

### 🔹 First Screen  
Users are taken to a screen where they can choose to log in as **Admin** or **Employee**.  
![First Screen](src/images/firstScreen.jpeg)

---

## 🔐 Login Screens

### 🔸 Admin Login  
Admins can securely log in to access their dashboard and task control panel.  
![Admin Login](src/images/admin_login.jpeg)

### 🔸 Employee Login  
Employees log in to view and manage their assigned tasks.  
![Employee Login](src/images/emp_login.jpeg)

---

## 📱 Features

### 👨‍💼 Admin Side

- Assign new tasks to employees
- Delete existing tasks
- Add/register new employees
- View the status of all tasks

#### Admin Screens:

1. **Dashboard**
   - Overview of tasks and employees  
   ![Admin Dashboard](src/images/dashboard_admin.jpeg)

2. **Tasks**
   - Click on a task to view its full details and assigned employee info  
   ![Admin Task List](src/images/tasks(admin).jpeg)  

3. **Employees**
   - View list of registered employees  
   ![Admin Employee List](src/images/add_employee(admin).jpeg)

4. **Notifications**
   - View all task-related notifications  
   ![Admin Notifications](src/images/activity_notifications.jpeg)

---

### 👩‍💼 Employee Side

- View tasks assigned by the admin
- Update the status of tasks (e.g., In Progress, Completed)
- Send delete requests for specific tasks

#### Employee Screens:

1. **Dashboard**
   - Overview of assigned tasks  
   ![Employee Dashboard](src/images/dashboard_emp.jpeg)

2. **Tasks**
   - View task details, status, and delete request option  
   ![Employee Task List](src/images/emp_tasks.jpeg)  
   ![Employee Task Details](src/images/tasks(emp_side).jpeg)

3. **Request Delete Task**
   - Allows employees to submit a reason for requesting task deletion  
   ![Request Delete Task](src/images/deletetas(emp_side).jpeg)

4. **Profile**
   - View and manage employee profile  
   *(Add image if available)*

---

## 🚀 Tech Stack

- Kotlin
- XML  
- Firebase Authentication  
- Firebase Realtime Database  
- Data Binding  
- RecyclerView (for list loading)

---

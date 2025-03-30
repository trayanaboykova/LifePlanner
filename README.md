# 🌟 LifePlanner App

I built LifePlanner as part of my journey to master advanced Java and Spring development. This comprehensive web application empowers users to manage various aspects of their life—including books, goals, media, recipes, and travel plans—through an integrated platform that follows best practices in software design and security.

## 🚀 Project Overview
LifePlanner is a multi-domain application designed to demonstrate robust object-oriented programming, clean layering, and secure user management. With over a dozen web pages (most of which are dynamic), the app includes features such as role-based access, validation, centralized exception handling, and even integration with an external microservice for daily quotes.

## 🎯 What I Learned
- 🏗️ Advanced Java and Spring Boot fundamentals
- 🔄 Effective use of MVC, dependency injection, and transaction management  
- 🔒 Secure user and role management with Spring Security  
- 🔄 Robust exception handling and validation techniques  
- 🔀 Integrating microservices using Feign Clients  
- ☁️ Working with cloud APIs (e.g., Cloudinary)

## 🔧 Features
- ✅ Manage books, goals, media, recipes, and travel plans with dedicated modules  
- ✅ User registration, login, profile editing, and role switching (user/admin)  
- ✅ Dynamic, interactive web pages with Thymeleaf templates  
- ✅ Centralized exception handling to avoid white-label error pages  
- ✅ Integration with a separate Daily Quote microservice  
- ✅ Scheduled tasks for enhanced user experience and real-time updates  
- ✅ Comprehensive unit, integration, and API tests

## 📂 Project Structure
- **Models, Services & Repositories:**  
  Separate packages for each domain ensuring high cohesion and loose coupling.
- **Controllers:**  
  Thin controllers delegate business logic to service layers.
- **DTOs & Mappers:**  
  Custom mapping between DTOs and domain entities.
- **Security:**  
  Role management and authentication via Spring Security.
- **External Integrations:**  
  Cloudinary for file uploads and a Feign Client for consuming microservices.

## 🛠️ Technologies I Used
[![Java](https://skillicons.dev/icons?i=java)](https://www.java.com/) [![Spring](https://skillicons.dev/icons?i=spring)](https://spring.io/) [![MySQL](https://skillicons.dev/icons?i=mysql)](https://www.mysql.com/)

## 🤔 How to Run
1. **Clone the Repository:**
   ```bash
   git clone https://github.com/yourusername/lifeplanner.git
   cd lifeplanner
2. **Configure the Database:**
   ```bash
   Update the application.properties (or application.yml) with your database credentials.
3. **Build and Run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   
## 📈 Learning Outcomes
This project strengthened my skills in designing modular, secure web applications using Spring Boot. I deepened my understanding of advanced Java concepts, transaction management, and microservice communication—all while building a user-friendly platform.

## 🌟 Acknowledgments
I’m grateful for the guidance and resources from the SoftUni community and all the mentors who supported my learning journey. Enjoy exploring LifePlanner and feel free to contribute!

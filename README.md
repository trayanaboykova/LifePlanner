# 🌟 LifePlanner App

I built LifePlanner as part of my journey to master advanced Java and Spring development. This comprehensive web application empowers users to manage various aspects of their life—including books, goals, media, recipes, and travel plans—through an integrated platform that follows best practices in software design and security.

## 🚀 Project Overview
LifePlanner is a multi-domain application designed to demonstrate robust object-oriented programming, clean layering, and secure user management. With over a dozen web pages (most of which are dynamic), the app includes features such as role-based access, validation, centralized exception handling, and even integration with an external microservice for daily quotes. Additionally, the application incorporates scheduled tasks that keep key data (like current date/time and user-specific daily quotes) updated in real time.

## 🎯 What I Learned
- 🏗️ Advanced Java and Spring Boot fundamentals
- 🔄 Effective use of MVC, dependency injection, and transaction management  
- 🔒 Secure user and role management with Spring Security  
- 🔄 Robust exception handling and validation techniques  
- 🔀 Integrating microservices using Feign Clients  
- ☁️ Working with cloud APIs (e.g., Cloudinary)

## 🔧 Features
- ✅ Multi-Domain Management:
  - Manage books, goals, media, recipes, and travel plans through dedicated modules.
  - Perform domain-specific operations such as adding, editing, sharing, liking, and deleting records.
- ✅ User Management & Security:
  - User Capabilities:
    - Register, log in, and edit their own profiles.
    - Deactivate their own account when needed.
  - Administrative Tools:
    - Administrators can switch user roles, update user statuses, and delete users.
    - Admins also review and approve shared posts submitted by users before they appear on LifeHub.
  - Role-based access is enforced using Spring Security.
- ✅ Responsive web pages built with Thymeleaf that provide real-time feedback and seamless navigation.
- ✅ External Integrations:
  - Cloudinary Integration:
    - Handle file uploads for profile pictures and media content.
  - [Daily Quote microservice](https://github.com/trayanaboykova/Daily-Quotes-Service-LifePlanner)
    - Integrates with a separate microservice to deliver dynamic daily quotes.
- ✅ Scheduled tasks for enhanced user experience and real-time updates
  - `DailyQuotesScheduler`: Automatically updates each user's daily quote image at regular intervals.

  - `DateAndTimeScheduler`: Continuously updates and provides the current date and time.
- ✅ Robust Exception Handling & Testing:
  - Centralized exception handling ensures meaningful error messages and avoids white-label error pages.
  - Comprehensive unit, integration, and API tests ensure the application's reliability and maintainability.

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
2. **Configure the Database:** <br>
   Update the `application.properties` (or `application.yml`) with your database credentials.
3. **Build and Run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
4. **Access the Application:** <br>
Open your browser at http://localhost:8080 and enjoy LifePlanner!
   
## 📈 Learning Outcomes
This project strengthened my skills in designing modular, secure web applications using Spring Boot. I deepened my understanding of advanced Java concepts, transaction management, and microservice communication—all while building a user-friendly platform.

## 🌟 Acknowledgments
I’m grateful for the guidance and resources from the SoftUni community and all the mentors who supported my learning journey. Enjoy exploring LifePlanner and feel free to contribute!

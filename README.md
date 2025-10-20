# ** 🚗 Web-based Vehicle Rental System - JourneyMATE **
###📘 Overview

The Vehicle Rental System is a full-stack web application designed to simplify the process of renting vehicles online.
It allows users to register, browse available vehicles, make bookings, complete payments, and submit feedback or inquiries to the system administrators.

The system is divided into six main management modules, ensuring scalability, modularity, and ease of maintenance.

🧩 Major Management Modules

1️⃣ User Management

Handles all user-related operations including registration, login, authentication, and profile management.
Features:

User registration and authentication

Admin and customer role separation

Profile update and password change

2️⃣ Vehicle Management

Manages vehicle records in the system and provides data for booking and display.
Features:

Add, update, and delete vehicles (admin only)

View available vehicles

Upload vehicle images

Attributes:

Vehicle ID, Brand, Model, Type, Picture, Price per Day, Availability Status

3️⃣ Booking Management

Handles the entire vehicle booking lifecycle from reservation to completion.
Features:

Create new bookings

Check vehicle availability

Calculate total rental amount based on days

Cancel or update bookings

Admin view of all bookings

Attributes:

Booking ID, Customer Name, Vehicle, Start Date, End Date, Total Amount, Status

4️⃣ Payment Management

Processes and records user payments for confirmed bookings.
Features:

Record and validate payments

Calculate total amount dynamically

Generate transaction reports

View payment history

Attributes:

Payment ID, Booking ID, Amount, Payment Date, Method, Status

5️⃣ Feedback Management

Allows customers to share experiences or rate their rental service.
Features:

Submit feedback or ratings

Admin view,respond of feedback 

Delete or manage feedback

Attributes:

Feedback ID, User ID, Rating, Comment, Date

6️⃣ Inquiry Management

Handles communication between customers and system administrators.
Features:

Submit inquiries or support requests

Admin can view, respond, and change inquiry status

Inquiry status tracking (Pending, Resolved)

Attributes:

Inquiry ID, User ID, Title, Description, Status, Created/Updated Date

🧱 Database Schema (Simplified Overview)

Tables:

users

vehicles

bookings

payments

feedbacks

inquiries

Each table is relationally linked using foreign keys, ensuring proper data integrity.

⚙️ Technologies Used

🖥️ Backend

Java Spring Boot

MySQL / PostgreSQL for database management

Spring Security with JWT authentication

🌐 Frontend

Javascript 

HTML

TailwindCSS

🧰 Tools

Maven for dependency management


🧰 Validations and Constraints

All required fields must be filled before submission.

Email and username must be unique for users.

Vehicle availability is checked before confirming booking.

Payments must be tied to valid booking records.

Feedback and inquiries require valid user IDs.

🚀 Future Enhancements

Add real-time notifications for booking confirmation.

Implement online payment gateway integration.

Introduce an admin dashboard with analytics.

Add email/SMS notifications for booking and inquiry updates.

Mobile app version.

👨‍💻 Author & Credits

Project Name: Vehicle Rental System - JourneyMATE
Developed by: SLIIT 2025-Y2-S1-MLB-B9G2-09
Date: October 2025
Language: Java (Spring Boot) & HTML,CSS,Java Script

🏁 License

This project is open-source and available under the MIT License.

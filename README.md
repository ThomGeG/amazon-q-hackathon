# Creditinder - Tinder for Credit Unions

A revolutionary loan approval application that uses community voting to help credit union members make lending decisions. Think "Tinder for Credit Unions" - applicants submit loan requests and credit union members swipe to approve or reject applications.

## Features

- **Loan Application Submission**: Easy-to-use form for submitting loan applications
- **Swipe-to-Vote Interface**: Tinder-like interface for voting on loan applications
- **Real-time Voting Results**: Track approval/rejection votes in real-time
- **Application Dashboard**: View all submitted applications with voting statistics
- **Risk Assessment**: Basic risk indicators based on credit score and income
- **Responsive Design**: Works on desktop and mobile devices

## Technology Stack

- **Java 21** (Latest LTS)
- **Spring Boot 3.2.0** with Spring Web
- **Thymeleaf** for server-side templating
- **Bootstrap 5.3.2** for responsive UI
- **Gradle 8.5** for build management
- **In-memory storage** (no database required for MVP)

## Getting Started

### Prerequisites

- Java 21 or higher
- Gradle 8.5+ (or use the included wrapper)

### Running the Application

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd amazon-q-hackathon
   ```

2. Run the application using Gradle wrapper:
   ```bash
   ./gradlew bootRun
   ```
   
   Or on Windows:
   ```cmd
   gradlew.bat bootRun
   ```

3. Open your browser and navigate to:
   ```
   http://localhost:8080
   ```

### Building the Application

To build the application:
```bash
./gradlew build
```

## Application Structure

```
src/
├── main/
│   ├── java/com/hackathon/creditinder/
│   │   ├── CreditinderApplication.java      # Main application class
│   │   ├── controller/
│   │   │   └── CreditinderController.java   # Web controller
│   │   ├── model/
│   │   │   └── LoanApplication.java         # Loan application model
│   │   └── service/
│   │       └── LoanApplicationService.java  # Business logic
│   └── resources/
│       ├── templates/                       # Thymeleaf templates
│       │   ├── layout.html                  # Base layout
│       │   ├── index.html                   # Home page
│       │   ├── apply.html                   # Application form
│       │   ├── swipe.html                   # Voting interface
│       │   ├── applications.html            # Applications list
│       │   └── application-details.html     # Application details
│       └── application.properties           # Configuration

## How It Works

1. **Apply for Loan**: Users fill out a comprehensive loan application form with personal and financial information
2. **Community Voting**: Credit union members can swipe through applications, voting to approve (❤️) or reject (❌)
3. **Real-time Results**: Voting results are tracked in real-time with approval percentages
4. **Decision Making**: Applications with high approval rates can be fast-tracked for processing

## Sample Data

The application comes pre-loaded with sample loan applications to demonstrate the voting functionality:

- Home improvement loan ($25,000)
- Debt consolidation loan ($15,000)
- Vehicle purchase loan ($35,000)
- Education loan ($8,000)

## API Endpoints

- `GET /` - Home page
- `GET /apply` - Loan application form
- `POST /apply` - Submit loan application
- `GET /swipe` - Voting interface
- `POST /vote` - Submit vote (AJAX)
- `GET /applications` - List all applications
- `GET /application/{id}` - Application details

## Future Enhancements

This is the "bicycle" version. Future enhancements could include:

- **Authentication & Authorization**: User accounts and role-based access
- **Database Integration**: Persistent storage with PostgreSQL/MySQL
- **Advanced Analytics**: Detailed voting analytics and trends
- **Notification System**: Email/SMS notifications for applicants
- **Mobile App**: Native mobile applications
- **Integration**: Connect with existing credit union systems
- **Machine Learning**: AI-powered risk assessment
- **Audit Trail**: Complete voting history and compliance features

## Contributing

This is a hackathon project. Feel free to fork and enhance!

## License

MIT License - see LICENSE file for details.

---

*Built with ❤️ for the Amazon Q Hackathon*

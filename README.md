# Explore Nepal 🏔️

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org/)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-orange.svg)](https://firebase.google.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 📱 Overview

**Explore Nepal** is a comprehensive Android mobile application designed to revolutionize travel experiences in Nepal. Built with modern Android development practices, this app serves as a complete digital travel companion for both domestic and international tourists seeking to discover Nepal's natural beauty and rich cultural heritage.

### 🎯 Purpose
Traditional travel planning relies on fragmented information sources, outdated guidebooks, and unreliable reviews. Explore Nepal addresses these challenges by providing a unified platform that combines real-time information, personalized recommendations, and offline capabilities - all in one user-friendly mobile application.

## ✨ Key Features

### 🔐 Authentication & Security
- **Firebase Authentication**: Secure email/password and social media login
- **Session Management**: Persistent login with automatic session handling
- **Data Privacy**: GDPR-compliant user data protection

### 🗺️ Interactive Maps & Navigation
- **Google Maps Integration**: Real-time location tracking and navigation
- **Offline Map Support**: Cached maps for remote trekking areas
- **Custom Markers**: Interactive markers with detailed information windows
- **Route Planning**: Dynamic trekking route suggestions

### 🏨 Hotel Discovery & Booking
- **Map-Based Search**: Find accommodations with visual map interface
- **Advanced Filtering**: Filter by price, rating, amenities, and reviews
- **Instant Booking**: Seamless hotel reservation system
- **Bookmark System**: Save favorite places for future reference

### 🥾 Trekking & Adventure
- **AI-Powered Recommendations**: Personalized trek suggestions based on preferences
- **Difficulty Assessment**: Routes categorized by fitness level and experience
- **Offline Trek Maps**: Access trekking information without internet connectivity
- **Cultural Integration**: Information about local customs and traditions

### 💰 Budget Management
- **Expense Tracking**: Real-time expense monitoring and categorization
- **Budget Alerts**: Smart notifications when approaching spending limits
- **Currency Support**: Multi-currency support for international travelers
- **Expense Analytics**: Visual reports and spending insights

### 🎨 Modern User Experience
- **Jetpack Compose UI**: Modern, responsive, and intuitive interface
- **Material Design**: Following Google's design principles
- **Dark Mode Support**: Eye-friendly viewing in all conditions
- **Accessibility**: Inclusive design for users with disabilities

## 🛠️ Technical Architecture

### Development Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Backend**: Firebase (Authentication, Firestore, Storage)
- **Maps**: Google Maps SDK for Android
- **Dependency Injection**: Hilt/Dagger
- **Image Loading**: Coil
- **Network**: Retrofit with OkHttp

### Project Structure
```
app/
├── src/
│   ├── main/
│   │   ├── java/com/explorenepal/
│   │   │   ├── ui/
│   │   │   │   ├── auth/          # Authentication screens
│   │   │   │   ├── dashboard/     # Main dashboard
│   │   │   │   ├── hotels/        # Hotel booking features
│   │   │   │   ├── trekking/      # Trekking routes
│   │   │   │   ├── budget/        # Budget tracking
│   │   │   │   └── maps/          # Map integration
│   │   │   ├── viewmodel/         # ViewModels
│   │   │   ├── repository/        # Data repositories
│   │   │   ├── model/             # Data models
│   │   │   ├── utils/             # Utility classes
│   │   │   └── di/                # Dependency injection
│   │   └── res/                   # Resources
│   ├── test/                      # Unit tests
│   └── androidTest/               # Instrumented tests
└── build.gradle
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 11 or higher
- Android SDK 24+ (API level 24)
- Google Maps API key
- Firebase project setup

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Saroj-thapa/TouristGuide.git
   cd TouristGuide
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Configure Firebase**
   - Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Add your Android app to the Firebase project
   - Download `google-services.json` and place it in the `app/` directory
   - Enable Authentication, Firestore, and Storage in Firebase console

4. **Set up Google Maps API**
   - Get a Google Maps API key from [Google Cloud Console](https://console.cloud.google.com/)
   - Add the API key to `local.properties`:
     ```properties
     MAPS_API_KEY=your_google_maps_api_key_here
     ```

5. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```

### Configuration

Create a `local.properties` file in the project root:
```properties
sdk.dir=/path/to/android/sdk
MAPS_API_KEY=your_google_maps_api_key
FIREBASE_API_KEY=your_firebase_api_key
```

## 🧪 Testing

The project includes comprehensive testing coverage with both unit and instrumented tests.

### Running Tests

**Unit Tests:**
```bash
./gradlew test
```

**Instrumented Tests:**
```bash
./gradlew connectedAndroidTest
```

### Test Coverage
- **Unit Tests**: ViewModel logic, data processing, utility functions
- **Instrumented Tests**: UI interactions, navigation flows, database operations
- **Integration Tests**: API communications, Firebase integration

### Key Test Cases
1. **Login Validation**: Email format and password strength validation
2. **Expense Calculation**: Accurate budget tracking and calculations
3. **Navigation Flow**: Screen transitions and user journey validation
4. **UI Interactions**: Button clicks, form submissions, list operations

## 👥 Team & Methodology

### Development Team
- **Saroj Thapa** - Team Lead, Backend & Frontend Development
- **Manish Rumba** - Scrum Master, Frontend Development & Testing
- **Mandish Pratap Sen** - Frontend Development, Documentation & Testing

### Agile Development
We followed **Scrum methodology** with:
- **Sprint Duration**: 2-week sprints
- **Daily Stand-ups**: Regular progress updates and blocker resolution
- **Sprint Planning**: Feature prioritization and task distribution
- **Sprint Reviews**: Stakeholder feedback and feature demonstrations
- **Retrospectives**: Continuous improvement and process optimization

### Collaboration Tools
- **Version Control**: Git with GitHub
- **Project Management**: Trello/Jira boards
- **Communication**: Daily stand-ups and team coordination
- **Documentation**: Comprehensive project documentation

## 📊 Performance & Analytics

### App Performance
- **Startup Time**: < 3 seconds on mid-range devices
- **Memory Usage**: Optimized for devices with 2GB+ RAM
- **Battery Efficiency**: Background location tracking optimized
- **Offline Support**: Core features available without internet

### User Analytics
- Firebase Analytics integration for user behavior tracking
- Crash reporting with Firebase Crashlytics
- Performance monitoring for app optimization

## 🔒 Privacy & Security

### Data Protection
- **Minimal Data Collection**: Only essential user information collected
- **Encryption**: All data encrypted in transit and at rest
- **User Consent**: Clear permission requests and privacy policies
- **GDPR Compliance**: European data protection standards followed

### Security Measures
- **Authentication**: Secure Firebase Authentication
- **API Security**: Encrypted API communications
- **Local Storage**: Sensitive data secured with Android Keystore
- **Input Validation**: Comprehensive user input sanitization

## 🌱 Future Enhancements

### Planned Features
- **AI Chat Assistant**: Real-time travel guidance and recommendations
- **Weather Integration**: Live weather updates for trekking routes
- **Social Features**: User reviews, photo sharing, and travel stories
- **Multi-language Support**: Nepali, Hindi, and other regional languages
- **Wearable Support**: Android Wear integration for hikers

### Technical Improvements
- **Performance Optimization**: Enhanced app loading and response times
- **Offline Capabilities**: Extended offline functionality
- **Push Notifications**: Smart alerts and travel reminders
- **Machine Learning**: Advanced recommendation algorithms

## 🤝 Contributing

We welcome contributions from the community! Please follow these guidelines:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Commit your changes**: `git commit -m 'Add some amazing feature'`
4. **Push to the branch**: `git push origin feature/amazing-feature`
5. **Open a Pull Request**

### Development Guidelines
- Follow Kotlin coding conventions
- Write unit tests for new features
- Update documentation as needed
- Ensure UI/UX consistency

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Coffee Quality Institute** for inspiration in data-driven applications
- **Firebase Team** for robust backend services
- **Android Development Community** for continuous learning resources
- **Nepal Tourism Board** for promoting sustainable tourism
- **Open Source Contributors** for various libraries and tools used

## 📞 Contact & Support

### Project Links
- **GitHub Repository**: [https://github.com/Saroj-thapa/TouristGuide](https://github.com/Saroj-thapa/TouristGuide/tree/saroj)
- **Trello Board**: [Project Management](https://trello.com/b/uyreEajg/kotlin)
- **Figma Design**: [UI/UX Designs](https://www.figma.com/design/L9t1MgNwPvZOfGM6XuQYux/ExploreNepal)
- **Demo Video**: [YouTube Presentation](https://youtu.be/dzpiQHgZOFk)

### Team Contact
- **Saroj Thapa**: saroj.thapa@softwarica.edu.np
- **Manish Rumba**: manish.rumba@softwarica.edu.np  
- **Mandish Pratap Sen**: mandish.sen@softwarica.edu.np

### Academic Supervision
- **Course**: STW5001 CEM Software Engineering
- **Institution**: Softwarica College of IT and E-Commerce
- **Supervisor**: Mr. Sandis Prajapati

---

**Made with ❤️ for Nepal Tourism** 🇳🇵

*Explore Nepal - Your Digital Travel Companion*

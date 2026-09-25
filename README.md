# Recipe Book App

A native Android application for discovering, creating, editing, and sharing recipes.

## Overview

Recipe Book App is a Java-based Android application that provides a structured way to browse recipes by category, search for recipes, manage personal recipes, and view recipe details.

The project integrates Firebase services for authentication and cloud data management, Cloudinary for image handling, and AndroidX components for building the application interface.

## Features

### Authentication
- User registration and login.
- Persistent login state using SharedPreferences.
- Firebase Authentication integration.
- Google sign-in flow support.

### Recipe Management
- Browse recipes by category.
- View recipe details including ingredients and preparation steps.
- Add new recipes.
- Edit existing recipes.
- Manage recipes associated with the user's profile.
- Add recipe images and optional YouTube links.

### Recipe Discovery
- Category-based recipe navigation.
- Search recipes from the home interface.
- View recipes through a tabbed interface.

### User Profiles
- User profile screen.
- Manage personal recipe content.
- Display user-related recipe information.

### Image Management
- Recipe and profile image handling through Cloudinary.
- Image loading and caching using Glide and Picasso.

### Cloud Data
- Firebase Firestore for recipe and category data.
- Firebase Authentication for account management.
- Firebase Analytics dependency for application analytics.

## Technology Stack

### Language
- Java 11

### Android
- Android SDK
- AndroidX
- Material Components
- ViewBinding
- RecyclerView
- ViewPager2
- TabLayout
- ConstraintLayout

### Backend and Cloud Services
- Firebase Authentication
- Firebase Firestore
- Firebase Analytics
- Cloudinary

### Libraries
- Glide
- Picasso
- SharedPreferences
- JUnit
- AndroidX Test
- Espresso

## Architecture and Project Organization

The application is organized by feature and responsibility rather than placing all activities and classes in a single package.

The main package structure includes:

```text
com.example.recipebook
├── CloudinaryLib.java
├── Recipe.java
├── Utils.java
├── a_SplashScreen
├── b_RegisterToTheApp
├── c_Home
├── d_AddEditRecipe
├── e_RecipeDetails
└── f_Profile
```

The feature-oriented structure separates major application flows such as authentication, home/category browsing, recipe creation and editing, recipe details, and profile management.

The home flow also uses ViewPager2 and TabLayout to provide category-based navigation.

## Firebase Integration

Firebase is used as the application's cloud backend.

The project uses:

- Firebase Authentication for user accounts.
- Cloud Firestore for recipe and category data.
- Firebase Analytics for application analytics.

Before running the application, a valid Firebase project configuration is required.

## Cloudinary Integration

Cloudinary is used for handling recipe and profile images.

The project contains a dedicated `CloudinaryLib` class for the Cloudinary integration, while Glide and Picasso are used for loading images in the Android UI.

When adapting the project for another Firebase or Cloudinary environment, configuration values should be kept outside source control whenever possible.

## UI Components

The application uses standard Android UI components and AndroidX libraries, including:

- ViewBinding
- RecyclerView
- ViewPager2
- TabLayout
- ConstraintLayout
- Material Components
- SearchView

These components are used to build the authentication, home, category, recipe editing, recipe details, and profile flows.

## Project Configuration

| Configuration | Value |
|---|---|
| Language | Java |
| Java Version | 11 |
| Compile SDK | 35 |
| Target SDK | 34 |
| Minimum SDK | 25 |
| ViewBinding | Enabled |
| Firebase BOM | 33.15.0 |

## Getting Started

### Prerequisites

Install the following before opening the project:

- Android Studio
- Android SDK
- JDK 11
- A Firebase project
- A Cloudinary account/configuration

### Clone the Repository

```bash
git clone https://github.com/hala-dalloul/Recipe-Book-App.git
cd Recipe-Book-App
```

Open the project in Android Studio and allow Gradle to synchronize the project dependencies.

### Firebase Configuration

1. Create or select a Firebase project.
2. Register an Android application using the package name:

```text
com.example.recipebook
```

3. Configure Firebase Authentication and Firestore.
4. Add the appropriate Firebase Android configuration file to the application module.
5. Review Firestore security rules before deploying the application.

Do not commit private credentials or environment-specific secrets to a public repository.

### Cloudinary Configuration

Configure Cloudinary using your own project credentials and follow Cloudinary's recommended approach for protecting sensitive configuration values.

## Development Notes

The project is intentionally implemented with a traditional Android Views approach using Java and ViewBinding.

Potential areas for future architectural improvement include:

- Migrating selected components to Kotlin.
- Introducing a repository layer.
- Moving UI state management into ViewModels.
- Applying Clean Architecture principles where appropriate.
- Introducing dependency injection.
- Expanding automated test coverage.
- Improving offline support and error handling.
- Adding pagination for larger datasets.
- Strengthening input validation and accessibility.

## What This Project Demonstrates

This project demonstrates practical experience with:

- Native Android development using Java.
- Firebase Authentication and Firestore.
- Cloud-based image management.
- Android UI development with XML and AndroidX.
- RecyclerView-based dynamic interfaces.
- TabLayout and ViewPager2 navigation.
- CRUD-oriented application flows.
- User session persistence.
- Integration of third-party Android libraries.
- Organizing an Android project by application features.

## Future Improvements

Possible future improvements include:

- Migration toward a modern Kotlin-based implementation.
- MVVM and repository-based architecture.
- Dependency injection.
- Improved automated testing.
- Offline-first data handling.
- Better loading, empty, and error states.
- More robust security and validation rules.
- Contributor documentation and issue templates.
- Accessibility improvements.

## Contributing

Contributions, suggestions, and improvements are welcome.

A typical contribution workflow is:

1. Fork the repository.
2. Create a feature branch.
3. Implement and test the change.
4. Keep commits focused and descriptive.
5. Open a pull request with a clear explanation of the change.

Before contributing, review the existing project structure and avoid committing credentials or environment-specific configuration.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

## Author

Hala Dalloul

GitHub: https://github.com/hala-dalloul

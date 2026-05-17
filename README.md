# mobile-scaffold
LLM prompting for mobile apps.

my_app/
├── shared/
│   └── domain/
│       ├── entities/
│       │   └── User.kt (or User.swift)
│       └── repositories/
│           └── UserRepository.kt (or UserRepository.swift)
│
├── android/
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/example/myapp/
│   │   │   │   ├── data/
│   │   │   │   │   ├── db/
│   │   │   │   │   │   ├── UserDao.kt
│   │   │   │   │   │   └── AppDatabase.kt
│   │   │   │   │   ├── api/
│   │   │   │   │   │   ├── UserApiClient.kt
│   │   │   │   │   │   └── ApiModule.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       └── UserRepositoryImpl.kt
│   │   │   │   ├── di/
│   │   │   │   │   └── AppModule.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   └── UserScreen.kt
│   │   │   │   │   ├── viewmodel/
│   │   │   │   │   │   └── UserViewModel.kt
│   │   │   │   │   └── theme/
│   │   │   │   │       └── Theme.kt
│   │   │   │   └── MainActivity.kt
│   │   │   └── res/
│   │   │       └── ...
│   │   └── build.gradle
│   └── settings.gradle
│
└── ios/
    ├── MyApp/
    │   ├── Data/
    │   │   ├── DB/
    │   │   │   ├── UserEntity+CoreDataClass.swift
    │   │   │   ├── UserEntity+CoreDataProperties.swift
    │   │   │   └── Persistence.swift
    │   │   ├── API/
    │   │   │   ├── UserAPIClient.swift
    │   │   │   └── APIConfiguration.swift
    │   │   └── Repositories/
    │   │       └── UserRepositoryImpl.swift
    │   ├── Domain/
    │   │   └── Entities/
    │   │       └── User.swift
    │   ├── Presentation/
    │   │   ├── ViewModels/
    │   │   │   └── UserViewModel.swift
    │   │   └── Views/
    │   │       └── UserView.swift
    │   └── MyAppApp.swift
    └── MyApp.xcodeproj

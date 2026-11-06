BreakTheScroll 📱
BreakTheScroll is an Android-native behavioral intercept application designed to disrupt doomscrolling. By monitoring foreground application usage and intercepting excessive feeds, 
it guides users toward real-world micro-tasks and offline wellness activities.


Key Features
Behavioral Interception: Automatically detects continuous dwell time inside target social media apps using Android system services.
Activity Flashcards: Swipeable flashcards offering offline wellness activities.
Calm Tech Design: Built on a clean, modern Android Material 3 design system to reduce eye strain and cognitive fatigue.


graph TD
    A[Foreground App Usage] -->|Polled by UsageStatsManager| B(Interception Service)
    B -->|Exceeds Time Threshold| C{Trigger Intercept Overlay}
    C -->|User Chooses Task| D[Swipeable Activity Deck]
    C -->|User Chooses Breathing| E[3-Second Calm Reset]
    D -->|Task Completed| F[(Room Database)]
    E -->|Session Reset| F


Tech stack
Platform: Android (API 26+)
UI Framework: Jetpack Compose & Material 3
Local Persistence: Room (SQLite)
System Services: UsageStatsManager & Accessibility APIs
Language: Kotlin



# NutraHelp

A comprehensive GLP-1 companion and nutrition tracking app for Android, built with Jetpack Compose.

![CI](https://github.com/gipghub/NutraHelp/actions/workflows/ci.yml/badge.svg)

## Features

### Food & Nutrition
- **Food Diary** — Log meals with hunger/fullness ratings; search the Open Food Facts database or scan product barcodes with the camera
- **Meal Log & Meal Plan** — Plan and track meals by day
- **Macro Tracker** — Monitor daily protein, carbs, and fat
- **Calorie Deficit Tracker** — Track daily energy balance
- **Fiber, Sugar, Sodium, Caffeine Trackers** — Individual nutrient logs
- **Recipe Nutrition Calculator** — Calculate nutrition for custom recipes
- **Meal Prep Planner** — Organize weekly meal prep
- **Grocery List** — Build and manage a shopping list
- **Portion Size Guide** — Visual portion reference
- **Protein Source Log** — Track protein-rich foods
- **Food Sensitivity Log** — Record and monitor food reactions
- **Mindful Eating Log** — Log eating pace and awareness

### GLP-1 & Medication
- **GLP-1 Injection Log** — Track Ozempic/Wegovy/Mounjaro dose and date
- **Injection Site Tracker** — Rotate injection sites with a visual guide
- **Medication Tracker & History** — Log all medications with reminders
- **Side Effects Log** — Record GLP-1 side effects over time
- **Nausea Log** — Dedicated nausea severity tracking
- **Non-Scale Victories (NSV)** — Celebrate non-weight wins

### Health Metrics
- **Weight Tracking** — Log weight with trend chart
- **BMI Stats** — Calculate and track BMI
- **Body Fat & Body Measurements** — Log body composition
- **Blood Sugar Log** — Track glucose readings
- **Blood Pressure Log** — Monitor systolic/diastolic
- **A1C Tracker** — Log A1C results over time
- **Cholesterol Log** — HDL, LDL, and total cholesterol
- **Lab Results** — Store and review lab values
- **Heart Rate Log** — Resting and active heart rate

### Wellness & Lifestyle
- **Sleep Tracker** — Log sleep duration and quality
- **Water Intake Log** — Track daily hydration
- **Exercise Log** — Record workouts and activity
- **Step Counter Log** — Daily step tracking
- **Stress Tracker** — Log stress levels and triggers
- **Mood Tracker** — Daily mood and emotional check-ins
- **Energy Level Log** — Track energy throughout the day
- **Fasting Timer** — Intermittent fasting countdown
- **Habit Tracker** — Build and maintain healthy habits
- **Daily Check-In** — Quick daily wellness snapshot
- **Daily Journal** — Free-form journaling
- **Alcohol Tracker** — Log and limit alcohol intake
- **Gut Health Log** — Track digestive symptoms
- **Inflammation Log** — Record inflammation symptoms
- **Craving Log** — Identify and manage food cravings
- **Hunger & Fullness Log** — Mindful hunger awareness
- **Meal Timing Log** — Log eating windows

### Tools & Calculators
- **BMR/TDEE Calculator** — Calculate calorie needs
- **Hydration Calculator** — Personalized water intake goal
- **Weight Loss Projection** — Visualize progress timeline
- **Progress Charts** — Graphs for weight, calories, macros
- **Milestone Log** — Set and celebrate goals
- **Goal Tracker** — Define and track health goals

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose |
| Database | Room (SQLite) |
| Async | Kotlin Coroutines + Flow |
| Camera | CameraX 1.4 |
| Barcode scanning | ML Kit Barcode Scanning |
| Food data | Open Food Facts API |
| Architecture | MVVM |

## Building

Requires Android Studio Hedgehog or newer, or JDK 17+.

```bash
./gradlew assembleDebug
```

The debug APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## CI/CD

Every push to `master` and every pull request triggers a GitHub Actions build that:
1. Runs unit tests
2. Builds the debug APK
3. Uploads the APK as a downloadable artifact (retained 14 days)

## Requirements

- Android 7.0+ (API 24)
- Camera required only for barcode scanning (optional feature)
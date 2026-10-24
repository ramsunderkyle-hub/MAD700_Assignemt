# Smart Pantry Manager 

A small Java Android app for keeping track of leftover ingredients and finding recipes that can be made with the available quantities. The Android Studio project is named DRAFT; the installed app is called Smart Pantry.

## Features
- Five Activities: Pantry, Add/Edit Ingredient, Suggested Recipes, Recipe Detail and Settings.
- Full pantry CRUD with SQLite persistence and confirmation before deletion.
- Custom ListView adapters, XML LinearLayouts, explicit Intents and a bottom navigation row.
- Twenty recipes seeded into relational tables on database creation.
- Strict matching: all ingredients and sufficient quantities are required. Separate batches add together.
- Common name aliases (eggs/egg, tomatoes/tomato, bread slices/bread, yogurt/yoghurt).
- Compatible unit conversion: kg/g; l/ml/tsp/tbsp/metric cup. No mass-to-volume or count-to-mass assumptions.
- Optional expiry dates, exclusion of expired batches, and saved expiring-soon labels.
- No account, backend, runtime internet, location, map or payment services.

## Database choice
SQLiteOpenHelper keeps this small, single-user app local and works without a network connection. It also follows the module guide's approach to local persistent storage. Pantry items are stored in pantry_items; recipes and recipe_ingredients hold the seeded collection. SharedPreferences stores the display setting. Uninstalling or clearing app storage removes pantry data.

## Requirements and run instructions
Android Studio with JDK 17, Android SDK Platform 35 and a device/emulator on Android 8.0+ (API 26). Open this directory, let Gradle sync, choose the app configuration and press Run. The wrapper uses Gradle 8.9 and Android Gradle Plugin 8.7.3. No Firebase keys or other credentials are needed. Android Studio normally creates local.properties for your own SDK path; this machine-specific file is deliberately excluded.

From a terminal with JDK 17 and ANDROID_HOME set:

Windows PowerShell: `.\gradlew.bat assembleDebug`

macOS/Linux: `./gradlew assembleDebug`

The APK is produced at app/build/outputs/apk/debug/app-debug.apk. A built draft APK, if supplied, is in ../APK/DRAFT.apk.

## How the code fits together
- DatabaseHelper.java creates/seeds SQLite and performs CRUD with bound ID parameters.
- IngredientRules.java validates input, normalises known aliases and defines compatible unit groups.
- RecipeMatcher.java aggregates quantities in base units and rejects a recipe as soon as any requirement is short.
- MainActivity.java reloads the pantry in onResume, so returning from Add/Edit shows current data.
- IngredientActivity.java handles both adding and editing, using the item_id Intent extra.
- PantryAdapter.java and RecipeAdapter.java connect model objects to recycled row layouts.
- SuggestedRecipesActivity.java filters the database recipe collection and passes recipe_id to RecipeDetailActivity.java.
- SettingsActivity.java persists the expiring-soon display preference.

## Behaviour and limits
Piece is a count unit; bread uses one piece per slice and garlic one piece per clove. Recipes stating beans assume cooked, drained beans, as the method explains. Water and oil are real requirements; neither is silently assumed to be available. Expiry today is allowed; dates before today are excluded. The switch only changes in-app labels, not expiry exclusion, and does not schedule notifications. Pantry stock is not deducted by viewing a recipe. Ingredients can have separate batches with different expiry dates. Name normalisation is a short explicit alias list, not full natural language processing. Matching recipes are individual options; they are not a simultaneous meal plan sharing stock. A 250 ml metric cup is used consistently. No density conversion is attempted.

## Repeatable matching tests
The tests use the real Java matcher, not a copied implementation. With a JDK installed, run tests/run_tests.bat on Windows or tests/run_tests.sh on Linux/macOS. The tests compile into a temporary directory outside the project and run validation, quantity, alias, expiry and unit conversion cases. Android UI and real persistence still need testing on a device; see ../Evidence for results obtained during draft preparation and ../Report/SCREENSHOT_CHECKLIST.md for your manual evidence.

## GitHub and honest development evidence
This ZIP includes the real local Git history created while preparing this draft. It is not a hosted GitHub repository and does not satisfy the brief's requirement for 10 genuine commits over the course of your development. Review and develop the work yourself. Do not manufacture a timeline, backdate commits, or publish a false claim of original authorship. See ../GITHUB_SETUP.md for uploading and recording your actual work.

## Sources and assistance
Richfield Graduate Institute of Technology (n.d.) MOBILE APP DEVELOPMENT 700 learner guide and Smart Pantry Manager practical assignment brief (supplied course PDFs).

Android Developers (n.d.) Save data using SQLite. https://developer.android.com/training/data-storage/sqlite

Android Developers (n.d.) The activity lifecycle. https://developer.android.com/guide/components/activities/activity-lifecycle

Android Developers (n.d.) BaseAdapter. https://developer.android.com/reference/android/widget/BaseAdapter

AI assistance: this project draft, initial report and test scaffolding were prepared with ChatGPT. Review and adapt the work and follow your institution's acknowledgement requirements.

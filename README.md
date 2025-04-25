# SleekChat - Your Modern Chat Solution📳

Welcome to SleekChat, My most technically advanced and farwell java based android application. This app isn't just an oridanry app, it reflects my coding philosophy and experience in this field. from wanting to keep the code neaty and structured and symmetric across layers to following a declarative and modular coding approach with OOP. Requirements are preset with contracts and fullfilled with implementations.

## App Features 

### User Presence Detection

The application perfectly communicates the availability between the users. It reflects 🟢Online, 🔴Offline, and 🟡InChat states.

### Material UI

The app experience is unique and well crafted using material3 recommended theme and design. The app is compteting with other modern android apps.

### Message Status

The message status is updated automatically by tracking the presence of the receiver. Right now, There are three states i.e. Sent, Seeable (receier is using the app but not in chat), and SEEN.

### Push Notifications

Notifications are sent and received with the message and sender's informaton to ensure ontime delivery of critical information even if the app is closed.


## Development Techniques

### Clean Architecture + MVVM - The Game Changer

The functionality is spread across layers i.e. presentation, ViewModel, UseCase, Respository (Contracts & Impl), and Service (Data). The UseCases played a major role in datasync, filteration, and payloading.

### Workers and WorkManagers

To Sync live messages to the databse, I've trusted workers to ensure the sync operation takes place under special conditions and retry if failed. Some goes with messages updates and deletes. 

### ChatGPT - My Personal Assistant

The development couldn't have completed in such a short span without the assistance of ChatGPT. This reiterates my opionion that AI is not the future, it is the Now. ChatGPT helped me with requirement identification, feature panning, and laying out the implementation strategies.
👍Kudos to OpenAI and ChatGPT. 


## Tech Stack

- **Langugaes** - Java
- **Auth** - FirebaseAuth
- **Database** - Firestore
- **Realtime Database** - Firesbase Realtime Database
- **UI/UX** - XML, Material3


## How to Use this App?

1. Clone the Repository
2. Open in your IDE
3. Make a Firebase Account and Project
4. Enable Fireabase Auth, Cloud Firestore and Realtime Database
5. Declare testing number for Firebase Auth
6. Submit your SHA1 and SHA256 values in the project settings.
7. Downlaod google-services.json
8. Install the file in your project
9. Login with your Testing number provided earlier.
10. Use the app.



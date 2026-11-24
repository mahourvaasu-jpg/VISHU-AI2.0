VISHU AI - Android starter project

IMPORTANT:
- Ye source project hai, ready-made APK nahi.
- Is starter build mein futuristic chat UI, Android speech recognition aur Android Text-to-Speech wired hain.
- Reply logic abhi demo/local hai. ChatGPT-level AI ke liye secure backend/API integration next step hai.
- Female voice device ke installed TTS voices par depend karti hai; Android Settings > Accessibility / Text-to-speech output mein voice change kar sakte hain.

APK build karne ke liye:
1. Is ZIP ko GitHub par repository mein upload karna hoga, ya kisi Android build environment (AndroidIDE) mein project open karna hoga.
2. Android Gradle Plugin 8.6.1 aur Gradle 8.7+ compatible build environment use karein.
3. Build command: ./gradlew assembleDebug
4. APK path: app/build/outputs/apk/debug/app-debug.apk

Phone-only user ke liye agla step:
GitHub account se cloud build workflow configure karke APK Actions tab se download karna sabse practical raasta hai. Iske liye main next turn mein exact mobile steps de sakta hoon.

Permissions:
- Microphone: speech input ke liye
- Internet: future AI connection ke liye

Security:
- API keys kabhi Android app ke source code mein hardcode na karein. Production AI connection ke liye backend proxy use karein.

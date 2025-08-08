Full Android Studio project ready for GitHub + Codemagic

Upload this project's contents to the root of your GitHub repository (not inside an extra folder).

Then on Codemagic:
- Add app -> Connect GitHub -> select this repo
- Start build (Codemagic will detect Android project and run Gradle)
- Download the APK from Artifacts (app/build/outputs/apk/debug/app-debug.apk)

If Codemagic still complains, paste the exact error here and I'll fix it.

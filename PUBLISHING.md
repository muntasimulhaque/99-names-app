# Publishing to Google Play

You already have a Play Console developer account with one app in closed testing, so this skips account setup.

## 1. Signing key

In Android Studio: **Build → Generate Signed App Bundle → Create new keystore**.
Keep the `.jks` file and passwords somewhere safe and OUT of the repo (`.gitignore` already excludes `*.jks`). Losing it is recoverable only because Play App Signing re-signs for you — enroll in Play App Signing when prompted (default).

## 2. Build the release bundle

Build → Generate Signed App Bundle → release. Output: `app/release/app-release.aab`.

## 3. Play Console — create the app

- App name: **99 Names of Allah** · Free · App (not game).
- Category: Books & Reference (or Lifestyle).
- Data safety form: "No data collected, no data shared" (true — see PRIVACY.md). You'll need the privacy policy hosted at a public URL — the PRIVACY.md on your GitHub repo (or a page on your blog) works; paste that URL.
- Content rating questionnaire: Reference/educational, no user content, no ads → typically rated 3+/Everyone.
- Ads declaration: contains no ads.

## 4. Store listing

- Short description (80 chars max): e.g. "Read and memorize the 99 Names of Allah. Offline, ad-free, open source."
- Full description: adapt README features section.
- Screenshots: at least 2 phone screenshots (take from your device; light + dark theme look good). Feature graphic 1024×500 required.
- App icon 512×512 PNG (export from the adaptive icon, or ask me to generate one).

## 5. Testing track first

Personal accounts must run a closed test with at least 12 testers for 14 days before production access (same as your first app). Create a closed testing release, upload the .aab, add your tester list, and apply for production after the window.

## 6. Releases after v1

Bump `versionCode` (and `versionName`) in `app/build.gradle.kts` for every upload.

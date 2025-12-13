---
description: 
---

---
description: Update app version, changelog, and build after commit
---

# Update Version After Commit
This workflow updates the app version in build.gradle.kts and SettingsScreen.kt, creates/updates the changelog, and optionally builds the app.
## Steps
1. Get the latest git commit hash from main branch:
   ```bash
   git rev-parse --short HEAD

---
**Summary:**
1. Save the changelog content above as `fastlane/metadata/android/en-US/changelogs/2.txt`
2. Replace your existing workflow with the updated version above that includes changelog generation
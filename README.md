# Pin Cat Bot — Android MVP

Vision-based Android automation for the cat/pin game. The app captures the game screen locally with MediaProjection, detects cat/pin pieces, checks the forward corridor, and taps a selected safe pin through AccessibilityService.

## Phone-only use
1. Install the debug APK.
2. Grant Screen Capture.
3. Enable Pin Cat Bot under Android Accessibility.
4. Open the game.
5. Press START BOT.

No PC, root, ADB, server, or Python is required on the recipient phone.

## Build
GitHub Actions builds `app-debug.apk` automatically on pushes to `main`, or manually from Actions → Build PinCatBot APK → Run workflow.

## MVP limitations
The detector is tuned to the supplied game's lavender cat/pin visuals and portrait layout. Validate it on several levels before unattended use. The solver intentionally waits when it cannot identify a safe pin.